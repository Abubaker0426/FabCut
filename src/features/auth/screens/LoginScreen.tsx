import { useState } from 'react';
import { ActivityIndicator, Text, View } from 'react-native';
import { useRouter } from 'expo-router';
import { Button } from '@/components/ui/Button';
import { webAuth0Service } from '@/lib/webAuth0Service';
import { useAuthStore } from '@/store/authStore';
import { toast } from '@/lib/toast';

export default function LoginScreen() {
  const router = useRouter();
  const setToken = useAuthStore((s) => s.setToken);
  const [loading, setLoading] = useState(false);

  const login = async () => {
    if (loading) return;
    setLoading(true);
    try {
      const credentials = await webAuth0Service.login();
      setToken(credentials.access_token);
      router.replace('/');
    } catch (e: any) {
      toast.error(e?.message ?? 'Sign in failed. Please try again.', 'Login Failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View
      style={{ flex: 1, alignItems: 'center', justifyContent: 'center', paddingHorizontal: 32 }}
      className="bg-lightBlue"
    >
      <Text className="text-4xl font-bold text-primary tracking-widest mb-2">FabCut</Text>
      <Text className="text-sm text-gray-500 mb-12">Sign in with your Fabtrakr account</Text>
      {loading ? (
        <ActivityIndicator size="large" color="#21226b" />
      ) : (
        <Button title="Sign In" onPress={login} className="w-full" />
      )}
    </View>
  );
}
