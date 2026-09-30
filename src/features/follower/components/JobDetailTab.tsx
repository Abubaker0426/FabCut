import { Ionicons } from '@expo/vector-icons'
import { useRouter } from 'expo-router'
import { FlatList, Pressable, Text, View } from 'react-native'

import { SizeRow } from '@/components/ui/SizeRow'
import type { JobDetailTabProps } from '@/types/JobDetailTab'
import { BarcodeRow } from './BarcodeRow'

export function JobDetailTab({ jobDetail, ocNumber, layLength, assignedQty, cutQty, numOfPlies, actualPlies, scannedBarcodes, validationStates = {}, onActualPliesChange, onValidate, onDelete, onReasonChange, refreshControl, }: JobDetailTabProps) {
  const router = useRouter()
  const sizes = jobDetail.ratioDetails.map((r) => r.size)
  const quantities = jobDetail.ratioDetails.map((r) => Number(r.quantity) || 0)

  return (
    <View className="flex-1 bg-lightBlue">
      <FlatList
        data={scannedBarcodes}
        keyExtractor={(item) => item.barcode}
        contentContainerStyle={{ paddingBottom: 100 }}
        refreshControl={refreshControl}
        ListHeaderComponent={
          <>
            <View className="px-8"  >
              <View
                className="bg-white rounded-lg p-6 mt-6 mb-6"
                style={{ elevation: 6, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 8, }}>
                <View className="border border-gray-200 rounded-lg p-2 mb-6">
                  <SizeRow sizes={sizes} quantities={quantities} />
                </View>
                <Text className="text-center text-xs font-bold italic text-greyText mb-1.5" style={{ fontSize: 13 }}>
                  {ocNumber}
                </Text>
                <Text className="text-center italic text-greyText mb-6" style={{ fontSize: 13 }}>
                  {`${jobDetail.itemCode} - ${jobDetail.itemDesc}`}
                </Text>
                <StatRow label="Assigned Quantity" value={String(assignedQty)} />
                <StatRow label="Cut Quantity" value={String(cutQty)} pt />
                <StatRow label="Lay Length" value={String(layLength)} pt />
                <StatRow label="Number Of Plies" value={String(numOfPlies)} pt />
                <StatRow label="Actual Plies" value={String(actualPlies)} pt />
                <StatRow label="Lay Number" value={String(jobDetail.layNumber)} pt />
              </View>
            </View>
            {/* <View style={{ height: 220 }} /> */}
          </>
        }
        renderItem={({ item }) => (
          <BarcodeRow 
            item={item}
            validationState={validationStates[item.barcode] ?? (item.validated ? 'valid' : 'idle')}
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

      {/* Scan Barcode FAB — mirrors layout_gravity="bottom|start" */}
      <View className="absolute bottom-6 left-6">
        <Pressable
          onPress={() => router.push('/scanner')}
          className="bg-white rounded-full px-5 py-3 flex-row items-center gap-2"
          style={{ elevation: 6, shadowColor: '#000', shadowOpacity: 0.12, shadowRadius: 8 }}
          accessibilityLabel="Scan barcode"
          accessibilityRole="button"
        >
          <Ionicons name="scan-outline" size={18} color="#21226b" />
          <Text className="text-primary font-semibold text-sm">Scan Barcode</Text>
        </Pressable>
      </View>
    </View>
  )
}

function StatRow({ label, value, pt }: { label: string; value: string; pt?: boolean }) {
  return (
    <View className={`flex-row ${pt ? 'pt-6' : ''}`}>
      <Text className="flex-1 text-base text-greyText">{label}</Text>
      <Text className="flex-1 text-base font-bold text-gray-900 text-right">{value}</Text>
    </View>
  )
}
