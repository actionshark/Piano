package kk.piano.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.stone.app.Res;
import com.stone.app.Setting;
import com.stone.log.Logger;

import android.content.Context;
import android.content.res.AssetManager;

public class NoteUtil {
	public static final long INTER_MIN = 100;
	public static final long INTER_MAX = 1500;
	public static final long INTER_DEF = 300;
	
	public static class Note {
		public int id;
		public char store;
		public int top;
		public String center;
		public int bottom;
		public int file;
		public boolean isWhite;
	}

	private static List<Note> sList;
	
	private static Note sDef;

	static final String KEY_ID = "id";
	static final String KEY_STORE = "store";
	static final String KEY_TOP = "top";
	static final String KEY_CENTER = "center";
	static final String KEY_BOTTOM = "bottom";
	static final String KEY_FILE = "file";

	public static void init(Context context) {
		if (sList != null) {
			return;
		}

		AssetManager am = context.getAssets();

		try {
			InputStream is = am.open("note_config.json");
			String str = FileUtil.readString(is);

			JSONArray ja = new JSONArray(str);
			int length = ja.length();
			sList = new ArrayList<Note>(length);

			sList.add(new Note());

			for (int i = 0; i < length; i++) {
				JSONObject jo = ja.getJSONObject(i);
				Note note = new Note();

				note.id = jo.getInt(KEY_ID);
				note.store = jo.getString(KEY_STORE).charAt(0);
				note.top = jo.getInt(KEY_TOP);
				note.center = jo.getString(KEY_CENTER);
				note.bottom = jo.getInt(KEY_BOTTOM);
				note.isWhite = note.center.length() == 1;

				int resId = Res.getInstance().getRawId(jo.getString(KEY_FILE));
				note.file = AudioUtil.load(context, resId);

				sList.add(note);
			}
		} catch (Exception e) {
			Logger.print(null, e);
		}
		
		sDef = new Note();
		sDef.id = 0;
		sDef.store = '0';
		sDef.top = 0;
		sDef.center = "0";
		sDef.bottom = 0;
		sDef.isWhite = true;
	}

	public static int size() {
		return sList.size() - 1;
	}
	
	public static long getInter() {
		return Setting.getInstance().getLong(Const.KEY_PLAY_INTER, INTER_DEF);
	}
	
	public static void setInter(long inter) {
		Setting.getInstance().setLong(Const.KEY_PLAY_INTER, inter);
	}
	
	public static int inter2Progress(long inter) {
		return (int) ((inter - INTER_MIN) * 100 / (INTER_MAX - INTER_MIN));
	}
	
	public static long progress2Inter(int progress) {
		return INTER_MIN + (INTER_MAX - INTER_MIN) * progress / 100;
	}
 
	public static Note getNodeById(int id) {
		Note note = sList.get(id);
		
		if (note == null) {
			note = sDef;
		}
		
		return note;
	}

	public static Note getNodeByStore(char ch) {
		for (Note tn : sList) {
			if (tn.store == ch) {
				return tn;
			}
		}

		return sDef;
	}
	
	public static List<Note> toNotes(String str) {
		List<Note> list = new ArrayList<Note>();
		int len = str == null ? 0 : str.length();
		
		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);
			Note note = getNodeByStore(ch);
			list.add(note);
		}
		
		return list;
	}
	
	public static String toString(List<Note> list) {
		StringBuilder sb = new StringBuilder();
		
		for (Note note : list) {
			sb.append(note.store);
		}
		
		return sb.toString();
	}
}
