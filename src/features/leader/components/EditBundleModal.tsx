/**
 * EditBundleModal — mirrors edit_bundle_dialog.xml + edit_bundle_row_item.xml
 *
 * Leader splits a bundle across countries.
 * Each row = quantity + country picker.
 * Validates total === totalQuantity before submit.
 */
import { Ionicons } from '@expo/vector-icons';
import { useEffect, useState } from 'react';
import {
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  Text,
  TextInput,
  View,
} from 'react-native';

import { Button } from '@/components/ui/Button';
import type { BundleDetail } from '@/types';
import { MOCK_COUNTRIES } from '../constants/leaderMockData';

interface EditBundleModalProps {
  visible: boolean;
  ocNumber: string;
  jobId: string;
  totalQuantity: number;
  onDismiss: () => void;
  onSubmit: (rows: BundleDetail[]) => void;
}

export function EditBundleModal({
  visible,
  ocNumber,
  totalQuantity,
  onDismiss,
  onSubmit,
}: EditBundleModalProps) {
  const [rows, setRows] = useState<BundleDetail[]>([
    { quantity: 0, country: MOCK_COUNTRIES[0] },
  ]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (visible) {
      setRows([{ quantity: 0, country: MOCK_COUNTRIES[0] }]);
      setError('');
    }
  }, [visible]);

  const addRow = () =>
    setRows((prev) => [...prev, { quantity: 0, country: MOCK_COUNTRIES[0] }]);

  const removeRow = (i: number) =>
    setRows((prev) => prev.filter((_, idx) => idx !== i));

  const updateRow = (i: number, field: keyof BundleDetail, value: string | number) =>
    setRows((prev) =>
      prev.map((row, idx) => (idx === i ? { ...row, [field]: value } : row))
    );

  const currentTotal = rows.reduce((s, r) => s + (Number(r.quantity) || 0), 0);

  const handleSubmit = () => {
    if (currentTotal !== totalQuantity) {
      setError(`Total must equal ${totalQuantity}. Current: ${currentTotal}`);
      return;
    }
    setError('');
    // TODO: fetch parts → generate ZPL → print via Zebra
    onSubmit(rows);
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onDismiss}
    >
      <Pressable
        className="flex-1 bg-black/40 justify-center px-4"
        onPress={onDismiss}
      >
        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <Pressable onPress={(e) => e.stopPropagation()}>
            <View
              className="bg-white rounded-2xl overflow-hidden"
              style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12 }}
            >
              <ScrollView contentContainerStyle={{ padding: 20 }}>

                <Text className="text-base font-bold text-primary mb-1">Split Bundle</Text>
                <Text className="text-xs text-gray-500 mb-4">
                  OC# {ocNumber} · Total Qty: {totalQuantity}
                </Text>

                {/* Column headers */}
                <View className="flex-row mb-2 px-1">
                  <Text className="flex-1 text-xs font-bold text-gray-500 uppercase">Quantity</Text>
                  <Text className="flex-[2] text-xs font-bold text-gray-500 uppercase ml-3">Country</Text>
                  <View className="w-8" />
                </View>

                {rows.map((row, i) => (
                  <BundleRow
                    key={i}
                    row={row}
                    countries={MOCK_COUNTRIES}
                    onQuantityChange={(v) => updateRow(i, 'quantity', Number(v) || 0)}
                    onCountryChange={(v) => updateRow(i, 'country', v)}
                    onRemove={rows.length > 1 ? () => removeRow(i) : undefined}
                  />
                ))}

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
                    currentTotal === totalQuantity ? 'bg-green-50' : 'bg-amber-50'
                  }`}
                >
                  <Text className="text-sm text-gray-600">Current total</Text>
                  <Text
                    className={`text-sm font-bold ${
                      currentTotal === totalQuantity ? 'text-green-700' : 'text-amber-700'
                    }`}
                  >
                    {currentTotal} / {totalQuantity}
                  </Text>
                </View>

                {error ? <Text className="text-red-500 text-xs mb-3">{error}</Text> : null}

                <Button title="Submit & Print" onPress={handleSubmit} />
              </ScrollView>
            </View>
          </Pressable>
        </KeyboardAvoidingView>
      </Pressable>
    </Modal>
  );
}

// ── Bundle row ────────────────────────────────────────────────────────────────

interface BundleRowProps {
  row: BundleDetail;
  countries: string[];
  onQuantityChange: (v: string) => void;
  onCountryChange: (v: string) => void;
  onRemove?: () => void;
}

function BundleRow({ row, countries, onQuantityChange, onCountryChange, onRemove }: BundleRowProps) {
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
        <Text className="text-sm text-gray-700">{row.country}</Text>
        <Ionicons name="chevron-down" size={14} color="#aaa" />
      </Pressable>
      {onRemove ? (
        <Pressable onPress={onRemove} className="w-8 items-center" hitSlop={8}>
          <Ionicons name="close-circle" size={20} color="#dc2626" />
        </Pressable>
      ) : (
        <View className="w-8" />
      )}

      {/* Country picker modal */}
      {showPicker && (
        <Modal
          transparent
          animationType="fade"
          onRequestClose={() => setShowPicker(false)}
        >
          <Pressable
            className="flex-1 bg-black/30"
            onPress={() => setShowPicker(false)}
          />
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
                  onPress={() => { onCountryChange(c); setShowPicker(false); }}
                  className={`px-4 py-3 border-b border-gray-50 ${
                    row.country === c ? 'bg-primary/5' : ''
                  }`}
                >
                  <Text
                    className={`text-sm ${
                      row.country === c ? 'text-primary font-semibold' : 'text-gray-700'
                    }`}
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
