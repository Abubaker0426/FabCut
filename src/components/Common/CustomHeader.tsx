import { Ionicons } from '@expo/vector-icons';
import { useState } from 'react';
import { Modal, Pressable, Text, TouchableOpacity, View } from 'react-native';
import {  CustomHeaderProps} from '@/types/customHeader'

export default function CustomHeader({ title, onBack, onLogout, badge,
  rightActions = [], overflowMenu = [], }: CustomHeaderProps) {
  const [menuVisible, setMenuVisible] = useState(false);

  return (
    <View
      className="bg-primary flex-row items-center px-4"
      style={{ minHeight: 52, paddingVertical: 10 }}
    >
      {/* Back arrow */}
      {onBack && (
        <Pressable onPress={onBack} hitSlop={12} className="mr-3" accessibilityRole="button">
          <Ionicons name="arrow-back" size={22} color="#fff" />
        </Pressable>
      )}

      {/* Title */}
      <Text className="text-white text-lg font-bold flex-1" numberOfLines={1}>
        {title}
      </Text>

      {/* Right side */}
      <View className="flex-row items-center gap-4">

        {badge ? (
          <View className="bg-amber-400 rounded-full px-3 py-0.5">
            <Text className="text-xs font-bold text-white">{badge}</Text>
          </View>
        ) : null}

        {rightActions.map((action, i) => (
          <Pressable key={i} onPress={action.onPress} hitSlop={12} accessibilityRole="button">
            <Ionicons name={action.icon} size={22} color="#fff" />
          </Pressable>
        ))}

        {/* 3-dot overflow menu — mirrors Android overflow_menu.xml */}
        {overflowMenu.length > 0 && (
          <>
            <Pressable
              onPress={() => setMenuVisible(true)}
              hitSlop={12}
              accessibilityLabel="More options"
              accessibilityRole="button"
            >
              <Ionicons name="ellipsis-vertical" size={22} color="#fff" />
            </Pressable>

            <Modal
              transparent
              visible={menuVisible}
              animationType="none"
              onRequestClose={() => setMenuVisible(false)}
              statusBarTranslucent
            >
              {/* Backdrop */}
              <TouchableOpacity
                activeOpacity={1}
                onPress={() => setMenuVisible(false)}
                style={{ flex: 1 }}
              >
                {/* Dropdown anchored top-right */}
                <View
                  style={{
                    position: 'absolute',
                    top: 52,
                    right: 8,
                    backgroundColor: '#fff',
                    borderRadius: 4,
                    elevation: 8,
                    shadowColor: '#000',
                    shadowOpacity: 0.2,
                    shadowRadius: 6,
                    shadowOffset: { width: 0, height: 3 },
                    minWidth: 180,
                    overflow: 'hidden',
                  }}
                >
                  {overflowMenu.map((item, i) => (
                    <TouchableOpacity
                      key={i}
                      onPress={() => { setMenuVisible(false); item.onPress(); }}
                      style={{
                        paddingHorizontal: 16,
                        paddingVertical: 14,
                        borderBottomWidth: i < overflowMenu.length - 1 ? 1 : 0,
                        borderBottomColor: '#f3f4f6',
                      }}
                    >
                      <Text style={{ fontSize: 14, color: '#1f2937' }}>{item.label}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </TouchableOpacity>
            </Modal>
          </>
        )}

        {onLogout ? (
          <Pressable onPress={onLogout} hitSlop={12} accessibilityRole="button">
            <Ionicons name="log-out-outline" size={22} color="#fff" />
          </Pressable>
        ) : null}

      </View>
    </View>
  );
}
