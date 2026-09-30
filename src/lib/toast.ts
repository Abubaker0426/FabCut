import Toast from 'react-native-toast-message';
export function getErrorMessage(err: any, fallback = 'Something went wrong.'): string {
  return (
    err?.response?.data?.message ||
    err?.response?.data?.error   ||
    err?.message                 ||
    fallback
  );
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
