package com.indiandesigns.fabcut.data.prefs;

import com.indiandesigns.fabcut.data.network.model.ItemQuantityData;

import java.util.HashMap;

/**
 * Interface for setting and getting Access Token from
 * SharedPreferences
 */

public interface PreferencesHelper {

    String getAccessToken();

    void setAccessToken(String accessToken);

    String getRefreshToken();

    void setRefreshToken(String refreshToken);

    void setLocation(String location);

    String getLocation();

    void setRole(String role);

    String getRole();

    void deleteRole();

    HashMap<String, ItemQuantityData> getItemQuantities();

    void setItemQuantityData(String itemCode, ItemQuantityData quantityData);

    void clearItemQuantityData();
}
