import React, { useEffect, useState } from 'react';
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { useAppStore } from '@/store/appStore';
import type { Role } from '@/types/common';
import { LocationPickerModal } from '../components/LocationPickerModal';
import { useDeviceId } from '../hooks/useDeviceId';
import { useLocationValidation } from '../hooks/useLocationValidation';
import { useCheckDeviceRegistration } from '../hooks/useCheckDeviceRegistration';
import { useRegisterDevice } from '../hooks/useRegisterDevice';
import { toast } from '@/lib/toast';
import { RegisterFormProps } from '@/types/auth';

type Screen = 'loading' | 'location' | 'register';

export default function MainScreen() {
  const router = useRouter();

  const [screen, setScreen] = useState<Screen>('loading');
  const [role, setRole] = useState<Role>('LEADER');
  const [tableNumber, setTableNumber] = useState('');
  const [locationModalVisible, setLocationModalVisible] = useState(false);
  const [locations, setLocations] = useState<string[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<string | null>(null);

  // ── Hooks ──────────────────────────────────────────────────────────────────

  // Loads deviceId from device + stores in Zustand — replaces manual getDeviceId()
  const deviceId = useDeviceId();

  // GPS + location API combined — replaces useGpsCoords + useLocations separately
  const {
    gps,
    locations: fetchedLocations,
    isLoading: isLocationLoading,
    locationsError,
  } = useLocationValidation();

  const {
    data: deviceRegData,
    isLoading: isDevRegLoading,
    error: deviceRegError,
  } = useCheckDeviceRegistration(deviceId, selectedLocation);

  const { mutate: register, isPending: isRegistering } = useRegisterDevice();

  // ── Effects ────────────────────────────────────────────────────────────────

  useEffect(() => {
    if (gps.status === 'denied')
      toast.error('Location permission is required.', 'Permission Denied');
    if (gps.status === 'error') toast.error((gps as any).message, 'Location Error');
  }, [gps.status]);

  useEffect(() => {
    if (locationsError) toast.error('Failed to validate location.', 'Network Error');
  }, [locationsError]);

  useEffect(() => {
    if (!deviceRegData) return;
    const { devRegStatus, role: savedRole } = deviceRegData;
    if (devRegStatus) {
      useAppStore.getState().setRole(savedRole as Role);
      router.replace(savedRole?.toLowerCase() === 'leader' ? '/leader' : '/follower');
    } else {
      setScreen('register');
    }
  }, [deviceRegData]);

  useEffect(() => {
    if (deviceRegError) toast.error('Failed to check device registration.', 'Error');
  }, [deviceRegError]);

  // ── Handlers ───────────────────────────────────────────────────────────────

  const handleLocationSelected = (location: string) => {
    setSelectedLocation(location);
    useAppStore.getState().setLocation(location);
    setLocationModalVisible(false);
  };

  useEffect(() => {
    if (!fetchedLocations) return;
    if (fetchedLocations.length === 1) {
      handleLocationSelected(fetchedLocations[0]);
    } else if (fetchedLocations.length > 1) {
      setLocations(fetchedLocations);
      setLocationModalVisible(true);
      setScreen('location');
    } else {
      toast.info('No valid locations found nearby.');
    }
  }, [fetchedLocations]);

  const handleRegister = () => {
    if (role === 'FOLLOWER') {
      const num = parseInt(tableNumber, 10);
      if (!tableNumber || isNaN(num) || num < 1 || num > 10) {
        toast.error('Table number must be between 1 and 10', 'Invalid Input');
        return;
      }
    }
    const location = useAppStore.getState().location ?? '';
    register(
      {
        deviceId: deviceId ?? '',
        data: {
          location,
          role,
          tableNumber: role === 'FOLLOWER' ? parseInt(tableNumber, 10) : 0,
        },
      },
      {
        onSuccess: () => {
          useAppStore.getState().setRole(role);
          useAppStore.getState().setRegistered(true);
          if (role === 'FOLLOWER') useAppStore.getState().setTableNumber(parseInt(tableNumber, 10));
          router.replace(role === 'LEADER' ? '/leader' : '/follower');
        },
        onError: (e: any) => toast.apiError(e, 'Registration Failed'),
      },
    );
  };

  // ── Loading ────────────────────────────────────────────────────────────────

  const isLoading = isLocationLoading || isDevRegLoading || isRegistering;

  const loadingMessage =
    gps.status === 'loading'
      ? 'Getting location...'
      : isLocationLoading
        ? 'Detecting location...'
        : isDevRegLoading
          ? 'Checking device...'
          : isRegistering
            ? 'Registering...'
            : 'Please wait...';

  // ── Render ─────────────────────────────────────────────────────────────────

  return (
    <SafeAreaView className="flex-1 bg-lightBlue">
      <LocationPickerModal
        visible={locationModalVisible}
        locations={locations}
        onSelect={handleLocationSelected}
        onDismiss={() => {}}
      />

      <LoadingOverlay visible={isLoading} message={loadingMessage} />

      {!isLoading && screen === 'register' && (
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="flex-1"
        >
          <ScrollView
            contentContainerStyle={{ flexGrow: 1, justifyContent: 'center', padding: 24 }}
            keyboardShouldPersistTaps="handled"
          >
            <View className="items-center mb-10">
              <Text className="text-4xl font-bold text-primary tracking-widest">FabCut</Text>
            </View>
            {selectedLocation && (
              <View className="items-center mb-4">
                <View className="flex-row items-center bg-primary/10 rounded-full px-4 py-1.5">
                  <Text className="text-xs text-primary font-semibold">📍 {selectedLocation}</Text>
                </View>
              </View>
            )}
            <View
              className="bg-white rounded-xl mx-4 p-6"
              style={{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 8 }}
            >
              <RegisterForm
                role={role}
                setRole={setRole}
                tableNumber={tableNumber}
                setTableNumber={setTableNumber}
                onRegister={handleRegister}
              />
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      )}
    </SafeAreaView>
  );
}

// ─── role Register Form ────────────────────────────────────────────────────────────

function RegisterForm({
  role,
  setRole,
  tableNumber,
  setTableNumber,
  onRegister,
}: RegisterFormProps) {
  return (
    <>
      <Text className="text-base font-semibold text-gray-700 mb-3">Select Role</Text>
      <View className="flex-row mb-5 gap-3">
        <RoleChip label="Leader" active={role === 'LEADER'} onPress={() => setRole('LEADER')} />
        <RoleChip
          label="Follower"
          active={role === 'FOLLOWER'}
          onPress={() => setRole('FOLLOWER')}
        />
      </View>
      {role === 'FOLLOWER' && (
        <Input
          label="Table Number"
          placeholder="Enter table number (1–10)"
          keyboardType="number-pad"
          value={tableNumber}
          onChangeText={setTableNumber}
          className="mb-4"
        />
      )}
      <Button title="Register" onPress={onRegister} className="mt-2" />
    </>
  );
}

function RoleChip({
  label,
  active,
  onPress,
}: {
  label: string;
  active: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable
      onPress={onPress}
      className={`flex-1 py-3 rounded-lg border items-center ${
        active ? 'bg-primary border-primary' : 'border-gray-300 bg-white'
      }`}
    >
      <Text className={`font-semibold text-sm ${active ? 'text-white' : 'text-gray-600'}`}>
        {label}
      </Text>
    </Pressable>
  );
}
