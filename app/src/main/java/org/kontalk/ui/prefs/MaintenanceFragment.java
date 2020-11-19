/*
 * Kontalk Android client
 * Copyright (C) 2020 Kontalk Devteam <devteam@kontalk.org>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kontalk.ui.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;

import org.kontalk.Kontalk;
import org.kontalk.Log;
import org.kontalk.R;
import org.kontalk.service.msgcenter.MessageCenterService;


/**
 * Maintenance settings fragment.
 */
public class MaintenanceFragment extends RootPreferenceFragment {
    static final String TAG = Kontalk.TAG;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_maintenance);

        // message center restart
        final Preference restartMsgCenter = findPreference("pref_restart_msgcenter");
        restartMsgCenter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.w(TAG, "manual message center restart requested");
                MessageCenterService.restart(preference.getContext().getApplicationContext());
                Toast.makeText(preference.getContext(), R.string.msg_msgcenter_restarted, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // send our stuff to the copy database preference
        final CopyDatabasePreference copyDatabase = findPreference("pref_copy_database");
        copyDatabase.setParentFragment(this);

        if (Kontalk.get().getDefaultAccount() == null) {
            // no account, hide/disable some stuff
            restartMsgCenter.setEnabled(false);
        }

        // explain the user that the foreground service is mandatory
        if (MessageCenterService.mustSetForeground(getContext())) {
            final CheckBoxPreference foregroundService = findPreference("pref_foreground_service");
            foregroundService.setEnabled(false);
            foregroundService.setSummary(R.string.pref_title_foreground_service_mandatory);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((PreferencesActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.pref_maintenance);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CopyDatabasePreference.REQUEST_COPY_DATABASE) {
            if (resultCode == Activity.RESULT_OK) {
                Context ctx = getActivity();
                if (ctx != null && data != null && data.getData() != null) {
                    CopyDatabasePreference.copyDatabase(ctx, data.getData());
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
