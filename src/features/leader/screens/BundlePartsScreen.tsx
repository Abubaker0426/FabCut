/**
 * BundlePartsScreen
 * Mirrors: BundlePartsActivity + activity_bundle_parts.xml
 *
 * Layout (exact Java structure):
 *  Toolbar
 *  ─────────────────────────────────────────────
 *  Row: [OC# EditText 250dp] [check icon 50×50]   ← marginTop 60dp, padding 10dp
 *  Divider
 *  CardView (margin 10dp, height ~513dp)
 *    └─ FlatList of parts rows
 *         Each row: [part name 250dp] [Checkbox]   ← marginH 24dp
 *  Submit Button (centered, height 40dp)
 */
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Pressable, RefreshControl, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import type { FetchPartsDetails } from '@/types';

const MOCK_PARTS: FetchPartsDetails[] = [
  { partsUnique: 'P1', partName: 'Front Panel',  isSelected: true  },
  { partsUnique: 'P2', partName: 'Back Panel',   isSelected: false },
  { partsUnique: 'P3', partName: 'Sleeve Left',  isSelected: true  },
  { partsUnique: 'P4', partName: 'Sleeve Right', isSelected: false },
  { partsUnique: 'P5', partName: 'Collar',       isSelected: false },
  { partsUnique: 'P6', partName: 'Pocket',       isSelected: true  },
];

export default function BundlePartsScreen() {
  const router = useRouter();

  const [ocNumber, setOcNumber]     = useState('');
  const [ocValid, setOcValid]       = useState<boolean | null>(null);
  const [parts, setParts]           = useState<FetchPartsDetails[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [loading, setLoading]       = useState(false);

  const handleFetchParts = () => {
    if (!ocNumber.trim()) return;
    setLoading(true);
    // TODO: GET /fabcut/cutting/parts/{ocNo}
    setTimeout(() => {
      setOcValid(true);
      setParts(MOCK_PARTS.map((p) => ({ ...p })));
      setLoading(false);
    }, 800);
  };

  const togglePart = (id: string) =>
    setParts((prev) =>
      prev.map((p) => (p.partsUnique === id ? { ...p, isSelected: !p.isSelected } : p))
    );

  const handleSubmit = () => {
    const selected = parts.filter((p) => p.isSelected);
    if (selected.length === 0) return;
    setLoading(true);
    // TODO: PUT /fabcut/cutting/parts/{ocNo}
    console.log('Submit parts:', selected.map((p) => p.partsUnique));
    setTimeout(() => setLoading(false), 800);
  };

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={loading} />

      <CustomHeader title="Bundle Parts" onBack={() => router.back()} />

      {/* Outer vertical layout — layout_gravity="center" in Java */}
      <View className="flex-1">

        {/* ── OC# row — marginTop 60dp, padding 10dp ─────────────────── */}
        <View
          className="flex-row items-center px-2.5"
          style={{ marginTop: 24, paddingHorizontal: 10 }}
        >
          {/* OC# EditText — fixed 250dp width */}
          <View style={{ width: 250 }}>
            <Input
              placeholder="Enter OC Number"
              value={ocNumber}
              onChangeText={(t) => { setOcNumber(t); setOcValid(null); }}
              autoCapitalize="characters"
              returnKeyType="search"
              onSubmitEditing={handleFetchParts}
            />
          </View>

          {/* Check icon — 50×50, padding 8dp, invisible until validated */}
          <View style={{ width: 50, height: 50, alignItems: 'center', justifyContent: 'center', padding: 8 }}>
            {ocValid !== null && (
              <Ionicons
                name={ocValid ? 'checkmark-circle' : 'close-circle'}
                size={32}
                color={ocValid ? '#16a34a' : '#dc2626'}
              />
            )}
          </View>
        </View>

        {/* ── Horizontal divider ──────────────────────────────────────── */}
        <View style={{ height: 1, backgroundColor: '#9e9e9e', marginTop: 8 }} />

        {/* ── CardView — margin 10dp, fixed height 513dp ─────────────── */}
        <View
          style={{
            margin: 10,
            height: 513,
            backgroundColor: '#fff',
            borderRadius: 6,
            elevation: 4,
            shadowColor: '#000',
            shadowOpacity: 0.1,
            shadowRadius: 4,
            overflow: 'hidden',
          }}
        >
          <FlatList
            data={parts}
            keyExtractor={(item) => item.partsUnique}
            refreshControl={
              <RefreshControl
                refreshing={refreshing}
                onRefresh={() => {
                  setRefreshing(true);
                  setTimeout(() => setRefreshing(false), 600);
                }}
              />
            }
            ListEmptyComponent={
              <View className="flex-1 items-center justify-center mt-20">
                <Text className="text-gray-400 text-sm">Enter OC number to load parts</Text>
              </View>
            }
            renderItem={({ item }) => (
              <PartRow
                part={item}
                onToggle={() => togglePart(item.partsUnique)}
              />
            )}
          />
        </View>

        {/* ── Submit button — centered, height 40dp ──────────────────── */}
        {parts.length > 0 && (
          <View className="items-center mt-1">
            <Pressable
              onPress={handleSubmit}
              style={{ height: 40, paddingHorizontal: 24 }}
              className="bg-primary rounded-lg items-center justify-center"
            >
              <Text className="text-white font-semibold text-sm">Submit</Text>
            </Pressable>
          </View>
        )}

      </View>
    </SafeAreaView>
  );
}

// ── Part row — mirrors bundle_parts_row_item.xml ──────────────────────────────
// Row: [part name 250dp, left-aligned] [Checkbox tint #21226b]
// marginH 24dp

function PartRow({
  part,
  onToggle,
}: {
  part: FetchPartsDetails;
  onToggle: () => void;
}) {
  return (
    <Pressable
      onPress={onToggle}
      className="active:bg-gray-50"
      style={{ paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#f3f4f6' }}
    >
      <View
        className="flex-row items-center"
        style={{ marginHorizontal: 24 }}
      >
        {/* Part name — 250dp, single line, ellipsize end, marginLeft 10dp */}
        <Text
          style={{ width: 250, marginLeft: 10 }}
          numberOfLines={1}
          ellipsizeMode="tail"
          className="text-sm text-gray-800"
        >
          {part.partName}
        </Text>

        {/* Checkbox — tint #21226b, marginLeft 6dp */}
        <View
          style={{
            marginLeft: 6,
            width: 22,
            height: 22,
            borderRadius: 3,
            borderWidth: 2,
            borderColor: part.isSelected ? '#21226b' : '#9e9e9e',
            backgroundColor: part.isSelected ? '#21226b' : '#fff',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {part.isSelected && (
            <Ionicons name="checkmark" size={13} color="#fff" />
          )}
        </View>
      </View>
    </Pressable>
  );
}
