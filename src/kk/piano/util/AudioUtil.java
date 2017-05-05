package kk.piano.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AudioUtil {
	private static SoundPool sPool;
	
	private static float sVolume;
	
	public static void init(Context context) {
		if (sPool != null) {
			return;
		}
		
		sVolume = Setting.getFloat(Setting.KEY_VOLUME, 0.5f);
		
		sPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
	}
	
	public static int load(Context context, int resId) {
		return sPool.load(context, resId, 1);
	}
	
	public static void play(int soundId) {
		sPool.play(soundId, sVolume, sVolume, 1, 0, 1f);
	}
	
	public static float getVolume() {
		return sVolume;
	}
	
	public static void setVolume(float volume) {
		sVolume = volume;
		Setting.putFloat(Setting.KEY_VOLUME, sVolume);
	}
}
