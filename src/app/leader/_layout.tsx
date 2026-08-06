import { Stack } from 'expo-router';

export default function LeaderLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="index" />
      <Stack.Screen name="jobs" />
      <Stack.Screen name="bundle-parts" />
      <Stack.Screen name="follower-details" />
    </Stack>
  );
}
