
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import type { PrintJob, PrintOcLayDetail, SizeDetailBarcode } from '@/types/print';

// ─── ZPL Template ─────────────────────────────────────────────────────────────
// Direct port of AppConstants.zplTemplate — placeholder syntax changed from
// ${key} (Java StringSubstitutor) to {{key}} to avoid JS template literal clash.

const ZPL_TEMPLATE = `CT~~CD,~CC^~CT~
^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR4,4~SD10^JUS^LRN^CI0^XZ
^XA
^MD5
^MMT
^PW812
^LL0305
^LS0
^BY3,3,81^FT188,120^BCN,,Y,N
^FH\\^FD>:{{barcode}}^FS
^FT102,183^A0N,28,28^FH\\^CI28^FD{{ocNumber}}^FS^CI27
^FT305,183^A0N,28,28^FH\\^CI28^FD{{styleName}}^FS^CI27
^FT586,183^A0N,28,28^FH\\^CI28^FD{{partName}}^FS^CI27
^FT104,220^A0N,28,28^FH\\^CI28^FD{{size}}^FS^CI27
^FT295,220^A0N,28,28^FH\\^CI28^FD{{fitType}}^FS^CI27
^FT628,220^A0N,28,28^FH\\^CI28^FDLay: {{layNumber}}^FS^CI27
^FT102,262^A0N,28,28^FH\\^CI28^FDTotal Qty: {{totalQuantity}}^FS^CI27
^FT625,262^A0N,28,28^FH\\^CI28^FD{{bundle}}: {{quantity}}^FS^CI27
^FT154,290^A0N,24,24^FH\\^CI28^FD{{itemDescription}}^FS^CI27
^PQ1,0,1,Y^XZ`;

// ─── ZPL helpers ──────────────────────────────────────────────────────────────

/** Mirrors Java stringSubstitutor() */
function fillZplTemplate(values: Record<string, string>): string {
  return ZPL_TEMPLATE.replace(/\{\{(\w+)\}\}/g, (_, key) => values[key] ?? '');
}

/** Mirrors Java getZplFormattedCode() */
function buildZplCommands(job: PrintJob): string[] {
  const { ocNumber, style, layNumber, augmentedSizeList, sizeDetails } = job;
  const commands: string[] = [];

  augmentedSizeList.forEach((ocLayDetail, index) => {
    const sizeDetail = sizeDetails[index];
    if (!sizeDetail) return;

    sizeDetail.barcodeDetails.forEach((sdb: SizeDetailBarcode) => {
      const size = ocLayDetail.augmentedSize
        ? `${ocLayDetail.size} ${ocLayDetail.augmentedSize}`
        : ocLayDetail.size;

      const description = ocLayDetail.itemDesc.toLowerCase();
      const itemDescription = description.length > 59 ? description.substring(0, 59) : description;

      commands.push(
        fillZplTemplate({
          barcode: sdb.barcode,
          ocNumber,
          styleName: style,
          partName: sdb.part,
          size,
          fitType: ocLayDetail.fitType,
          layNumber: String(layNumber),
          quantity: String(ocLayDetail.quantity),
          totalQuantity: String(ocLayDetail.totalQuantity),
          bundle: ocLayDetail.bundleName,
          itemDescription,
        })
      );
    });
  });

  return commands;
}

// ─── PDF helpers ──────────────────────────────────────────────────────────────

