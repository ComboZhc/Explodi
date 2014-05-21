package org.example.explodi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.explodi.dialog.MyDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Achievement extends Activity implements OnClickListener{
	private static final String TAG = "Achievement";
	public static final String ICON = "Icon";
	public static final String TITLE = "Title";
	public static final String SUMMARY = "Summary";
	
	//Database
	private SharedPreferences db;
	private Editor dbEditor;
	private ListView listView;
	private Resources res;
	private static final String[] achievementsDB = {"HERO", "STAR", "TROPHY", "FARSEER", "MASTER", "GOLD_EYE", "PAINTER", "LIGHTNING"};
	private static final int[] achievementsID = {
		R.drawable.achievement_hero, 
		R.drawable.achievement_star,
		R.drawable.achievement_trophy,
		R.drawable.achievement_luck, 
		R.drawable.achievement_master, 
		R.drawable.achievement_eye, 
		R.drawable.achievement_painter,
		R.drawable.achievement_lightning
		};
	private static final int[] achievementsBlackID = {
		R.drawable.achievement_hero_black, 
		R.drawable.achievement_star_black,
		R.drawable.achievement_trophy_black,
		R.drawable.achievement_luck_black, 
		R.drawable.achievement_master_black, 
		R.drawable.achievement_eye_black, 
		R.drawable.achievement_painter_black,
		R.drawable.achievement_lightning_black
		};
	
	private String[] achievementsTitle;

	public static final String SCORE = "SCORE";
	public static final String MAX_COUNT = "MAX_COUNT";
	public static final String NOW_COUNT = "COUNT";
	public static final String TOTAL_TIME = "TOTAL_TIME";
	public static final String TOTAL_WIN = "TOTAL_WIN";
	public static final String NUMBER_IN_A_ROW = "IN_A_ROW";
	public static final String HAS_WON = "HAS_WON";
	public static final String WIN_TIME = "WIN_TIME";
	
	private static final int COUNT_OF_HARD_MODE_VICTORY = 4;
	private static final int TIME_PLAYED = 86400;
	private static final int COUNT_OF_VICTORY = 100;
	private static final int IN_A_ROW_CLASSIC_MODE = 5;
	private static final int SCORE_HIGH_ROLLER = 30000;
	private static final int IN_A_ROW_TIME_MODE = 3;
	private static final int COUNT_IN_FILL_MODE = 15;
	private static final int TIME_GRAVITY_MODE = 30;
	private ArrayList<Map<String, Object>> list;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onAchievement");
		super.onCreate(savedInstanceState);
		//Set Content
		setContentView(R.layout.achievement);
		//DataBase
		db = this.getSharedPreferences(Explodi.TAG_DB, MODE_PRIVATE);
		dbEditor = db.edit();
		
		listView = (ListView)findViewById(R.id.achievement_listview);
		LinearLayout footerView = (LinearLayout)this.getLayoutInflater().inflate(R.layout.achievement_footer, null);
		listView.addFooterView(footerView);
		findViewById(R.id.button_clear_achievements).setOnClickListener(this);
		res = getResources();
	
		achievementsTitle = res.getStringArray(R.array.achievements_title);
		showAchievements();
	}
	private void showAchievements() {
		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < achievementsTitle.length; ++i)
			list.add(getAchievement(this, db, dbEditor, i));
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.achievement_items,
				new String[]{ICON, TITLE, SUMMARY},
				new int[]{R.id.achievement_icon, R.id.achievement_title, R.id.achievement_summary});
		listView.setAdapter(adapter);
	}
	
	private static void clearAchievements(Context context, SharedPreferences db, Editor dbEditor) {
		for (int i = 0; i < achievementsDB.length; ++i)
			dbEditor.remove(Explodi.TAG_DB + achievementsDB[i]);
		dbEditor.remove(Explodi.TAG_DB + achievementsDB[0] + NOW_COUNT);
		for (Mode m : Mode.values())
			dbEditor.remove(Utils.getKey(m, Difficulty.HARD) + HAS_WON);
		dbEditor.remove(Explodi.TAG_DB + TOTAL_TIME);
		dbEditor.remove(Explodi.TAG_DB + TOTAL_WIN);
		dbEditor.remove(Utils.getKey(Mode.CLASSIC) + NUMBER_IN_A_ROW);
		dbEditor.remove(Utils.getKey(Mode.TIME) + NUMBER_IN_A_ROW);
		dbEditor.remove(Utils.getKey(Mode.FILL) + MAX_COUNT);
		dbEditor.remove(Utils.getKey(Mode.GRAVITY) + WIN_TIME);
		dbEditor.commit();
	}
	
	private static Map<String, Object> getAchievement(Context context, SharedPreferences db, Editor dbEditor, int which) {
		Map<String, Object> ret = new HashMap<String, Object>();
		boolean isAchieved = db.getBoolean(Explodi.TAG_DB + achievementsDB[which], false);
		ret.put(ICON, isAchieved ? achievementsID[which] : achievementsBlackID[which]);
		String appendString = "";
		switch (which) {
		case 0:
			if (!isAchieved)
				appendString = "(" + db.getInt(Explodi.TAG_DB + achievementsDB[0] + NOW_COUNT, 0) + "/" + COUNT_OF_HARD_MODE_VICTORY + ")" ;
			break;
		case 1:
			if (!isAchieved)
				appendString = "(" + db.getInt(Explodi.TAG_DB + TOTAL_TIME, 0) + "/" + TIME_PLAYED + ")";
			break;
		case 2:
			if (!isAchieved)
				appendString = "(" + db.getInt(Explodi.TAG_DB + TOTAL_WIN, 0) + "/" + COUNT_OF_VICTORY + ")";
			break;
		case 4:
			if (!isAchieved)
				appendString = "(" + db.getInt(Utils.getKey(Mode.CLASSIC) + NUMBER_IN_A_ROW, 0) + "/" + IN_A_ROW_CLASSIC_MODE + ")";
			break;
		case 5:
			if (!isAchieved)
				appendString = "(" + db.getInt(Utils.getKey(Mode.TIME) + NUMBER_IN_A_ROW, 0) + "/" + IN_A_ROW_TIME_MODE + ")";
			break;
		}
		ret.put(TITLE, context.getResources().getStringArray(R.array.achievements_title)[which] + appendString);
		ret.put(SUMMARY, context.getResources().getStringArray(R.array.achievements_summary)[which]);
		return ret;
	}
	public static List<Map<String, Object>> refresh(Context context, SharedPreferences db, Editor dbEditor) {
		//HERO
		List<Map<String, Object>> newAchievements = new ArrayList<Map<String, Object>>();
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[0], false)) {
			int count = 0;
			for (Mode m : Mode.values())
				if (db.getBoolean(Utils.getKey(m, Difficulty.HARD) + HAS_WON, false))
						++count;
			dbEditor.putInt(Explodi.TAG_DB + achievementsDB[0] + NOW_COUNT, count);
			if (count == Mode.values().length) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[0], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 0));
			}
		}
		//STAR
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[1], false)) {
			if (db.getInt(Explodi.TAG_DB + TOTAL_TIME, 0) >= TIME_PLAYED) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[1], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 1));
			}
		}
		//TROPHY
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[2], false)) {
			if (db.getInt(Explodi.TAG_DB + TOTAL_WIN, 0) >= COUNT_OF_VICTORY) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[2], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 2));
			}
		}
 		//HIGH ROLLER
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[3], false)) {
			if (db.getInt(Explodi.TAG_DB + SCORE, 0) > SCORE_HIGH_ROLLER) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[3], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 3));
			}
		}
		//MASTER
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[4], false)) {
			if (db.getInt(Utils.getKey(Mode.CLASSIC) + NUMBER_IN_A_ROW, 0) >= IN_A_ROW_CLASSIC_MODE) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[4], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 4));
			}
		}
		//GOLD EYE
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[5], false)) {
			if (db.getInt(Utils.getKey(Mode.TIME) + NUMBER_IN_A_ROW, 0) >= IN_A_ROW_TIME_MODE) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[5], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 5));
			}
		}
		//PAINTER
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[6], false)) {
			if (db.getInt(Utils.getKey(Mode.FILL) + MAX_COUNT, 0) >= COUNT_IN_FILL_MODE) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[6], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 6));
			}
		}
		//LIGHTNING		
		if (!db.getBoolean(Explodi.TAG_DB + achievementsDB[7], false)) {
			if (db.getInt(Utils.getKey(Mode.GRAVITY) + WIN_TIME, TIME_GRAVITY_MODE + 1) <= TIME_GRAVITY_MODE) {
				dbEditor.putBoolean(Explodi.TAG_DB + achievementsDB[7], true);
				dbEditor.commit();
				newAchievements.add(getAchievement(context, db, dbEditor, 7));
			}
		}
		dbEditor.commit();
		return newAchievements;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_clear_achievements:
			showClearDialog();
			break;
		}
	}
	
	private void showClearDialog() {
		MyDialog.Builder dialog = new MyDialog.Builder(this);
		dialog.setTitle(Explodi.TAG);
		dialog.setMessage(res.getString(R.string.info_clear_achievements));
		dialog.setPositiveButton(res.getString(R.string.text_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clearAchievements(Achievement.this, db, dbEditor);
				showAchievements();
				dialog.cancel();
			}
		});
		dialog.setNegativeButton(res.getString(R.string.text_no),  new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show(R.style.InfoDialog);
	}
	
}
