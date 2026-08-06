/**
 * BarcodeRow
 *
 * One row in the barcode list on the follower's job screen.
 * Mirrors follower_job_row_item.xml exactly.
 *
 * Layout:
 *  ┌──────────────────────────────────────────────┐
 *  │  BC-00001  (barcode — bold)                  │
 *  │                                              │
 *  │  Expected Plies    │  Actual Plies  [ icon ] │
 *  │  24                │  [  input   ]           │
 *  │                                              │
 *  │  [ Reason dropdown ─────────── ]  [ delete ] │
 *  └──────────────────────────────────────────────┘
 *
 * Validation states (feedback icon):
 *   idle       → nothing shown
 *   validating → ActivityIndicator spinner
 *   valid      → green checkmark  (ic_check)
 *   error      → red X            (ic_error)
 */
import { Ionicons } from '@expo/vector-icons';
import { ActivityIndicator, Pressable, Text, TextInput, View } from 'react-native';
import { useState } from 'react';

import CustomDropdown from '@/components/Common/CustomDropdown';
import type { ScanBarcode } from '@/types';

// Mirrors @array/reason_options in strings.xml
const REASON_OPTIONS = ['Reason', 'Excess', 'Shortage'];

export type ValidationState = 'idle' | 'validating' | 'valid' | 'error';

interface BarcodeRowProps {
  item: ScanBarcode;
  validationState?: ValidationState;
  onActualPliesChange: (barcode: string, value: string) => void;
  /** Called when the actual plies input loses focus — triggers validate API */
  onValidate?: (barcode: string, actualPlies: number) => void;
  onDelete?: (barcode: string) => void;
  onReasonChange?: (barcode: string, reason: string) => void;
}

export function BarcodeRow({
  item,
  validationState = item.validated ? 'valid' : 'idle',
  onActualPliesChange,
  onValidate,
  onDelete,
  onReasonChange,
}: BarcodeRowProps) {
  const [reason, setReason] = useState('Reason');

  const handleReasonSelect = (selected: string) => {
    setReason(selected);
    onReasonChange?.(item.barcode, selected);
  };

  const handlePliesBlur = () => {
    if (item.actualPlies > 0) {
      onValidate?.(item.barcode, item.actualPlies);
    }
  };

  return (
    <View
      className="bg-white rounded-xl mx-3 mb-3"
      style={{
        elevation: 3,
        shadowColor: '#000',
        shadowOpacity: 0.07,
        shadowRadius: 6,
        shadowOffset: { width: 0, height: 2 },
      }}
    >
      <View className="p-4">

        {/* ── Barcode string ─────────────────────────────────────────── */}
        <Text className="text-base font-bold text-gray-900 mb-5">
          {item.barcode}
        </Text>

        {/* ── Expected Plies | Actual Plies row ──────────────────────── */}
        <View className="flex-row items-start mb-5">

          {/* Expected plies — read-only, left column */}
          <View className="flex-1">
            <Text className="text-xs text-gray-500 mb-2">Expected Plies</Text>
            <Text className="text-sm font-bold text-gray-900">
              {item.expectedPlies}
            </Text>
          </View>

          {/* Actual plies — editable input + validation icon, right column */}
          <View className="flex-row items-center gap-3">
            <View>
              <Text className="text-xs text-gray-500 mb-2">Actual Plies</Text>
              {/* Rounded border — mirrors @drawable/circular_corners */}
              <TextInput
                className="border border-gray-300 rounded-lg px-3 py-2 text-sm font-bold text-gray-900"
                keyboardType="decimal-pad"
                value={item.actualPlies > 0 ? String(item.actualPlies) : ''}
                onChangeText={(v) => onActualPliesChange(item.barcode, v)}
                onBlur={handlePliesBlur}
                placeholder="0"
                placeholderTextColor="#aaa"
                style={{ minWidth: 80 }}
              />
            </View>

            {/* Feedback icon — mirrors feedback_icon + loader in XML */}
            <View
              className="w-6 h-6 items-center justify-center"
              style={{ marginTop: 20 }}
            >
              {validationState === 'validating' && (
                <ActivityIndicator size="small" color="#21226b" />
              )}
              {validationState === 'valid' && (
                <Ionicons name="checkmark-circle" size={22} color="#16a34a" />
              )}
              {validationState === 'error' && (
                <Ionicons name="close-circle" size={22} color="#dc2626" />
              )}
              {/* idle → nothing rendered (invisible in Java) */}
            </View>
          </View>
        </View>

        {/* ── Reason dropdown + Delete row ───────────────────────────── */}
        <View className="flex-row items-center gap-3">

          {/* Reason spinner — mirrors @drawable/circular_corners, width ~250dp */}
          <View style={{ flex: 1, maxWidth: 260 }}>
            <CustomDropdown
              data={REASON_OPTIONS}
              placeholder="Reason"
              selectedValue={reason === 'Reason' ? undefined : reason}
              onSelect={handleReasonSelect}
            />
          </View>

          {/* Delete icon — mirrors ic_delete */}
          <Pressable
            onPress={() => onDelete?.(item.barcode)}
            hitSlop={12}
            className="p-1"
            accessibilityLabel="Delete barcode"
            accessibilityRole="button"
          >
            <Ionicons name="trash-outline" size={22} color="#dc2626" />
          </Pressable>
        </View>

      </View>
    </View>
  );
}