/** Mirrors Java printPdfFromGenericPrinter() — 2-column A4 layout */
function buildPdfHtml(job: PrintJob): string {
  const { ocNumber, style, layNumber, augmentedSizeList, sizeDetails } = job;

  const cellsHtml: string[] = [];

  augmentedSizeList.forEach((ocLayDetail, index) => {
    const sizeDetail = sizeDetails[index];
    if (!sizeDetail) return;

    sizeDetail.barcodeDetails.forEach((sdb: SizeDetailBarcode) => {
      const size = ocLayDetail.augmentedSize
        ? `${ocLayDetail.size}(${ocLayDetail.augmentedSize})`
        : ocLayDetail.size;

      const description = ocLayDetail.itemDesc.toLowerCase();
      const formattedDesc = description.length > 59 ? description.substring(0, 59) : description;

      // Barcode rendered as CODE128 via SVG bars (no external lib needed)
      const barcodeSvg = buildCode128Svg(sdb.barcode);

      cellsHtml.push(`
        <td style="
          border: none;
          padding: 19px 65px;
          vertical-align: top;
          text-align: center;
        ">
          <div style="margin-bottom:4px">${barcodeSvg}</div>
          <div style="font-size:6.5px;line-height:1.4;font-family:Arial,sans-serif">
            <div style="display:flex;justify-content:space-between;margin-bottom:2px">
              <span>${ocNumber}</span><span>${style}</span><span>${sdb.part}</span>
            </div>
            <div style="display:flex;justify-content:space-between;margin-bottom:2px">
              <span>${size}</span><span>${ocLayDetail.fitType}</span><span>Lay: ${layNumber}</span>
            </div>
            <div style="display:flex;justify-content:space-between;margin-bottom:2px">
              <span>Total Qty: ${ocLayDetail.totalQuantity}</span>
              <span>${ocLayDetail.bundleName}: ${ocLayDetail.quantity}</span>
            </div>
            <div style="text-align:center">${formattedDesc}</div>
          </div>
        </td>
      `);
    });
  });

  // Pair cells into rows of 2 (mirrors Java cols=2 logic)
  const rows: string[] = [];
  for (let i = 0; i < cellsHtml.length; i += 2) {
    const col1 = cellsHtml[i];
    const col2 = cellsHtml[i + 1] ?? '<td style="border:none"></td>';
    rows.push(`<tr>${col1}${col2}</tr>`);
  }

  return `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8"/>
        <style>
          @page { size: A4; margin: 24px 0 0 0; }
          body  { margin: 0; padding: 0; }
          table { width: 100%; border-collapse: collapse; }
        </style>
      </head>
      <body>
        <table>${rows.join('')}</table>
      </body>
    </html>
  `;
}

// ─── Minimal CODE128 SVG renderer ─────────────────────────────────────────────
// Encodes the barcode string as a narrow/wide bar pattern so the PDF label
// looks identical to the iText Barcode128 output in Java.

const CODE128_B_START = 104;
const CODE128_STOP    = 106;

const CODE128_PATTERNS: string[] = [
  '11011001100','11001101100','11001100110','10010011000','10010001100',
  '10001001100','10011001000','10011000100','10001100100','11001001000',
  '11001000100','11000100100','10110011100','10011011100','10011001110',
  '10111001100','10011101100','10011100110','11001110010','11001011100',
  '11001001110','11011100100','11001110100','11101101110','11101001100',
  '11100101100','11100100110','11101100100','11100110100','11100110010',
  '11011011000','11011000110','11000110110','10100011000','10001011000',
  '10001000110','10110001000','10001101000','10001100010','11010001000',
  '11000101000','11000100010','10110111000','10110001110','10001101110',
  '10111011000','10111000110','10001110110','11101110110','11010001110',
  '11000101110','11011101000','11011100010','11011101110','11101011000',
  '11101000110','11100010110','11101101000','11101100010','11100011010',
  '11101111010','11001000010','11110001010','10100110000','10100001100',
  '10010110000','10010000110','10000101100','10000100110','10110010000',
  '10110000100','10011010000','10011000010','10000110100','10000110010',
  '11000010010','11001010000','11110111010','11000010100','10001111010',
  '10100111100','10010111100','10010011110','10111100100','10011110100',
  '10011110010','11110100100','11110010100','11110010010','11011011110',
  '11011110110','11110110110','10101111000','10100011110','10001011110',
  '10111101000','10111100010','11110101000','11110100010','10111011110',
  '10111101110','11101011110','11110101110','11010000100','11010010000',
  '11010011100','1100011101011',
];

function code128BValue(char: string): number {
  return char.charCodeAt(0) - 32;
}

