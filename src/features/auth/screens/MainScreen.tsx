import React, { useEffect, useState } from 'react';
import { ActivityIndicator, KeyboardAvoidingView, Platform, Pressable, ScrollView, Text, View, } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { getDeviceId } from '@/lib/deviceId';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useAppStore } from '@/store/appStore';
import type { Role } from '@/types/common';
import { LocationPickerModal } from '../components/LocationPickerModal';
import { useGpsCoords } from '../hooks/useGpsCoords';
import { useLocations } from '../hooks/useLocations';
import { useCheckDeviceRegistration } from '../hooks/useCheckDeviceRegistration';
import { useRegisterDevice } from '../hooks/useRegisterDevice';
import { useUnregisterDevice } from '../hooks/useUnregisterDevice';
import { toast } from '@/lib/toast';

type Screen = 'loading' | 'location' | 'register' | 'unregister';


export default function MainScreen() {
  const router = useRouter();
  const storeRole = useAppStore((s) => s.role);
  const storeLocation = useAppStore((s) => s.location);
  // UI state
  const [screen, setScreen] = useState<Screen>('loading');
  const [role, setRole] = useState<Role>('LEADER');
  const [tableNumber, setTableNumber] = useState('');
  const [locationModalVisible, setLocationModalVisible] = useState(false);
  const [locations, setLocations] = useState<string[]>([]);
  const [selectedLocation, setSelectedLocation] = useState<string | null>(null);

  // Device ID — mirrors Java CommonUtils.getDeviceId()
  // Loaded async on mount via expo-application (Application.getAndroidId / getIosIdForVendorAsync)
  const [deviceId, setDeviceId] = useState<string>('');

  useEffect(() => {
    getDeviceId().then((id) => setDeviceId(id));
  }, []);

  // ── Hook: GPS ──────────────────────────────────────────────────────────────
  const gps = useGpsCoords();
  const coords = gps.status === 'granted' ? gps.coords : null;

  // ── Hook: Validate location via API ───────────────────────────────────────
  const { data: fetchedLocations, isLoading: isLocationsLoading, error: locationsError } =
    useLocations(coords);

  // ── Hook: Check device registration ───────────────────────────────────────
  const {
    data: deviceRegData,
    isLoading: isDevRegLoading,
    error: deviceRegError,
  } = useCheckDeviceRegistration(deviceId, selectedLocation);

  // ── Hook: Register device ──────────────────────────────────────────────────
  const { mutate: register, isPending: isRegistering } = useRegisterDevice();

  // ── Hook: Unregister device ────────────────────────────────────────────────
  const { mutate: unregister, isPending: isUnregistering } = useUnregisterDevice();

  // ── Effect: GPS permission denied ─────────────────────────────────────────
  useEffect(() => {
    if (gps.status === 'denied') {
      toast.error('Location permission is required to use FabCut.', 'Permission Denied');
    }
    if (gps.status === 'error') {
      toast.error(gps.message, 'Location Error');
    }
  }, [gps.status]);

  // ── Effect: Location API response ─────────────────────────────────────────
  useEffect(() => {
    if (!fetchedLocations) return;

    if (fetchedLocations.length === 1) {
      // Single location — auto-select, same as Java
      handleLocationSelected(fetchedLocations[0]);
    } else if (fetchedLocations.length > 1) {
      // Multiple locations — show picker
      setLocations(fetchedLocations);
      setLocationModalVisible(true);
      setScreen('location');
    } else {
      toast.info('No valid locations found nearby.');
    }
  }, [fetchedLocations]);

  useEffect(() => {
    if (locationsError) {
      toast.error('Failed to validate location. Check your network connection.', 'Network Error');
    }
  }, [locationsError]);

  // ── Effect: Device registration check response ─────────────────────────────
  useEffect(() => {
    if (!deviceRegData) return;

    const { devRegStatus, role: savedRole } = deviceRegData;
    if (devRegStatus) {
      // Device is already registered — store role, show unregister screen,
      // then navigate to the appropriate screen (same as Java's showUnRegisterButton + showUiBasedOnRole)
      useAppStore.getState().setRole(savedRole as Role);
      setScreen('unregister');
      router.replace(savedRole?.toLowerCase() === 'leader' ? '/leader' : '/follower');
    } else {
      // Not registered — show the register form (mirrors Java's showRegisterButton)
      setScreen('register');
    }
  }, [deviceRegData]);

  useEffect(() => {
    if (deviceRegError) {
      toast.error('Failed to check device registration.', 'Registration Error');
    }
  }, [deviceRegError]);

  // ── Handlers (no API logic — just call hooks) ──────────────────────────────

  const handleLocationSelected = (location: string) => {
    setSelectedLocation(location);
    useAppStore.getState().setLocation(location);
    setLocationModalVisible(false);
    // useCheckDeviceRegistration fires automatically via enabled: !!selectedLocation
  };

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
        deviceId,
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
          if (role === 'FOLLOWER') {
            useAppStore.getState().setTableNumber(parseInt(tableNumber, 10));
          }
          // Mirrors Java: showUnRegisterButton() + hideRegisterButton() + showUiBasedOnRole()
          // Show unregister screen first, then navigate
          setScreen('unregister');
          router.replace(role === 'LEADER' ? '/leader' : '/follower');
        },
        onError: (e: any) => {
          toast.apiError(e, 'Registration Failed');
        },
      },
    );
  };

  const handleUnregister = () => {
    const location = useAppStore.getState().location ?? '';
    const currentRole = useAppStore.getState().role ?? role;

    unregister(
      { deviceId, data: { location, role: currentRole } },
      {
        onSuccess: () => {
          useAppStore.getState().reset();
          setScreen('register');
        },
        onError: (e: any) => {
          toast.apiError(e, 'Unregister Failed');
        },
      },
    );
  };

  // ── Derived loading state ──────────────────────────────────────────────────
  const isLoading =
    gps.status === 'loading' ||
    isLocationsLoading ||
    isDevRegLoading ||
    isRegistering ||
    isUnregistering;

  const loadingMessage =
    gps.status === 'loading' ? 'Getting location...' :
      isLocationsLoading ? 'Detecting location...' :
        isDevRegLoading ? 'Checking device...' :
          isRegistering ? 'Registering...' :
            isUnregistering ? 'Unregistering...' :
              'Please wait...';

  // ── Render ─────────────────────────────────────────────────────────────────

  return (
    <SafeAreaView className="flex-1 bg-lightBlue">

      {/* Location picker — shown immediately if multiple locations returned */}
      <LocationPickerModal
        visible={locationModalVisible}
        locations={locations}
        onSelect={handleLocationSelected}
        onDismiss={() => {
          // Don't allow dismissing without picking — same as Java behaviour
        }}
      />

      {/* Loading state */}
      {isLoading && (
        <View className="flex-1 items-center justify-center">
          <Text className="text-4xl font-bold text-primary tracking-widest mb-4">FabCut</Text>
          <ActivityIndicator size="large" color="#208AEF" />
          <Text className="text-sm text-primary/70 mt-3">{loadingMessage}</Text>
        </View>
      )}

      {/* Form — only shown once a location has been selected and not loading */}
      {!isLoading && (screen === 'register' || screen === 'unregister') && (
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          className="flex-1"
        >
          <ScrollView
            contentContainerStyle={{ flexGrow: 1, justifyContent: 'center', padding: 24 }}
            keyboardShouldPersistTaps="handled"
          >
            {/* Logo */}
            <View className="items-center mb-10">
              <Text className="text-4xl font-bold text-primary tracking-widest">FabCut</Text>
            </View>

            {/* Selected location badge */}
            {selectedLocation && (
              <View className="items-center mb-4">
                <View className="flex-row items-center bg-primary/10 rounded-full px-4 py-1.5">
                  <Text className="text-xs text-primary font-semibold">
                    📍 {selectedLocation}
                  </Text>
                </View>
              </View>
            )}

            {/* Card */}
            <View
              className="bg-white rounded-xl mx-4 p-6"
              style={{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 8 }}
            >
              {screen === 'register' ? (
                <RegisterForm
                  role={role}
                  setRole={setRole}
                  tableNumber={tableNumber}
                  setTableNumber={setTableNumber}
                  onRegister={handleRegister}
                />
              ) : (
                <UnregisterView
                  onUnregister={handleUnregister}
                  role={storeRole}
                  location={storeLocation}
                />
              )}
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      )}
    </SafeAreaView>
  );
}

