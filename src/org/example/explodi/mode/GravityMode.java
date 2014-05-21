package org.example.explodi.mode;

import org.example.explodi.Difficulty;
import org.example.explodi.Mode;
import org.example.explodi.R;
import org.example.explodi.Utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class GravityMode extends ClassicMode implements SensorEventListener {
	@Override
	public Mode getMode() {
		return Mode.GRAVITY;
	}

	private SensorManager sensorMgr;
	private Sensor sensor;
	private boolean isLeft = false, isRight = false, isTop = false,
			isBottom = false, isSteep = false;
	private int steepCount = 0;
	private int timeLimit = 0;
	private static final int TIME_TO_TOAST = 6;
	private int timeToast = TIME_TO_TOAST;

	private void clearInclinedState() {
		isLeft = false;
		isRight = false;
		isTop = false;
		isBottom = false;
	}

	@Override
	protected void setDifficulty(Difficulty difficulty) {
		switch (difficulty) {
		case EASY:
			columnsSize = 8;
			itemColorsOrdinaryKind = 4;
			itemColorsExtraordinaryKind = 0;
			itemColorsExtraordinarySize = 0;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 60;
			break;
		case NORMAL:
			columnsSize = 9;
			itemColorsOrdinaryKind = 5;
			itemColorsExtraordinaryKind = 0;
			itemColorsExtraordinarySize = 0;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 150;
			break;
		case HARD:
			columnsSize = 10;
			itemColorsOrdinaryKind = 6;
			itemColorsExtraordinaryKind = 0;
			itemColorsExtraordinarySize = 0;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 240;
			break;
		}
	}

	@Override
	public void initValue() {
		super.initValue();
		clearInclinedState();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorMgr.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorMgr.unregisterListener(this);
	}

	@Override
	public void clear() {
		clearSelectedItems();
		initMapItemFocus();
		statusView.setTotalRateText(remainCount, totalCount);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		clearInclinedState();
		if (event.values[SensorManager.DATA_X] >= 3)
			isLeft = true;
		if (event.values[SensorManager.DATA_X] <= -3)
			isRight = true;
		if (event.values[SensorManager.DATA_Y] >= 3)
			isBottom = true;
		if (event.values[SensorManager.DATA_Y] <= -3)
			isTop = true;
		if (Math.abs(event.values[SensorManager.DATA_X]) >= 5
				|| Math.abs(event.values[SensorManager.DATA_Y]) >= 5) {
			isSteep = true;
		} else {
			if (isSteep)
				steepCount = 0;
			isSteep = false;
		}
	}

	private void checkInclined() {
		if (isBottom) {
			for (int j = rowsSize - 1; j >= 1; --j)
				for (int i = 0; i < columnsSize; ++i)
					if (mapItemColor[i][j] == 0 && mapItemColor[i][j - 1] != 0) {
						moveItem(i, j, i, j - 1);
						if (mapItemFocus[i][j])
							initMapItemFocus();
					}
		}
		if (isTop) {
			for (int j = 0; j < rowsSize - 1; ++j)
				for (int i = 0; i < columnsSize; ++i)
					if (mapItemColor[i][j] == 0 && mapItemColor[i][j + 1] != 0) {
						moveItem(i, j, i, j + 1);
						if (mapItemFocus[i][j])
							initMapItemFocus();
					}
		}
		if (isLeft) {
			for (int i = 0; i < columnsSize - 1; ++i)
				for (int j = 0; j < rowsSize; ++j)
					if (mapItemColor[i][j] == 0 && mapItemColor[i + 1][j] != 0) {
						moveItem(i, j, i + 1, j);
						if (mapItemFocus[i][j])
							initMapItemFocus();
					}
		}
		if (isRight) {
			for (int i = columnsSize - 1; i >= 1; --i)
				for (int j = 0; j < rowsSize; ++j)
					if (mapItemColor[i][j] == 0 && mapItemColor[i - 1][j] != 0) {
						moveItem(i, j, i - 1, j);
						if (mapItemFocus[i][j])
							initMapItemFocus();
					}
		}
	}

	@Override
	public void checkResult() {
		if ((remainCount == 0 || time >= timeLimit)) {
			isFinishing = true;
			Log.d(getClass().getSimpleName(), "endGame by no more squares or times up");
			if (!gameView.isViewAnimating())
				endGame(remainCount == 0);
			return;
		}
		int rowU = rowsSize, rowD = 0;
		int colL = columnsSize, colR = 0;
		boolean needToast = true;
		out_break: for (int i = 0; i < columnsSize; ++i) {
			for (int j = 0; j < rowsSize; ++j) {
				if (isValidItem(i, j)) {
					timeToast = time + TIME_TO_TOAST;
					needToast = false;
					break out_break;
				}
			}
		}
		for (int i = 0; i < columnsSize; ++i) {
			for (int j = 0; j < rowsSize; ++j) {
				if (getItemColor(i, j) != 0) {
					if (i < colL)
						colL = i;
					if (i > colR)
						colR = i;
					if (j < rowU)
						rowU = j;
					if (j > rowD)
						rowD = j;
				}
			}
		}
		if (needToast && (rowD - rowU + 1) * (colR - colL + 1) == remainCount) {
			isFinishing = true;
			if (!gameView.isViewAnimating())
				endGame(false);
			Log.d(getClass().getSimpleName(), "endGame by no more moves");
		}
		if (time >= timeToast) {
			timeToast = time + TIME_TO_TOAST;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(GravityMode.this,
							R.string.toast_gravity_mode, Toast.LENGTH_LONG)
							.show();
					Looper.loop();
				}
			}).start();
		}
	}

	@Override
	public String getReadyText() {
		return Utils.getTimeBySecond(timeLimit);
	}

	@Override
	protected boolean judgeNewRecord(int score, int time, float complete,
			String lastDate, int lastScore, int lastTime, float lastComplete) {
		return lastDate == null
				|| complete > lastComplete + EPSILON
				|| (Math.abs(complete - lastComplete) < EPSILON && totalScore > lastScore);
	}

	@Override
	public void run() {
		while (!finished && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(40);
				if (time >= timeLimit)
					gameTimer.stop();
				if (isGameViewReady && isStatusViewReady && isMapReady) {
					decreaseMapItemFaceChangeTime();
					randomSetMapItemFaceChangeTime();
					if (isSteep || (steepCount == 4)) {
						steepCount = 0;
						if (!gameView.isViewLocked() && !isFinishing)
							checkInclined();
					} else
						++steepCount;
					if (gameView.isStarted())
						checkResult();
					if (!isAllVisible) {
						randomSetMapItemVisible();
					}
				}
				time = gameTimer.getTime();
				statusView.setTime(timeLimit - time);
				gameView.postInvalidate();
				statusView.postInvalidate();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
