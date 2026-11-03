import type { PrintJob, SizeDetailBarcode } from '@/types/print';

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

/** Clips a string to at most `max` characters. */
function clip(text: string, max: number): string {
  return text.length > max ? text.substring(0, max) : text;
}

// function buildZplCommands(job: PrintJob): string[] {
export function buildZplCommands(job: PrintJob): string[] {
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
          styleName: clip(style, 20),
          partName: clip(sdb.part, 16),
          size,
          fitType: ocLayDetail.fitType,
          layNumber: String(layNumber),
          quantity: String(ocLayDetail.quantity),
          totalQuantity: String(ocLayDetail.totalQuantity),
          bundle: ocLayDetail.bundleName,
          itemDescription,
        }),
      );
    });
  });

  return commands;
}

// ─── Public API ───────────────────────────────────────────────────────────────

export interface PrintResult {
  success: boolean;
  error?: string;
}

/**
 * Main entry point — prints one Zebra label per barcode over USB.
 * Uses the local native module in modules/zebra-usb-print.
 */
export async function printBarcodes(job: PrintJob): Promise<PrintResult> {
  try {
    return await printZebra(job);
  } catch (e: unknown) {
    return { success: false, error: e instanceof Error ? e.message : String(e) };
  }
}

// ─── Zebra path ───────────────────────────────────────────────────────────────

async function printZebra(job: PrintJob): Promise<PrintResult> {
  const commands = buildZplCommands(job);
  if (commands.length === 0) return { success: false, error: 'No barcodes to print' };

  // Lazy import — keeps the app from crashing in Expo Go, where the
  // native ZebraUsbPrint module is not available.
  let ZebraUsbPrint: (typeof import('../../modules/zebra-usb-print'))['default'];
  try {
    ZebraUsbPrint = (await import('../../modules/zebra-usb-print')).default;
  } catch {
    return { success: false, error: 'Zebra native module not available on this build.' };
  }

  // Check printer is ready before sending labels
  const status = await ZebraUsbPrint.getPrinterStatus();
  if (!status.success) {
    return {
      success: false,
      error: status.message ?? 'No Zebra printer found. Make sure it is connected.',
    };
  }
  if (status.data && !status.data.isReadyToPrint) {
    return { success: false, error: 'Zebra printer is not ready to print.' };
  }

  for (const zpl of commands) {
    const result = await ZebraUsbPrint.passthrough(zpl);
    if (!result.success) {
      return { success: false, error: result.message };
    }
    // 500 ms delay between labels — mirrors Java Thread.sleep(500)
    await new Promise((r) => setTimeout(r, 500));
  }

  return { success: true };
}
