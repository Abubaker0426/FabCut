import { Ionicons } from '@expo/vector-icons';
import React, { useEffect, useState } from 'react';
import { Alert, Pressable, RefreshControl, ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import CustomHeader from '@/components/Common/CustomHeader';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { getDeviceId } from '@/lib/deviceId';
import { toast } from '@/lib/toast';
import { useAppStore } from '@/store/appStore';
import type {
  FetchFollowerEndBitJobResponse,
  FetchFollowerJobResponse,
  ScanBarcode,
} from '@/types/follower';

import { EndBitJobDetailTab } from '../components/EndBitJobDetailTab';
import { JobDetailTab } from '../components/JobDetailTab';
import RefreshScreen from '../components/RefreshScreen';
import { useGetMyJob } from '../hooks/useGetMyJob';
import { useScanBarcode } from '../hooks/useScanBarcode';
import { useValidatePlies, useDeleteBarcode } from '../hooks/useBarcodeActions';
import { useCompleteJob } from '../hooks/useCompleteJob';
import {
  useEndBitScanBarcode,
  useEndBitValidatePlies,
  useEndBitDeleteBarcode,
  useCompleteEndBitJob,
} from '../hooks/useEndBitBarcodeActions';

type ValidationMap = Record<string, 'idle' | 'validating' | 'valid' | 'error'>;

export default function FollowerScreen() {
  const deviceId = useAppStore((s) => s.deviceId) ?? '';
  const location = useAppStore((s) => s.location) ?? '';
  const lastScannedCode = useAppStore((s) => s.lastScannedCode);

  const [activeTab, setActiveTab]           = useState(0);
  const [barcodes, setBarcodes]             = useState<ScanBarcode[]>([]);
  const [showRefresh, setShowRefresh]       = useState(false);
  const [validationStates, setValidationStates] = useState<ValidationMap>({});

  // ── Normal job hooks ───────────────────────────────────────────────────────
  const { mutate: submitScan,        isPending: isScanning }   = useScanBarcode();
  const { mutate: submitValidate }                              = useValidatePlies();
  const { mutate: submitDelete }                                = useDeleteBarcode();
  const { mutate: submitCompleteJob, isPending: isCompleting } = useCompleteJob();

  // ── End-bit job hooks ──────────────────────────────────────────────────────
  const { mutate: submitEndBitScan,     isPending: isEndBitScanning }   = useEndBitScanBarcode();
  const { mutate: submitEndBitValidate }                                 = useEndBitValidatePlies();
  const { mutate: submitEndBitDelete }                                   = useEndBitDeleteBarcode();
  const { mutate: submitCompleteEndBit, isPending: isEndBitCompleting }  = useCompleteEndBitJob();

  // ── Ensure deviceId is loaded ──────────────────────────────────────────────
  useEffect(() => {
    if (!useAppStore.getState().deviceId) {
      getDeviceId().then((id) => {
        if (id) useAppStore.getState().setDeviceId(id);
      });
    }
  }, []);

  // ── Fetch job on mount + refetch on pull-to-refresh ────────────────────────
  const {
    data: jobResult,
    isLoading:   isJobLoading,
    isRefetching,
    refetch,
    error,
  } = useGetMyJob(deviceId, location);

  // ── Derived job state ──────────────────────────────────────────────────────
  const jobType   = jobResult?.type ?? 'NONE';
  const normalJob = jobResult?.type === 'NORMAL'  ? jobResult.data as FetchFollowerJobResponse  : null;
  const endBitJob = jobResult?.type === 'END_BIT' ? jobResult.data as FetchFollowerEndBitJobResponse : null;

  // ── Error toast ────────────────────────────────────────────────────────────
  React.useEffect(() => {
    if (error) toast.apiError(error, 'Error', 'Failed to load job.');
  }, [error]);

  // ── Reset on job change ────────────────────────────────────────────────────
  React.useEffect(() => {
    setActiveTab(0);
    setBarcodes([]);
    setValidationStates({});
  }, [jobResult]);

  // ── Watch scanner result ───────────────────────────────────────────────────
  // Fires when ScannerRoute stores a code and calls router.back()
  React.useEffect(() => {
    if (!lastScannedCode) return;
    useAppStore.getState().setLastScannedCode(null);

    if (normalJob) {
      handleNormalScan(lastScannedCode);
    } else if (endBitJob) {
      handleEndBitScan(lastScannedCode);
    }
  }, [lastScannedCode]);

  const onRefresh = () => { refetch(); };

  // ── Shared actual plies change ─────────────────────────────────────────────
  const handleActualPliesChange = (barcode: string, value: string) => {
    setBarcodes((prev) =>
      prev.map((b) => b.barcode === barcode ? { ...b, actualPlies: Number(value) || 0 } : b),
    );
  };

  // ════════════════════════════════════════════════════════════════════════════
  // NORMAL JOB HANDLERS
  // ════════════════════════════════════════════════════════════════════════════

  const handleNormalScan = (scannedCode: string) => {
    if (!normalJob) return;
    const currentTab = normalJob.jobDetails[activeTab];
    if (!currentTab) return;

    submitScan(
      { jobId: String(normalJob.jobId), barcode: scannedCode, itemCode: currentTab.itemCode },
      {
        onSuccess: (res) => {
          setBarcodes((prev) => [...prev, {
            jobId:         String(normalJob.jobId),
            barcode:       scannedCode,
            itemCode:      currentTab.itemCode,
            expectedPlies: res.plies,
            actualPlies:   0,
            validated:     false,
          }]);
        },
        onError: (err: any) => toast.apiError(err, 'Scan Failed'),
      },
    );
  };

  const handleNormalValidate = (barcode: string, actualPlies: number) => {
    if (!normalJob) return;
    setValidationStates((prev) => ({ ...prev, [barcode]: 'validating' }));
    submitValidate(
      { jobId: String(normalJob.jobId), barcode, data: { actualPlies } },
      {
        onSuccess: () => {
          setValidationStates((prev) => ({ ...prev, [barcode]: 'valid' }));
          setBarcodes((prev) => prev.map((b) => b.barcode === barcode ? { ...b, validated: true } : b));
        },
        onError: (err: any) => {
          setValidationStates((prev) => ({ ...prev, [barcode]: 'error' }));
          setBarcodes((prev) => prev.map((b) => b.barcode === barcode ? { ...b, validated: false } : b));
          toast.apiError(err, 'Validation Failed');
        },
      },
    );
  };

  const handleNormalDelete = (barcode: string) => {
    if (!normalJob) return;
    submitDelete(
      { jobId: String(normalJob.jobId), barcode },
      {
        onSuccess: () => {
          setBarcodes((prev) => prev.filter((b) => b.barcode !== barcode));
          setValidationStates((prev) => { const n = { ...prev }; delete n[barcode]; return n; });
        },
        onError: (err: any) => toast.apiError(err, 'Delete Failed'),
      },
    );
  };

  // ════════════════════════════════════════════════════════════════════════════
  // END-BIT JOB HANDLERS
  // ════════════════════════════════════════════════════════════════════════════

  // Current tab's partName — needed as query param for all end-bit barcode ops
  const currentPartName = endBitJob?.jobDetails[activeTab]?.partName ?? '';

  const handleEndBitScan = (scannedCode: string) => {
    if (!endBitJob || !deviceId || !location) return;

    submitEndBitScan(
      {
        jobId:    String(endBitJob.endBitJobId),
        barcode:  scannedCode,
        partName: currentPartName,
        data:     { deviceId, location },  // EndBitJobRequest
      },
      {
        onSuccess: (res) => {
          setBarcodes((prev) => [...prev, {
            jobId:         String(endBitJob.endBitJobId),
            barcode:       scannedCode,
            itemCode:      currentPartName,  // keyed by partName for end-bit
            expectedPlies: res.plies,
            actualPlies:   0,
            validated:     false,
          }]);
        },
        onError: (err: any) => toast.apiError(err, 'Scan Failed'),
      },
    );
  };

  const handleEndBitValidate = (barcode: string, actualPlies: number) => {
    if (!endBitJob || !deviceId || !location) return;
    setValidationStates((prev) => ({ ...prev, [barcode]: 'validating' }));

    submitEndBitValidate(
      {
        jobId:    String(endBitJob.endBitJobId),
        barcode,
        partName: currentPartName,
        data:     { actualPlies, deviceId, location },  // all 3 fields required for end-bit
      },
      {
        onSuccess: () => {
          setValidationStates((prev) => ({ ...prev, [barcode]: 'valid' }));
          setBarcodes((prev) => prev.map((b) => b.barcode === barcode ? { ...b, validated: true } : b));
        },
        onError: (err: any) => {
          setValidationStates((prev) => ({ ...prev, [barcode]: 'error' }));
          setBarcodes((prev) => prev.map((b) => b.barcode === barcode ? { ...b, validated: false } : b));
          toast.apiError(err, 'Validation Failed');
        },
      },
    );
  };

  const handleEndBitDelete = (barcode: string) => {
    if (!endBitJob || !deviceId || !location) return;
    submitEndBitDelete(
      {
        jobId:    String(endBitJob.endBitJobId),
        barcode,
        partName: currentPartName,
        data:     { deviceId, location },
      },
      {
        onSuccess: () => {
          setBarcodes((prev) => prev.filter((b) => b.barcode !== barcode));
          setValidationStates((prev) => { const n = { ...prev }; delete n[barcode]; return n; });
        },
        onError: (err: any) => toast.apiError(err, 'Delete Failed'),
      },
    );
  };

  // ════════════════════════════════════════════════════════════════════════════
  // SUBMIT — handles both normal and end-bit
  // Mirrors Java: FollowerPresenter.validatedAllPlies() → completeJob/completeEndBitJob
  // ════════════════════════════════════════════════════════════════════════════

  const handleSubmit = () => {
    const unvalidated = barcodes.filter((b) => !b.validated);
    if (unvalidated.length > 0) {
      toast.error('Please validate all actual plies before submitting.', 'Error');
      return;
    }
    const cutSum   = barcodes.reduce((s, b) => s + b.actualPlies, 0);
    const assigned = (normalJob?.assignedQty ?? endBitJob?.assignedQty) ?? 0;
    if (cutSum <= 0) {
      toast.error('Cut quantity cannot be zero.', 'Error');
      return;
    }

    Alert.alert('Confirm Submit', 'Are you sure you want to complete this job?', [
      { text: 'No', style: 'cancel' },
      {
        text: 'Yes',
        onPress: () => {
          if (cutSum < assigned) {
            Alert.alert(
              'Warning',
              'The cut quantity is less than the assigned quantity. Do you still want to submit?',
              [
                { text: 'No', style: 'cancel' },
                { text: 'Yes', onPress: () => doComplete() },
              ],
            );
          } else {
            doComplete();
          }
        },
      },
    ]);
  };

  const doComplete = () => {
    const onSuccess = () => {
      setBarcodes([]);
      setValidationStates({});
      toast.success('Job completed successfully!');
      refetch();
    };
    const onError = (err: any) => toast.apiError(err, 'Submit Failed');

    if (normalJob) {
      // PUT /jobs/{jobId} — no body
      submitCompleteJob(
        { jobId: String(normalJob.jobId) },
        { onSuccess, onError },
      );
    } else if (endBitJob && deviceId && location) {
      // PUT /end-bits/jobs/{jobId} — body: { deviceId, location }
      submitCompleteEndBit(
        { jobId: String(endBitJob.endBitJobId), data: { deviceId, location } },
        { onSuccess, onError },
      );
    }
  };

  // ── No job / back → RefreshScreen ─────────────────────────────────────────
  if ((!isJobLoading && jobType === 'NONE') || showRefresh) {
    return (
      <RefreshScreen
        onRefresh={() => { setShowRefresh(false); refetch(); }}
        isLoading={isRefetching}
      />
    );
  }

  // ── Tab metadata ───────────────────────────────────────────────────────────
  const normalTabs = normalJob?.jobDetails ?? [];
  const endBitTabs = endBitJob?.jobDetails ?? [];
  const tabCount   = jobType === 'NORMAL' ? normalTabs.length : endBitTabs.length;
  const tabLabel   = (i: number) =>
    jobType === 'NORMAL' ? (normalTabs[i]?.itemCode ?? '') : (endBitTabs[i]?.partName ?? '');

  // ── Active tab content ─────────────────────────────────────────────────────
  const activeTabContent = (() => {
    if (jobType === 'NORMAL' && normalJob && normalTabs[activeTab]) {
      return (
        <JobDetailTab
          jobDetail={normalTabs[activeTab]}
          ocNumber={normalJob.ocNumber}
          layLength={normalJob.layLength}
          assignedQty={normalJob.assignedQty ?? 0}
          cutQty={normalJob.cutQty ?? 0}
          numOfPlies={normalJob.numOfPlies ?? 0}
          actualPlies={normalJob.actualPlies ?? 0}
          scannedBarcodes={barcodes.filter((b) => b.itemCode === normalTabs[activeTab].itemCode)}
          validationStates={validationStates}
          onActualPliesChange={handleActualPliesChange}
          onValidate={handleNormalValidate}
          onDelete={handleNormalDelete}
          refreshControl={<RefreshControl refreshing={isRefetching} onRefresh={onRefresh} />}
        />
      );
    }

    if (jobType === 'END_BIT' && endBitJob && endBitTabs[activeTab]) {
      const partName = endBitTabs[activeTab].partName;
      return (
        <EndBitJobDetailTab
          jobDetail={endBitTabs[activeTab]}
          ocNumber={endBitJob.ocNumber}
          itemCode={endBitJob.itemCode}
          itemDesc={endBitJob.itemDesc}
          assignedQty={endBitJob.assignedQty ?? 0}
          cutQty={endBitJob.cutQty ?? 0}
          numOfPlies={endBitJob.numOfPlies ?? 0}
          actualPlies={endBitJob.actualPlies ?? 0}
          scannedBarcodes={barcodes.filter((b) => b.itemCode === partName)}
          validationStates={validationStates}
          onActualPliesChange={handleActualPliesChange}
          onValidate={handleEndBitValidate}
          onDelete={handleEndBitDelete}
          refreshControl={<RefreshControl refreshing={isRefetching} onRefresh={onRefresh} />}
        />
      );
    }

    return null;
  })();

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <LoadingOverlay visible={isJobLoading || isCompleting || isScanning || isEndBitScanning || isEndBitCompleting} />

      <CustomHeader
        title="FabCut"
        badge={jobType === 'END_BIT' ? 'END BIT' : undefined}
        onBack={() => setShowRefresh(true)}
      />

      <View className="flex-1">
        {activeTabContent}

        {/* Submit FAB */}
        {!isJobLoading && jobType !== 'NONE' && (
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

      {/* Tab bar */}
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
                className={`px-4 py-3 border-b-2 ${activeTab === i ? 'border-white' : 'border-transparent'}`}
              >
                <Text className={`text-sm font-medium ${activeTab === i ? 'text-white' : 'text-white/60'}`}>
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
