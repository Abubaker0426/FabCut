/**
 * AuthGuard — watches auth + registration state and redirects accordingly.
 * Runs as a child of the root layout so it has access to the router.
 */
import { useRouter, useSegments } from 'expo-router';
import { useEffect } from 'react';

import { useAppStore } from '@/store/appStore';
import { useAuthStore } from '@/store/authStore';

export function AuthGuard() {
  const router = useRouter();
  const segments = useSegments();

  const { isAuthenticated } = useAuthStore();
  const { isRegistered, role } = useAppStore();

  useEffect(() => {
    const inAuthGroup = segments[0] === undefined;
    const inLeader = segments[0] === 'leader';
    const inFollower = segments[0] === 'follower';
    const inScanner = segments[0] === 'scanner';

    // Allow scanner from anywhere
    if (inScanner) return;

    if (!isAuthenticated && !inAuthGroup) {
      // Not logged in → go to registration
      router.replace('/');
      return;
    }

    if (isAuthenticated && isRegistered) {
      // Registered as Leader → go to leader screen
      if (role === 'LEADER' && !inLeader) {
        router.replace('/leader');
        return;
      }
      // Registered as Follower → go to follower screen
      if (role === 'FOLLOWER' && !inFollower) {
        router.replace('/follower');
        return;
      }
    }
  }, [isAuthenticated, isRegistered, role, segments, router]);

  return null;
}
