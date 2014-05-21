package org.example.explodi;

import org.example.explodi.dialog.DialogChooseGame;
import org.example.explodi.dialog.MyDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Explodi extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    public static final String TAG = "Explodi";
    public static final String TAG_DB = "DB_EXPLODI_";
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.button_start:
			i = new Intent(this, DialogChooseGame.class);
			startActivity(i);
			break;
		case R.id.button_record:
			i = new Intent(this, Record.class);
			startActivity(i);
			break;
		case R.id.button_setting:
			i = new Intent(this, GameSetting.class);
			startActivity(i);
			break;		
		case R.id.button_help:
			i = new Intent(this, Help.class);
			startActivity(i);
			break;
		case R.id.button_achievement:
			i = new Intent(this, Achievement.class);
			startActivity(i);
			break;
		}
	}
    
	@Override
	public void onBackPressed() {
		openExitDialog();
	};
	
	private void openExitDialog() {
		MyDialog.Builder dialog = new MyDialog.Builder(this);
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(R.string.info_quit);
		dialog.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Explodi.this.finish();
				dialog.cancel();
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show(R.style.InfoDialog);
	}
	
	
}