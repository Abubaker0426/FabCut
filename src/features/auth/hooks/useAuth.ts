import { useCallback, useEffect, useState } from 'react';
import { useAuthStore } from '@/store/authStore';
import { webAuth0Service } from '@/lib/webAuth0Service';

export type AuthStatus = 'loading' | 'unauthenticated' | 'authenticated';

export const useAuth = () => {
  const { setToken, clearAuth, token } = useAuthStore();
  const [status, setStatus] = useState<AuthStatus>('loading');

  // ── On mount: restore stored credentials ────────────────────────────────
  useEffect(() => {
    (async () => {
      try {
        const credentials = await webAuth0Service.getStoredCredentials();
        if (credentials?.access_token) {
          setToken(credentials.access_token);
          setStatus('authenticated');
          console.log('[useAuth] ✅ Credentials restored');
        } else {
          setStatus('unauthenticated');
        }
      } catch {
        setStatus('unauthenticated');
      }
    })();
  }, []);

  // ── Login ────────────────────────────────────────────────────────────────
  const login = useCallback(async () => {
    try {
      setStatus('loading');
      const credentials = await webAuth0Service.login();
      setToken(credentials.access_token);
      setStatus('authenticated');
      console.log('[useAuth] ✅ Login successful');
    } catch (e: any) {
      console.error('[useAuth] ❌ Login failed:', e?.message);
      setStatus('unauthenticated');
      throw e;
    }
  }, [setToken]);

  // ── Logout ───────────────────────────────────────────────────────────────
  const logout = useCallback(async () => {
    await webAuth0Service.logout();
    clearAuth();
    setStatus('unauthenticated');
  }, [clearAuth]);

  return {
    status,
    isLoading: status === 'loading',
    isAuthenticated: status === 'authenticated',
    token,
    login,
    logout,
  };
};
