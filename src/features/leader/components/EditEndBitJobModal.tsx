/**
 * EditEndBitJobModal
 * Mirrors: edit_end_bit_job_dialog.xml + EditEndBitJobDialog.java
 *
 * Fields:
 *  - Lay Number dropdown (from GET /end-bits/lay-numbers)
 *  - Item Code selector (from marker.items — always 1 item when End Bits visible)
 *  - Parts multi-select (from GET /parts/{ocNo}) — mirrors booleanParts checkboxes
 *  - Quantities per size per selected part
 *
 * Validation mirrors Java submit():
 *  1. Lay number must not be "0" / empty
 *  2. At least 1 part must be selected
 *  3. All quantities must not all be zero
 */

import { useEffect, useState } from 'react'
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
} from 'react-native'

import { Button } from '@/components/ui/Button'
import type { AssignEndBitData, EditEndBitJobModalProps } from '@/types/EditEndBitJobModal'

const SCREEN_HEIGHT = Dimensions.get('window').height

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

  const [layNumber, setLayNumber]         = useState('')
  const [itemCode, setItemCode]           = useState('')
  const [selectedParts, setSelectedParts] = useState<string[]>([])
  // quantities keyed by "partName___size" — one input per part per size
  const [quantities, setQuantities]       = useState<Record<string, string>>({})

  // ── Reset on open ────────────────────────────────────────────────────────
  useEffect(() => {
    if (!visible) return
    setLayNumber(layNumbers[0] ?? '')
    setItemCode(items[0]?.itemCode ?? '')
    setSelectedParts([])
    setQuantities({})
  }, [visible])

  // ── Toggle a part in/out of selection ────────────────────────────────────
  // Mirrors Java: booleanParts[i] toggle + partList add/remove
  const togglePart = (partName: string) => {
    setSelectedParts((prev) => {
      if (prev.includes(partName)) {
        // Remove part + clear its quantities
        setQuantities((q) => {
          const next = { ...q }
          ratios.forEach((r) => { delete next[`${partName}___${r.size}`] })
          return next
        })
        return prev.filter((p) => p !== partName)
      } else {
        // Add part + init quantities from ratios
        setQuantities((q) => {
          const next = { ...q }
          ratios.forEach((r) => { next[`${partName}___${r.size}`] = String(r.ratio ?? 0) })
          return next
        })
        return [...prev, partName]
      }
    })
  }

  // ── Validation — mirrors Java submit() validation ────────────────────────
  const handleSubmit = () => {
    // 1. Lay number must be selected
    if (!layNumber || layNumber === '0') {
      Alert.alert('Error', 'Please select a lay number.')
      return
    }
    // 2. At least 1 part must be selected — mirrors Java partList.size() == 0
    if (selectedParts.length === 0) {
      Alert.alert('Error', 'Please select at least one part.')
      return
    }
    // 3. All quantities for each selected part must not all be zero
    for (const part of selectedParts) {
      const allZero = ratios.every(
        (r) => !parseInt(quantities[`${part}___${r.size}`] ?? '0', 10),
      )
      if (allZero) {
        Alert.alert('Error', `Quantities cannot all be zero for part: ${part}`)
        return
      }
    }

    onSubmit({ layNumber, itemCode, quantities, selectedParts })
  }

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={onDismiss}
      statusBarTranslucent
    >
      <View style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.4)', justifyContent: 'center', paddingHorizontal: 16 }}>

        <TouchableWithoutFeedback onPress={onDismiss}>
          <View style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }} />
        </TouchableWithoutFeedback>

        <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
          <View
            className="bg-white rounded-2xl overflow-hidden"
            style={{ elevation: 8, shadowColor: '#000', shadowOpacity: 0.15, shadowRadius: 12, maxHeight: SCREEN_HEIGHT * 0.85 }}
            onStartShouldSetResponder={() => true}
          >
            <ScrollView contentContainerStyle={{ padding: 20 }} keyboardShouldPersistTaps="handled" nestedScrollEnabled bounces={false}>

              <Text className="text-base font-bold text-primary mb-4">Assign End Bit Job</Text>

              {/* ── Lay Number ── */}
              <Text className="text-xs font-semibold text-gray-600 mb-1">Lay Number</Text>
              <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
                <View className="flex-row gap-2">
                  {layNumbers.length > 0 ? layNumbers.map((ln) => (
                    <Pressable
                      key={ln}
                      onPress={() => setLayNumber(ln)}
                      className={`px-3 py-2 rounded-lg border ${layNumber === ln ? 'bg-primary border-primary' : 'border-gray-300'}`}
                    >
                      <Text className={`text-sm ${layNumber === ln ? 'text-white' : 'text-gray-600'}`}>
                        Lay {ln}
                      </Text>
                    </Pressable>
                  )) : (
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
                      <Text className={`text-sm ${itemCode === item.itemCode ? 'text-white' : 'text-gray-600'}`}>
                        {item.itemCode}
                      </Text>
                    </Pressable>
                  ))}
                </View>
              </ScrollView>

              {/* ── Parts multi-select ── */}
              {/* Mirrors Java: booleanParts checkboxes in AlertDialog.Builder */}
              <Text className="text-xs font-semibold text-gray-600 mb-1">Select Parts</Text>
              {isPartsLoading ? (
                <Text className="text-xs text-gray-400 mb-4">Loading parts...</Text>
              ) : parts.length === 0 ? (
                <Text className="text-xs text-gray-400 mb-4">No parts found for this OC.</Text>
              ) : (
                <View className="mb-4 border border-gray-200 rounded-lg overflow-hidden">
                  {parts.map((part, idx) => {
                    const isSelected = selectedParts.includes(part.part)
                    return (
                      <Pressable
                        key={part.partsUnique}
                        onPress={() => togglePart(part.part)}
                        className={`flex-row items-center px-4 py-3 ${idx < parts.length - 1 ? 'border-b border-gray-100' : ''} ${isSelected ? 'bg-primary/5' : 'bg-white'}`}
                      >
                        {/* Checkbox */}
                        <View
                          className={`w-5 h-5 rounded border-2 items-center justify-center mr-3 ${isSelected ? 'bg-primary border-primary' : 'border-gray-300'}`}
                        >
                          {isSelected && <Text className="text-white text-xs font-bold">✓</Text>}
                        </View>
                        <Text className={`text-sm ${isSelected ? 'text-primary font-semibold' : 'text-gray-700'}`}>
                          {part.part}
                        </Text>
                      </Pressable>
                    )
                  })}
                </View>
              )}

              {/* ── Quantities per selected part per size ── */}
              {/* Mirrors Java: setQuantitiesContainer() — one row per selected part */}
              {selectedParts.length > 0 && ratios.length > 0 && (
                <>
                  <Text className="text-xs font-semibold text-gray-600 mb-2">Quantities per Size</Text>
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
                                setQuantities((prev) => ({ ...prev, [`${partName}___${r.size}`]: v }))
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
  )
}
