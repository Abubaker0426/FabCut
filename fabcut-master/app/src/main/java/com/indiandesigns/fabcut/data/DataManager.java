package com.indiandesigns.fabcut.data;

import com.indiandesigns.fabcut.data.db.DbHelper;
import com.indiandesigns.fabcut.data.network.ApiHelper;
import com.indiandesigns.fabcut.data.prefs.PreferencesHelper;

/**
 * Manages Database functions, SharedPreferences actions and API calls
 */

public interface DataManager extends PreferencesHelper, ApiHelper, DbHelper {

    void updateApiHeader(String accessToken);
}
