import { useRouter } from 'expo-router'
import { Alert, Text, View } from 'react-native'  // Add Alert import
import { SafeAreaView } from 'react-native-safe-area-context'
import CustomHeader from '@/components/Common/CustomHeader'
import { Button } from '@/components/ui/Button'
import { useUnregisterDevice } from '@/features/auth/hooks/useUnregisterDevice'
import { toast } from '@/lib/toast'
import { useAppStore } from '@/store/appStore'
import type { Role } from '@/types/common'

interface RefreshScreenProps {
    onRefresh: () => void
    isLoading?: boolean
}

const RefreshScreen = ({ onRefresh, isLoading = false }: RefreshScreenProps) => {
    const router = useRouter()
    const location = useAppStore((s) => s.location)
    const role = useAppStore((s) => s.role)
    const deviceId = useAppStore((s) => s.deviceId)

    const { mutate: unregister, isPending: isUnregistering } = useUnregisterDevice()

    const performUnregister = () => {
        // All three must be non-null before calling unregister
        if (!deviceId || !location || !role) return
        unregister({
            deviceId,
            data: { location, role: role as Role },
        },
            {
                onSuccess: () => {
                    useAppStore.getState().reset()
                    router.replace('/')
                },
                onError: (err: any) => {
                    toast.apiError(err, 'Unregister Failed')
                },
            },
        )
    }

    const handleUnregister = () => {
        Alert.alert(
            'Unregister device',
            'Are you sure you want to unregister the device.',
            [
                {
                    text: 'NO',
                    style: 'cancel',
                },
                {
                    text: 'YES',
                    style: 'destructive',
                    onPress: performUnregister,
                },
            ],
            { cancelable: true }
        )
    }

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
