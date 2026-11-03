import { Ionicons } from '@expo/vector-icons';
import { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from 'react-native';

import { Button } from '@/components/ui/Button';
import { printBarcodes } from '@/lib/printManager';
import { toast } from '@/lib/toast';
import { useAppStore } from '@/store/appStore';
import type { BundleDetail, FetchPartsDetails } from '@/types/leader';
import type { BundleRowProps, EditBundleModalProps } from '@/types/EditBundleModal';

import {
  useFetchCountries,
  useFetchPartsForPrint,
  useFetchPartsCount,
  useSaveBarcodes,
} from '../hooks/useBundleModal';
import {
  getAllowedQuantity,
  buildAugmentedSizeList,
  buildSizeDetailsList,
  buildPrintJob,
} from '../lib/bundleBarcode';

export function EditBundleModal({ visible, job, onDismiss, onSubmit }: EditBundleModalProps) {
  const location = useAppStore((s) => s.location) ?? '';

  const [rows, setRows] = useState<BundleDetail[]>([{ quantity: 0, country: '' }]);
  const [error, setError] = useState('');
  const [printing, setPrinting] = useState(false);

  // ── Fetch countries when modal opens ──────────────────────────────────────
  const { data: countries, isLoading: isCountriesLoading } = useFetchCountries(
    job?.ocNo ?? '',
    visible && !!job?.ocNo,
  );

  const { mutate: fetchParts, isPending: isFetchingParts } = useFetchPartsForPrint();
  const { mutate: saveBarcodes, isPending: isSaving } = useSaveBarcodes();

  // Pre-fetch parts count so label preview is shown before Submit & Print
  useFetchPartsCount(job?.ocNo ?? '', location, visible && !!job?.ocNo);

  // Auto-set country once countries load
  useEffect(() => {
    if (countries && countries.length > 0) {
      setRows((prev) => prev.map((r) => (r.country ? r : { ...r, country: countries[0] })));
    }
  }, [countries]);

  // Reset state on every open
  useEffect(() => {
    if (visible) {
      setRows([{ quantity: 0, country: countries?.[0] ?? '' }]);
      setError('');
    }
  }, [visible]);

  // allowedQuantity — mirrors Java getBundleSplitAllowedQuantity()
  const allowedQuantity = getAllowedQuantity(job?.details ?? []);
  const currentTotal = rows.reduce((s, r) => s + (Number(r.quantity) || 0), 0);

  const addRow = () => setRows((prev) => [...prev, { quantity: 0, country: countries?.[0] ?? '' }]);
  const removeRow = (i: number) => setRows((prev) => prev.filter((_, idx) => idx !== i));
  const updateRow = (i: number, field: keyof BundleDetail, value: string | number) =>
    setRows((prev) => prev.map((row, idx) => (idx === i ? { ...row, [field]: value } : row)));

  // ── Submit & Print handler ────────────────────────────────────────────────
  const handlePrint = () => {
    // 1. All rows must have quantity + country
    for (const row of rows) {
      if (!row.quantity || !row.country) {
        setError('All rows must have a quantity and country.');
        return;
      }
    }
    // 2. Total must equal allowedQuantity
    if (currentTotal !== allowedQuantity) {
      setError(`Total must equal ${allowedQuantity}. Current: ${currentTotal}`);
      return;
    }
    setError('');
    if (!job) return;

    // 3. Fetch parts config from server
    fetchParts(
      { ocNo: job.ocNo, location },
      {
        onSuccess: (partsResponse) => {
          const partsList = partsResponse.parts ?? [];

          if (partsList.length === 0) {
            toast.error(
              'No parts configured for this OC. Set them up in Bundle Parts first.',
              'No Parts',
            );
            onDismiss();
            return;
          }

          // 4. Bundle 2+ requires at least one selected part — mirrors Java validateBundlePartsDetailList()
          if (rows.length > 1) {
            const hasSelected = partsList.some((p: FetchPartsDetails) => p.isSelected === 1);
            if (!hasSelected) {
              toast.error(
                'Please select at least one part in Bundle Parts before splitting into multiple bundles.',
                'No Parts Selected',
              );
              onDismiss();
              return;
            }
          }

          // 5 + 6. Build augmented size list + barcode details
          const augmentedSizeList = buildAugmentedSizeList(job, rows);
          const sizeDetailList = buildSizeDetailsList(augmentedSizeList, partsList);

          // 7. Save barcodes to server
          saveBarcodes(
            {
              ocNo: job.ocNo,
              data: {
                location,
                fitType: job.fitType,
                lay: job.lay,
                sizeDetails: sizeDetailList,
                jobId: parseInt(job.jobId, 10) || 0,
              },
            },
            {
              onSuccess: async () => {
                // 8. Print labels via Zebra
                const printJob = buildPrintJob(job, augmentedSizeList, sizeDetailList);
                setPrinting(true);
                const result = await printBarcodes(printJob);
                setPrinting(false);

                if (!result.success) {
                  toast.error(result.error ?? 'Print failed', 'Print Failed');
                  return;
                }

                toast.success('Barcodes printed successfully!');
                onSubmit();
                onDismiss();
              },
              onError: (err: any) => toast.apiError(err, 'Save Failed'),
            },
          );
        },
        onError: (err: any) => {
          toast.apiError(err, 'Error', 'Failed to fetch parts.');
          onDismiss();
        },
      },
    );
  };

  const isWorking = isCountriesLoading || isFetchingParts || isSaving || printing;

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onDismiss}
      statusBarTranslucent
    >
      <View
        style={{
          flex: 1,
          backgroundColor: 'rgba(0,0,0,0.4)',
          justifyContent: 'center',
          paddingHorizontal: 16,
        }}
      >
        <TouchableWithoutFeedback onPress={onDismiss}>
          <View style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }} />
        </TouchableWithoutFeedback>

        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
          <View
            className="bg-white rounded-2xl"
            style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12 }}
            onStartShouldSetResponder={() => true}
          >
            <ScrollView
              contentContainerStyle={{ padding: 20 }}
              keyboardShouldPersistTaps="handled"
              nestedScrollEnabled
              bounces={false}
            >
              <Text className="text-base font-bold text-primary mb-1">Split Bundle</Text>
              <Text className="text-xs text-gray-500 mb-1">
                OC# {job?.ocNo ?? '—'} · Fit: {job?.fitType ?? '—'} · Lay: {job?.lay ?? '—'}
              </Text>
              <Text className="text-xs text-gray-500 mb-4">
                Allowed Quantity: <Text className="font-bold text-primary">{allowedQuantity}</Text>
              </Text>

              {/* Column headers */}
              <View className="flex-row mb-2 px-1">
                <Text className="flex-1 text-xs font-bold text-gray-500 uppercase">Quantity</Text>
                <Text className="flex-[2] text-xs font-bold text-gray-500 uppercase ml-3">
                  Country
                </Text>
                <View className="w-8" />
              </View>

              {isCountriesLoading ? (
                <View className="items-center py-4">
                  <ActivityIndicator color="#21226b" />
                  <Text className="text-xs text-gray-400 mt-2">Loading countries...</Text>
                </View>
              ) : (
                rows.map((row, i) => (
                  <BundleRow
                    key={i}
                    row={row}
                    countries={countries ?? []}
                    onQuantityChange={(v) => updateRow(i, 'quantity', Number(v) || 0)}
                    onCountryChange={(v) => updateRow(i, 'country', v)}
                    onRemove={rows.length > 1 ? () => removeRow(i) : undefined}
                  />
                ))
              )}

              {/* Add row */}
              <Pressable
                onPress={addRow}
                className="flex-row items-center gap-1 mt-1 mb-4 self-start"
              >
                <Ionicons name="add-circle-outline" size={18} color="#21226b" />
                <Text className="text-primary text-sm font-medium">Add Row</Text>
              </Pressable>

              {/* Running total */}
              <View
                className={`flex-row justify-between rounded-lg px-4 py-2 mb-3 ${
                  currentTotal === allowedQuantity ? 'bg-green-50' : 'bg-amber-50'
                }`}
              >
                <Text className="text-sm text-gray-600">Current total</Text>
                <Text
                  className={`text-sm font-bold ${
                    currentTotal === allowedQuantity ? 'text-green-700' : 'text-amber-700'
                  }`}
                >
                  {currentTotal} / {allowedQuantity}
                </Text>
              </View>
              {error ? <Text className="text-red-500 text-xs mb-3">{error}</Text> : null}

              {isWorking ? (
                <View className="items-center py-3">
                  <ActivityIndicator color="#21226b" />
                  <Text className="text-xs text-gray-500 mt-2">
                    {printing
                      ? 'Printing labels…'
                      : isSaving
                        ? 'Saving barcodes…'
                        : 'Fetching parts…'}
                  </Text>
                </View>
              ) : (
                <Button
                  title="Submit & Print"
                  onPress={handlePrint}
                  disabled={isCountriesLoading}
                />
              )}
            </ScrollView>
          </View>
        </KeyboardAvoidingView>
      </View>
    </Modal>
  );
}

