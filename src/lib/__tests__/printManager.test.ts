import { buildZplCommands } from '@/lib/printManager';
import type { PrintJob } from '@/types/print';

const style = 'A very long style name that overflows the label';
const longPart = 'COLLAR WITH A VERY LONG NAME';

// Minimal fake job. Cast because the real type has many fields these tests do not need.
const job = {
  ocNumber: 'ID46454',
  style,
  layNumber: 9,
  augmentedSizeList: [
    {
      size: 'S',
      fitType: 'REGULAR FIT',
      bundleName: 'B1',
      bundleNumber: 1,
      quantity: 41,
      totalQuantity: 41,
      itemCode: 'X1',
      itemDesc: 'Ludde CW 24 119 Yellow Light Shell Fabric 61" CW',
      country: 'IN | INDIA',
    },
  ],
  sizeDetails: [
    {
      barcodeDetails: [
        { partsUnique: 1, barcode: 'abc123', part: 'CUFF' },
        { partsUnique: 2, barcode: 'def456', part: longPart },
      ],
    },
  ],
} as unknown as PrintJob;

describe('buildZplCommands', () => {
  const commands = buildZplCommands(job);

  it('creates one label per barcode', () => {
    expect(commands).toHaveLength(2);
  });

  it('puts each barcode into its own label', () => {
    expect(commands[0]).toContain('>:abc123');
    expect(commands[1]).toContain('>:def456');
  });

  it('clips the style name to 20 characters so it cannot overlap the part name', () => {
    expect(commands[0]).toContain(`^FD${style.slice(0, 20)}^FS`);
    expect(commands[0]).not.toContain(style);
  });

  it('clips the part name to 16 characters', () => {
    expect(commands[1]).toContain(`^FD${longPart.slice(0, 16)}^FS`);
    expect(commands[1]).not.toContain(longPart);
  });

  it('produces complete ZPL (starts with setup, ends with ^XZ)', () => {
    for (const zpl of commands) {
      expect(zpl.startsWith('CT~~CD')).toBe(true);
      expect(zpl.endsWith('^XZ')).toBe(true);
    }
  });

  it('leaves no unfilled {{placeholders}}', () => {
    for (const zpl of commands) {
      expect(zpl).not.toMatch(/\{\{\w+\}\}/);
    }
  });
});
