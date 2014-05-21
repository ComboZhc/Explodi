package org.example.explodi.dialog;

import org.example.explodi.Difficulty;
import org.example.explodi.Explodi;
import org.example.explodi.Mode;
import org.example.explodi.R;
import org.example.explodi.mode.AbstractMode;
import org.example.explodi.mode.ClassicMode;
import org.example.explodi.mode.FillMode;
import org.example.explodi.mode.GravityMode;
import org.example.explodi.mode.TimeMode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogChooseGame extends Activity implements OnClickListener {
	private Class<? extends AbstractMode> nowClass;
	private Difficulty nowDifficulty = Difficulty.NORMAL;
	private Mode nowMode = Mode.CLASSIC;
	private String nowTagName = null;
	protected SharedPreferences db;
	protected Editor dbEditor;
	private Resources res;

	private static String LAST_MODE = "LMODE";
	private static String LAST_DIFFICULTY = "LDIFF";
	private Mode lastMode = Mode.CLASSIC;
	private int lastModeID = 0;
	private Difficulty lastDifficulty = Difficulty.NORMAL;
	private int[] modesID = { R.id.choose_classic, R.id.choose_time,
			R.id.choose_fill, R.id.choose_gravity };
	private int[] difficultiesID = { R.id.choose_easy, R.id.choose_medium,
			R.id.choose_hard };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_game);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		db = this.getSharedPreferences(Explodi.TAG_DB, MODE_PRIVATE);
		dbEditor = db.edit();
		res = this.getResources();
		lastMode = Mode.valueOf(db.getString(Explodi.TAG_DB + LAST_MODE, Mode.CLASSIC.name()));
		lastDifficulty = Difficulty.valueOf(db.getInt(Explodi.TAG_DB
				+ LAST_DIFFICULTY, Difficulty.EASY.value()));
		switch (lastMode) {
		case CLASSIC:
			lastModeID = R.id.choose_classic;
			break;
		case TIME:
			lastModeID = R.id.choose_time;
			break;
		case FILL:
			lastModeID = R.id.choose_fill;
			break;
		case GRAVITY:
			lastModeID = R.id.choose_gravity;
			break;
		}
		chooseMode(lastModeID);
		chooseDifficulty(difficultiesID[lastDifficulty.value()]);

		for (int id : modesID)
			findViewById(id).setOnClickListener(this);
		for (int id : difficultiesID)
			findViewById(id).setOnClickListener(this);

	}

	private void chooseMode(int id) {
		for (int i : modesID) {
			ImageView iv = (ImageView) findViewById(i);
			iv.setBackgroundResource(R.drawable.icon_unpressed);
		}
		ImageView iv = (ImageView) findViewById(id);
		iv.setBackgroundResource(R.drawable.icon_pressed);
		TextView tv = (TextView) findViewById(R.id.choose_mode);
		switch (id) {
		case R.id.choose_classic:
			nowMode = Mode.CLASSIC;
			nowTagName = res.getString(R.string.mode_classic);
			nowClass = ClassicMode.class;
			break;
		case R.id.choose_time:
			nowMode = Mode.TIME;
			nowTagName = res.getString(R.string.mode_time);
			nowClass = TimeMode.class;
			break;
		case R.id.choose_fill:
			nowMode = Mode.FILL;
			nowTagName = res.getString(R.string.mode_fill);
			nowClass = FillMode.class;
			break;
		case R.id.choose_gravity:
			nowMode = Mode.GRAVITY;
			nowTagName = res.getString(R.string.mode_gravity);
			nowClass = GravityMode.class;
			break;
		}
		tv.setText(getResources().getString(R.string.text_mode) + ':'
				+ nowTagName);
	}

	private void chooseDifficulty(int id) {
		for (int i : difficultiesID) {
			ImageView iv = (ImageView) findViewById(i);
			iv.setBackgroundResource(R.drawable.icon_unpressed);
		}
		ImageView iv = (ImageView) findViewById(id);
		iv.setBackgroundResource(R.drawable.icon_pressed);
		TextView tv = (TextView) findViewById(R.id.choose_difficulty);
		switch (id) {
		case R.id.choose_easy:
			nowDifficulty = Difficulty.EASY;
			break;
		case R.id.choose_medium:
			nowDifficulty = Difficulty.NORMAL;
			break;
		case R.id.choose_hard:
			nowDifficulty = Difficulty.HARD;
			break;
		}
		tv.setText(getResources().getString(R.string.text_difficulty)
				+ ':'
				+ getResources().getStringArray(R.array.difficulty)[nowDifficulty
						.value()]);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.choose_classic:
		case R.id.choose_time:
		case R.id.choose_fill:
		case R.id.choose_gravity:
			chooseMode(v.getId());
			break;
		case R.id.choose_easy:
		case R.id.choose_medium:
		case R.id.choose_hard:
			chooseDifficulty(v.getId());
			break;
		case R.id.button_start_game:
			dbEditor.putString(Explodi.TAG_DB + LAST_MODE, nowMode.name());
			dbEditor.putInt(Explodi.TAG_DB + LAST_DIFFICULTY,
					nowDifficulty.value());
			dbEditor.commit();
			Intent i = new Intent(this, nowClass);
			i.putExtra(AbstractMode.KEY_DIFFICULTY, nowDifficulty.value());
			startActivity(i);
			finish();
			break;
		}

	}
}
