package com.indiandesigns.fabcut.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indiandesigns.fabcut.data.network.model.ItemQuantityData;
import com.indiandesigns.fabcut.di.ApplicationContext;
import com.indiandesigns.fabcut.di.PreferenceInfo;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the retrieval and update of Access Token
 * from SharedPreferences
 */

@Singleton
public class AppPreferencesHelper implements PreferencesHelper {

    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
    private static final String PREF_KEY_LOCATION = "PREF_KEY_LOCATION";
    private static final String PREF_KEY_ROLE = "PREF_KEY_ROLE";
    private static final String PREF_KEY_REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN";
    private static final String PREF_ITEM_QUANTITIES = "PREF_ITEM_QUANTITIES";

    private final SharedPreferences mPrefs;

    /**
     * Parameterized Constructor
     * <p>
     * Instantiates SharedPreferences with prefFileName
     *
     * @param context      Injected with Dagger
     * @param prefFileName Injected with Dagger
     */

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    /**
     * Fetches the Access token from SharedPreferences
     *
     * @return Value of Access Token from SharedPreferences
     */

    @Override
    public String getAccessToken() {
        return mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null);
    }

    /**
     * Fetches the Refresh token from SharedPreferences
     *
     * @return Value of Refresh Token from SharedPreferences
     */

    @Override
    public String getRefreshToken() {
        return mPrefs.getString(PREF_KEY_REFRESH_TOKEN, null);
    }

    /**
     * Opens the editor for SharedPreferences and
     * updates the value of Access Token
     *
     * @param accessToken New value for Access Token
     */

    @Override
    public void setAccessToken(String accessToken) {
        mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    /**
     * Opens the editor for SharedPreferences and
     * updates the value of Refresh Token
     *
     * @param refreshToken New value for Refresh Token
     */

    @Override
    public void setRefreshToken(String refreshToken) {
        mPrefs.edit().putString(PREF_KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    /**
     * Opens the editor for SharedPreferenes and updates the current location
     * @param location the current location
     */
    public void setLocation(String location) {
        mPrefs.edit().putString(PREF_KEY_LOCATION, location).apply();
    }

    /**
     * Fetches the current location from SharedPreferences and returns it
     * @return
     */
    public String getLocation() {
        return mPrefs.getString(PREF_KEY_LOCATION, null);
    }

    /**
     * Sets the current role in the SharedPreferences
     * @param role the current role for the device
     */
    public void setRole(String role) {
        mPrefs.edit().putString(PREF_KEY_ROLE, role).apply();
    }

    /**
     * Fetches the current role stored in SharedPreferences
     * @return the current role
     */
    public String getRole() {
        return mPrefs.getString(PREF_KEY_ROLE, null);
    }

    /**
     * Removes the PREF_KEY_ROLE from SharedPreferences because the device has been unregistered
     */
    public void deleteRole() {
        mPrefs.edit().remove(PREF_KEY_ROLE).apply();
    }

    /**
     * Fetches the current cut and assigned quantities for all item codes
     * @return Map of quantities data with item code
     */
    @Override
    public HashMap<String, ItemQuantityData> getItemQuantities() {
        String defQuantities = new Gson().toJson(new HashMap<String, ItemQuantityData>());
        String quantitiesString = mPrefs.getString(PREF_ITEM_QUANTITIES, defQuantities);
        TypeToken<HashMap<String, ItemQuantityData>> token = new TypeToken<HashMap<String, ItemQuantityData>>() {};
        HashMap<String, ItemQuantityData> quantitiesMap = new Gson().fromJson(quantitiesString, token.getType());
        return quantitiesMap;
    }

    /**
     * Sets the current cut and assigned quantities for an item codes
     * @return
     */
    @Override
    public void setItemQuantityData(String itemCode, ItemQuantityData quantityData) {
        HashMap<String, ItemQuantityData> savedQuantities = getItemQuantities();
        savedQuantities.put(itemCode, quantityData);
        String savedQuantitiesString = new Gson().toJson(savedQuantities);
        mPrefs.edit().putString(PREF_ITEM_QUANTITIES, savedQuantitiesString).apply();
    }

    /**
     * Clear quantity item data
     */
    @Override
    public void clearItemQuantityData() {
        mPrefs.edit().putString(PREF_ITEM_QUANTITIES, null).apply();
    }
}
