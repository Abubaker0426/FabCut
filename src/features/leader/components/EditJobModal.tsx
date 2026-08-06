/**
 * EditJobModal — mirrors edit_job_dialog.xml + EditJobDialog.java
 *
 * Structure (per Java code):
 *  - Size row (TextViews)
 *  - Ratio row (editable EditTexts)
 *  - Per item code block:
 *      • Item description + X (clear) button
 *      • Shade | Shrinkage | Pattern inputs (horizontal)
 *      • Quantity inputs per size + Lay Length + Total (running sum)
 *  - Submit button
 *
 * Validation mirrors Java:
 *  - All quantities and ratios must be non-zero
 *  - Quantities must be proportional to ratios
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
import type { Item, Ratio } from '@/types';

// ─── Types ────────────────────────────────────────────────────────────────────

export interface AssignJobData {
  ratioNumber: number;
  /** Editable ratios keyed by size */
  ratios: Record<string, string>;
  /** Per item code: shade, shrinkage, pattern, quantities, layLength */
  items: {
    itemCode: string;
    shade: string;
    shrinkage: string;
    pattern: string;
    quantities: Record<string, string>;
    layLength: string;
  }[];
}

interface EditJobModalProps {
  visible: boolean;
  ratioNumber: number | null;
  /** Original ratio rows from server */
  ratios: Ratio[];
  /** Item codes for this marker */
  items?: Item[];
  onDismiss: () => void;
  onSubmit: (data: AssignJobData) => void;
}

// ─── Component ────────────────────────────────────────────────────────────────

export function EditJobModal({
  visible,
  ratioNumber,
  ratios,
  items = [],
  onDismiss,
  onSubmit,
}: EditJobModalProps) {
  // Editable ratio values (mirrors ratioContainer in Java)
  const [editableRatios, setEditableRatios] = useState<Record<string, string>>({});

  // Per-item state (mirrors quantitiesContainer blocks in Java)
  const [itemData, setItemData] = useState<
    Record<
      string,
      {
        shade: string;
        shrinkage: string;
        pattern: string;
        quantities: Record<string, string>;
        layLength: string;
      }
    >
  >({});

  // Active item codes (X button removes from list, mirrors clearImageView in Java)
  const [activeItems, setActiveItems] = useState<string[]>([]);

  useEffect(() => {
    if (!visible) return;

    // Init editable ratios
    const rInit: Record<string, string> = {};
    ratios.forEach((r) => { rInit[r.size] = String(r.ratio); });
    setEditableRatios(rInit);

    // Init all item codes as active
    const codes = items.length > 0 ? items.map((i) => i.itemCode) : ['DEFAULT'];
    setActiveItems(codes);

    // Init per-item data
    const dInit: typeof itemData = {};
    codes.forEach((code) => {
      const qInit: Record<string, string> = {};
      ratios.forEach((r) => { qInit[r.size] = String(r.ratioQty); });
      dInit[code] = { shade: '', shrinkage: '', pattern: '', quantities: qInit, layLength: '' };
    });
    setItemData(dInit);
  }, [visible, ratios, items]);

  // Running total for a given item
  const getTotal = (code: string) =>
    Object.values(itemData[code]?.quantities ?? {})
      .reduce((s, v) => s + (Number(v) || 0), 0);

  const removeItem = (code: string) =>
    setActiveItems((prev) => prev.filter((c) => c !== code));

  const updateItemField = (
    code: string,
    field: 'shade' | 'shrinkage' | 'pattern' | 'layLength',
    value: string
  ) =>
    setItemData((prev) => ({
      ...prev,
      [code]: { ...prev[code], [field]: value },
    }));

  const updateQty = (code: string, size: string, value: string) =>
    setItemData((prev) => ({
      ...prev,
      [code]: {
        ...prev[code],
        quantities: { ...prev[code]?.quantities, [size]: value },
      },
    }));

  const handleSubmit = () => {
    if (ratioNumber === null) return;
    onSubmit({
      ratioNumber,
      ratios: editableRatios,
      items: activeItems.map((code) => ({
        itemCode: code,
        ...itemData[code],
      })),
    });
  };

  const sizes = ratios.map((r) => r.size);

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onDismiss}
    >
      <Pressable className="flex-1 bg-black/40 justify-center px-4" onPress={onDismiss}>
        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <Pressable onPress={(e) => e.stopPropagation()}>
            <View
              className="bg-white rounded-2xl overflow-hidden"
              style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12, maxHeight: 600 }}
            >
              <ScrollView contentContainerStyle={{ padding: 20 }}>
                <Text className="text-base font-bold text-primary mb-4">
                  Assign Job — Ratio #{ratioNumber}
                </Text>

                {/* ── Size row (bold TextViews) ──────────────────────── */}
                <View className="flex-row mb-1">
                  {sizes.map((s) => (
                    <Text key={s} className="flex-1 text-center text-xs font-bold text-gray-800 uppercase">
                      {s}
                    </Text>
                  ))}
                  <Text className="w-16 text-center text-xs font-bold text-gray-500 uppercase">Total</Text>
                </View>

                {/* ── Ratio row (editable inputs) ────────────────────── */}
                <View className="flex-row mb-4 border border-gray-200 rounded-lg py-2">
                  {sizes.map((s) => (
                    <TextInput
                      key={s}
                      className="flex-1 text-center text-xs text-gray-800"
                      keyboardType="numeric"
                      value={editableRatios[s] ?? ''}
                      onChangeText={(v) =>
                        setEditableRatios((prev) => ({ ...prev, [s]: v }))
                      }
                      placeholder="0"
                      placeholderTextColor="#aaa"
                    />
                  ))}
                  <View className="w-16" />
                </View>

                {/* ── Per-item blocks ────────────────────────────────── */}
                {activeItems.map((code) => {
                  const item = items.find((i) => i.itemCode === code);
                  const data = itemData[code];
                  if (!data) return null;
                  return (
                    <ItemBlock
                      key={code}
                      itemCode={code}
                      itemDesc={item?.itemDesc}
                      sizes={sizes}
                      data={data}
                      total={getTotal(code)}
                      onRemove={() => removeItem(code)}
                      onFieldChange={(field, v) => updateItemField(code, field, v)}
                      onQtyChange={(size, v) => updateQty(code, size, v)}
                    />
                  );
                })}

                <Button title="Submit" onPress={handleSubmit} className="mt-2" />
              </ScrollView>
            </View>
          </Pressable>
        </KeyboardAvoidingView>
      </Pressable>
    </Modal>
  );
}

