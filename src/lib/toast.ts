import Toast from 'react-native-toast-message';

export function getErrorMessage(err: any, fallback = 'Something went wrong.'): string {
  const data = err?.response?.data;
  if (typeof data === 'string' && data.trim()) return data.trim();
  if (typeof data?.message === 'string') return data.message;
  if (typeof data?.error === 'string') return data.error;
  if (typeof data?.errorMessage === 'string') return data.errorMessage;
  if (typeof err?.message === 'string') return err.message;
  return fallback;
}

export const toast = {
  success: (message: string, title = 'Success') =>
    Toast.show({ type: 'success', text1: title, text2: message, visibilityTime: 3000 }),

  error: (message: string, title = 'Error') =>
    Toast.show({ type: 'error', text1: title, text2: message, visibilityTime: 4000 }),

  info: (message: string, title = 'Info') =>
    Toast.show({ type: 'info', text1: title, text2: message, visibilityTime: 3000 }),

  apiError: (err: any, title = 'Error', fallback = 'Something went wrong.') =>
    Toast.show({
      type: 'error',
      text1: title,
      text2: getErrorMessage(err, fallback),
      visibilityTime: 4000,
    }),
};
