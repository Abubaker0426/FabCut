/**
 * FollowerScreen — mirrors FollowerActivity + activity_follower.xml
 *
 * On mount:
 *  1. GET /fabcut/cutting/jobs/{deviceId}?location=  → normal job
 *     - jobId != null → show NORMAL tabs (one per itemCode)
 *     - jobId == null → try end-bit job
 *  2. GET /fabcut/cutting/end-bits/jobs/{deviceId}?location=
 *     - endBitJobId != null → show END_BIT tabs (one per partName)
 *     - both null → show "No job assigned" state
 */
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import React, { useCallback, useEffect, useState } from 'react';
import {  Alert,  Pressable,  RefreshControl,  ScrollView,  Text,  View,} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import type {
  FetchFollowerEndBitJobResponse,
  FetchFollowerJobResponse,
  ScanBarcode,
} from '@/types';
import { EndBitJobDetailTab } from '../components/EndBitJobDetailTab';
import { JobDetailTab } from '../components/JobDetailTab';

// ─── Mock data ────────────────────────────────────────────────────────────────
const MOCK_NORMAL_JOB: FetchFollowerJobResponse = {
  jobId: 'J001',
  ocNumber: 'OC2024001',
  layLength: 120,
  fitType: 'Regular',
  assignedQty: 100,
  cutQty: 60,
  numOfPlies: 24,
  actualPlies: 18,
  jobType: 'NORMAL',
  jobDetails: [
    {
      itemCode: 'IC-001',
      itemDesc: 'Men Shirt - Blue',
      layNumber: 3,
      ratioDetails: [
        { ratioNumber: 1, size: 'S',  ratioQty: 20, ratio: 1   },
        { ratioNumber: 1, size: 'M',  ratioQty: 40, ratio: 2   },
        { ratioNumber: 1, size: 'L',  ratioQty: 30, ratio: 1.5 },
        { ratioNumber: 1, size: 'XL', ratioQty: 10, ratio: 0.5 },
      ],
    },
    {
      itemCode: 'IC-002',
      itemDesc: 'Men Shirt - White',
      layNumber: 3,
      ratioDetails: [
        { ratioNumber: 1, size: 'S', ratioQty: 15, ratio: 1   },
        { ratioNumber: 1, size: 'M', ratioQty: 30, ratio: 2   },
        { ratioNumber: 1, size: 'L', ratioQty: 25, ratio: 1.5 },
      ],
    },
  ],
};

const MOCK_ENDBIT_JOB: FetchFollowerEndBitJobResponse = {
  endBitJobId: 'EB001',
  ocNumber: 'OC2024001',
  itemCode: 'IC-001',
  itemDesc: 'Men Shirt - Blue',
  assignedQty: 30,
  cutQty: 10,
  numOfPlies: 8,
  actualPlies: 6,
  jobDetails: [
    {
      partName: 'Front Panel',
      layLength: 60,
      ratioDetails: [
        { ratioNumber: 1, size: 'S', ratioQty: 10, ratio: 1 },
        { ratioNumber: 1, size: 'M', ratioQty: 15, ratio: 1 },
      ],
    },
    {
      partName: 'Back Panel',
      layLength: 58,
      ratioDetails: [
        { ratioNumber: 1, size: 'S', ratioQty: 10, ratio: 1 },
        { ratioNumber: 1, size: 'M', ratioQty: 15, ratio: 1 },
      ],
    },
  ],
};

const MOCK_BARCODES: ScanBarcode[] = [
  { barcode: 'BC-00001', jobId: 'J001', expectedPlies: 24, actualPlies: 0,  itemCode: 'IC-001', validated: false },
  { barcode: 'BC-00002', jobId: 'J001', expectedPlies: 24, actualPlies: 22, itemCode: 'IC-001', validated: true  },
];

// Change to 'END_BIT' or 'NONE' to test those states
const DEMO_JOB_TYPE: 'NORMAL' | 'END_BIT' | 'NONE' = 'NORMAL';

type JobState = 'loading' | 'normal' | 'endbit' | 'none';

