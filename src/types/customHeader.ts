import React from 'react'
import { Ionicons } from '@expo/vector-icons'

type IoniconName = React.ComponentProps<typeof Ionicons>['name']

 interface RightAction {
  icon: IoniconName
  onPress: () => void
  label?: string
}

 interface OverflowItem {
  label: string
  onPress: () => void
}

export interface CustomHeaderProps {
  title: string
  onBack?: () => void
  onLogout?: () => void
  badge?: string
  rightActions?: RightAction[]
  /** 3-dot overflow menu items */
  overflowMenu?: OverflowItem[]
}