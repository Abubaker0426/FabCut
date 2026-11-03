import { useEffect, useState, type PropsWithChildren } from 'react';
import { ActivityIndicator, View } from 'react-native';
import { useRouter, useSegments } from 'expo-router';
import { webAuth0Service } from '@/lib/webAuth0Service';
import { useAuthStore } from '@/store/authStore';

export function AuthGuard({ children }: PropsWithChildren) {
  const [ready, setReady] = useState(false);
  const router = useRouter();
  const segments = useSegments();
  const setToken = useAuthStore((s) => s.setToken);
  const clearAuth = useAuthStore((s) => s.clearAuth);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const credentials = await webAuth0Service.getStoredCredentials();
        if (!active) return;
        if (credentials?.access_token) {
          setToken(credentials.access_token);
        } else {
          clearAuth();
        }
      } catch {
        if (active) clearAuth();
      } finally {
        if (active) setReady(true);
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    if (!ready) return;

    const onLogin = (segments[0] as string) === 'login' || (segments[0] as string) === 'android';

    if (!isAuthenticated && !onLogin) {
      router.replace('/login');
    } else if (isAuthenticated && onLogin) {
      router.replace('/');
    }
  }, [ready, isAuthenticated, segments]);

  // Keep spinner until ready AND on the correct route
  const onLogin = (segments[0] as string) === 'login' || (segments[0] as string) === 'android';
  const routeMatchesAuth = isAuthenticated ? !onLogin : onLogin;

  if (!ready || !routeMatchesAuth) {
    return (
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
        <ActivityIndicator size="large" color="#21226b" />
      </View>
    );
  }

  return <>{children}</>;
}
