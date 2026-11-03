import { getAllowedQuantity } from '@/features/leader/lib/bundleBarcode';
import type { OcLay } from '@/types/leader';

// nanoid is ESM-only and Jest cannot load it as-is. This mock also makes IDs predictable.
jest.mock('nanoid/non-secure', () => ({
  customAlphabet: () => () => 'ABCDEFGHIJ',
}));

const details = (rows: { ratio: number; quantity: number }[]) =>
  rows as unknown as OcLay['details'];

describe('getAllowedQuantity', () => {
  it('uses the first row that has a ratio above 0', () => {
    expect(
      getAllowedQuantity(
        details([
          { ratio: 0, quantity: 10 },
          { ratio: 2, quantity: 9 },
        ]),
      ),
    ).toBe(4); // floor(9 / 2)
  });

  it('returns 0 when no row has a ratio', () => {
    expect(getAllowedQuantity(details([{ ratio: 0, quantity: 10 }]))).toBe(0);
  });

  it('returns 0 for an empty list', () => {
    expect(getAllowedQuantity(details([]))).toBe(0);
  });
});
