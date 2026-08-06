/**
 * EditEndBitJobModal — mirrors edit_end_bit_job_dialog.xml
 * Leader assigns an End-Bit job: choose lay number, item code,
 * enter quantities per size, shade, shrinkage, pattern.
 */
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

export interface AssignEndBitData {
  layNumber: string;
  itemCode: string;
  shade: string;
  shrinkage: string;
  pattern: string;
  quantities: Record<string, string>;
}

interface EditEndBitJobModalProps {
  visible: boolean;
  ratios: Ratio[];
  items: Item[];
  layNumbers: string[]; // fetched from /end-bits/lay-numbers/{markerId}
  onDismiss: () => void;
  onSubmit: (data: AssignEndBitData) => void;
}

export function EditEndBitJobModal({
  visible,
  ratios,
  items,
  layNumbers,
  onDismiss,
  onSubmit,
}: EditEndBitJobModalProps) {
  const [layNumber, setLayNumber] = useState('');
  const [itemCode, setItemCode] = useState('');
  const [shade, setShade] = useState('');
  const [shrinkage, setShrinkage] = useState('');
  const [pattern, setPattern] = useState('');
  const [quantities, setQuantities] = useState<Record<string, string>>({});

  useEffect(() => {
    if (visible) {
      setLayNumber(layNumbers[0] ?? '');
      setItemCode(items[0]?.itemCode ?? '');
      setShade('');
      setShrinkage('');
      setPattern('');
      const initial: Record<string, string> = {};
      ratios.forEach((r) => { initial[r.size] = String(r.ratioQty); });
      setQuantities(initial);
    }
  }, [visible, ratios, items, layNumbers]);

  const handleSubmit = () => {
    onSubmit({ layNumber, itemCode, shade, shrinkage, pattern, quantities });
  };

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
              style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12 }}
            >
              <ScrollView contentContainerStyle={{ padding: 20 }}>
                <Text className="text-base font-bold text-primary mb-4">
                  Assign End Bit Job
                </Text>

                {/* Lay number picker */}
                <Text className="text-xs font-semibold text-gray-600 mb-1">Lay Number</Text>
                <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                  <View className="flex-row gap-2">
                    {layNumbers.length > 0 ? layNumbers.map((ln) => (
                      <Pressable
                        key={ln}
                        onPress={() => setLayNumber(ln)}
                        className={`px-3 py-2 rounded-lg border ${
                          layNumber === ln
                            ? 'bg-primary border-primary'
                            : 'border-gray-300'
                        }`}
                      >
                        <Text className={`text-sm ${layNumber === ln ? 'text-white' : 'text-gray-600'}`}>
                          {ln}
                        </Text>
                      </Pressable>
                    )) : (
                      <Text className="text-xs text-gray-400 py-2">No lay numbers available</Text>
                    )}
                  </View>
                </ScrollView>

                {/* Item code picker */}
                <Text className="text-xs font-semibold text-gray-600 mb-1">Item Code</Text>
                <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                  <View className="flex-row gap-2">
                    {items.map((item) => (
                      <Pressable
                        key={item.itemCode}
                        onPress={() => setItemCode(item.itemCode)}
                        className={`px-3 py-2 rounded-lg border ${
                          itemCode === item.itemCode
                            ? 'bg-primary border-primary'
                            : 'border-gray-300'
                        }`}
                      >
                        <Text
                          className={`text-sm ${
                            itemCode === item.itemCode ? 'text-white' : 'text-gray-600'
                          }`}
                        >
                          {item.itemCode}
                        </Text>
                      </Pressable>
                    ))}
                  </View>
                </ScrollView>

                {/* Size row header */}
                {ratios.length > 0 && (
                  <>
                    <Text className="text-xs font-semibold text-gray-600 mb-1">
                      Quantities per Size
                    </Text>
                    <View className="flex-row mb-1">
                      {ratios.map((r) => (
                        <Text
                          key={r.size}
                          className="flex-1 text-center text-xs font-bold text-primary uppercase"
                        >
                          {r.size}
                        </Text>
                      ))}
                    </View>
                    <View className="flex-row mb-4 gap-2">
                      {ratios.map((r) => (
                        <View key={r.size} className="flex-1 items-center">
                          <TextInput
                            className="border border-gray-200 rounded-lg px-2 py-2 text-center text-sm w-full"
                            keyboardType="numeric"
                            value={quantities[r.size] ?? ''}
                            onChangeText={(v) =>
                              setQuantities((prev) => ({ ...prev, [r.size]: v }))
                            }
                            placeholder="0"
                            placeholderTextColor="#aaa"
                          />
                        </View>
                      ))}
                    </View>
                  </>
                )}

                <LabeledInput label="Shade"     value={shade}     onChange={setShade}     placeholder="e.g. Navy" />
                <LabeledInput label="Shrinkage" value={shrinkage} onChange={setShrinkage} placeholder="e.g. 2.5" keyboardType="decimal-pad" />
                <LabeledInput label="Pattern"   value={pattern}   onChange={setPattern}   placeholder="e.g. Plain" />

                <Button title="Submit" onPress={handleSubmit} className="mt-4" />
              </ScrollView>
            </View>
          </Pressable>
        </KeyboardAvoidingView>
      </Pressable>
    </Modal>
  );
}

function LabeledInput({
  label,
  value,
  onChange,
  placeholder,
  keyboardType = 'default',
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  keyboardType?: 'default' | 'decimal-pad' | 'numeric';
}) {
  return (
    <View className="mb-3">
      <Text className="text-xs font-semibold text-gray-600 mb-1">{label}</Text>
      <TextInput
        className="border border-gray-200 rounded-lg px-3 py-2 text-sm"
        value={value}
        onChangeText={onChange}
        placeholder={placeholder}
        placeholderTextColor="#aaa"
        keyboardType={keyboardType}
      />
    </View>
  );
}
