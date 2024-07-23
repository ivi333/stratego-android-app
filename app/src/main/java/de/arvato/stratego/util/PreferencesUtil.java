package de.arvato.stratego.util;

import android.content.SharedPreferences;

import de.arvato.stratego.StrategoConstants;

public class PreferencesUtil {

    public static String getUserPreferredName () {
        SharedPreferences sharedPreferences = getSharedPreferences(StrategoConstants.PREFERENCES_NAME, MODE_PRIVATE);

        return "";
    }

}
