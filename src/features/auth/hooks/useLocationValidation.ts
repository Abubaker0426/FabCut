import { useGpsCoords } from './useGpsCoords';
import { useLocations } from './useLocations';

export const useLocationValidation = () => {
  const gps = useGpsCoords();
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
