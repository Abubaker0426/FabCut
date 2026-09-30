import type { OcLay } from './leader';

export interface JobListRowProps {
  job: OcLay;
  onBundlePress?: () => void;
}
