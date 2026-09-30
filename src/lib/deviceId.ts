import * as Application from 'expo-application';
import { Platform } from 'react-native';

export const getDeviceId = async (): Promise<string> => {
  if (Platform.OS === 'android') {
    return Application.getAndroidId() ?? '';
  }

  if (Platform.OS === 'ios') {
    // Returns the IDFV — closest iOS equivalent to ANDROID_ID
    return (await Application.getIosIdForVendorAsync()) ?? '';
  }
  return '';
};
