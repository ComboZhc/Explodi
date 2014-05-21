package org.example.explodi;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Sound {
	//Sound
	private static SoundPool soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
	private static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
	public static final int SE_CLEAR = 0;
	public static final int SE_WARNING = 1;
	public static final int VOICE_GOOD = 2;
	public static final int VOICE_EXCELLENT = 3;
	public static final int VOICE_AWESOME = 4;
	public static void init(Activity act) {
		soundMap.put(SE_CLEAR, soundPool.load(act, R.raw.se_clear, 1));
		soundMap.put(SE_WARNING, soundPool.load(act, R.raw.se_warning, 1));
		soundMap.put(VOICE_GOOD, soundPool.load(act, R.raw.voice_good, 1));
		soundMap.put(VOICE_EXCELLENT, soundPool.load(act, R.raw.voice_excellent, 1));
		soundMap.put(VOICE_AWESOME, soundPool.load(act, R.raw.voice_awesome, 1));
	}
	public static void play(Context context, int sound, int times, Activity act) {
		if (GameSetting.getSound(context)){
			AudioManager am = (AudioManager)act.getSystemService(Context.AUDIO_SERVICE);
			float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float audioVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float ratio = audioVolume / audioMaxVolume;
			int i = soundPool.play(soundMap.get(sound), ratio, ratio, 0, times, 1);
			Log.d("GLOBAL", "PLAYSOUND" + i);		
		}
	}
}