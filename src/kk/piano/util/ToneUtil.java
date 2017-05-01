package kk.piano.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

public class ToneUtil {
	public static class ToneNode {
		public int id;
		public char store;
		public String top;
		public String center;
		public String bottom;
		public int file;
		public boolean isWhite;
	}
	
	private static List<ToneNode> sList;
	
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
			InputStream is = am.open("tone_config.json");
			String str = FileUtil.readString(is);
			
			JSONArray ja = new JSONArray(str);
			int length = ja.length();
			sList = new ArrayList<ToneNode>(length);
			
			sList.add(new ToneNode());
			
			for (int i = 0; i < length; i++) {
				JSONObject jo = ja.getJSONObject(i);
				ToneNode tn = new ToneNode();
				
				tn.id = jo.getInt(KEY_ID);
				tn.store = jo.getString(KEY_STORE).charAt(0);
				tn.top = jo.getString(KEY_TOP);
				tn.center = jo.getString(KEY_CENTER);
				tn.bottom = jo.getString(KEY_BOTTOM);
				tn.file = AppUtil.getId("raw", jo.getString(KEY_FILE));
				tn.isWhite = tn.center.length() == 1;
				
				sList.add(tn);
			}
		} catch (Exception e) {
			Logger.print(null, e);
		}
	}
	
	public static int size() {
		return sList.size() - 1;
	}
	
	public static ToneNode getNodeById(int id) {
		return sList.get(id);
	}
	
	public static ToneNode getNodeByStore(char ch) {
		for (ToneNode tn : sList) {
			if (tn.store == ch) {
				return tn;
			}
		}
		
		return sList.get(0);
	}
}
