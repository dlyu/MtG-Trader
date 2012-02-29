/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtg.trade.guide;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class OptionsActivity extends PreferenceActivity {
	private Preference mUpdatePrefs;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.PREFERENCES_NAME));
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        CheckBoxPreference enableAutoUpdate = (CheckBoxPreference)getPreferenceManager().findPreference("enableUpdate");
        if (enableAutoUpdate != null) {
        	mUpdatePrefs = getPreferenceManager().findPreference("updatePrefs");
        	mUpdatePrefs.setEnabled(enableAutoUpdate.isChecked());
        	
        	enableAutoUpdate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference arg0, Object arg1) {
					// NOTE THAT THIS IS BEFORE THE CHECKBOX PREFERENCE CHANGES ITS VALUE
					mUpdatePrefs.setEnabled(!((CheckBoxPreference)arg0).isChecked());
					
					return true;
				}
        		
        	});
        }
    }
}
