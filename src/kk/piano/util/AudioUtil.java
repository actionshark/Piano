package kk.piano.util;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioUtil {
	public static void play(Context context, int resid) {
		MediaPlayer player = MediaPlayer.create(context, resid);
		player.start();
	}
}