// ─── Register Form ────────────────────────────────────────────────────────────

interface RegisterFormProps {
  role: Role;
  setRole: (r: Role) => void;
  tableNumber: string;
  setTableNumber: (v: string) => void;
  onRegister: () => void;
}

function RegisterForm({ role, setRole, tableNumber, setTableNumber, onRegister }: RegisterFormProps) {
  return (
    <>
      <Text className="text-base font-semibold text-gray-700 mb-3">Select Role</Text>
      <View className="flex-row mb-5 gap-3">
        <RoleChip label="Leader" active={role === 'LEADER'} onPress={() => setRole('LEADER')} />
        <RoleChip label="Follower" active={role === 'FOLLOWER'} onPress={() => setRole('FOLLOWER')} />
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

// ─── Role Chip ────────────────────────────────────────────────────────────────

function RoleChip({ label, active, onPress }: { label: string; active: boolean; onPress: () => void }) {
  return (
    <Pressable
      onPress={onPress}
      className={`flex-1 py-3 rounded-lg border items-center ${active ? 'bg-primary border-primary' : 'border-gray-300 bg-white'
        }`}
    >
      <Text className={`font-semibold text-sm ${active ? 'text-white' : 'text-gray-600'}`}>
        {label}
      </Text>
    </Pressable>
  );
}

// ─── Unregister View ──────────────────────────────────────────────────────────
// Mirrors Java: activity_main.xml unregister button (visibility="gone" → visible)
// Shows when devRegStatus = true or after a successful register.
// User can tap to unregister → back to RegisterForm.

interface UnregisterViewProps {
  onUnregister: () => void;
  role: Role | null;
  location: string | null;
}

function UnregisterView({ onUnregister, role, location }: UnregisterViewProps) {
  return (
    <View className="items-center gap-4">
      {/* Registered info */}
      <View className="w-full bg-primary/5 rounded-lg p-4 gap-2">
        <Text className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
          Registered As
        </Text>
        <View className="flex-row items-center justify-between">
          <Text className="text-base font-bold text-primary capitalize">
            {role ? role.charAt(0) + role.slice(1).toLowerCase() : '—'}
          </Text>
          {location && (
            <View className="flex-row items-center bg-primary/10 rounded-full px-3 py-1">
              <Text className="text-xs text-primary font-semibold">📍 {location}</Text>
            </View>
          )}
        </View>
      </View>

      <Text className="text-sm text-gray-500 text-center">
        This device is already registered. Unregister to change role or location.
      </Text>

      <Button
        title="Unregister Device"
        variant="danger"
        onPress={onUnregister}
        className="w-full mt-1"
      />
    </View>
  );
}
