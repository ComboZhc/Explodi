package org.example.explodi;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class GameSetting extends PreferenceActivity {
	private static final String KEY_SOUND = "Sound";
	private static final boolean DEFAULT_SOUND = true;
	private static final String KEY_SHAKE = "Shake";
	private static final boolean DEFAULT_SHAKE = true;
	private static final String KEY_ONECLICK = "OneClick";
	private static final boolean DEFAULT_ONECLICK = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add Preference
		addPreferencesFromResource(R.xml.game_setting);
		// Set Media Volume Control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public static boolean getSound(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(KEY_SOUND, DEFAULT_SOUND);
	}

	public static boolean getShake(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(KEY_SHAKE, DEFAULT_SHAKE);
	}

	public static boolean getOneClick(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(KEY_ONECLICK, DEFAULT_ONECLICK);
	}

}
