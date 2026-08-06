/**
 * MainScreen — Device Registration / Login
 * Mirrors: MainActivity + activity_main.xml
 *
 * Flow:
 *  1. App opens → immediately get GPS → validate location against server
 *  2. If multiple locations returned → show LocationPickerModal (BEFORE anything else)
 *  3. User picks location → location is stored
 *  4. Check if device is already registered
 *     - YES → show Unregister button
 *     - NO  → show Register card (role picker + table number)
 */
import React, { useEffect, useState } from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';

import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { LoadingOverlay } from '@/components/ui/LoadingOverlay';
import { useAppStore } from '@/store/appStore';
import type { Location, Role } from '@/types';
import { LocationPickerModal } from '../components/LocationPickerModal';

type Screen = 'loading' | 'location' | 'register' | 'unregister';

// ─── Mock locations — replace with real GPS + API call ────────────────────────
const MOCK_LOCATIONS: Location[] = [
  { locationId: 'L1', locationName: 'IDPL1'},
  { locationId: 'L2', locationName: 'IDPL2'},
  { locationId: 'L3', locationName: 'IDPL3'},
];

export default function MainScreen() {
  const router = useRouter();
  const [screen, setScreen]                       = useState<Screen>('loading');
  const [role, setRole]                           = useState<Role>('LEADER');
  const [tableNumber, setTableNumber]             = useState('');
  const [loading, setLoading]                     = useState(false);
  const [locationModalVisible, setLocationModalVisible] = useState(false);
  const [locations, setLocations]                 = useState<Location[]>([]);
  const [selectedLocation, setSelectedLocation]   = useState<Location | null>(null);

  // ── On mount: GPS → validate → show location picker if needed ──────────────
  useEffect(() => {
    (async () => {
      setLoading(true);
      // TODO: replace with real expo-location + API call
      //   1. const coords = await Location.getCurrentPositionAsync()
      //   2. const locs   = await api.get(`/service/validate/locations?lat=...&lng=...`)
      await new Promise((r) => setTimeout(r, 800)); // simulate network

      const fetchedLocations = MOCK_LOCATIONS;
      setLoading(false);

      if (fetchedLocations.length === 1) {
        // Only one location — select it automatically
        handleLocationSelected(fetchedLocations[0]);
      } else {
        // Multiple locations → show picker
        setLocations(fetchedLocations);
        setLocationModalVisible(true);
        setScreen('location');
      }
    })();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleLocationSelected = (location: Location) => {
    setSelectedLocation(location);
    setLocationModalVisible(false);

    // TODO: check if device is already registered
    //   GET /fabcut/cutting/registrations/{deviceId}
    //   → devRegStatus true  → setScreen('unregister')
    //   → devRegStatus false → setScreen('register')
    setScreen('register'); // default to register for now
  };

  const handleRegister = () => {
    if (role === 'FOLLOWER') {
      const num = parseInt(tableNumber, 10);
      if (!tableNumber || isNaN(num) || num < 1 || num > 10) {
        alert('Table number must be between 1 and 10');
        return;
      }
    }
    setLoading(true);
    // TODO: POST /fabcut/cutting/registrations/{deviceId}
    //   body: { role, tableNumber, location: selectedLocation.locationId }
    //   on success → navigate to /leader or /follower
    setTimeout(() => {
      setLoading(false);
      // Save to store
      useAppStore.getState().setRole(role);
      if (role === 'FOLLOWER') {
        useAppStore.getState().setTableNumber(parseInt(tableNumber, 10));
      }
      useAppStore.getState().setRegistered(true);
      // Navigate
      if (role === 'LEADER') {
        router.replace('/leader');
      } else {
        router.replace('/follower');
      }
    }, 1000);
  };

  const handleUnregister = () => {
    setLoading(true);
    // TODO: DELETE /fabcut/cutting/registrations/{deviceId}
    //   on success → reset state, setScreen('register')
    setTimeout(() => {
      useAppStore.getState().reset();
      setLoading(false);
      setScreen('register');
    }, 1200);
  };

  return (
    <SafeAreaView className="flex-1 bg-lightBlue">
      <LoadingOverlay visible={loading} message="Please wait..." />

      {/* Location picker — shown immediately on app open if multiple locations */}
      <LocationPickerModal
        visible={locationModalVisible}
        locations={locations}
        onSelect={handleLocationSelected}
        onDismiss={() => {
          // Don't allow dismissing without picking — same as Java behaviour
          // (user MUST pick a location to proceed)
        }}
      />

      {/* Only render form content once a location has been resolved */}
      {(screen === 'register' || screen === 'unregister') && (
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
                <View className="flex-row items-center bg-primary/10 rounded-full px-4 py-1.5 gap-1">
                  <Text className="text-xs text-primary font-semibold">
                    📍 {selectedLocation.locationName}
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
                <UnregisterView onUnregister={handleUnregister} />
              )}
            </View>
          </ScrollView>
        </KeyboardAvoidingView>
      )}

      {/* Blank state while location is loading / being picked */}
      {screen === 'loading' && !loading && (
        <View className="flex-1 items-center justify-center">
          <Text className="text-4xl font-bold text-primary tracking-widest">FabCut</Text>
          <Text className="text-sm text-primary/70 mt-2">Detecting location...</Text>
        </View>
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
        <RoleChip label="Leader"   active={role === 'LEADER'}   onPress={() => setRole('LEADER')} />
        <RoleChip label="Follower" active={role === 'FOLLOWER'} onPress={() => setRole('FOLLOWER')} />
      </View>

      {role === 'FOLLOWER' && (
        <Input
          label="Table Number"
          placeholder="Enter table number"
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

// ─── Unregister View ──────────────────────────────────────────────────────────

function UnregisterView({ onUnregister }: { onUnregister: () => void }) {
  return (
    <View className="items-center gap-4">
      <Text className="text-base text-gray-600 text-center">
        This device is already registered.
      </Text>
      <Button title="Unregister Device" variant="danger" onPress={onUnregister} className="w-full" />
    </View>
  );
}
