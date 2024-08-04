package de.arvato.stratego.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import de.arvato.stratego.R;
import de.arvato.stratego.SettingsActivity;
import de.arvato.stratego.StrategoConstants;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference languagePreference = findPreference(SettingsActivity.KEY_LANGUAGE);
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Save new language preference
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SettingsActivity.KEY_LANGUAGE, (String) newValue);
                editor.apply();

                return true;
            });
        }
    }
}