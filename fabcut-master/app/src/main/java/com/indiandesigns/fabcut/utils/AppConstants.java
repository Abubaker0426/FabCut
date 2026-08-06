package com.indiandesigns.fabcut.utils;

/**
 * Contains Application wide constants
 */

public final class AppConstants {

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;
    public static final int DOCUMENT_STALE_THRESHOLD = 3;
    public static final String PREF_NAME = "fabcut_pref";
    public static final String SCANNED_CODE = "code";

    public static final String QUANTITY_TAG = "Quantity";
    public static final String TOTAL_TAG = "Total";
    public static final String ITEM_CODE_TAG = "ItemCode";
    public static final String QUANTITY_GROUP_TAG = "ItemCode";
    public static final String RATIO_TAG = "Ratio";
    public static final String LAY_LENGTH_TAG = "LayLength";
    public static final String SHADE_GROUP_TAG = "ShadeGroup";
    public static final String ITEM_SHADE_TAG = "ItemShade";
    public static final String ITEM_SHRINKAGE_TAG = "ItemShrinkage";
    public static final String ITEM_PATTERN_TAG = "ItemPattern";

    public static final String RATIO_MATCH = "1.0";
    public static final String DB_NAME = "fabcut.db";
    public static final String PDF_FONT_PATH = "assets/fonts/brandon_medium.otf";
    public static final String ALPHABETS = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //  Printer Manufacturer
    public static final String PRINTER_MANUFACTURER_NAME = "Zebra Technologies";

    //  The ZPL template string which will be replaced with dynamic data
    public static final String zplTemplate = "CT~~CD,~CC^~CT~\n" +
            "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR4,4~SD10^JUS^LRN^CI0^XZ\n" +
            "^XA\n" +
            "^MD5\n" +
            "^MMT\n" +
            "^PW812\n" +
            "^LL0305\n" +
            "^LS0\n" +
            "^BY3,3,81^FT188,120^BCN,,Y,N\n" +
            "^FH\\^FD>:${barcode}^FS\n" +
            "^FT102,183^A0N,28,28^FH\\^CI28^FD${ocNumber}^FS^CI27\n" +
            "^FT305,183^A0N,28,28^FH\\^CI28^FD${styleName}^FS^CI27\n" +
            "^FT586,183^A0N,28,28^FH\\^CI28^FD${partName}^FS^CI27\n" +
            "^FT104,220^A0N,28,28^FH\\^CI28^FD${size}^FS^CI27\n" +
            "^FT295,220^A0N,28,28^FH\\^CI28^FD${fitType}^FS^CI27\n" +
            "^FT628,220^A0N,28,28^FH\\^CI28^FDLay: ${layNumber}^FS^CI27\n" +
            "^FT102,262^A0N,28,28^FH\\^CI28^FDTotal Qty: ${totalQuantity}^FS^CI27\n" +
            "^FT625,262^A0N,28,28^FH\\^CI28^FD${bundle}: ${quantity}^FS^CI27\n" +
            "^FT154,290^A0N,24,24^FH\\^CI28^FD${itemDescription}^FS^CI27\n" +
            "^PQ1,0,1,Y^XZ";

    private AppConstants() {
    }
}
