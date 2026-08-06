import React from 'react';
import {
  ActivityIndicator,
  Pressable,
  Text,
  type PressableProps,
} from 'react-native';

interface ButtonProps extends PressableProps {
  title: string;
  variant?: 'primary' | 'outline' | 'ghost' | 'danger';
  loading?: boolean;
  className?: string;
}

const variantStyles = {
  primary: 'bg-primary rounded-lg',
  outline: 'border border-primary rounded-lg bg-transparent',
  ghost: 'bg-transparent',
  danger: 'bg-red-600 rounded-lg',
};

const textStyles = {
  primary: 'text-white font-semibold text-base',
  outline: 'text-primary font-semibold text-base',
  ghost: 'text-primary font-semibold text-base',
  danger: 'text-white font-semibold text-base',
};

export function Button({
  title,
  variant = 'primary',
  loading = false,
  disabled,
  className = '',
  ...props
}: ButtonProps) {
  const isDisabled = disabled || loading;

  return (
    <Pressable
      className={`px-6 py-3 items-center justify-center flex-row ${variantStyles[variant]} ${isDisabled ? 'opacity-50' : ''} ${className}`}
      disabled={isDisabled}
      {...props}
    >
      {loading && (
        <ActivityIndicator
          size="small"
          color={variant === 'primary' || variant === 'danger' ? '#fff' : '#21226b'}
          className="mr-2"
        />
      )}
      <Text className={textStyles[variant]}>{title}</Text>
    </Pressable>
  );
}