// ── Per-item block ────────────────────────────────────────────────────────────

interface ItemBlockProps {
  itemCode: string;
  itemDesc?: string;
  sizes: string[];
  data: {
    shade: string;
    shrinkage: string;
    pattern: string;
    quantities: Record<string, string>;
    layLength: string;
  };
  total: number;
  onRemove: () => void;
  onFieldChange: (field: 'shade' | 'shrinkage' | 'pattern' | 'layLength', v: string) => void;
  onQtyChange: (size: string, v: string) => void;
}

function ItemBlock({
  itemCode, itemDesc, sizes, data, total, onRemove, onFieldChange, onQtyChange,
}: ItemBlockProps) {
  return (
    <View className="mb-4 border border-gray-100 rounded-xl p-3 bg-gray-50">

      {/* Item description + X button — mirrors createItemDescriptionTextView + clearImageView */}
      <View className="flex-row items-center mb-2">
        <Text className="flex-1 text-sm font-semibold text-gray-800">
          {itemDesc ? `${itemCode} - ${itemDesc}` : itemCode}
        </Text>
        <Pressable onPress={onRemove} hitSlop={8}>
          <Ionicons name="close-circle" size={18} color="#dc2626" />
        </Pressable>
      </View>

      {/* Shade | Shrinkage | Pattern — horizontal row, mirrors addShadeEditTextViewToLayout */}
      <View className="flex-row items-center gap-2 mb-3">
        <LabeledSmallInput label="Shade"    value={data.shade}    onChange={(v) => onFieldChange('shade', v)} />
        <LabeledSmallInput label="Shrink%"  value={data.shrinkage} onChange={(v) => onFieldChange('shrinkage', v)} keyboardType="decimal-pad" />
        <LabeledSmallInput label="Pattern"  value={data.pattern}  onChange={(v) => onFieldChange('pattern', v)} />
      </View>

      {/* Quantity inputs per size + Lay Length + Total — mirrors quantityContainer */}
      <View className="flex-row items-end gap-1">
        {sizes.map((s) => (
          <View key={s} className="flex-1 items-center">
            <Text className="text-xs text-gray-400 mb-1">{s}</Text>
            <TextInput
              className="border border-gray-200 rounded px-1 py-1.5 text-center text-xs w-full bg-white"
              keyboardType="numeric"
              value={data.quantities[s] ?? ''}
              onChangeText={(v) => onQtyChange(s, v)}
              placeholder="0"
              placeholderTextColor="#aaa"
            />
          </View>
        ))}
        {/* Lay Length */}
        <View className="w-16 items-center">
          <Text className="text-xs text-gray-400 mb-1">Lay L.</Text>
          <TextInput
            className="border border-gray-200 rounded px-1 py-1.5 text-center text-xs w-full bg-white"
            keyboardType="decimal-pad"
            value={data.layLength}
            onChangeText={(v) => onFieldChange('layLength', v)}
            placeholder="0"
            placeholderTextColor="#aaa"
          />
        </View>
        {/* Running total — mirrors TOTAL_TAG TextView */}
        <View className="w-12 items-center">
          <Text className="text-xs text-gray-400 mb-1">Total</Text>
          <Text className="text-xs font-bold text-primary py-2">{total}</Text>
        </View>
      </View>
    </View>
  );
}

function LabeledSmallInput({
  label, value, onChange, keyboardType = 'default',
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  keyboardType?: 'default' | 'decimal-pad';
}) {
  return (
    <View className="flex-1">
      <Text className="text-xs font-bold text-gray-700 mb-0.5">{label}</Text>
      <TextInput
        className="border border-gray-200 rounded-lg px-2 py-1.5 text-xs bg-white"
        value={value}
        onChangeText={onChange}
        keyboardType={keyboardType}
        autoCapitalize="characters"
        placeholder=""
        placeholderTextColor="#aaa"
      />
    </View>
  );
}
