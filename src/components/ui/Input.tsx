import React, { forwardRef } from 'react';
import {
  Text,
  TextInput,
  type TextInputProps,
  View,
} from 'react-native';

interface InputProps extends TextInputProps {
  label?: string;
  error?: string;
  className?: string;
}

export const Input = forwardRef<TextInput, InputProps>(
  ({ label, error, className = '', ...props }, ref) => {
    return (
      <View className="w-full">
        {label ? (
          <Text className="text-sm font-medium text-gray-700 mb-1">{label}</Text>
        ) : null}
        <TextInput
          ref={ref}
          className={`border border-gray-200 rounded-lg px-4 py-3 text-base text-gray-900 bg-white ${error ? 'border-red-500' : ''} ${className}`}
          placeholderTextColor="#aaa"
          {...props}
        />
        {error ? (
          <Text className="text-red-500 text-xs mt-1">{error}</Text>
        ) : null}
      </View>
    );
  }
);

Input.displayName = 'Input';
