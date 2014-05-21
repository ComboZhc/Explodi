package org.example.explodi.mode;

import org.example.explodi.Difficulty;
import org.example.explodi.GameSetting;
import org.example.explodi.Mode;
import org.example.explodi.Sound;
import org.example.explodi.Utils;

import android.util.Log;

class TimeGamePoint {
	public int t, i, j;

	public TimeGamePoint(int i, int j, int iLimit, int jLimit) {
		if (j >= jLimit) {
			this.t = 1;
			this.i = i;
			this.j = j - jLimit;
		} else {
			this.t = 0;
			this.i = iLimit - i - 1;
			this.j = jLimit - j - 1;
		}
	}
}

public class TimeMode extends AbstractMode implements Runnable {

	@Override
	public Mode getMode() {
		return Mode.TIME;
	}

	private static final int TABLES_SIZE = 2;

	private int subRowsSize;

	private int itemFacesSize = 0;
	private int itemColorsOrdinarySize = 0;
	private int itemPopupTime = 0;

	private int[][][] mapItemColor;
	private boolean[][][] mapItemFocus;
	private boolean[][][] mapItemAppear;
	private int[][][] mapItemFace;
	protected boolean[][][] mapItemVisible;

	private int motionT = 0;
	private int focusCount = 0;
	private int timeLimit = 0;
	private int timePopup = 0;

	@Override
	protected void setDifficulty(Difficulty difficulty) {
		switch (difficulty) {
		case EASY:
			columnsSize = 7;
			itemColorsOrdinarySize = 2;
			itemFacesSize = 7;
			itemPopupTime = 3;
			timeLimit = 60;
			break;
		case NORMAL:
			columnsSize = 8;
			itemColorsOrdinarySize = 3;
			itemFacesSize = 7;
			itemPopupTime = 3;
			timeLimit = 60;
			break;
		case HARD:
			columnsSize = 9;
			itemColorsOrdinarySize = 4;
			itemFacesSize = 7;
			itemPopupTime = 3;
			timeLimit = 60;
			break;
		}
		timePopup += itemPopupTime;
	}

	// Setters and getters
	@Override
	public void setRowsSize(int rowsSize) {
		this.rowsSize = rowsSize;
		this.subRowsSize = this.rowsSize / TABLES_SIZE;
	}

	@Override
	public int getItemColor(int i, int j) {
		TimeGamePoint point = new TimeGamePoint(i, j, columnsSize, subRowsSize);
		if (mapItemVisible[point.t][point.i][point.j])
			return mapItemColor[point.t][point.i][point.j];
		else
			return 0;
	}

	@Override
	public boolean getItemAppear(int i, int j) {
		TimeGamePoint point = new TimeGamePoint(i, j, columnsSize, subRowsSize);
		return mapItemAppear[point.t][point.i][point.j];
	}

	@Override
	public boolean getItemFocus(int i, int j) {
		TimeGamePoint point = new TimeGamePoint(i, j, columnsSize, subRowsSize);
		return mapItemFocus[point.t][point.i][point.j];
	}

	@Override
	public boolean getItemAffiliated(int i, int j) {
		return false;
	}

	@Override
	public boolean getItemLarge(int i, int j) {
		return false;
	}

	@Override
	public int getItemFace(int i, int j) {
		TimeGamePoint point = new TimeGamePoint(i, j, columnsSize, subRowsSize);
		return mapItemFace[point.t][point.i][point.j];
	}

	@Override
	public int getItemColorsSize() {
		return itemColorsOrdinarySize;
	}

	@Override
	public int getItemFacesSize() {
		return itemFacesSize;
	}

	@Override
	public boolean getPositionFilled(int i, int j) {
		return true;
	}

	// Initialization
	@Override
	public void initValue() {
		remainCount = TABLES_SIZE * columnsSize * (subRowsSize - 3);
		totalCount = remainCount;
	}

	@Override
	public void initMap() {
		initMapItemColor();
		initMapItemFace();
		initMapItemFocus();
		initMapItemAppear();
		initMapItemVisible();
		isMapReady = true;
	}