export default function FollowerScreen() {
  const router = useRouter();

  const [jobState, setJobState]     = useState<JobState>('loading');
  const [activeTab, setActiveTab]   = useState(0);
  const [normalJob, setNormalJob]   = useState<FetchFollowerJobResponse | null>(null);
  const [endBitJob, setEndBitJob]   = useState<FetchFollowerEndBitJobResponse | null>(null);
  const [barcodes, setBarcodes]     = useState<ScanBarcode[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [loading, setLoading]       = useState(false);

  const fetchJob = useCallback(async () => {
    setLoading(true);
    setActiveTab(0);
    await new Promise((r) => setTimeout(r, 800));

    if (DEMO_JOB_TYPE === 'NORMAL') {
      setNormalJob(MOCK_NORMAL_JOB);
      setEndBitJob(null);
      setBarcodes(MOCK_BARCODES);
      setJobState('normal');
    } else if (DEMO_JOB_TYPE === 'END_BIT') {
      setNormalJob(null);
      setEndBitJob(MOCK_ENDBIT_JOB);
      setBarcodes([]);
      setJobState('endbit');
    } else {
      setNormalJob(null);
      setEndBitJob(null);
      setJobState('none');
    }

    setLoading(false);
  }, []);

  useEffect(() => { fetchJob(); }, [fetchJob]);

  const onRefresh = useCallback(() => {
    setRefreshing(true);
    fetchJob().finally(() => setRefreshing(false));
  }, [fetchJob]);

  const handleActualPliesChange = (barcode: string, value: string) => {
    setBarcodes((prev) =>
      prev.map((b) =>
        b.barcode === barcode ? { ...b, actualPlies: Number(value) || 0 } : b
      )
    );
  };

  const handleSubmit = () => {
    const unvalidated = barcodes.filter((b) => !b.validated);
    if (unvalidated.length > 0) {
      Alert.alert('Error', 'Please validate all actual plies before submitting.');
      return;
    }
    const cutSum = barcodes.reduce((s, b) => s + b.actualPlies, 0);
    if (cutSum <= 0) {
      Alert.alert('Error', 'Cut quantity cannot be zero.');
      return;
    }
    Alert.alert('Confirm Submit', 'Are you sure you want to complete this job?', [
      { text: 'No', style: 'cancel' },
      {
        text: 'Yes',
        onPress: () => {
          setLoading(true);
          // TODO: PUT /fabcut/cutting/jobs/{jobId} or PUT /end-bits/jobs/{jobId}
          setTimeout(() => {
            setLoading(false);
            Alert.alert('Success', 'Job completed successfully!');
            fetchJob();
          }, 1000);
        },
      },
    ]);
  };

  // Tab metadata
  const normalTabs = normalJob?.jobDetails  ?? [];
  const endBitTabs = endBitJob?.jobDetails  ?? [];
  const tabCount   = jobState === 'normal' ? normalTabs.length : endBitTabs.length;
  const tabLabel   = (i: number) =>
    jobState === 'normal'
      ? (normalTabs[i]?.itemCode ?? '')
      : (endBitTabs[i]?.partName ?? '');

  if (jobState === 'none') {
    return (
      <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
        <CustomHeader title="FabCut" onLogout={() => router.replace('/')} />
        <View className="flex-1 items-center justify-center gap-4">
          <Ionicons name="briefcase-outline" size={64} color="#21226b" />
          <Text className="text-primary text-base text-center">No job assigned yet</Text>
          <Pressable onPress={onRefresh} className="px-6 py-3 border-2 border-primary rounded-xl">
            <Text className="text-primary font-semibold">Refresh</Text>
          </Pressable>
        </View>
      </SafeAreaView>
    );
  }
  const activeTabContent = (() => {
    if (jobState === 'normal' && normalJob && normalTabs[activeTab]) {
      return (
        <JobDetailTab
          jobDetail={normalTabs[activeTab]}
          ocNumber={normalJob.ocNumber}
          layLength={normalJob.layLength}
          assignedQty={normalJob.assignedQty}
          cutQty={normalJob.cutQty}
          numOfPlies={normalJob.numOfPlies}
          actualPlies={normalJob.actualPlies}
          scannedBarcodes={barcodes.filter(
            (b) => b.itemCode === normalTabs[activeTab].itemCode
          )}
          onActualPliesChange={handleActualPliesChange}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        />
      );
    }

    if (jobState === 'endbit' && endBitJob && endBitTabs[activeTab]) {
      return (
        <EndBitJobDetailTab
          jobDetail={endBitTabs[activeTab]}
          ocNumber={endBitJob.ocNumber}
          itemCode={endBitJob.itemCode}
          itemDesc={endBitJob.itemDesc}
          assignedQty={endBitJob.assignedQty}
          cutQty={endBitJob.cutQty}
          numOfPlies={endBitJob.numOfPlies}
          actualPlies={endBitJob.actualPlies}
          scannedBarcodes={barcodes.filter(
            (b) => b.itemCode === endBitJob.itemCode
          )}
          onActualPliesChange={handleActualPliesChange}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        />
      );
    }

    return null;
  })();

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={loading || jobState === 'loading'} message="Loading job..." />
      <CustomHeader
        title="FabCut"
        badge={jobState === 'endbit' ? 'END BIT' : undefined}
        onLogout={() => router.replace('/')}
      />
      {/* Main content — single node, no overlap possible */}
      <View className="flex-1">
        {activeTabContent}

        {/* Submit FAB — bottom|end, mirrors layout_gravity="bottom|end" */}
        {jobState !== 'loading' && (
          <View className="absolute bottom-6 right-6">
            <Pressable
              onPress={handleSubmit}
              className="bg-white rounded-full px-5 py-3 flex-row items-center gap-2"
              style={{ elevation: 6, shadowColor: '#000', shadowOpacity: 0.12, shadowRadius: 8 }}
            >
              <Ionicons name="checkmark-done-outline" size={20} color="#21226b" />
              <Text className="text-primary font-semibold text-base">Submit</Text>
            </Pressable>
          </View>
        )}
      </View>

      {/* Tab bar — bottom, scrollable, mirrors TabLayout */}
      {tabCount > 0 && (
        <View className="bg-primary">
          <ScrollView
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={{ flexGrow: 1 }}
          >
            {Array.from({ length: tabCount }).map((_, i) => (
              <Pressable
                key={i}
                onPress={() => setActiveTab(i)}
                className={`px-4 py-3 border-b-2 ${
                  activeTab === i ? 'border-white' : 'border-transparent'
                }`}
              >
                <Text
                  className={`text-sm font-medium ${ activeTab === i ? 'text-white' : 'text-white/60' }`}>
                  {tabLabel(i)}
                </Text>
              </Pressable>
            ))}
          </ScrollView>
        </View>
      )}
    </SafeAreaView>
  );
}