function buildCode128Svg(text: string): string {
  const values: number[] = [CODE128_B_START];
  for (const ch of text) values.push(code128BValue(ch));

  // checksum
  let checksum = CODE128_B_START;
  for (let i = 1; i < values.length; i++) checksum += i * values[i];
  values.push(checksum % 103);
  values.push(CODE128_STOP);

  const barWidth  = 1.5;
  const barHeight = 40;
  let x = 10;
  let bars = '';

  for (const val of values) {
    const pattern = CODE128_PATTERNS[val] ?? '';
    for (let i = 0; i < pattern.length; i++) {
      const w = barWidth * Number(pattern[i] === '1' ? 1 : 0.5);
      if (i % 2 === 0) {
        bars += `<rect x="${x.toFixed(1)}" y="0" width="${w.toFixed(1)}" height="${barHeight}" fill="black"/>`;
      }
      x += barWidth;
    }
  }

  const totalWidth = x + 10;
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${totalWidth}" height="${barHeight + 14}">
    ${bars}
    <text x="${(totalWidth / 2).toFixed(1)}" y="${barHeight + 12}" text-anchor="middle"
      font-family="monospace" font-size="8">${text}</text>
  </svg>`;
}

// ─── Public API ───────────────────────────────────────────────────────────────

export type PrintPath = 'zebra' | 'pdf';

export interface PrintResult {
  success: boolean;
  error?: string;
}

/**
 * Main entry point — mirrors Java PrintManager.print()
 *
 * @param job      All data needed to generate labels
 * @param path     'zebra' → ZPL via expo-zebra-print-connect
 *                 'pdf'   → HTML→PDF via expo-print + share dialog
 */
export async function printBarcodes(job: PrintJob, path: PrintPath): Promise<PrintResult> {
  try {
    if (path === 'zebra') {
      return await printZebra(job);
    }
    return await printPdf(job);
  } catch (e: unknown) {
    return { success: false, error: e instanceof Error ? e.message : String(e) };
  }
}

// ─── Zebra path ───────────────────────────────────────────────────────────────

async function printZebra(job: PrintJob): Promise<PrintResult> {
  const commands = buildZplCommands(job);
  if (commands.length === 0) return { success: false, error: 'No barcodes to print' };

  // Lazy import — keeps the module from crashing in Expo Go where the
  // native ExpoZebraPrintConnect module is not available.
  let ExpoZebraPrintConnect: typeof import('expo-zebra-print-connect')['default'];
  try {
    ExpoZebraPrintConnect = (await import('expo-zebra-print-connect')).default;
  } catch {
    return { success: false, error: 'Zebra native module not available on this build.' };
  }

  // Check printer is ready before sending labels
  const status = await ExpoZebraPrintConnect.getPrinterStatus();
  if (!status.success) {
    return { success: false, error: status.message ?? 'No Zebra printer found. Make sure it is connected.' };
  }
  if (status.data && !status.data.isReadyToPrint) {
    return { success: false, error: 'Zebra printer is not ready to print.' };
  }

  for (const zpl of commands) {
    const result = await ExpoZebraPrintConnect.passthrough(zpl);
    if (!result.success) {
      return { success: false, error: result.message };
    }
    // 500 ms delay between labels — mirrors Java Thread.sleep(500)
    await new Promise((r) => setTimeout(r, 500));
  }

  return { success: true };
}

// ─── PDF path ─────────────────────────────────────────────────────────────────

async function printPdf(job: PrintJob): Promise<PrintResult> {
  const html = buildPdfHtml(job);

  const { uri } = await Print.printToFileAsync({ html });

  const canShare = await Sharing.isAvailableAsync();
  if (canShare) {
    await Sharing.shareAsync(uri, {
      mimeType: 'application/pdf',
      dialogTitle: 'Print or share barcode labels',
      UTI: 'com.adobe.pdf',
    });
  } else {
    // Fallback: open system print dialog directly
    await Print.printAsync({ uri });
  }

  return { success: true };
}
