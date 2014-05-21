package org.example.explodi;

import java.util.List;
import java.util.Map;

import org.example.explodi.mode.AbstractMode;
import org.example.explodi.mode.ClassicMode;
import org.example.explodi.mode.FillMode;
import org.example.explodi.mode.GravityMode;
import org.example.explodi.mode.TimeMode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Result extends Activity {
	private static final String TAG = "Result";
	private Mode mode;
	private String modeName;
	private Class<?> modeClass;
	private Difficulty difficulty;
	private int totalScore;
	private float complete;
	private float accuracy;
	private int time;
	private boolean win;
	private boolean isNewRecord;
	private TextView resultTitleView;
	private TextView resultNewRecordView;
	private TextView resultNewAchievementView;
	private TextView statisticLabelView;
	private TextView statisticContentView;
	private Vibrator vibrator;
	private Animation scaleAnimation;
	private LayoutInflater inflater;
	private LinearLayout achievementLayout;

	// Database
	private SharedPreferences db;
	private Editor dbEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onResult");
		super.onCreate(savedInstanceState);
		// Database
		db = this.getSharedPreferences(Explodi.TAG_DB, MODE_PRIVATE);
		dbEditor = db.edit();
		// Set content
		setContentView(R.layout.result);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		achievementLayout = (LinearLayout) findViewById(R.id.record_achievement_view);
		// Set Media Volume Control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		resultTitleView = (TextView) this.findViewById(R.id.result_title);
		resultNewRecordView = (TextView) this
				.findViewById(R.id.result_new_record);
		resultNewAchievementView = (TextView) this
				.findViewById(R.id.result_new_achievement);
		statisticLabelView = (TextView) this.findViewById(R.id.statistic_label);
		statisticContentView = (TextView) this
				.findViewById(R.id.statistic_content);
		statisticLabelView.setText("");
		statisticContentView.setText("");

		// GetExtra
		mode = Mode.valueOf(getIntent().getStringExtra(AbstractMode.KEY_MODE));
		Log.d(TAG, mode.toString());
		switch (mode) {
		case CLASSIC:
			modeName = getString(R.string.mode_classic);
			modeClass = ClassicMode.class;
			break;
		case TIME:
			modeName = getString(R.string.mode_time);
			modeClass = TimeMode.class;
			break;
		case FILL:
			modeName = getString(R.string.mode_fill);
			modeClass = FillMode.class;
			break;
		case GRAVITY:
			modeName = getString(R.string.mode_gravity);
			modeClass = GravityMode.class;
			break;
		}
		difficulty = Difficulty.valueOf(getIntent().getIntExtra(
				AbstractMode.KEY_DIFFICULTY, Difficulty.NORMAL.value()));
		complete = getIntent().getFloatExtra(AbstractMode.KEY_COMPLETE, 0f);
		totalScore = getIntent().getIntExtra(AbstractMode.KEY_TOTAL_SCORE, 0);
		accuracy = getIntent().getFloatExtra(AbstractMode.KEY_ACCURACY, 0f);
		time = getIntent().getIntExtra(AbstractMode.KEY_TIME, 0);
		win = getIntent().getBooleanExtra(AbstractMode.KEY_WIN, true);
		isNewRecord = getIntent().getBooleanExtra(AbstractMode.KEY_NEW_RECORD,
				false);

		// SetResult
		Typeface typeface = Typeface
				.createFromAsset(getAssets(), "bradley.ttf");
		resultTitleView.setTypeface(typeface, Typeface.BOLD);
		resultTitleView.setTextColor(win ? Color.RED : Color.GREEN);
		resultTitleView.setText(win ? getString(R.string.text_win) : getString(R.string.text_lose));
		resultNewRecordView.setTypeface(typeface, Typeface.BOLD);
		if (isNewRecord)
			resultNewRecordView
					.setText(getString(R.string.text_new_record));
		else
			resultNewRecordView.setVisibility(View.GONE);

		// SetStatistics
		// Set Achievements
		List<Map<String, Object>> newAchievements = Achievement.refresh(this,
				db, dbEditor);

		resultNewAchievementView.setTypeface(typeface, Typeface.BOLD);
		if (!newAchievements.isEmpty())
			resultNewAchievementView.setText(getString(R.string.text_new_achievement));
		else
			resultNewAchievementView.setVisibility(View.GONE);

		for (Map<String, Object> element : newAchievements) {
			LinearLayout recordAchievementLayout = (LinearLayout) inflater
					.inflate(R.layout.result_achievement_items, null);
			ImageView iv = (ImageView) recordAchievementLayout
					.findViewById(R.id.record_achievement_icon);
			iv.setImageResource((Integer) element.get(Achievement.ICON));
			TextView tv = (TextView) recordAchievementLayout
					.findViewById(R.id.record_achievement_text);
			tv.setText((String) element.get(Achievement.TITLE));
			achievementLayout.addView(recordAchievementLayout);
		}
		// Mode
		statisticLabelView.append(getString(R.string.text_mode) + "\n");
		statisticContentView.append(modeName + "\n");

		// Difficulty
		statisticLabelView.append(getString(R.string.text_difficulty)
				+ "\n");
		statisticContentView
				.append(getResources().getStringArray(R.array.difficulty)[difficulty.value()]
						+ "\n");

		// Complete
		statisticLabelView.append(getString(R.string.text_complete) + "\n");
		statisticContentView.append(Utils.getPercentByDouble(complete) + "\n");

		// Accuracy
		statisticLabelView.append(getString(R.string.text_accuracy) + "\n");
		statisticContentView.append(Utils.getPercentByDouble(accuracy) + "\n");

		// Score
		statisticLabelView.append(getString(R.string.text_score) + "\n");
		statisticContentView.append(totalScore + "\n");

		// Time
		statisticLabelView.append(getString(R.string.text_time));
		statisticContentView.append(Utils.getTimeBySecond(time));

		// Shake
		if (GameSetting.getShake(this)) {
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			long[] vibrateArray = null;
			if (win)
				vibrateArray = new long[] { 0, 150, 150, 150, 150, 150, 150,
						150, 150, 1250 };
			else
				vibrateArray = new long[] { 0, 500, 200, 500, 200, 1250 };
			vibrator.vibrate(vibrateArray, -1);
		}

		// Load Animation
		scaleAnimation = AnimationUtils
				.loadAnimation(this, R.anim.scale_result);

		// Proceed Animation
		resultTitleView.startAnimation(scaleAnimation);
	}

	@Override
	protected void onStop() {
		if (vibrator != null)
			vibrator.cancel();
		super.onStop();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_restart:
			restartGame();
			break;
		}
	}

	private void restartGame() {
		Log.d(TAG, "on Restart()");
		Intent i = new Intent(this, modeClass);
		i.putExtra(AbstractMode.KEY_DIFFICULTY, difficulty);
		startActivity(i);
		finish();
	}

}
