import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import React, { useEffect, useState } from 'react';
import { FlatList, Pressable, RefreshControl, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { useAppStore } from '@/store/appStore';
import { toast } from '@/lib/toast';
import type { FetchPartsDetails } from '@/types/leader';

import { useFetchPartsForOC } from '../hooks/useFetchPartsForOC';
import { useUpdateBundleParts } from '../hooks/useUpdateBundleParts';

export default function BundlePartsScreen() {
  const router   = useRouter();
  const location = useAppStore((s) => s.location);

  const [ocNumber, setOcNumber]         = useState('');
  const [parts, setParts]               = useState<FetchPartsDetails[]>([]);
  const [submittedOc, setSubmittedOc]   = useState(''); // the OC that was actually queried

  // ── Hook: fetch parts on OC blur ──────────────────────────────────────────
  // Mirrors Java: BundlePartsActivity.onFocusChange(hasFocus=false)
  //   → mPresenter.fetchParts(ocNo)
  //   → doFetchPartsForOCCall(ocNo, LocationRequest)
  const {
    data: partsData,
    isLoading: isFetching,
    isError: isFetchError,
    isSuccess: isFetchSuccess,
    refetch,
    isRefetching,
  } = useFetchPartsForOC(submittedOc, location, !!submittedOc);

  // Sync API response into local parts state so toggles work locally
  // Mirrors Java: updateRecyclerView(parts) — sets adapter data once on fetch
  useEffect(() => {
    if (partsData?.parts) {
      setParts(partsData.parts.map((p) => ({ ...p })));
    }
  }, [partsData]);

  // ── Hook: save parts on Submit ────────────────────────────────────────────
  // Mirrors Java: BundlePartsPresenter.submitParts(ocNo, parts)
  //   → doPutSelectedBundleParts(ocNo, request)
  //   success → finish()
  const { mutate: saveParts, isPending: isSaving } = useUpdateBundleParts();

  // ── OC blur handler ────────────────────────────────────────────────────────
  // Mirrors Java: onFocusChange(hasFocus=false) → validateOcNumberAndGetParts(ocNo)
  const handleOcBlur = () => {
    const oc = ocNumber.trim();
    if (!oc) {
      // Mirrors Java: TextUtils.isEmpty → show red X + error
      toast.error('Please enter an OC number.', 'Error');
      return;
    }
    setParts([]);
    setSubmittedOc(oc);  // triggers the query with the exact OC typed
  };

  const togglePart = (id: number) =>
    setParts((prev) =>
      prev.map((p) => (p.partsUnique === id ? { ...p, isSelected: p.isSelected === 1 ? 0 : 1 } : p)),
    );

  // ── Submit ─────────────────────────────────────────────────────────────────
  // Mirrors Java: submit button click → validate → doPutSelectedBundleParts
  const handleSubmit = () => {
    const selected = parts.filter((p) => p.isSelected === 1);
    if (selected.length === 0) {
      toast.error('Please select at least one part.', 'Validation Error');
      return;
    }

    saveParts(
      { ocNo: ocNumber.trim(), parts },
      {
        onSuccess: () => {
          toast.success('Parts saved successfully.');
          router.back();  // mirrors Java: finish()
        },
        onError: (err: any) => {
          toast.apiError(err, 'Save Failed');
        },
      },
    );
  };

  // Green tick = success, Red X = error (mirrors Java validateIcon)
  const ocValid = submittedOc
    ? isFetchSuccess
      ? true
      : isFetchError
        ? false
        : null
    : null;

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={isFetching || isSaving} />

      <CustomHeader title="Bundle Parts" onBack={() => router.back()} />

      <View className="flex-1">

        {/* ── OC# input row ─────────────────────────────────────────── */}
        <View
          className="flex-row items-center px-2.5"
          style={{ marginTop: 24, paddingHorizontal: 10 }}
        >
          <View style={{ width: 250 }}>
            <Input
              placeholder="Enter OC Number"
              value={ocNumber}
              onChangeText={(t) => {
                setOcNumber(t);
                setSubmittedOc('');
                setParts([]);
              }}
              onBlur={handleOcBlur}
              autoCapitalize="characters"
              returnKeyType="search"
              onSubmitEditing={handleOcBlur}
            />
          </View>

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

        {/* ── Divider ───────────────────────────────────────────────── */}
        <View style={{ height: 1, backgroundColor: '#9e9e9e', marginTop: 8 }} />

        {/* ── Parts list card ───────────────────────────────────────── */}
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
            keyExtractor={(item) => String(item.partsUnique)}
            refreshControl={
              <RefreshControl
                refreshing={isRefetching}
                onRefresh={() => {
                  // Mirrors Java: refreshRecycleView → validateOcNumberAndGetParts(ocNumber)
                  if (submittedOc) refetch();
                }}
              />
            }
            ListEmptyComponent={
              <View className="flex-1 items-center justify-center mt-20">
                <Text className="text-gray-400 text-sm">
                  {isFetching ? 'Loading parts...' : 'Enter OC number to load parts'}
                </Text>
              </View>
            }
            renderItem={({ item }) => (
              <PartRow part={item} onToggle={() => togglePart(item.partsUnique)} />
            )}
          />
        </View>

        {/* ── Submit button ─────────────────────────────────────────── */}
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

function PartRow({ part, onToggle }: { part: FetchPartsDetails; onToggle: () => void }) {
  return (
    <Pressable
      onPress={onToggle}
      className="active:bg-gray-50"
      style={{ paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#f3f4f6' }}
    >
      <View className="flex-row items-center" style={{ marginHorizontal: 24 }}>
        <Text
          style={{ width: 250, marginLeft: 10 }}
          numberOfLines={1}
          ellipsizeMode="tail"
          className="text-sm text-gray-800"
        >
          {part.part}
        </Text>

        <View
          style={{
            marginLeft: 6,
            width: 22,
            height: 22,
            borderRadius: 3,
            borderWidth: 2,
            borderColor: part.isSelected === 1 ? '#21226b' : '#9e9e9e',
            backgroundColor: part.isSelected === 1 ? '#21226b' : '#fff',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {part.isSelected === 1 && (
            <Ionicons name="checkmark" size={13} color="#fff" />
          )}
        </View>
      </View>
    </Pressable>
  );
}
