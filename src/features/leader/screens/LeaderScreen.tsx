/**
 * LeaderScreen
 * Mirrors: LeaderActivity + activity_leader.xml
 *
 * Step 1 — Form: OC# input + Fit Type spinner → Submit
 * Step 2 — Markers: list → tap to go to LeaderJobsScreen
 *
 * Bottom sheet (peeks at bottom):
 *  Tab 1 — Followers grid → tap follower → FollowerDetailsScreen
 *  Tab 2 — Assigned Jobs → tap job → EditBundleModal
 */
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomDropdown from '@/components/Common/CustomDropdown';
import CustomHeader from '@/components/Common/CustomHeader';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import type { Marker, OcLay } from '@/types';
import { EditBundleModal } from '../components/EditBundleModal';
import { FollowersBottomSheet } from '../components/FollowersBottomSheet';
import { MarkerCard } from '../components/MarkerCard';
import { MOCK_FIT_TYPES, MOCK_MARKERS } from '../constants/leaderMockData';

type Step = 'form' | 'markers';

export default function LeaderScreen() {
  const router = useRouter();

  const [step, setStep]           = useState<Step>('form');
  const [ocNumber, setOcNumber]   = useState('');
  const [fitType, setFitType]     = useState(MOCK_FIT_TYPES[0]);
  const [ocValid, setOcValid]     = useState<boolean | null>(null);
  const [markers, setMarkers]     = useState<Marker[]>([]);
  const [loading, setLoading]     = useState(false);
  const [sheetOpen, setSheetOpen] = useState(false);
  const [bundleVisible, setBundleVisible] = useState(false);
  const [selectedJob, setSelectedJob]     = useState<OcLay | null>(null);

  const handleSubmit = () => {
    if (!ocNumber.trim()) return;
    setLoading(true);
    // TODO: GET /ratios/{ocNo}/fitTypes → validate OC → GET /ratios/markers/{ocNo}
    setTimeout(() => {
      setOcValid(true);
      setMarkers(MOCK_MARKERS);
      setStep('markers');
      setLoading(false);
    }, 1000);
  };

  const handleMarkerPress = (marker: Marker) => {
    router.push({
      pathname: '/leader/jobs',
      params: { markerJson: JSON.stringify(marker) },
    });
  };

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={loading} />

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
          setOcNumber={(v) => { setOcNumber(v); setOcValid(null); }}
          fitType={fitType}
          setFitType={setFitType}
          fitTypes={MOCK_FIT_TYPES}
          ocValid={ocValid}
          onSubmit={handleSubmit}
        />
      ) : (
        <MarkerList markers={markers} onPress={handleMarkerPress} />
      )}

      {/* Bottom sheet — followers grid + assigned jobs */}
      <FollowersBottomSheet
        visible={sheetOpen}
        onClose={() => setSheetOpen(false)}
        onFollowerPress={(follower) => {
          // Mirrors Java: onItemClickListener(Follower) → fetchFollowerJob → openFollowerDetailActivity
          setSheetOpen(false);
          router.push({
            pathname: '/leader/follower-details',
            params: { deviceId: follower.deviceId },
          });
        }}
        onJobPress={(job) => {
          // Mirrors Java: bundleSplitDialog → getCountriesList → showBundleSplitDialog
          setSelectedJob(job);
          setSheetOpen(false);
          setBundleVisible(true);
        }}
      />

      {/* Bundle split modal — opened when a job is tapped in bottom sheet */}
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

// ── OC# Form ──────────────────────────────────────────────────────────────────

interface OcFormProps {
  ocNumber: string;
  setOcNumber: (v: string) => void;
  fitType: string;
  setFitType: (v: string) => void;
  fitTypes: string[];
  ocValid: boolean | null;
  onSubmit: () => void;
}

function OcForm({ ocNumber, setOcNumber, fitType, setFitType, fitTypes, ocValid, onSubmit }: OcFormProps) {
  return (
    <View className="flex-1 justify-center px-8">
      {/* Card — mirrors CardView, margin 16dp */}
      <View
        className="bg-white rounded-xl"
        style={{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 8 }}
      >
        <View className="m-6">
          {/* Horizontal row: inputs + check icon */}
          <View className="flex-row items-start">

            {/* Inputs column — marginLeft 48dp */}
            <View className="flex-1 ml-12">
              <Input
                placeholder="Enter OC Number"
                value={ocNumber}
                onChangeText={setOcNumber}
                autoCapitalize="characters"
                returnKeyType="done"
                onSubmitEditing={onSubmit}
              />
              
              <View className="mt-4">
                <CustomDropdown
                  data={fitTypes}
                  placeholder="Select Fit Type"
                  selectedValue={fitType}
                  onSelect={setFitType}
                />
              </View>
            </View>

            {/* Feedback icon — 32×32, marginTop 12dp */}
            <View className="w-8 h-8 items-center justify-center mt-3 mx-2">
              {ocValid !== null && (
                <Ionicons
                  name={ocValid ? 'checkmark-circle' : 'close-circle'}
                  size={28}
                  color={ocValid ? '#16a34a' : '#dc2626'}
                />
              )}
            </View>
          </View>

          {/* Submit — centered, marginTop 16dp */}
          <View className="items-center mt-4">
            <Button title="Submit" onPress={onSubmit} className="px-10" />
          </View>
        </View>
      </View>
    </View>
  );
}

// ── Marker list ───────────────────────────────────────────────────────────────

function MarkerList({ markers, onPress }: { markers: Marker[]; onPress: (m: Marker) => void }) {
  return (
    <View className="flex-1">
      <Text className="text-center text-xl font-bold text-primary mt-4 mb-2">
        Select Marker
      </Text>
      <FlatList
        data={markers}
        keyExtractor={(item) => item.markerUnique}
        contentContainerStyle={{ padding: 16, paddingBottom: 100 }}
        renderItem={({ item }) => (
          <MarkerCard marker={item} onPress={() => onPress(item)} />
        )}
      />
    </View>
  );
}
