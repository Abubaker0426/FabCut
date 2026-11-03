package expo.modules.zebrausbprint

import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

// Thin bridge only. All real logic is in ZebraPrinterHelper.java
class ZebraUsbPrintModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ZebraUsbPrint")

    AsyncFunction("getPrinterStatus") { promise: Promise ->
      val context = appContext.reactContext
      if (context == null) {
        promise.reject("NO_CONTEXT", "React context unavailable", null)
      } else {
        ZebraPrinterHelper.getPrinterStatus(context, promise)
      }
    }

    AsyncFunction("passthrough") { zpl: String, promise: Promise ->
      ZebraPrinterHelper.passthrough(zpl, promise)
    }

    AsyncFunction("unselectPrinter") { promise: Promise ->
      ZebraPrinterHelper.unselectPrinter(promise)
    }
  }
}