	private void initMapItemFocus() {
		isFocusing = false;
		if (mapItemFocus == null)
			mapItemFocus = new boolean[TABLES_SIZE][columnsSize][subRowsSize];
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < subRowsSize; ++j)
					mapItemFocus[t][i][j] = false;
	}

	private void initMapItemAppear() {
		isAppearing = false;
		if (mapItemAppear == null)
			mapItemAppear = new boolean[TABLES_SIZE][columnsSize][subRowsSize];
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < subRowsSize; ++j)
					mapItemAppear[t][i][j] = false;
	}

	private void initMapItemFace() {
		if (mapItemFace == null)
			mapItemFace = new int[TABLES_SIZE][columnsSize][subRowsSize];
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i) {
				mapItemFace[t][i][0] = 0;
				for (int j = 1; j < subRowsSize; ++j)
					mapItemFace[t][i][j] = Utils.random.nextInt(itemFacesSize);
			}
	}

	private void initMapItemColor() {
		if (mapItemColor == null)
			mapItemColor = new int[TABLES_SIZE][columnsSize][subRowsSize];
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i) {
				for (int j = 0; j < 3; ++j)
					mapItemColor[t][i][j] = 0;
				for (int j = 3; j < subRowsSize; ++j)
					mapItemColor[t][i][j] = Utils.random
							.nextInt(itemColorsOrdinarySize) + 1;
			}
	}

	protected void initMapItemVisible() {
		isAllVisible = false;
		if (mapItemVisible == null)
			mapItemVisible = new boolean[TABLES_SIZE][columnsSize][rowsSize];
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < rowsSize; ++j)
					mapItemVisible[t][i][j] = false;
	}

	private boolean isEmptyColumn(int t, int i) {
		for (int j = 0; j < subRowsSize; ++j)
			if (mapItemColor[t][i][j] != 0)
				return false;
		return true;
	}

	private void clearColumn(int t, int i) {
		for (int j = 0; j < subRowsSize; ++j)
			clearItem(t, i, j);
	}

	private void clearItem(int t, int i, int j) {
		mapItemColor[t][i][j] = 0;
		mapItemFace[t][i][j] = 0;
		mapItemAppear[t][i][j] = false;
	}

	private void moveItem(int t, int i, int j, int srcI, int srcJ) {
		mapItemColor[t][i][j] = mapItemColor[t][srcI][srcJ];
		mapItemFace[t][i][j] = mapItemFace[t][srcI][srcJ];
		mapItemAppear[t][i][j] = mapItemAppear[t][srcI][srcJ];
		clearItem(t, srcI, srcJ);
	}

	private boolean isMergeColumns(int t, int i, int srcI) {
		for (int j = 0; j < subRowsSize; ++j)
			if (mapItemColor[t][i][j] != 0 && mapItemColor[t][srcI][j] != 0)
				return false;
		return true;
	}

	private void mergeColumn(int t, int i, int srcI) {
		for (int j = 0; j < subRowsSize; ++j)
			if (mapItemColor[t][i][j] == 0)
				moveItem(t, i, j, srcI, j);
		clearColumn(t, srcI);
	}

	// GameFunction
	private void clearSelectedItems() {
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < subRowsSize; ++j)
					if (mapItemFocus[t][i][j]) {
						--remainCount;
						clearItem(t, i, j);
					}
	}

	private void focusItem(int t, int i, int j) {
		if (mapItemColor[t][i][j] != this.chooseItemColor
				|| mapItemFocus[t][i][j])
			return;
		mapItemFocus[t][i][j] = true;
		for (int d = 0; d < directionI.length; ++d) {
			int tI = i + directionI[d], tJ = j + directionJ[d];
			if (isInside(tI, tJ))
				focusItem(t, tI, tJ);
		}
	}

	private boolean isValidItem(int t, int i, int j) {
		if (!isInside(i, j) || mapItemColor[t][i][j] == 0)
			return false;
		return getSurroundingCount(t, i, j) > 0;
	}

	private boolean isInside(int i, int j) {
		return (i >= 0 && i < columnsSize && j >= 0 && j < subRowsSize);
	}

	protected void randomSetMapItemVisible() {
		if (mapItemVisible != null)
			for (int t = 0; t < TABLES_SIZE; ++t)
				for (int i = 0; i < columnsSize; ++i)
					for (int j = 0; j < rowsSize; ++j)
						if (Utils.random.nextInt(15) == 0)
							mapItemVisible[t][i][j] = true;
	}

	@Override
	protected void setAllVisible(boolean isAllVisible) {
		this.isAllVisible = isAllVisible;
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < rowsSize; ++j)
					mapItemVisible[t][i][j] = true;
	}

	private int getSurroundingCount(int t, int i, int j) {
		int ret = 0;
		for (int d = 0; d < directionI.length; ++d) {
			int tI = i + directionJ[d], tJ = j + directionI[d];
			if (isInside(tI, tJ)
					&& mapItemColor[t][tI][tJ] == mapItemColor[t][i][j])
				++ret;
		}
		return ret;
	}

	// Statistics
	protected void generateScore() {
		nowScore = 0;
		nowBonusText = "";
		if (focusCount > 0) {
			nowScore += focusCount * 50;
			nowScoreText = Integer.toString(nowScore);
		}
		if (focusCount >= 10) {
			nowBonusText += "+1000";
			nowScore += 500;
		} else if (focusCount >= 5) {
			nowBonusText += "+500";
			nowScore += 250;
		}
		if (focusCount > maxCount)
			maxCount = focusCount;
	}

	protected void generateCount() {
		focusCount = 0;
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i)
				for (int j = 0; j < subRowsSize; ++j) {
					if (mapItemFocus[t][i][j])
						++focusCount;
				}
	}

	private void setTotalScore(int nowScore) {
		totalScore += nowScore;
		statusView.setTotalScore(totalScore);
	}

	@Override
	protected String remarkScore(int score) {
		if (score >= 2500) {
			Sound.play(this, Sound.VOICE_AWESOME, 0, this);
			return "Awesome";
		} else if (score >= 2000) {
			Sound.play(this, Sound.VOICE_EXCELLENT, 0, this);
			return "Excellent";
		} else if (score >= 1500) {
			Sound.play(this, Sound.VOICE_GOOD, 0, this);
			return "Good";
		}
		return "";
	}

	// GameLogic
	private void clearItemsTowardsLeft() {
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i) {
				int mergeI = i + 1;
				while (mergeI < columnsSize && isEmptyColumn(t, mergeI))
					++mergeI;
				if (mergeI >= columnsSize)
					continue;
				if (isMergeColumns(t, i, mergeI))
					mergeColumn(t, i, mergeI);
			}
	}

	private void clearItemsTowardsDown() {
		for (int t = 0; t < TABLES_SIZE; ++t) {
			for (int j = subRowsSize - 1; j >= 0; --j) {
				for (int i = 0; i < columnsSize; ++i) {
					if (mapItemColor[t][i][j] == 0) {
						int srcJ = j - 1;
						while (isInside(i, srcJ)
								&& mapItemColor[t][i][srcJ] == 0)
							--srcJ;
						if (srcJ < 0)
							continue;
						moveItem(t, i, j, i, srcJ);
					}
				}
			}
		}
	}

	@Override
	public void clear() {
		clearSelectedItems();
		clearItemsTowardsLeft();
		clearItemsTowardsDown();
		initMapItemFocus();
		statusView.setTotalRateText(remainCount, totalCount);
	}

	@Override
	public void appear() {
		isAppearing = true;
		for (int t = 0; t < TABLES_SIZE; ++t)
			for (int i = 0; i < columnsSize; ++i) {
				int j = 0;
				while (j < subRowsSize - 1 && mapItemColor[t][i][j + 1] == 0)
					++j;
				mapItemColor[t][i][j] = Utils.random
						.nextInt(itemColorsOrdinarySize) + 1;
				mapItemFace[t][i][j] = Utils.random.nextInt(itemFacesSize);
				mapItemAppear[t][i][j] = true;
			}
		totalCount += TABLES_SIZE * columnsSize;
		remainCount += TABLES_SIZE * columnsSize;
	}

	@Override
	public void endAppear() {
		initMapItemAppear();
	}

	@Override
	protected boolean judgeNewRecord(int score, int time, float complete,
			String lastDate, int lastScore, int lastTime, float lastComplete) {
		return lastDate == null || time > lastTime
				|| (time == lastTime && totalScore > lastScore);
	}

	public void checkWarning() {
		boolean failed = false;
		out: for (int t = 0; t < TABLES_SIZE; ++t) {
			for (int i = 0; i < columnsSize; ++i)
				if (mapItemColor[t][i][0] != 0) {
					failed = true;
					break out;
				}
		}
		if (!gameView.isWarning() && !failed)
			for (int t = 0; t < TABLES_SIZE; ++t)
				for (int i = 0; i < columnsSize; ++i)
					if (mapItemColor[t][i][1] != 0) {
						gameView.setWarning(true);
						Sound.play(this, Sound.SE_WARNING, 2, this);
						return;
					}
	}

	@Override
	public void checkResult() {
		if (mapItemColor != null) {
			// There is a bug which i cannot detect, the last line is to prevent
			// it from happening in a disgraceful way.
			boolean failed = false;
			out: for (int t = 0; t < TABLES_SIZE; ++t) {
				for (int i = 0; i < columnsSize; ++i)
					if (mapItemColor[t][i][0] != 0) {
						failed = true;
						break out;
					}
			}
			isFinishing = false;
			if ((failed || time >= timeLimit)) {
				isFinishing = true;
				if (!gameView.isViewAnimating())
					endGame(time >= timeLimit);
			}
		}
	}

	@Override
	public int getBackgroundAlpha() {
		return 0x7F + 0x80 * gameTimer.getTime() / timeLimit;
	}

	@Override
	public String getReadyText() {
		return Utils.getTimeBySecond(timeLimit);
	}

	@Override
	public void manageMotion(float x, float y) {
		if (!gameView.isViewLocked() && !isFinishing) {
			int i = (int) (x / gameView.getItemImageWidth());
			int j = (int) (y / gameView.getItemImageHeight());
			TimeGamePoint point = new TimeGamePoint(i, j, columnsSize,
					subRowsSize);
			motionT = point.t;
			motionI = point.i;
			motionJ = point.j;
			if (isValidItem(motionT, motionI, motionJ)) {
				++totalClick;
				++validClick;
				this.chooseItemColor = mapItemColor[motionT][motionI][motionJ];
				Log.d(getClass().getSimpleName(), "choose item : (" + motionI + "," + motionJ + ")");
				if (GameSetting.getOneClick(this)) {
					initMapItemFocus();
					focusItem(motionT, motionI, motionJ);
					generateCount();
					generateScore();
					setTotalScore(nowScore);
					Sound.play(this, Sound.SE_CLEAR, 0, this);
					gameView.setItemFading(true);
					gameView.setNowScoreAnimating(true);
					gameView.setScoreTextByCenter(
							(int) (15 + Math.log(nowScore) * 10), nowScoreText,
							nowBonusText, remarkScore(nowScore), x, y);
				} else if (isFocusing
						&& mapItemFocus[motionT][motionI][motionJ]) {
					// Start Clearing Animation
					generateScore();
					setTotalScore(nowScore);
					Sound.play(this, Sound.SE_CLEAR, 0, this);
					gameView.setItemFading(true);
					gameView.setNowScoreAnimating(true);
					gameView.setScoreTextByCenter(
							(int) (15 + Math.log(nowScore) * 10), nowScoreText,
							nowBonusText, remarkScore(nowScore), x, y);
				} else {
					// Focus
					initMapItemFocus();
					isFocusing = true;
					focusItem(motionT, motionI, motionJ);
					generateCount();
				}
			} else {
				// Invalid Touch
				initMapItemFocus();
				++totalClick;
			}
		}
	}

	// Main Thread
	@Override
	public void run() {
		while (!finished && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(40);
				if (time >= timeLimit)
					gameTimer.stop();
				if (isGameViewReady && isStatusViewReady && isMapReady) {
					if (time >= timePopup) {
						appear();
						checkWarning();
						gameView.setItemPopuping(true);
						timePopup += itemPopupTime;
					}
				}
				if (gameView.isStarted())
					checkResult();
				if (!isAllVisible) {
					randomSetMapItemVisible();
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
