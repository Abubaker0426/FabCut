/**
 * FollowerCard — mirrors follower_item.xml + FollowerAdapter.java
 *
 * Java Status enum values (Title Case from API):
 *   "Idle" → available, selectable, green in selection mode
 *   "Busy" → has a job, not selectable in selection mode, red/dimmed
 *
 * Normal mode (viewing followers):
 *   - White card with table number + status badge
 *   - X button visible → tap to view/delete follower's job
 *
 * Selection mode (leader assigning a job):
 *   - X button hidden
 *   - Idle  → green background  (selectable)
 *   - Busy  → red background    (disabled, can't select)
 */
import { Ionicons } from '@expo/vector-icons';
import { Pressable, Text, View } from 'react-native';

import type { FollowerCardProps } from '@/types/FollowerCard';

// ── Color mapping — mirrors Java color resources ───────────────────────────────
// Normal mode — white card always
const NORMAL_BG = '#FFFFFF';

// Selection mode backgrounds
const IDLE_BG   = '#C8E6C9';  // colorGreenPastel — Idle follower, selectable
const BUSY_BG   = '#FFCDD2';  // colorAccent (light red) — Busy follower, disabled

// Status badge colors
const IDLE_COLOR = '#16a34a';  // green
const BUSY_COLOR = '#dc2626';  // red

export function FollowerCard({
  follower,
  selectionMode = false,
  onPress,
  onDelete,
}: FollowerCardProps) {
  const isBusy = follower.status === 'Busy';

  // Background mirrors FollowerAdapter.java setSelectionMode logic
  let bgColor = NORMAL_BG;
  if (selectionMode) {
    bgColor = isBusy ? BUSY_BG : IDLE_BG;
  }

  const statusColor = isBusy ? BUSY_COLOR : IDLE_COLOR;

  return (
    <Pressable
      onPress={selectionMode && isBusy ? undefined : onPress}
      disabled={selectionMode && isBusy}
      className="m-2 active:opacity-60"
    >
      <View
        className="rounded-xl items-center justify-center relative"
        style={{
          backgroundColor: bgColor,
          elevation: selectionMode ? 3 : 4,
          shadowColor: '#000',
          shadowOpacity: 0.08,
          shadowRadius: 6,
          opacity: selectionMode && isBusy ? 0.45 : 1,
          height: 120,
          borderWidth: selectionMode && !isBusy ? 2 : 0,
          borderColor: IDLE_COLOR,
        }}
      >
        {/* X button — hidden in selection mode */}
        {!selectionMode && onDelete && (
          <Pressable
            onPress={onDelete}
            className="absolute top-2 right-2 p-1"
            hitSlop={8}
            accessibilityLabel="Delete follower job"
          >
            <Ionicons name="close-circle" size={20} color="#21226b" />
          </Pressable>
        )}

        {/* Table number — large, always dark primary */}
        <Text className="text-3xl font-bold text-primary">
          {follower.tableNumber ?? '?'}
        </Text>

        {/* Status badge */}
        <View
          className="mt-1 px-2 py-0.5 rounded-full"
          style={{ backgroundColor: `${statusColor}20` }}
        >
          <Text
            className="text-xs font-bold uppercase"
            style={{ color: statusColor }}
          >
            {follower.status}
          </Text>
        </View>

        {/* Selection mode hint — green tick on idle */}
        {selectionMode && !isBusy && (
          <View className="absolute top-2 right-2">
            <Ionicons name="checkmark-circle" size={18} color={IDLE_COLOR} />
          </View>
        )}
      </View>
    </Pressable>
  );
}
