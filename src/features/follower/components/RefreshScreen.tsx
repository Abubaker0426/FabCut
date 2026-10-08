import { Text, View } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'
import CustomHeader from '@/components/Common/CustomHeader'
import { Button } from '@/components/ui/Button'
import { useUnregister } from '@/features/auth/hooks/useUnregister'

interface RefreshScreenProps {
  onRefresh: () => void
  isLoading?: boolean
}

const RefreshScreen = ({ onRefresh, isLoading = false }: RefreshScreenProps) => {
  // Shared unregister hook — same as LeaderScreen
  const { handleUnregister, isUnregistering } = useUnregister()

  return (
    <SafeAreaView className="flex-1 bg-lightBlue" edges={['top']}>
      <CustomHeader
        title="FabCut"
        overflowMenu={[
          { label: 'Unregister', onPress: handleUnregister },
        ]}
      />
      <View className="flex-1 items-center justify-center gap-6">
        <Text className="text-primary text-base font-medium text-center px-8">
          No job assigned yet.{'\n'}Tap Refresh to check for a new job.
        </Text>
        <Button
          title="Refresh"
          onPress={onRefresh}
          loading={isLoading || isUnregistering}
          className="px-10"
        />
      </View>
    </SafeAreaView>
  )
}

export default RefreshScreen