// ── BundleRow — one quantity + country row in the split form ──────────────────

function BundleRow({
  row,
  countries,
  onQuantityChange,
  onCountryChange,
  onRemove,
}: BundleRowProps) {
  const [showPicker, setShowPicker] = useState(false);

  return (
    <View className="flex-row items-center mb-2 gap-2">
      <TextInput
        className="flex-1 border border-gray-200 rounded-lg px-3 py-2 text-sm text-center"
        keyboardType="numeric"
        value={row.quantity > 0 ? String(row.quantity) : ''}
        onChangeText={onQuantityChange}
        placeholder="0"
        placeholderTextColor="#aaa"
      />
      <Pressable
        onPress={() => setShowPicker(true)}
        className="flex-[2] border border-gray-200 rounded-lg px-3 py-2.5 flex-row items-center justify-between"
      >
        <Text className="text-sm text-gray-700" numberOfLines={1}>
          {row.country || 'Select Country'}
        </Text>
        <Ionicons name="chevron-down" size={14} color="#aaa" />
      </Pressable>
      {onRemove ? (
        <Pressable onPress={onRemove} className="w-8 items-center" hitSlop={8}>
          <Ionicons name="close-circle" size={20} color="#dc2626" />
        </Pressable>
      ) : (
        <View className="w-8" />
      )}

      {showPicker && (
        <Modal transparent animationType="fade" onRequestClose={() => setShowPicker(false)}>
          <Pressable className="flex-1 bg-black/30" onPress={() => setShowPicker(false)} />
          <View
            className="absolute left-8 right-8 bg-white rounded-xl overflow-hidden"
            style={{ top: '30%', elevation: 12 }}
          >
            <Text className="text-sm font-bold text-primary px-4 py-3 border-b border-gray-100">
              Select Country
            </Text>
            <ScrollView style={{ maxHeight: 240 }}>
              {countries.map((c) => (
                <Pressable
                  key={c}
                  onPress={() => {
                    onCountryChange(c);
                    setShowPicker(false);
                  }}
                  className={`px-4 py-3 border-b border-gray-50 ${row.country === c ? 'bg-primary/5' : ''}`}
                >
                  <Text
                    className={`text-sm ${row.country === c ? 'text-primary font-semibold' : 'text-gray-700'}`}
                  >
                    {c}
                  </Text>
                </Pressable>
              ))}
            </ScrollView>
          </View>
        </Modal>
      )}
    </View>
  );
}
