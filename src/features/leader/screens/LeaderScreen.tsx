import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { FlatList, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomDropdown from '@/components/Common/CustomDropdown';
import CustomHeader from '@/components/Common/CustomHeader';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { useAppStore } from '@/store/appStore';
import { toast } from '@/lib/toast';
import type { Marker, OcLay } from '@/types/leader';
import type { OcFormProps } from '@/types/LeaderScreen';

import { EditBundleModal } from '../components/EditBundleModal';
import { FollowersBottomSheet } from '../components/FollowersBottomSheet';
import { MarkerCard } from '../components/MarkerCard';
import { useValidateOc } from '../hooks/useValidateOc';
import { useFetchMarkers } from '../hooks/useFetchMarkers';

type Step = 'form' | 'markers';

export default function LeaderScreen() {
  const router   = useRouter();
  const location = useAppStore((s) => s.location);

  // ── UI state ────────────────────────────────────────────────────────────────
  const [step, setStep]                 = useState<Step>('form');
  const [ocNumber, setOcNumber]         = useState('');
  const [ocTriggered, setOcTriggered]   = useState(false); // true only after input blur
  const [fitType, setFitType]           = useState('');
  const [markers, setMarkers]           = useState<Marker[]>([]);
  const [sheetOpen, setSheetOpen]             = useState(false);
  const [bundleVisible, setBundleVisible]     = useState(false);
  const [selectedJob, setSelectedJob]         = useState<OcLay | null>(null);


  const {
    data: fitTypes,
    isLoading: isValidatingOc,
    isError: isOcInvalid,
    isSuccess: isOcValid,
    error: ocError,
  } = useValidateOc(ocNumber.trim(), location, ocTriggered);

  // Auto-select first fit type when the list loads
  useEffect(() => {
    if (fitTypes && fitTypes.length > 0) {
      setFitType(fitTypes[0]);
    }
  }, [fitTypes]);

  // Clear fit type selection when OC is cleared or invalid
  useEffect(() => {
    if (!ocNumber.trim() || isOcInvalid) {
      setFitType('');
    }
  }, [ocNumber, isOcInvalid]);

  // ── Hook: fetch markers on Submit ───────────────────────────────────────────
  const {
    mutate: fetchMarkersMutate,
    isPending: isFetchingMarkers,
  } = useFetchMarkers();

  // ── Submit handler ──────────────────────────────────────────────────────────
  const handleSubmit = () => {
    if (!ocNumber.trim()) {
      toast.error('Please enter an OC number.', 'Missing OC');
      return;
    }
    // Still loading — don't show invalid yet
    if (isValidatingOc) {
      toast.info('Validating OC number, please wait...');
      return;
    }
    if (!isOcValid) {
      toast.apiError(ocError, 'Invalid OC', 'Please enter a valid OC number.');
      return;
    }
    if (!fitType) {
      toast.error('Please select a fit type.', 'Missing Fit Type');
      return;
    }

    fetchMarkersMutate(
      { ocNo: ocNumber.trim(), location: location!, fitType },
      {
        onSuccess: (data) => {
          if (!data || data.length === 0) {
            toast.info('No markers found for this OC and fit type.');
            return;
          }
          // Map MarkerResponse → Marker (attach ocNo, parse shrinkage to number)
          // Mirrors Java: new MarkerItem(marker.getMarkerUnique(), marker.getShrinkage(), marker.getItems(), ocNo)
          const mapped: Marker[] = data.map((m) => ({
            markerUnique: m.markerUnique,
            shrinkage: parseFloat(m.shrinkage) || 0,
            items: m.items,
            ocNumber: ocNumber.trim(),
          }));
          setMarkers(mapped);
          setStep('markers');
        },
        onError: (err: any) => {
          toast.apiError(err, 'Error', 'Failed to fetch markers.');
        },
      },
    );
  };

  // ── Back from markers → form ────────────────────────────────────────────────
  const handleBack = () => {
    setStep('form');
    setMarkers([]);
  };

  const handleMarkerPress = (marker: Marker) => {
    // Store in Zustand instead of URL params — Expo Router corrupts long strings
    // with special characters (underscores, slashes) in markerUnique
    useAppStore.getState().setSelectedMarker(marker as any);
    router.push('/leader/jobs');
  };

  const isLoading = isValidatingOc || isFetchingMarkers;

  // ── Render ──────────────────────────────────────────────────────────────────
  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={isLoading} />

      <CustomHeader
        title="FabCut"
        rightActions={[
          { icon: 'people-outline', onPress: () => setSheetOpen(true), label: 'Followers' },
        ]}
        overflowMenu={[
          { label: 'Select Bundle Parts', onPress: () => router.push('/leader/bundle-parts') },
        ]}
      />

      {step === 'form' ? (
        <OcForm
          ocNumber={ocNumber}
          setOcNumber={(v) => {
            setOcNumber(v);
            setOcTriggered(false); // reset trigger on every keystroke — mirrors Java clearing state on text change
            if (!v.trim()) setStep('form');
          }}
          onOcBlur={() => {
            // Mirrors Java: onFocusChange(hasFocus=false) → mPresenter.validateOcNumber(ocNo)
            if (ocNumber.trim()) setOcTriggered(true);
          }}
          fitType={fitType}
          setFitType={setFitType}
          fitTypes={fitTypes ?? []}
          ocValid={
            !ocNumber.trim()  ? null :
            isValidatingOc    ? null :   // still loading — show neither tick nor cross
            isOcValid         ? true :
            isOcInvalid       ? false :
            null
          }
          onSubmit={handleSubmit}
        />
      ) : (
        <MarkerList
          markers={markers}
          ocNumber={ocNumber}
          fitType={fitType}
          onPress={handleMarkerPress}
          onBack={handleBack}
        />
      )}

      {/* Bottom sheet — followers grid + assigned jobs */}
      <FollowersBottomSheet
        visible={sheetOpen}
        onClose={() => setSheetOpen(false)}
        onJobPress={(job) => {
          setSelectedJob(job);
          setSheetOpen(false);
          setBundleVisible(true);
        }}
      />

      {/* Bundle split modal */}
      <EditBundleModal
        visible={bundleVisible}
        ocNumber={selectedJob?.ocNo ?? ''}
        jobId={selectedJob?.jobId ?? ''}
        totalQuantity={100}
        onDismiss={() => { setBundleVisible(false); setSelectedJob(null); }}
        onSubmit={(rows) => {
          setBundleVisible(false);
          console.log('Bundle split:', rows);
        }}
      />
    </SafeAreaView>
  );
}

