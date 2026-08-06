package com.indiandesigns.fabcut.ui.common;

import android.app.Activity;
import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.SizeDetail;
import com.indiandesigns.fabcut.data.network.model.SizeDetailBarcodes;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;
import com.indiandesigns.fabcut.utils.AppConstants;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterUsb;

import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintManager {
    private final static String TAG = "PrintManager";

    private Activity activity;

    public PrintManager() {
    }

    public void setup(Activity activity, PrintManagerListener listener) {
        this.mListener = listener;
        this.activity = activity;
    }

    private PrintManagerListener mListener;

    public interface PrintManagerListener {
        void showError(int messageId);
    }

    /**
     * Generate and print barcodes from parts and job information
     * Invoke print method
     *
     * @param ocLay    Job info
     * @param response Parts info
     */

    public void print(OcLay ocLay, FetchPartsResponse response, String filename, DiscoveredPrinterUsb discoveredPrinterUsb, List<OcLayDetail> augmentedSizeList, List<SizeDetail> sizeDetailList) {

        String ocNumber = ocLay.getOcNo();
        String style = response.getStyle();
        int layNumber = ocLay.getLay();

        if (discoveredPrinterUsb != null) {
            ArrayList<String> zplFormattedBarcodes = getZplFormattedCode(ocNumber, style, layNumber, augmentedSizeList, sizeDetailList);
            printFromZebraPrinter(discoveredPrinterUsb, zplFormattedBarcodes);
            return;
        }

        printPdfFromGenericPrinter(augmentedSizeList, filename, ocNumber, style, layNumber, sizeDetailList);
        return;
    }


    /**
     * This function will format all the information required for the Zebra printer in the form of ZPL
     *
     * @param ocNumber
     * @param style
     * @param layNumber
     * @param augmentedSizeList
     * @return
     */
    private ArrayList<String> getZplFormattedCode(String ocNumber, String style, int layNumber, List<OcLayDetail> augmentedSizeList, List<SizeDetail> sizeDetailList) {
        ArrayList<String> resolvedStrings = new ArrayList<>();
        for (int index = 0; index < augmentedSizeList.size(); index++) {
            OcLayDetail ocLayDetail = augmentedSizeList.get(index);
            SizeDetail sizeDetail = sizeDetailList.get(index);

            List<SizeDetailBarcodes> sizeDetailBarcodesList = sizeDetail.getBarcodeDetails();

            for (SizeDetailBarcodes sizeDetailBarcodes: sizeDetailBarcodesList) {
                String barcode = sizeDetailBarcodes.getBarcode();

                String size = null;
                if (ocLayDetail.getAugmentedSize() != null) {
                    size = ocLayDetail.getSize() + " " + ocLayDetail.getAugmentedSize();
                } else {
                    size = ocLayDetail.getSize();
                }

                //  Get remaining details for barcode
                String partName = sizeDetailBarcodes.getPart();
                String fitType = ocLayDetail.getFitType();
                String bundleName = ocLayDetail.getBundleName();
                String description = ocLayDetail.getItemDesc().toLowerCase();
                String itemDescription = description.length() > 59 ? description.substring(0, 59) : description;
                int quantity = ocLayDetail.getQuantity();
                int totalQuantity = ocLayDetail.getTotalQuantity();
                resolvedStrings.add(stringSubstitutor(barcode, ocNumber, style, partName, size, fitType, layNumber, quantity, totalQuantity, bundleName, itemDescription));
            }
        }
        return resolvedStrings;
    }

    /**
     * This function will swap out all placeholders in the ZPL command and replace it with dynamic values
     *
     * @param barcode
     * @param ocNumber
     * @param styleName
     * @param partName
     * @param size
     * @param fitType
     * @param layNumber
     * @param quantity
     * @param bundle
     * @param itemDescription
     * @return
     */
    private String stringSubstitutor(String barcode, String ocNumber, String styleName, String partName, String size, String fitType, int layNumber, int quantity, int totalQuantity, String bundle, String itemDescription) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("barcode", barcode);
        valuesMap.put("ocNumber", ocNumber);
        valuesMap.put("styleName", styleName);
        valuesMap.put("partName", partName);
        valuesMap.put("size", size);
        valuesMap.put("fitType", fitType);
        valuesMap.put("layNumber", String.valueOf(layNumber));
        valuesMap.put("quantity", String.valueOf(quantity));
        valuesMap.put("totalQuantity", String.valueOf(totalQuantity));
        valuesMap.put("bundle", bundle);
        valuesMap.put("itemDescription", itemDescription);
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String resolvedString = sub.replace(AppConstants.zplTemplate);
        return resolvedString;
    }

    /**
     * This method will be used to write to the zebra printer using a Connection interface
     *
     * @param discoveredPrinterUsb
     * @param zplCommands
     */
    private void printFromZebraPrinter(DiscoveredPrinterUsb discoveredPrinterUsb, ArrayList<String> zplCommands) {
        Connection connection = discoveredPrinterUsb.getConnection();
        try {
            connection.open();
            for (String zplCommand : zplCommands) {
                Thread.sleep(500);
                connection.write(zplCommand.getBytes());
            }
            connection.close();
        } catch (ConnectionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will be used to print from a generic non-Zebra printer
     *
     * @param augmentedSizeList
     * @param filename
     * @param ocNumber
     * @param style
     * @param layNumber
     */
    private void printPdfFromGenericPrinter(List<OcLayDetail> augmentedSizeList, String filename, String ocNumber, String style, int layNumber, List<SizeDetail> sizeDetailList) {
        int cols = 2;
        PdfPTable table = new PdfPTable(cols);

        String path = null;
        try {
            File file = File.createTempFile(filename, ".pdf");
            file.deleteOnExit();
            path = file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Document document = new Document(PageSize.A4);
            document.setMargins(0, 0, 24, 0);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("ID");
            document.addCreator("ID");

            BaseColor colorAccent = new BaseColor(0, 0, 0, 255);
            float fontSize = 6.5f;

            BaseFont fontName = BaseFont.createFont(AppConstants.PDF_FONT_PATH, "UTF-8", BaseFont.EMBEDDED);

            Font medium = new Font(fontName, fontSize, Font.NORMAL, colorAccent);

            table.setWidthPercentage(100);

            for (int index = 0; index < augmentedSizeList.size(); index++) {
                OcLayDetail ocLayDetail = augmentedSizeList.get(index);
                SizeDetail sizeDetail = sizeDetailList.get(index);

                List<SizeDetailBarcodes> sizeDetailBarcodesList = sizeDetail.getBarcodeDetails();

                int i = 0;
                int rows;

                if (sizeDetailBarcodesList.size() / cols == 0) {
                    rows = sizeDetailBarcodesList.size() / cols;
                } else rows = sizeDetailBarcodesList.size() / cols + 1;

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        if (i < sizeDetailBarcodesList.size()) {

                            SizeDetailBarcodes sizeDetailBarcodes = sizeDetail.getBarcodeDetails().get(i);

                            //  Get the barcode
                            String barcode = sizeDetailBarcodes.getBarcode();
                            Barcode128 barcode128 = new Barcode128();
                            barcode128.setCode(barcode);
                            barcode128.setSize(5f);
                            barcode128.setCodeType(Barcode.CODE128);

                            Image code128Image = barcode128.createImageWithBarcode(cb, null, null);

                            PdfPCell cell = new PdfPCell();
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPaddingLeft(65f);
                            cell.setPaddingRight(65f);
                            cell.setPaddingBottom(19f);
                            cell.setPaddingTop(19f);
                            cell.addElement(code128Image);

                            Phrase ocPhrase = new Phrase();
                            ocPhrase.setLeading(5);
                            ocPhrase.add(new Chunk(ocNumber, medium));
                            ocPhrase.add(new Chunk(new VerticalPositionMark()));
                            ocPhrase.add(new Chunk(style, medium));
                            ocPhrase.add(new Chunk(new VerticalPositionMark()));
                            ocPhrase.add(new Chunk(sizeDetailBarcodes.getPart(), medium));
                            cell.addElement(ocPhrase);

                            Phrase stylePhrase = new Phrase();
                            stylePhrase.setLeading(8);
                            if (ocLayDetail.getAugmentedSize() == null) {
                                stylePhrase.add(new Chunk(ocLayDetail.getSize(), medium));
                            } else
                                stylePhrase.add(new Chunk(ocLayDetail.getSize() + "(" + ocLayDetail.getAugmentedSize() + ")", medium));
                            stylePhrase.add(new Chunk(new VerticalPositionMark()));
                            stylePhrase.add(new Chunk(ocLayDetail.getFitType(), medium));
                            stylePhrase.add(new Chunk(new VerticalPositionMark()));
                            stylePhrase.add(new Chunk("Lay: " + layNumber, medium));
                            cell.addElement(stylePhrase);

                            Phrase layPhrase = new Phrase();
                            layPhrase.setLeading(8);
                            layPhrase.add(new Chunk("Total Qty: " + ocLayDetail.getTotalQuantity(), medium));
                            layPhrase.add(new Chunk(new VerticalPositionMark()));
                            layPhrase.add(new Chunk(ocLayDetail.getBundleName() + ": " + ocLayDetail.getQuantity(), medium));
                            cell.addElement(layPhrase);

                            String description = ocLayDetail.getItemDesc().toLowerCase();
                            String formattedDesc = description.length() > 59 ? description.substring(0, 59) : description;
                            Paragraph itemDescription = new Paragraph(formattedDesc, medium);
                            itemDescription.setLeading(8);
                            itemDescription.setAlignment(Element.ALIGN_CENTER);
                            cell.addElement(itemDescription);

                            table.addCell(cell);
                        } else {
                            PdfPCell cell = new PdfPCell();
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);
                        }
                        i++;
                    }
                }
            }

            document.add(table);
            document.close();

            printPDF(path, sizeDetailList);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Print the file by path
     *
     * @param path        Path of the file to be printed
     * @param sizeDetails Generated parts information
     */

    private void printPDF(String path, List<SizeDetail> sizeDetails) {
        android.print.PrintManager printManager = (android.print.PrintManager) this.activity.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(this.activity, path);
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
