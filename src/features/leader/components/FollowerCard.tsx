/**
 * FollowerCard — mirrors follower_item.xml + FollowerAdapter.java
 *
 * Normal mode:
 *   - lightBlue background
 *   - X button visible (calls getFollowerJob → view job details)
 *
 * Selection mode (leader assigning a job):
 *   - X button hidden
 *   - IDLE → green background (colorGreenPastel)
 *   - BUSY → red/accent background (disabled, not selectable)
 *   - DONE → primary background
 */
import { Ionicons } from '@expo/vector-icons';
import { Pressable, Text, View } from 'react-native';

import type { Follower } from '@/types';

// Mirrors Java color resources
const NORMAL_BG   = '#E6F4FE'; // colorLightBlue
const IDLE_BG     = '#C8E6C9'; // colorGreenPastel
const BUSY_BG     = '#FFCDD2'; // colorAccent (light red)
const DONE_BG     = '#21226b'; // colorPrimary

const STATUS_TEXT_COLOR: Record<string, string> = {
  IDLE: '#16a34a',
  BUSY: '#dc2626',
  DONE: '#fff',
};

interface FollowerCardProps {
  follower: Follower;
  /** In selection mode: highlights available followers, hides X button */
  selectionMode?: boolean;
  onPress?: () => void;
  /** X button — calls getFollowerJob (view job details) in normal mode */
  onDelete?: () => void;
}

export function FollowerCard({
  follower,
  selectionMode = false,
  onPress,
  onDelete,
}: FollowerCardProps) {
  const isBusy = follower.status === 'BUSY';

  // Background color mirrors FollowerAdapter.java setSelectionMode logic
  let bgColor = NORMAL_BG;
  if (selectionMode) {
    if (follower.status === 'IDLE')      bgColor = IDLE_BG;
    else if (follower.status === 'BUSY') bgColor = BUSY_BG;
    else                                  bgColor = DONE_BG;
  }

  return (
    // In selection mode, BUSY followers are disabled (can't be selected in Java)
    <Pressable
      onPress={selectionMode && isBusy ? undefined : onPress}
      className="m-2 active:opacity-70"
      disabled={selectionMode && isBusy}
    >
      <View
        className="rounded-xl h-36 items-center justify-center relative"
        style={{
          backgroundColor: bgColor,
          elevation: 4,
          shadowColor: '#000',
          shadowOpacity: 0.08,
          shadowRadius: 6,
          opacity: selectionMode && isBusy ? 0.5 : 1,
        }}
      >
        {/* X button — hidden in selection mode (mirrors deleteJob.setVisibility(View.GONE)) */}
        {!selectionMode && onDelete && (
          <Pressable
            onPress={onDelete}
            className="absolute top-2 right-2 p-1"
            hitSlop={8}
            accessibilityLabel="View follower job"
          >
            <Ionicons name="close-circle" size={20} color="#21226b" />
          </Pressable>
        )}

        {/* Table number */}
        <Text
          className="text-2xl font-bold"
          style={{ color: follower.status === 'DONE' && selectionMode ? '#fff' : '#21226b' }}
        >
          {follower.tableNumber}
        </Text>

        {/* Status */}
        <Text
          className="text-sm font-bold uppercase mt-1"
          style={{
            color: selectionMode && follower.status === 'DONE'
              ? '#fff'
              : STATUS_TEXT_COLOR[follower.status] ?? '#21226b',
          }}
        >
          {follower.status}
        </Text>
      </View>
    </Pressable>
  );
}
