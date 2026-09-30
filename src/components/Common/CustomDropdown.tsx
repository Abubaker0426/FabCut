import { MaterialIcons } from '@expo/vector-icons';
import { useRef, useState } from 'react';
import { Dimensions, Modal, Pressable, Text, TouchableOpacity, View, } from 'react-native';
import { CustomDropdownProps, Layout } from "@/types/customDropdown"

const ITEM_HEIGHT = 48; // height of each option row
const SCREEN = Dimensions.get('window');

export default function CustomDropdown({ data, placeholder, selectedValue, disabled = false, onSelect, }: CustomDropdownProps) {
  const triggerRef = useRef<View>(null);
  const [open, setOpen] = useState(false);
  const [layout, setLayout] = useState<Layout | null>(null);

  const openDropdown = () => {
    triggerRef.current?.measureInWindow((x, y, width, height) => {
      setLayout({ x, y, width, height });
      setOpen(true);
    });
  };

  const closeDropdown = () => setOpen(false);

  const handleSelect = (item: string) => {
    onSelect(item);
    closeDropdown();
  };

  // Total height the list needs
  const listHeight = data.length * ITEM_HEIGHT;
  // Space below the trigger
  const spaceBelow = layout ? SCREEN.height - (layout.y + layout.height) - 16 : 0;
  // Open upward if not enough space below
  const opensUp = layout ? spaceBelow < listHeight : false;

  const listTop = layout
    ? opensUp
      ? layout.y - listHeight - 4          // above trigger
      : layout.y + layout.height + 4       // below trigger
    : 0;

  return (
    <View>
      {/* Trigger button */}
      <Pressable
        ref={triggerRef}
        disabled={disabled}
        onPress={openDropdown}
        className={`flex-row items-center justify-between border rounded-lg px-3 py-2.5 bg-white ${open ? 'border-primary' : 'border-gray-300'
          } ${disabled ? 'opacity-50 bg-gray-100' : ''}`}
      >
        <Text
          className={`text-sm flex-1 mr-2 ${selectedValue ? 'text-gray-800' : 'text-gray-400'
            }`}
          numberOfLines={1}
        >
          {selectedValue || placeholder}
        </Text>
        <MaterialIcons
          name={open ? 'keyboard-arrow-up' : 'keyboard-arrow-down'}
          size={20}
          color="#666"
        />
      </Pressable>

      {/* Option list rendered in a Modal — always above everything */}
      {open && layout && (
        <Modal
          transparent
          visible
          animationType="none"
          onRequestClose={closeDropdown}
          statusBarTranslucent
        >
          {/* Full-screen backdrop — tap anywhere to close */}
          <TouchableOpacity
            activeOpacity={1}
            onPress={closeDropdown}
            style={{ flex: 1 }}
          >
            {/* Option list — absolutely positioned at measured location */}
            <View
              style={{
                position: 'absolute',
                top: listTop,
                left: layout.x,
                width: layout.width,
                backgroundColor: '#ffffff',
                borderRadius: 8,
                borderWidth: 1,
                borderColor: '#e5e7eb',
                elevation: 12,
                shadowColor: '#000',
                shadowOpacity: 0.15,
                shadowRadius: 8,
                shadowOffset: { width: 0, height: 4 },
                overflow: 'hidden',
              }}
            >
              {data.map((item, index) => (
                <TouchableOpacity
                  key={item}
                  activeOpacity={0.7}
                  onPress={() => handleSelect(item)}
                  style={{
                    height: ITEM_HEIGHT,
                    paddingHorizontal: 16,
                    justifyContent: 'center',
                    borderBottomWidth: index === data.length - 1 ? 0 : 1,
                    borderBottomColor: '#f3f4f6',
                    backgroundColor:
                      selectedValue === item ? '#eef2ff' : '#ffffff',
                  }}
                >
                  <Text
                    style={{
                      fontSize: 14,
                      lineHeight: 20,
                      color: selectedValue === item ? '#21226b' : '#1f2937',
                      fontWeight: selectedValue === item ? '600' : '400',
                    }}
                  >
                    {item}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </TouchableOpacity>
        </Modal>
      )}
    </View>
  );
}
