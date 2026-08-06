import React from 'react';
import { View, type ViewProps } from 'react-native';

interface CardProps extends ViewProps {
  children: React.ReactNode;
  className?: string;
}

export function Card({ children, className = '', style, ...props }: CardProps) {
  return (
    <View
      className={`bg-white rounded-lg shadow-sm ${className}`}
      style={[{ elevation: 4, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 4, shadowOffset: { width: 0, height: 2 } }, style]}
      {...props}
    >
      {children}
    </View>
  );
}
