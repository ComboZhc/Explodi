package org.example.explodi.mode;

import org.example.explodi.Achievement;
import org.example.explodi.Difficulty;
import org.example.explodi.Explodi;
import org.example.explodi.GameSetting;
import org.example.explodi.GameTimer;
import org.example.explodi.Help;
import org.example.explodi.Mode;
import org.example.explodi.R;
import org.example.explodi.Result;
import org.example.explodi.Sound;
import org.example.explodi.Utils;
import org.example.explodi.dialog.MyDialog;
import org.example.explodi.view.GameView;
import org.example.explodi.view.StatusView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public abstract class AbstractMode extends Activity implements Runnable{
	//Static Field
	protected static final float EPSILON = 0.000001f;
	protected boolean finished = false;
	//String
	public abstract Mode getMode();
	public static final String KEY_MODE = "mode";
	public static final String KEY_DIFFICULTY = "difficulty";
	public static final String KEY_COMPLETE = "complete";
	public static final String KEY_ACCURACY = "accuracy";
	public static final String KEY_TOTAL_SCORE = "totalScore";
	public static final String KEY_TIME = "time";
	public static final String KEY_WIN = "win";
	public static final String KEY_NEW_RECORD = "newRecord";
	
	public static final String DB_KEY_DATE = "DATE";
	public static final String DB_KEY_COMPLETE = "COMPLETE";
	public static final String DB_KEY_SCORE = "SCORE";
	public static final String DB_KEY_TIME = "TIME";
	public static final String DB_KEY_ACCURACY = "ACCURACY";
	//Database
	protected SharedPreferences db;
	protected Editor dbEditor;
	//Static Final
	public static final int[] directionI = new int[]{1, -1, 0, 0};
	public static final int[] directionJ = new int[]{0, 0, 1, -1};
	public static final int MENU_SETTINGS = 0;
	public static final int MENU_HELP = 1;
	public static final int MENU_FINISH = 2;

	protected Difficulty difficulty = Difficulty.NORMAL;
	protected int columnsSize;
	protected int rowsSize;
	
	protected boolean isGameViewReady = false;
	protected boolean isStatusViewReady = false;
	protected boolean isMapReady = false;
	protected boolean isFocusing = false;
	protected boolean isAppearing = false;
	protected boolean isFinishing = false;
	protected boolean isAllVisible = false;
	
	protected int itemFacesKind;
	protected int itemColorsOrdinaryKind;
	
	protected GameView gameView;
	protected StatusView statusView;
	protected GameTimer gameTimer = new GameTimer();
	
	protected int chooseItemColor = 0;
	protected int motionI = 0, motionJ = 0;
	protected int validClick = 0;
	protected int totalClick = 0;
	protected int remainCount = 0;
	protected int totalCount = 0;
	protected int totalScore = 0;
	protected int time = 0;
	protected int maxCount = 0;
	
	protected int nowScore = 0;
	protected String nowScoreText = null;
	protected String nowBonusText = null;
	protected String nowRemarkText = null;
	//Override
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = this.getSharedPreferences(Explodi.TAG_DB, MODE_PRIVATE);
		dbEditor = db.edit();
		//Difficulty
		difficulty = Difficulty.valueOf((getIntent().getIntExtra(KEY_DIFFICULTY, Difficulty.NORMAL.value())));
		setDifficulty(difficulty);
		//Set Media Volume Control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		//Set Content
		setContentView(R.layout.game);
		statusView = (StatusView) this.findViewById(R.id.status_view);
		statusView.setBackgroundResource(android.R.drawable.status_bar_item_app_background);
		gameView = (GameView) this.findViewById(R.id.game_view);		
		//Init Sound
		Sound.init(this);
		new Thread(this).start();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SETTINGS, 0, R.string.title_gamesetting).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_HELP, 0, R.string.title_help).setIcon(android.R.drawable.ic_menu_help);
		menu.add(0, MENU_FINISH, 0, R.string.title_finish).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {   
		Intent i;
		switch (item.getItemId()) {   
			case MENU_SETTINGS:       
				i = new Intent(this, GameSetting.class);
				startActivity(i);
				return true;   
			case MENU_HELP:       
				i = new Intent(this, Help.class);
				startActivity(i);
				return true;   
			case MENU_FINISH:
				endGame(false);
				return true;   
		}
		return false;
	}
	@Override
	protected void onPause() {
		gameTimer.pause();
		super.onPause();
	}
	@Override
	protected void onResume() {
		gameTimer.resume();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		gameTimer.stop();
		super.onDestroy();
	}
	//Setters and Getters
	public int getColumnsSize() {
		return columnsSize;
	}
	public void setColumnsSize(int columnsSize) {
		this.columnsSize = columnsSize;
	}
	public int getRowsSize() {
		return rowsSize;
	}
	public void setRowsSize(int rowsSize) {
		this.rowsSize = rowsSize;
	}
	public boolean isReady() {
		return isGameViewReady && isStatusViewReady;
	}
	public void setGameViewReady(boolean isGameViewReady) {
		this.isGameViewReady = isGameViewReady;
		if (isGameViewReady) {
			statusView.setValuesReady(true);
			statusView.setTotalRateText(remainCount, totalCount);
			statusView.setTotalScore(0);
		}
	}
	public void setStatusViewReady(boolean isStatusViewReady) {
		this.isStatusViewReady = isStatusViewReady;
	}
	public void startGame() {
		gameTimer.start();
		setAllVisible(true);
	}
	protected void setAllVisible(boolean isAllVisible){
		this.isAllVisible = isAllVisible; 
	}
	public void endGame(final boolean win) {
		if (finished)
			return;
		finished = true;
		float nowComplete = (float)(totalCount - remainCount) / (totalCount);
		float nowAccuracy = (float)(validClick) / totalClick;
		String nameDiffRecord = Utils.getKey(getMode(), difficulty);
		String nameRecord = Utils.getKey(getMode());
		
		//MANAGE RECORDS
		boolean isNewRecord = false;
		final String lastDate = db.getString(nameDiffRecord  + AbstractMode.DB_KEY_DATE, null);
		final int lastScore = db.getInt(nameDiffRecord  + AbstractMode.DB_KEY_SCORE, 0);
		final int lastTime = db.getInt(nameDiffRecord + AbstractMode.DB_KEY_TIME, 0);
		final float lastComplete = db.getFloat(nameDiffRecord  + AbstractMode.DB_KEY_COMPLETE, 0.0f);
		
		if (judgeNewRecord(totalScore, time, nowComplete, lastDate, lastScore, lastTime, lastComplete)) {
			dbEditor.putString(nameDiffRecord  + AbstractMode.DB_KEY_DATE, Utils.getNowDateString());
			dbEditor.putInt(nameDiffRecord  + AbstractMode.DB_KEY_SCORE, totalScore);
			dbEditor.putInt(nameDiffRecord  + AbstractMode.DB_KEY_TIME, time);
			dbEditor.putFloat(nameDiffRecord  + AbstractMode.DB_KEY_COMPLETE, nowComplete);
			dbEditor.commit();
			isNewRecord = true;
		}
		
		//MANAGE ACHIEVEMENTS
		if (!db.getBoolean(nameDiffRecord + Achievement.HAS_WON, false))
			dbEditor.putBoolean(nameDiffRecord + Achievement.HAS_WON, win);
		int continuous = db.getInt(nameRecord + Achievement.NUMBER_IN_A_ROW, 0);
		
		if (win)
			dbEditor.putInt(Explodi.TAG_DB + Achievement.TOTAL_WIN, db.getInt(Explodi.TAG_DB + Achievement.TOTAL_WIN, 0) + 1);
		dbEditor.putInt(Explodi.TAG_DB + Achievement.TOTAL_TIME, db.getInt(Explodi.TAG_DB + Achievement.TOTAL_TIME, 0) + time);
		
		if (win && getClass().equals(ClassicMode.class))
			dbEditor.putInt(nameRecord + Achievement.NUMBER_IN_A_ROW, continuous + 1);
		else
			if (getClass().equals(ClassicMode.class)) dbEditor.putInt(nameRecord + Achievement.NUMBER_IN_A_ROW, 0);
		
		if (win && Math.abs(nowAccuracy - 1) < EPSILON && getClass().equals(TimeMode.class))
			dbEditor.putInt(nameRecord + Achievement.NUMBER_IN_A_ROW, continuous + 1);
		else
			if (getClass().equals(TimeMode.class)) dbEditor.putInt(nameRecord + Achievement.NUMBER_IN_A_ROW, 0);
		
		dbEditor.putInt(nameRecord + Achievement.MAX_COUNT, maxCount);
		if (win) 
			dbEditor.putInt(nameRecord + Achievement.WIN_TIME, time);
		else
			dbEditor.remove(nameRecord + Achievement.WIN_TIME);
		dbEditor.putInt(Explodi.TAG_DB + Achievement.SCORE, totalScore);
		dbEditor.commit();
		
		//START RESULT
		Intent i = new Intent(this, Result.class);
		i.putExtra(KEY_MODE, getMode().name());
		i.putExtra(KEY_DIFFICULTY, difficulty);
		i.putExtra(KEY_TOTAL_SCORE, totalScore);
		i.putExtra(KEY_COMPLETE, nowComplete);
		i.putExtra(KEY_ACCURACY, nowAccuracy);
		i.putExtra(KEY_TIME, time);
		i.putExtra(KEY_WIN, win);
		i.putExtra(KEY_NEW_RECORD, isNewRecord);

		gameTimer.stop();
		startActivity(i);
		finish();
	}
	public int getItemColorsSize(){return 0;}
	public int getItemColor(int i, int j){return 0;}
	public int getItemFacesSize(){return 0;}
	public int getItemFace(int i, int j){return 0;}
	public boolean getItemFaceChanged(int i, int j){return false;}
	public boolean getItemFocus(int i, int j){return false;}
	public boolean getItemAffiliated(int i, int j){return false;}
	public boolean getItemLarge(int i, int j){return false;}
	public boolean getItemAppear(int i, int j){return false;}
	public boolean getPositionFilled(int i, int j){return true;}
	//Public Function	
	public int getBackgroundAlpha(){return 0;}
	public String getReadyText(){return null;}
	public void clear(){}
	public void initValue(){}
	public void initMap(){}
	public void appear(){}
	public void endAppear(){}
	public void checkResult(){}
	public void manageMotion(float x, float y){}
	//Protected Function
	protected boolean judgeNewRecord(int score, int time, float complete,
									 String lastDate, int lastScore, int lastTime, float lastComplete) {return false;}
	protected void generateScore(){}
	protected String remarkScore(int score) {return null;}
	protected void setDifficulty(Difficulty difficulty){}
	@Override
	public void run(){}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openAbortDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void openAbortDialog() {
		MyDialog.Builder dialogBuilder = new MyDialog.Builder(this);
		dialogBuilder.setTitle(R.string.app_name);
		dialogBuilder.setMessage(R.string.info_abort);
		dialogBuilder.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AbstractMode.this.finish();
				dialog.cancel();
			}
		});
		dialogBuilder.setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialogBuilder.show();
	}
}
