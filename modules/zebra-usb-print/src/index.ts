import { requireNativeModule } from 'expo';

export interface PrintResponse {
  success: boolean;
  message: string;
}

export interface PrinterStatusData {
  friendlyName: string;
  isHeadCold: boolean;
  isHeadOpen: boolean;
  isPaused: boolean;
  isRibbonOut: boolean;
  isPaperOut: boolean;
  isReceiveBufferFull: boolean;
  isHeadTooHot: boolean;
  isReadyToPrint: boolean;
  isPartialFormatInProgress: boolean;
}

export interface PrinterStatusResponse extends PrintResponse {
  data?: PrinterStatusData;
}

interface ZebraUsbPrintNativeModule {
  getPrinterStatus(): Promise<PrinterStatusResponse>;
  passthrough(zpl: string): Promise<PrintResponse>;
  unselectPrinter(): Promise<PrintResponse>;
}

const ZebraUsbPrint =
  requireNativeModule<ZebraUsbPrintNativeModule>('ZebraUsbPrint');

export default ZebraUsbPrint;
