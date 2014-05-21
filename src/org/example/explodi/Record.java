package org.example.explodi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.example.explodi.dialog.MyDialog;
import org.example.explodi.mode.AbstractMode;
import org.example.explodi.mode.ClassicMode;
import org.example.explodi.mode.FillMode;
import org.example.explodi.mode.GravityMode;
import org.example.explodi.mode.TimeMode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class Record extends Activity implements OnClickListener {
	private static final String TAG = "Record";
	private static final String LABEL_RECORD = "label_record";
	private Resources res = null;
	// Database
	private SharedPreferences db;
	private Editor dbEditor;
	private ListView listView;
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onRecord");
		super.onCreate(savedInstanceState);
		// Get Resources
		res = this.getResources();
		// Set Media Volume Control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Set Content
		setContentView(R.layout.record);
		// DataBase
		db = this.getSharedPreferences(Explodi.TAG_DB, MODE_PRIVATE);
		dbEditor = db.edit();
		// TabHost
		tabHost = (TabHost) findViewById(R.id.tabhost_record);
		tabHost.setup();
		tabHost.addTab(tabHost
				.newTabSpec(Mode.CLASSIC.name())
				.setIndicator(res.getString(R.string.mode_classic),
						res.getDrawable(R.drawable.tab_classic))
				.setContent(R.id.record_view));
		tabHost.addTab(tabHost
				.newTabSpec(Mode.TIME.name())
				.setIndicator(res.getString(R.string.mode_time),
						res.getDrawable(R.drawable.tab_time))
				.setContent(R.id.record_view));
		tabHost.addTab(tabHost
				.newTabSpec(Mode.FILL.name())
				.setIndicator(res.getString(R.string.mode_fill),
						res.getDrawable(R.drawable.tab_fill))
				.setContent(R.id.record_view));
		tabHost.addTab(tabHost
				.newTabSpec(Mode.GRAVITY.name())
				.setIndicator(res.getString(R.string.mode_gravity),
						res.getDrawable(R.drawable.tab_gravity))
				.setContent(R.id.record_view));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tag) {
				showRecords(Mode.valueOf(tag));
			}
		});

		listView = (ListView) findViewById(R.id.record_listview);
		LinearLayout footerView = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.record_footer, null);
		listView.addFooterView(footerView);
		findViewById(R.id.button_clear_records).setOnClickListener(this);
		// To solve a bug that when setCurrentTabByTag(0), it would not trigger
		// setContent();
		tabHost.setCurrentTabByTag(Mode.TIME.name());
		tabHost.setCurrentTabByTag(Mode.CLASSIC.name());
	}

	private Map<String, Object> getRecords(Mode m, Difficulty difficulty) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String date = db.getString(Utils.getKey(m, difficulty)
				+ AbstractMode.DB_KEY_DATE, null);
		int score = db.getInt(Utils.getKey(m, difficulty)
				+ AbstractMode.DB_KEY_SCORE, 0);
		int time = db.getInt(Utils.getKey(m, difficulty)
				+ AbstractMode.DB_KEY_TIME, 0);
		float complete = db.getFloat(Utils.getKey(m, difficulty)
				+ AbstractMode.DB_KEY_COMPLETE, 0.0f);
		ret.put(LABEL_RECORD,
				getResources().getStringArray(R.array.difficulty)[difficulty
						.value()]);
		if (date == null) {
			;
			ret.put(AbstractMode.DB_KEY_DATE,
					res.getString(R.string.text_no_record));
			ret.put(AbstractMode.DB_KEY_SCORE, null);
			ret.put(AbstractMode.DB_KEY_TIME, null);
			ret.put(AbstractMode.DB_KEY_COMPLETE, null);
		} else {
			ret.put(AbstractMode.DB_KEY_DATE, res.getString(R.string.text_date)
					+ ": " + date);
			ret.put(AbstractMode.DB_KEY_SCORE,
					res.getString(R.string.text_score) + ": " + score);
			ret.put(AbstractMode.DB_KEY_TIME, res.getString(R.string.text_time)
					+ ": " + Utils.getTimeBySecond(time));
			ret.put(AbstractMode.DB_KEY_COMPLETE,
					res.getString(R.string.text_complete) + ": "
							+ Utils.getPercentByDouble(complete));
		}
		return ret;
	}

	private void showRecords(Mode m) {
		ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (Difficulty d : Difficulty.values())
			listItems.add(getRecords(m, d));
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.record_items,
				new String[] { LABEL_RECORD, AbstractMode.DB_KEY_DATE,
						AbstractMode.DB_KEY_SCORE, AbstractMode.DB_KEY_TIME,
						AbstractMode.DB_KEY_COMPLETE }, new int[] {
						R.id.label_record, R.id.date_record, R.id.score_record,
						R.id.time_record, R.id.complete_record });
		listView.setAdapter(listItemAdapter);
	}

	private void clearRecords(Mode m, Difficulty difficulty) {
		dbEditor.remove(Utils.getKey(m, difficulty) + AbstractMode.DB_KEY_DATE);
		dbEditor.remove(Utils.getKey(m, difficulty) + AbstractMode.DB_KEY_SCORE);
		dbEditor.remove(Utils.getKey(m, difficulty) + AbstractMode.DB_KEY_TIME);
		dbEditor.remove(Utils.getKey(m, difficulty)
				+ AbstractMode.DB_KEY_COMPLETE);
		dbEditor.commit();
	}

	private void clearRecords() {
		for (Mode m : Mode.values())
			for (Difficulty d : Difficulty.values())
				clearRecords(m, d);
		showRecords(Mode.valueOf(tabHost.getCurrentTabTag()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_clear_records:
			showClearDialog();
			break;
		}
	}

	private void showClearDialog() {

		MyDialog.Builder dialog = new MyDialog.Builder(this);
		dialog.setTitle(Explodi.TAG);
		dialog.setMessage(res.getString(R.string.info_clear_records));
		dialog.setPositiveButton(res.getString(R.string.text_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearRecords();
						dialog.cancel();
					}
				});
		dialog.setNegativeButton(res.getString(R.string.text_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dialog.show(R.style.InfoDialog);
	}

}
