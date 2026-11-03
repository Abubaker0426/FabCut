package expo.modules.zebrausbprint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterUsb;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import expo.modules.kotlin.Promise;

/**
 * Port of the native fabcut app:
 *  - EditBundleDialog.checkForZebraPrinter / requestConnectionToZebraPrinter
 *  - PrintManager.printFromZebraPrinter
 * AsyncFunction runs off the main thread, so blocking on a latch is safe.
 */
public class ZebraPrinterHelper {

    private static final String ACTION_USB_PERMISSION = "expo.modules.zebrausbprint.USB_PERMISSION";
    // Same value as AppConstants.PRINTER_MANUFACTURER_NAME in the Java app
    private static final String PRINTER_MANUFACTURER_NAME = "Zebra Technologies";
    private static final int TIMEOUT_SECONDS = 15;

    private static DiscoveredPrinterUsb activePrinter;
    private static Connection activeConnection;

    public static synchronized void getPrinterStatus(Context context, Promise promise) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        // close any previous connection so we always start clean
        closeQuietly();

        boolean zebraPresent = false;
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            String manufacturer = device.getManufacturerName();
            if (manufacturer != null && manufacturer.equals(PRINTER_MANUFACTURER_NAME)) {
                zebraPresent = true;
                break;
            }
        }
        if (!zebraPresent) {
            resolveStatus(promise, false, "No Zebra printer was detected over USB", null);
            return;
        }

        discoverAndConnect(context, usbManager, promise);
    }

    private static void discoverAndConnect(Context context, UsbManager usbManager, Promise promise) {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] granted = {false};
        final boolean[] found = {false};

        // Explicit intent (setPackage) is required on Android 14+ for a mutable PendingIntent
        Intent permissionIntent = new Intent(ACTION_USB_PERMISSION);
        permissionIntent.setPackage(context.getPackageName());
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, permissionIntent, flags);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    granted[0] = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    latch.countDown();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(receiver, filter);
        }

        try {
            UsbDiscoverer.findPrinters(context, new DiscoveryHandler() {
                @Override
                public void foundPrinter(DiscoveredPrinter printer) {
                    if (found[0]) return;
                    found[0] = true;
                    activePrinter = (DiscoveredPrinterUsb) printer;
                    usbManager.requestPermission(activePrinter.device, pendingIntent);
                }

                @Override
                public void discoveryFinished() {
                    if (!found[0]) latch.countDown();
                }

                @Override
                public void discoveryError(String message) {
                    if (!found[0]) latch.countDown();
                }
            });

            latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            unregister(context, receiver);
            promise.reject("ZEBRA_DISCOVERY_ERROR", String.valueOf(e.getMessage()), e);
            return;
        }
        unregister(context, receiver);

        if (!found[0]) {
            resolveStatus(promise, false, "No Zebra printer was found", null);
            return;
        }
        if (!granted[0]) {
            resolveStatus(promise, false, "USB permission was not granted", null);
            return;
        }

        try {
            activeConnection = activePrinter.getConnection();
            activeConnection.open();
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(activeConnection);
            PrinterStatus status = printer.getCurrentStatus();
            resolveStatus(promise, true, "OK", status);
        } catch (Exception e) {
            resolveStatus(promise, false, String.valueOf(e.getMessage()), null);
        }
    }

    /** Port of PrintManager.printFromZebraPrinter(): write one ZPL command. */
    public static synchronized void passthrough(String zpl, Promise promise) {
        if (activeConnection == null) {
            promise.resolve(result(false, "No printer connected. Call getPrinterStatus() first."));
            return;
        }
        try {
            if (!activeConnection.isConnected()) {
                activeConnection.open();
            }
            activeConnection.write(zpl.getBytes());
            promise.resolve(result(true, "OK"));
        } catch (Exception e) {
            promise.resolve(result(false, String.valueOf(e.getMessage())));
        }
    }

    public static synchronized void unselectPrinter(Promise promise) {
        closeQuietly();
        promise.resolve(result(true, "OK"));
    }

    private static void closeQuietly() {
        try {
            if (activeConnection != null) activeConnection.close();
        } catch (Exception ignored) {
        }
        activeConnection = null;
        activePrinter = null;
    }

    private static void unregister(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private static void resolveStatus(Promise promise, boolean success, String message, PrinterStatus status) {
        Map<String, Object> res = result(success, message);
        if (status != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("friendlyName", activePrinter != null ? String.valueOf(activePrinter.device.getProductName()) : "");
            data.put("isHeadCold", status.isHeadCold);
            data.put("isHeadOpen", status.isHeadOpen);
            data.put("isPaused", status.isPaused);
            data.put("isRibbonOut", status.isRibbonOut);
            data.put("isPaperOut", status.isPaperOut);
            data.put("isReceiveBufferFull", status.isReceiveBufferFull);
            data.put("isHeadTooHot", status.isHeadTooHot);
            data.put("isReadyToPrint", status.isReadyToPrint);
            data.put("isPartialFormatInProgress", status.isPartialFormatInProgress);
            res.put("data", data);
        }
        promise.resolve(res);
    }

    private static Map<String, Object> result(boolean success, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", success);
        m.put("message", message);
        return m;
    }
}
