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
  /** Optional element rendered inside the input on the right side (e.g. a validation icon) */
  rightIcon?: React.ReactNode;
}

export const Input = forwardRef<TextInput, InputProps>(
  ({ label, error, className = '', rightIcon, ...props }, ref) => {
    return (
      <View className="w-full">
        {label ? (
          <Text className="text-sm font-medium text-gray-700 mb-1">{label}</Text>
        ) : null}

        {/* Wrapper needed so rightIcon can be absolutely positioned inside */}
        <View className="relative justify-center">
          <TextInput
            ref={ref}
            className={`border border-gray-200 rounded-lg px-4 py-3 text-base text-gray-900 bg-white ${rightIcon ? 'pr-10' : ''} ${error ? 'border-red-500' : ''} ${className}`}
            placeholderTextColor="#aaa"
            {...props}
          />
          {rightIcon ? (
            <View className="absolute right-3 items-center justify-center" pointerEvents="none">
              {rightIcon}
            </View>
          ) : null}
        </View>

        {error ? (
          <Text className="text-red-500 text-xs mt-1">{error}</Text>
        ) : null}
      </View>
    );
  }
);

Input.displayName = 'Input';

