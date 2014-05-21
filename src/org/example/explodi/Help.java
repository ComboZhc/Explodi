package org.example.explodi;

import org.example.explodi.mode.ClassicMode;
import org.example.explodi.mode.FillMode;
import org.example.explodi.mode.GravityMode;
import org.example.explodi.mode.TimeMode;

import android.app.Activity;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class Help extends Activity{
	private static final String TAG = "Help";
	private static final String SUMMARY = "Summary";
	private Resources res = null;
	//Database
	private TabHost tabHost;
	private TextView helpTitle;
	private TextView helpContent;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onRecord");
		super.onCreate(savedInstanceState);
		//Get Resources
		res = this.getResources();
		//Set Media Volume Control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		//Set Content
		setContentView(R.layout.help);
		helpTitle = (TextView)findViewById(R.id.help_title);
		helpContent = (TextView)findViewById(R.id.help_content);
		//TabHost
		tabHost = (TabHost)findViewById(R.id.tabhost_help);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec(SUMMARY).setIndicator(res.getString(R.string.text_summary), res.getDrawable(R.drawable.tab_summary)).setContent(R.id.help_layout));
		tabHost.addTab(tabHost.newTabSpec(Mode.CLASSIC.name()).setIndicator(res.getString(R.string.mode_classic), res.getDrawable(R.drawable.tab_classic)).setContent(R.id.help_layout));
		tabHost.addTab(tabHost.newTabSpec(Mode.TIME.name()).setIndicator(res.getString(R.string.mode_time), res.getDrawable(R.drawable.tab_time)).setContent(R.id.help_layout));
		tabHost.addTab(tabHost.newTabSpec(Mode.FILL.name()).setIndicator(res.getString(R.string.mode_fill), res.getDrawable(R.drawable.tab_fill)).setContent(R.id.help_layout));
		tabHost.addTab(tabHost.newTabSpec(Mode.GRAVITY.name()).setIndicator(res.getString(R.string.mode_gravity), res.getDrawable(R.drawable.tab_gravity)).setContent(R.id.help_layout));
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
            public void onTabChanged(String tag) {
				if (tag.equals(SUMMARY)) {
					helpTitle.setText(R.string.title_help_summary);
					helpContent.setText(R.string.text_help_summary);
				}
				if (tag.equals(Mode.CLASSIC.name())) {
					helpTitle.setText(res.getStringArray(R.array.mode)[0]);
					helpContent.setText(R.string.text_help_classic);
				}
				if (tag.equals(Mode.TIME.name())) {
					helpTitle.setText(res.getStringArray(R.array.mode)[1]);
					helpContent.setText(R.string.text_help_time);
				}
				if (tag.equals(Mode.FILL.name())) {
					helpTitle.setText(res.getStringArray(R.array.mode)[2]);
					helpContent.setText(R.string.text_help_fill);
				}
				if (tag.equals(Mode.GRAVITY.name())) {
					helpTitle.setText(res.getStringArray(R.array.mode)[3]);
					helpContent.setText(R.string.text_help_gravity);
				}
			}
		});
		//To solve a bug that when setCurrentTabByTag(0), it would not trigger setContent();
		tabHost.setCurrentTabByTag(Mode.CLASSIC.name());
		tabHost.setCurrentTabByTag(SUMMARY);
	}
}