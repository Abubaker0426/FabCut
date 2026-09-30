 //* EditJobModal --> pop-up after the selecting the job
import { Ionicons } from '@expo/vector-icons'
import { useEffect, useState } from 'react'
import { Dimensions, KeyboardAvoidingView, Modal, Platform, Pressable, ScrollView, Text, TextInput, TouchableWithoutFeedback, View, } from 'react-native'
import { Button } from '@/components/ui/Button'
import type { AssignJobItemData, EditJobModalProps, ItemBlockProps } from '@/types/EditJobModal'

const SCREEN_HEIGHT = Dimensions.get('window').height

 
export function EditJobModal({ visible, ratioNumber, ratios, items = [], onDismiss, onSubmit, }: EditJobModalProps) {
  // Editable ratio values (mirrors ratioContainer in Java)
  const [editableRatios, setEditableRatios] = useState<Record<string, string>>({})
  const [itemData, setItemData] = useState<Record<string, AssignJobItemData>>({})
  // Active item codes (X button removes from list, mirrors clearImageView in Java)
  const [activeItems, setActiveItems] = useState<string[]>([])
  useEffect(() => {
    if (!visible) return

    // Init editable ratios
    const rInit: Record<string, string> = {}
    ratios.forEach((r) => { rInit[r.size] = String(r.ratio) })
    setEditableRatios(rInit)

    // Init all item codes as active
    const codes = items.length > 0 ? items.map((i) => i.itemCode) : ['DEFAULT']
    setActiveItems(codes)

    // Init per-item data
    const dInit: Record<string, AssignJobItemData> = {}
    codes.forEach((code) => {
      const qInit: Record<string, string> = {}
      ratios.forEach((r) => { qInit[r.size] = String(r.ratioQty) })
      dInit[code] = { itemCode: code, shade: '', shrinkage: '', pattern: '', quantities: qInit, layLength: '' }
    })
    setItemData(dInit)
  }, [visible, ratios, items])

  // Running total for a given item
  const getTotal = (code: string) =>
    Object.values(itemData[code]?.quantities ?? {})
      .reduce((s, v) => s + (Number(v) || 0), 0)

  const removeItem = (code: string) =>
    setActiveItems((prev) => prev.filter((c) => c !== code))

  const updateItemField = (
    code: string,
    field: 'shade' | 'shrinkage' | 'pattern' | 'layLength',
    value: string
  ) =>
    setItemData((prev) => ({
      ...prev,
      [code]: { ...prev[code], [field]: value },
    }))

  const updateQty = (code: string, size: string, value: string) =>
    setItemData((prev) => ({
      ...prev,
      [code]: {
        ...prev[code],
        quantities: { ...prev[code]?.quantities, [size]: value },
      },
    }))

  const handleSubmit = () => {
    if (ratioNumber === null) return

    // ── Validation — mirrors EditJobDialogPresenter.prepareJobData() ──────────

    // 1. Shade, Shrinkage, Pattern must be non-empty for every active item
    //    Java: if(TextUtils.isEmpty(itemShadeValue)) → showMessage(R.string.item_shade_error)
    for (const code of activeItems) {
      const d = itemData[code]
      if (!d) continue
      if (!d.shade.trim()) {
        alert(`Please enter Shade for ${code}`)
        return
      }
      if (!d.shrinkage.trim()) {
        alert(`Please enter Shrinkage for ${code}`)
        return
      }
      if (!d.pattern.trim()) {
        alert(`Please enter Pattern for ${code}`)
        return
      }
      // 2. Lay Length must be non-empty
      //    Java: if(TextUtils.isEmpty(layLength)) → showMessage(R.string.lay_length_error)
      if (!d.layLength.trim()) {
        alert(`Please enter Lay Length for ${code}`)
        return
      }
    }

    // 3. All ratio + quantity values must be non-zero
    //    Java: if(!areQuantitiesValid || allRatiosZero) → showMessage(R.string.zero_quantity_ratio_error)
    const allRatiosZero = Object.values(editableRatios).every((v) => !parseInt(v, 10))
    if (allRatiosZero) {
      alert('Ratios cannot all be zero')
      return
    }

    for (const code of activeItems) {
      const d = itemData[code]
      if (!d) continue
      const allQtyZero = Object.values(d.quantities).every((v) => !parseInt(v, 10))
      if (allQtyZero) {
        alert(`Quantities cannot all be zero for ${code}`)
        return
      }
    }

    onSubmit({
      ratioNumber,
      ratios: editableRatios,
      items: activeItems.map((code) => ({ ...itemData[code] })),
    })
  }

  const sizes = ratios.map((r) => r.size)

  // Split sizes into chunks of 4 — mirrors Java's TableLayout which wraps automatically.
  // On a phone screen, more than 4 sizes in one row becomes unreadable.
  const CHUNK_SIZE = 6
  const sizeChunks: string[][] = []
  for (let i = 0; i < sizes.length; i += CHUNK_SIZE) {
    sizeChunks.push(sizes.slice(i, i + CHUNK_SIZE))
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

        {/* Backdrop tap area — sits behind the card */}
        <TouchableWithoutFeedback onPress={onDismiss}>
          <View style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }} />
        </TouchableWithoutFeedback>

        {/* Card — plain View, no Pressable, nothing to fight with ScrollView */}
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        >
          <View
            className="bg-white rounded-2xl overflow-hidden"
            style={{
              elevation: 8,
              shadowColor: '#000',
              shadowOpacity: 0.15,
              shadowRadius: 12,
              maxHeight: SCREEN_HEIGHT * 0.85,
            }}
          >
            <ScrollView
              contentContainerStyle={{ padding: 20 }}
              keyboardShouldPersistTaps="handled"
              showsVerticalScrollIndicator
              bounces={false}
              nestedScrollEnabled
            >
                <Text className="text-base font-bold text-primary mb-4">
                  Assign Job — Ratio {ratioNumber}
                </Text>

                {/* ── Size + Ratio rows — chunked into rows of 4 ──────── */}
                {sizeChunks.map((chunk, chunkIdx) => (
                  <View key={chunkIdx}>
                    {/* Size labels */}
                    <View className="flex-row mb-1">
                      {chunk.map((s) => (
                        <Text key={s} className="flex-1 text-center text-xs font-bold text-gray-800 uppercase">
                          {s}
                        </Text>
                      ))}
                      {/* Spacer for Total column — only on last chunk */}
                      {chunkIdx === sizeChunks.length - 1 && (
                        <Text className="w-16 text-center text-xs font-bold text-gray-500 uppercase">Total</Text>
                      )}
                    </View>

                    {/* Editable ratio inputs */}
                    <View className="flex-row mb-3 border border-gray-200 rounded-lg py-2">
                      {chunk.map((s) => (
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
                      {chunkIdx === sizeChunks.length - 1 && <View className="w-16" />}
                    </View>
                  </View>
                ))}

                {/* ── Per-item blocks ────────────────────────────────── */}
                {activeItems.map((code) => {
                  const item = items.find((i) => i.itemCode === code)
                  const data = itemData[code]
                  if (!data) return null
                  return (
                    <ItemBlock
                      key={code}
                      itemCode={code}
                      itemDesc={item?.itemDesc}
                      sizes={sizes}
                      sizeChunks={sizeChunks}
                      data={data}
                      total={getTotal(code)}
                      onRemove={() => removeItem(code)}
                      onFieldChange={(field, v) => updateItemField(code, field, v)}
                      onQtyChange={(size, v) => updateQty(code, size, v)}
                    />
                  )
                })}

                <Button title="Submit" onPress={handleSubmit} className="mt-2" />
              </ScrollView>
            </View>
        </KeyboardAvoidingView>
      </View>
    </Modal>
  )
}

// ── Per-item block ────────────────────────────────────────────────────────────

function ItemBlock({
  itemCode, itemDesc, sizes, sizeChunks, data, total, onRemove, onFieldChange, onQtyChange,
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
        <LabeledSmallInput label="Shade" value={data.shade} onChange={(v) => onFieldChange('shade', v)} />
        <LabeledSmallInput label="Shrink%" value={data.shrinkage} onChange={(v) => onFieldChange('shrinkage', v)} keyboardType="decimal-pad" />
        <LabeledSmallInput label="Pattern" value={data.pattern} onChange={(v) => onFieldChange('pattern', v)} />
      </View>

      {/* Quantity inputs per size — chunked into rows of 4 + Lay Length + Total on last row */}
      {sizeChunks.map((chunk, chunkIdx) => (
        <View key={chunkIdx} className="flex-row items-end gap-1 mb-1">
          {chunk.map((s) => (
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
          {/* Lay Length + Total only on last chunk */}
          {chunkIdx === sizeChunks.length - 1 && (
            <>
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
              <View className="w-12 items-center">
                <Text className="text-xs text-gray-400 mb-1">Total</Text>
                <Text className="text-xs font-bold text-primary py-2">{total}</Text>
              </View>
            </>
          )}
        </View>
      ))}
    </View>
  )
}

function LabeledSmallInput({
  label, value, onChange, keyboardType = 'default',
}: {
  label: string
  value: string
  onChange: (v: string) => void
  keyboardType?: 'default' | 'decimal-pad'
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
  )
}