// ── OC Form ───────────────────────────────────────────────────────────────────

function OcForm({ ocNumber, setOcNumber, onOcBlur, fitType, setFitType, fitTypes, ocValid, onSubmit }: OcFormProps) {
  const validationIcon =
    ocValid === true  ? <Ionicons name="checkmark-circle" size={22} color="#16a34a" /> :
    ocValid === false ? <Ionicons name="close-circle"     size={22} color="#dc2626" /> :
    null;

  return (
    <View className="flex-1 justify-center px-8">
      <View
        className="bg-white rounded-xl"
        style={{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 8 }}
      >
        <View className="m-6">
          <Input
            placeholder="Enter OC Number"
            value={ocNumber}
            onChangeText={setOcNumber}
            onBlur={onOcBlur}
            autoCapitalize="characters"
            returnKeyType="done"
            onSubmitEditing={onSubmit}
            rightIcon={validationIcon}
          />

          <View className="mt-4">
            <CustomDropdown
              data={fitTypes}
              placeholder={fitTypes.length === 0 ? 'Enter a valid OC first' : 'Select Fit Type'}
              selectedValue={fitType}
              onSelect={setFitType}
              disabled={fitTypes.length === 0}
            />
          </View>

          <View className="items-center mt-6">
            <Button
              title="Submit"
              onPress={onSubmit}
              className="px-10"
              disabled={ocValid !== true || !fitType}
            />
          </View>
        </View>
      </View>
    </View>
  );
}

// ── Marker list ───────────────────────────────────────────────────────────────

interface MarkerListProps {
  markers: Marker[];
  ocNumber: string;
  fitType: string;
  onPress: (m: Marker) => void;
  onBack: () => void;
}

function MarkerList({ markers, ocNumber, fitType, onPress, onBack }: MarkerListProps) {
  return (
    <View className="flex-1">
      {/* Header row with back button + subtitle */}
      <View className="flex-row items-center px-4 pt-4 pb-1">
        <Ionicons
          name="arrow-back"
          size={22}
          color="#208AEF"
          onPress={onBack}
          style={{ marginRight: 8 }}
        />
        <View>
          <Text className="text-lg font-bold text-primary">Select Marker</Text>
          <Text className="text-xs text-gray-500">{ocNumber} · {fitType}</Text>
        </View>
      </View>

      <FlatList
        data={markers}
        keyExtractor={(item) => item.markerUnique}
        contentContainerStyle={{ padding: 16, paddingBottom: 100 }}
        renderItem={({ item }) => (
          <MarkerCard marker={item} onPress={() => onPress(item)} />
        )}
        ListEmptyComponent={
          <View className="items-center mt-20">
            <Text className="text-gray-400">No markers found.</Text>
          </View>
        }
      />
    </View>
  );
}
