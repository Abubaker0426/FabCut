import {
  forwardRef,
  useCallback,
  useMemo,
  ReactNode,
} from "react";
import { View, Text } from "react-native";
import BottomSheet, {
  BottomSheetBackdrop,
  BottomSheetFlatList,
  BottomSheetScrollView,
} from "@gorhom/bottom-sheet";

interface AppSheetModalProps {
  title: string;
  badge?: number;
  snapPoints?: (string | number)[];
  initialIndex?: number;
  showBackdrop?: boolean;
  onClose?: () => void;
  scrollable?: boolean;
  children: ReactNode;
}

const AppSheetModal = forwardRef<BottomSheet, AppSheetModalProps>(
  (
    {
      title,
      badge,
      snapPoints: snapPointsProp,
      initialIndex = -1,
      showBackdrop = true,
      onClose,
      scrollable = true,
      children,
    },
    ref
  ) => {
    const snapPoints = useMemo(
      () => snapPointsProp ?? ["15%", "50%", "92%"],
      [snapPointsProp]
    );

    const renderBackdrop = useCallback(
      (props: any) =>
        showBackdrop ? (
          <BottomSheetBackdrop
            {...props}
            appearsOnIndex={1}
            disappearsOnIndex={0}
            pressBehavior="collapse"
          />
        ) : null,
      [showBackdrop]
    );

    const renderHandle = useCallback(
      () => (
        <View className="items-center pt-2 pb-2 px-4 border-b border-gray-200 bg-gray-100 rounded-t-3xl">

          {/* Handle Pill */}
          <View className="w-10 h-[5px] rounded-full bg-gray-300 mb-1" />

          <View className="flex-row items-center">

            <Text
              numberOfLines={1}
              className="text-sm font-semibold text-gray-600"
            >
              {title}
            </Text>

            {badge !== undefined && badge > 0 && (
              <View className="ml-2 min-w-[22px] px-2 py-[2px] rounded-full bg-blue-500 items-center">
                <Text className="text-white text-[12px] font-bold">
                  {badge}
                </Text>
              </View>
            )}

          </View>

        </View>
      ),
      [title, badge]
    );

    return (
      <BottomSheet
        ref={ref}
        index={initialIndex}
        snapPoints={snapPoints}
        enablePanDownToClose={false}
        enableDynamicSizing={false}
        animateOnMount
        backdropComponent={renderBackdrop}
        handleComponent={renderHandle}
        onClose={onClose}
        style={{
          shadowColor: "#000",
          shadowOffset: { width: 0, height: -3 },
          shadowOpacity: 0.1,
          shadowRadius: 3,
          elevation: 5,
        }}
        backgroundStyle={{
          backgroundColor: "#fff",
          borderTopLeftRadius: 20,
          borderTopRightRadius: 20,
        }}
      >
        {scrollable ? (
          <BottomSheetScrollView
            showsVerticalScrollIndicator={false}
            contentContainerStyle={{
              paddingHorizontal: 16,
              paddingBottom: 30,
            }}
          >
            {children}
          </BottomSheetScrollView>
        ) : (
          children
        )}
      </BottomSheet>
    );
  }
);

AppSheetModal.displayName = "AppSheetModal";

export { BottomSheetFlatList };

export default AppSheetModal;