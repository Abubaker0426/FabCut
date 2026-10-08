import { useGpsCoords } from './useGpsCoords';
import { useLocations } from './useLocations';
import { useAppStore } from '@/store/appStore';

/**
 * useLocationValidation
 *
 * Combines useGpsCoords + useLocations into a single hook.
 * GPS and location validation are always used together — GPS feeds into
 * the location API call, so it makes sense to pair them.
 *
 * Returns:
 *   - gps         : full GPS state (loading/granted/denied/error)
 *   - locations   : list of valid factory locations from the API
 *   - isLoading   : true while GPS or API call is in progress
 *   - error       : API error if location validation failed
 *   - isRefetching: true while re-fetching
 *
 * Used in: MainScreen
 * Replaces: separate useGpsCoords + useLocations calls
 */
export const useLocationValidation = () => {
  const gps    = useGpsCoords();
  const coords = gps.status === 'granted' ? gps.coords : null;

  const {
    data: locations,
    isLoading: isLocationsLoading,
    error: locationsError,
    isRefetching,
  } = useLocations(coords);

  const isLoading = gps.status === 'loading' || isLocationsLoading;

  return {
    gps,
    coords,
    locations,
    isLoading,
    isLocationsLoading,
    locationsError,
    isRefetching,
  };
};
