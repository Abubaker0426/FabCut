import '../constants/global.css';
import { StatusBar, useColorScheme } from 'react-native';
import { Stack } from 'expo-router';
import { QueryClientProvider } from '@tanstack/react-query';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import Toast from 'react-native-toast-message';
import { queryClient } from '@/lib/queryClient';
import { AuthGuard } from '@/features/auth/AuthGuard';
import { configureReanimatedLogger } from 'react-native-reanimated';

configureReanimatedLogger({ strict: false });

export default function RootLayout() {
  const colorScheme = useColorScheme();
  const isDarkMode = colorScheme === 'dark';

  return (
    <>
      <StatusBar
        translucent
        backgroundColor="transparent"
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
      />
      <GestureHandlerRootView style={{ flex: 1 }}>
        <QueryClientProvider client={queryClient}>
          <AuthGuard>
            <Stack screenOptions={{ headerShown: false }}>
              <Stack.Screen name="login" />
              <Stack.Screen name="android/com.idflo.FabCut/callback" />
              <Stack.Screen name="index" />
              <Stack.Screen name="leader" />
              <Stack.Screen name="follower" />
              <Stack.Screen name="scanner" options={{ animation: 'slide_from_bottom' }} />
            </Stack>
          </AuthGuard>
          <Toast />
        </QueryClientProvider>
      </GestureHandlerRootView>
    </>
  );
}
