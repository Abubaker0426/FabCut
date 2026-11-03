import { useEffect, useState } from 'react';
import {
  Alert,
  Dimensions,
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
import type { EditEndBitJobModalProps } from '@/types/EditEndBitJobModal';

const SCREEN_HEIGHT = Dimensions.get('window').height;

export function EditEndBitJobModal({
  visible,
  ratios,
  items,
  layNumbers,
  parts,
  isPartsLoading = false,
  onDismiss,
  onSubmit,
}: EditEndBitJobModalProps) {
  const [layNumber, setLayNumber] = useState('');
  const [itemCode, setItemCode] = useState('');
  const [selectedParts, setSelectedParts] = useState<string[]>([]);
  const [quantities, setQuantities] = useState<Record<string, string>>({});

  // ── Reset on open ────────────────────────────────────────────────────────
  useEffect(() => {
    if (!visible) return;
    setLayNumber(layNumbers[0] ?? '');
    setItemCode(items[0]?.itemCode ?? '');
    setSelectedParts([]);
    setQuantities({});
  }, [visible]);

  // ── Toggle a part in/out of selection ────────────────────────────────────
  // Mirrors Java: booleanParts[i] toggle + partList add/remove
  const togglePart = (partName: string) => {
    setSelectedParts((prev) => {
      if (prev.includes(partName)) {
        setQuantities((q) => {
          const next = { ...q };
          ratios.forEach((r) => {
            delete next[`${partName}___${r.size}`];
          });
          return next;
        });
        return prev.filter((p) => p !== partName);
      } else {
        setQuantities((q) => {
          const next = { ...q };
          ratios.forEach((r) => {
            next[`${partName}___${r.size}`] = String(r.ratio ?? 0);
          });
          return next;
        });
        return [...prev, partName];
      }
    });
  };

  // ── Validation ───────────────────────────────────────────────────────────
  const handleSubmit = () => {
    if (!layNumber || layNumber === '0') {
      Alert.alert('Error', 'Please select a lay number.');
      return;
    }
    if (selectedParts.length === 0) {
      Alert.alert('Error', 'Please select at least one part.');
      return;
    }
    for (const part of selectedParts) {
      const allZero = ratios.every((r) => !parseInt(quantities[`${part}___${r.size}`] ?? '0', 10));
      if (allZero) {
        Alert.alert('Error', `Quantities cannot all be zero for part: ${part}`);
        return;
      }
    }
    onSubmit({ layNumber, itemCode, quantities, selectedParts });
  };

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
            className="bg-white rounded-2xl overflow-hidden"
            style={{
              elevation: 8,
              shadowColor: '#000',
              shadowOpacity: 0.15,
              shadowRadius: 12,
              maxHeight: SCREEN_HEIGHT * 0.85,
            }}
            onStartShouldSetResponder={() => true}
          >
            <ScrollView
              contentContainerStyle={{ padding: 20 }}
              keyboardShouldPersistTaps="handled"
              nestedScrollEnabled
              bounces={false}
            >
              <Text className="text-base font-bold text-primary mb-4">Assign End Bit Job</Text>

              {/* ── Lay Number ── */}
              <Text className="text-xs font-semibold text-gray-600 mb-1">Lay Number</Text>
              <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                <View className="flex-row gap-2">
                  {layNumbers.length > 0 ? (
                    layNumbers.map((ln) => (
                      <Pressable
                        key={ln}
                        onPress={() => setLayNumber(ln)}
                        className={`px-3 py-2 rounded-lg border ${layNumber === ln ? 'bg-primary border-primary' : 'border-gray-300'}`}
                      >
                        <Text
                          className={`text-sm ${layNumber === ln ? 'text-white' : 'text-gray-600'}`}
                        >
                          Lay {ln}
                        </Text>
                      </Pressable>
                    ))
                  ) : (
                    <Text className="text-xs text-gray-400 py-2">No lay numbers available</Text>
                  )}
                </View>
              </ScrollView>

              {/* ── Item Code ── */}
              <Text className="text-xs font-semibold text-gray-600 mb-1">Item Code</Text>
              <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                <View className="flex-row gap-2">
                  {items.map((item) => (
                    <Pressable
                      key={item.itemCode}
                      onPress={() => setItemCode(item.itemCode)}
                      className={`px-3 py-2 rounded-lg border ${itemCode === item.itemCode ? 'bg-primary border-primary' : 'border-gray-300'}`}
                    >
                      <Text
                        className={`text-sm ${itemCode === item.itemCode ? 'text-white' : 'text-gray-600'}`}
                      >
                        {item.itemCode}
                      </Text>
                    </Pressable>
                  ))}
                </View>
              </ScrollView>

              {/* ── Parts — same chip pattern as Lay Number, multi-select ── */}
              <Text className="text-xs font-semibold text-gray-600 mb-1">Select Parts</Text>
              {isPartsLoading ? (
                <Text className="text-xs text-gray-400 mb-4">Loading parts...</Text>
              ) : parts.length === 0 ? (
                <Text className="text-xs text-gray-400 mb-4">No parts found for this OC.</Text>
              ) : (
                <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                  <View className="flex-row flex-wrap gap-2" style={{ maxWidth: 600 }}>
                    {parts.map((p) => {
                      const isSelected = selectedParts.includes(p.part);
                      return (
                        <Pressable
                          key={p.partsUnique}
                          onPress={() => togglePart(p.part)}
                          className={`px-3 py-2 rounded-lg border ${isSelected ? 'bg-primary border-primary' : 'border-gray-300'}`}
                        >
                          <Text
                            className={`text-sm ${isSelected ? 'text-white' : 'text-gray-600'}`}
                          >
                            {p.part}
                          </Text>
                        </Pressable>
                      );
                    })}
                  </View>
                </ScrollView>
              )}

              {/* ── Quantities per selected part per size ── */}
              {selectedParts.length > 0 && ratios.length > 0 && (
                <>
                  <Text className="text-xs font-semibold text-gray-600 mb-2">
                    Quantities per Size
                  </Text>
                  {selectedParts.map((partName) => (
                    <View key={partName} className="mb-4">
                      <Text className="text-xs text-gray-500 mb-1">{partName}</Text>
                      <View className="flex-row gap-1">
                        {ratios.map((r) => (
                          <View key={r.size} className="flex-1 items-center">
                            <Text className="text-xs text-gray-400 mb-1">{r.size}</Text>
                            <TextInput
                              className="border border-gray-200 rounded-lg px-1 py-1.5 text-center text-xs w-full"
                              keyboardType="numeric"
                              value={quantities[`${partName}___${r.size}`] ?? ''}
                              onChangeText={(v) =>
                                setQuantities((prev) => ({
                                  ...prev,
                                  [`${partName}___${r.size}`]: v,
                                }))
                              }
                              placeholder="0"
                              placeholderTextColor="#aaa"
                            />
                          </View>
                        ))}
                      </View>
                    </View>
                  ))}
                </>
              )}
              <Button title="Submit" onPress={handleSubmit} className="mt-2" />
            </ScrollView>
          </View>
        </KeyboardAvoidingView>
      </View>
    </Modal>
  );
}
