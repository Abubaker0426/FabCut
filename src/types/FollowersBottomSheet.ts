import type { Follower, OcLay } from './leader';

export interface FollowersBottomSheetProps {
  visible: boolean;
  onClose: () => void;
  selectionMode?: boolean;
  onFollowerPress?: (f: Follower) => void;
  onJobPress?: (j: OcLay) => void;
}
