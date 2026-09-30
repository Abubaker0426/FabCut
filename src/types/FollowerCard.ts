import type { Follower } from './leader';

export interface FollowerCardProps {
  follower: Follower;
  selectionMode?: boolean;
  onPress?: () => void;
  onDelete?: () => void;
}
