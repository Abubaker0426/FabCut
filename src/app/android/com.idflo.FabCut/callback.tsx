import * as WebBrowser from 'expo-web-browser';
import { ActivityIndicator, View } from 'react-native';

// This MUST be called here — it signals the browser to close and
// hands the redirect URL back to openAuthSessionAsync in the opener.
WebBrowser.maybeCompleteAuthSession();

export default function AuthCallbackScreen() {
  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <ActivityIndicator size="large" color="#21226b" />
    </View>
  );
}
