/**
 * EndBitJobDetailTab — exactly mirrors fragment_end_bit_job_detail.xml
 *
 * Identical layout to JobDetailTab EXCEPT:
 *  - No "Lay Number" stat row
 *  - Description = itemCode + itemDesc (top-level, not per-part)
 *  - layLength comes from jobDetail.layLength (per-part)
 *  - Tabs are titled by partName
 */
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { FlatList, Pressable, RefreshControlProps, Text, View } from 'react-native';

import { SizeRow } from '@/components/ui/SizeRow';
import type { EndBitJobDetail, ScanBarcode } from '@/types';
import { BarcodeRow } from './BarcodeRow';

interface EndBitJobDetailTabProps {
  jobDetail: EndBitJobDetail;
  ocNumber: string;
  itemCode: string;
  itemDesc: string;
  assignedQty: number;
  cutQty: number;
  numOfPlies: number;
  actualPlies: number;
  scannedBarcodes: ScanBarcode[];
  onActualPliesChange: (barcode: string, value: string) => void;
  onValidate?: (barcode: string, actualPlies: number) => void;
  onDelete?: (barcode: string) => void;
  onReasonChange?: (barcode: string, reason: string) => void;
  refreshControl?: React.ReactElement<RefreshControlProps>;
}

export function EndBitJobDetailTab({
  jobDetail,
  ocNumber,
  itemCode,
  itemDesc,
  assignedQty,
  cutQty,
  numOfPlies,
  actualPlies,
  scannedBarcodes,
  onActualPliesChange,
  onValidate,
  onDelete,
  onReasonChange,
  refreshControl,
}: EndBitJobDetailTabProps) {
  const router = useRouter();
  const sizes      = jobDetail.ratioDetails.map((r) => r.size);
  const quantities = jobDetail.ratioDetails.map((r) => r.ratioQty);

  return (
    <View className="flex-1 bg-lightBlue">
      <FlatList
        data={scannedBarcodes}
        keyExtractor={(item) => item.barcode}
        contentContainerStyle={{ paddingBottom: 100 }}
        refreshControl={refreshControl}

        ListHeaderComponent={
          <>
            {/* ── Blue strip + floating card ── */}
            <View style={{ position: 'relative', marginBottom: 16 }}>
              <View className="bg-primary w-full" style={{ height: 200 }} />

              <View
                className="bg-white rounded-lg absolute"
                style={{
                  top: 42,
                  left: 36,
                  right: 36,
                  elevation: 6,
                  shadowColor: '#000',
                  shadowOpacity: 0.1,
                  shadowRadius: 8,
                  paddingHorizontal: 24,
                  paddingVertical: 32,
                }}
              >
                {/* Size / quantity grid */}
                <View className="border border-gray-200 rounded-lg p-2 mb-6">
                  <SizeRow sizes={sizes} quantities={quantities} />
                </View>

                {/* OC# — italic bold, grey, centered */}
                <Text
                  className="text-center font-bold italic text-greyText mb-1.5"
                  style={{ fontSize: 13 }}
                >
                  {ocNumber}
                </Text>

                {/* Description = itemCode - itemDesc (italic, grey, centered) */}
                <Text
                  className="text-center italic text-greyText mb-6"
                  style={{ fontSize: 13 }}
                >
                  {`${itemCode} - ${itemDesc}`}
                </Text>

                {/* Stats — same as normal job but NO Lay Number */}
                <StatRow label="Assigned Quantity" value={String(assignedQty)} />
                <StatRow label="Cut Quantity"       value={String(cutQty)}                 pt />
                <StatRow label="Lay Length"         value={String(jobDetail.layLength)}    pt />
                <StatRow label="Number Of Plies"    value={String(numOfPlies)}             pt />
                <StatRow label="Actual Plies"       value={String(actualPlies)}            pt />
              </View>
            </View>

            <View style={{ height: 200 }} />
          </>
        }

        renderItem={({ item }) => (
          <BarcodeRow
            item={item}
            onActualPliesChange={onActualPliesChange}
            onValidate={onValidate}
            onDelete={onDelete}
            onReasonChange={onReasonChange}
          />
        )}

        ListEmptyComponent={
          <Text className="text-center text-gray-400 mt-6 mb-4">
            No barcodes scanned yet
          </Text>
        }
      />

      {/* Scan Barcode FAB — bottom|start */}
      <View className="absolute bottom-6 left-6">
        <Pressable
          onPress={() => router.push('/scanner')}
          className="bg-white rounded-full px-5 py-3 flex-row items-center gap-2"
          style={{
            elevation: 6,
            shadowColor: '#000',
            shadowOpacity: 0.12,
            shadowRadius: 8,
          }}
          accessibilityLabel="Scan barcode"
          accessibilityRole="button"
        >
          <Ionicons name="scan-outline" size={18} color="#21226b" />
          <Text className="text-primary font-semibold text-sm">Scan Barcode</Text>
        </Pressable>
      </View>
    </View>
  );
}

function StatRow({ label, value, pt }: { label: string; value: string; pt?: boolean }) {
  return (
    <View className={`flex-row ${pt ? 'pt-6' : ''}`}>
      <Text className="flex-1 text-base text-greyText">{label}</Text>
      <Text className="flex-1 text-base font-bold text-gray-900 text-right">{value}</Text>
    </View>
  );
}
