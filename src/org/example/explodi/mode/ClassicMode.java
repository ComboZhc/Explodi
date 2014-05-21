package org.example.explodi.mode;

import org.example.explodi.Difficulty;
import org.example.explodi.GameSetting;
import org.example.explodi.Mode;
import org.example.explodi.R;
import org.example.explodi.Sound;
import org.example.explodi.Utils;

import android.graphics.Point;
import android.util.Log;

public class ClassicMode extends AbstractMode implements Runnable {
	@Override
	public Mode getMode() {
		return Mode.CLASSIC;
	}

	// LOGIC
	protected int itemLargeSize;
	protected int itemColorsExtraordinaryKind;
	protected int itemColorsExtraordinarySize;

	protected int[][] mapItemColor;
	protected int[][] mapItemFace;
	protected int[][] mapItemFaceChangeTime;
	protected boolean[][] mapItemFocus;
	protected boolean[][] mapItemLarge;
	protected boolean[][] mapItemAffiliated;
	protected boolean[][] mapItemVisible;

	protected int smallCount = 0;
	protected int largeCount = 0;

	@Override
	protected void setDifficulty(Difficulty difficulty) {
		switch (difficulty) {
		case EASY:
			columnsSize = 8;
			itemColorsOrdinaryKind = 3;
			itemColorsExtraordinaryKind = 0;
			itemColorsExtraordinarySize = 0;
			itemFacesKind = 7;
			itemLargeSize = Utils.random.nextInt(6);
			break;
		case NORMAL:
			columnsSize = 9;
			itemColorsOrdinaryKind = 3;
			itemColorsExtraordinaryKind = 1;
			itemColorsExtraordinarySize = 5;
			itemFacesKind = 7;
			itemLargeSize = Utils.random.nextInt(7);
			break;
		case HARD:
			columnsSize = 10;
			itemColorsOrdinaryKind = 4;
			itemColorsExtraordinaryKind = 0;
			itemColorsExtraordinarySize = 0;
			itemFacesKind = 7;
			itemLargeSize = Utils.random.nextInt(8);
			break;
		}
	}

	// Getters and setters
	@Override
	public int getItemColor(int i, int j) {
		if (mapItemVisible[i][j])
			return mapItemColor[i][j];
		else
			return 0;
	}

	@Override
	public boolean getItemAppear(int i, int j) {
		return false;
	}

	@Override
	public boolean getItemFocus(int i, int j) {
		return mapItemFocus[i][j];
	}

	@Override
	public boolean getItemAffiliated(int i, int j) {
		return mapItemAffiliated[i][j];
	}

	@Override
	public boolean getItemLarge(int i, int j) {
		return mapItemLarge[i][j];
	}

	@Override
	public boolean getPositionFilled(int i, int j) {
		return true;
	}

	@Override
	public int getItemFace(int i, int j) {
		return mapItemFace[i][j];
	}

	@Override
	public boolean getItemFaceChanged(int i, int j) {
		return mapItemFaceChangeTime[i][j] != 0;
	}

	@Override
	public int getItemColorsSize() {
		return itemColorsOrdinaryKind + itemColorsExtraordinaryKind;
	}

	@Override
	public int getItemFacesSize() {
		return itemFacesKind;
	}

	// Initialization
	@Override
	public void initValue() {
		remainCount = columnsSize * rowsSize - itemLargeSize * 3;
		totalCount = remainCount;
	}

	@Override
	public void initMap() {
		initMapItemColor();
		initMapItemFace();
		initMapItemFaceChangeTime();
		initMapItemAffiliated();
		initMapItemLarge();
		initMapItemFocus();
		initMapItemVisible();
		isMapReady = true;
	}

	private void initMapItemFace() {
		mapItemFace = new int[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemFace[i][j] = Utils.random.nextInt(itemFacesKind);
	}

	private void initMapItemAffiliated() {
		mapItemAffiliated = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemAffiliated[i][j] = false;
	}

	private void setItemAffiliated(int i, int j, int srcI, int srcJ) {
		mapItemAffiliated[i][j] = true;
		mapItemColor[i][j] = mapItemColor[srcI][srcJ];
		mapItemFace[i][j] = mapItemFace[srcI][srcJ];
	}

	private void initMapItemLarge() {
		mapItemLarge = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemLarge[i][j] = false;
		for (int c = 0; c < itemLargeSize; ++c) {
			int i, j;
			do {
				i = Utils.random.nextInt(columnsSize - 1);
				j = Utils.random.nextInt(rowsSize - 1);
			} while (mapItemAffiliated[i][j] || mapItemAffiliated[i][j + 1]
					|| mapItemAffiliated[i + 1][j]
					|| mapItemAffiliated[i + 1][j + 1]);
			mapItemLarge[i][j] = true;
			mapItemAffiliated[i][j] = true;
			setItemAffiliated(i, j + 1, i, j);
			setItemAffiliated(i + 1, j, i, j);
			setItemAffiliated(i + 1, j + 1, i, j);
		}
	}

	private void initMapItemFaceChangeTime() {
		mapItemFaceChangeTime = new int[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemFaceChangeTime[i][j] = 0;
	}

	private void initMapItemColor() {
		mapItemColor = new int[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemColor[i][j] = Utils.random
						.nextInt(itemColorsOrdinaryKind) + 1;
		if (itemColorsExtraordinaryKind != 0) {
			for (int i = 0; i < itemColorsExtraordinaryKind; ++i) {
				for (int j = 0; j < itemColorsExtraordinarySize; ++j)
					mapItemColor[Utils.random.nextInt(columnsSize)][Utils.random
							.nextInt(rowsSize)] = itemColorsOrdinaryKind + i
							+ 1;
			}
		}
	}

	protected void initMapItemFocus() {
		isFocusing = false;
		mapItemFocus = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemFocus[i][j] = false;
	}

	protected void initMapItemVisible() {
		isAllVisible = false;
		mapItemVisible = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemVisible[i][j] = false;
	}

	@Override
	protected void setAllVisible(boolean isAllVisible) {
		this.isAllVisible = isAllVisible;
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemVisible[i][j] = true;
	}

	protected Point getItemLargePoint(int i, int j) {
		Point ret = new Point();
		ret.set(-1, -1);
		if (this.isInside(i, j) && mapItemAffiliated[i][j]) {
			if (mapItemLarge[i][j])
				ret.set(i, j);
			else if (this.isInside(i - 1, j) && mapItemLarge[i - 1][j])
				ret.set(i - 1, j);
			else if (this.isInside(i, j - 1) && mapItemLarge[i][j - 1])
				ret.set(i, j - 1);
			else if (this.isInside(i - 1, j - 1) && mapItemLarge[i - 1][j - 1])
				ret.set(i - 1, j - 1);
		}
		return ret;
	}

	protected void decreaseMapItemFaceChangeTime() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemFaceChangeTime[i][j] != 0)
					--mapItemFaceChangeTime[i][j];
	}

	protected void randomSetMapItemFaceChangeTime() {
		if (Utils.random.nextInt(5) == 0 && isGameViewReady) {
			mapItemFaceChangeTime[Utils.random.nextInt(columnsSize)][Utils.random
					.nextInt(rowsSize)] = 5;
		}
	}

	protected void randomSetMapItemVisible() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (Utils.random.nextInt(15) == 0)
					mapItemVisible[i][j] = true;
	}

	// GameFunction
	private int getSurroundingCount(int i, int j) {
		int ret = 0;
		for (int d = 0; d < directionI.length; ++d) {
			int tI = i + directionJ[d], tJ = j + directionI[d];
			if (isInside(tI, tJ) && mapItemColor[tI][tJ] == mapItemColor[i][j])
				++ret;
		}
		return ret;
	}

	private boolean isInside(int i, int j) {
		return (i >= 0 && i < columnsSize && j >= 0 && j < rowsSize);
	}

	protected boolean isValidItem(int i, int j) {
		if (!isInside(i, j) || mapItemColor[i][j] == 0)
			return false;
		if (!mapItemAffiliated[i][j]) {
			return getSurroundingCount(i, j) > 0;
		} else {
			Point p = getItemLargePoint(i, j);
			i = p.x;
			j = p.y;
			return (getSurroundingCount(i, j) + getSurroundingCount(i, j + 1)
					+ getSurroundingCount(i + 1, j) + getSurroundingCount(
						i + 1, j + 1)) > 8;
		}
	}

	private void focusItem(int i, int j) {
		if (mapItemColor[i][j] != this.chooseItemColor || mapItemFocus[i][j])
			return;
		mapItemFocus[i][j] = true;
		for (int d = 0; d < directionI.length; ++d) {
			int tI = i + directionI[d], tJ = j + directionJ[d];
			if (isInside(tI, tJ))
				focusItem(tI, tJ);
		}
	}

	private boolean isEmptyColumn(int i) {
		for (int j = 0; j < rowsSize; ++j)
			if (mapItemColor[i][j] != 0)
				return false;
		return true;
	}

	private boolean isEmptyColumn(int i, int srcJ, int dstJ) {
		for (int j = srcJ; j <= dstJ; ++j)
			if (mapItemColor[i][j] != 0)
				return false;
		return true;
	}

	private void clearColumn(int i) {
		for (int j = 0; j < rowsSize; ++j)
			clearItem(i, j);
	}

	protected void clearItem(int i, int j) {
		mapItemColor[i][j] = 0;
		mapItemFace[i][j] = 0;
		mapItemLarge[i][j] = false;
		mapItemAffiliated[i][j] = false;
		mapItemFocus[i][j] = false;
	}

	protected void moveItem(int i, int j, int srcI, int srcJ) {
		mapItemColor[i][j] = mapItemColor[srcI][srcJ];
		mapItemFace[i][j] = mapItemFace[srcI][srcJ];
		mapItemLarge[i][j] = mapItemLarge[srcI][srcJ];
		mapItemAffiliated[i][j] = mapItemAffiliated[srcI][srcJ];
		mapItemFocus[i][j] = mapItemFocus[srcI][srcJ];
		clearItem(srcI, srcJ);
	}

	private void moveColumn(int i, int srcI) {
		mapItemColor[i] = mapItemColor[srcI].clone();
		mapItemFace[i] = mapItemFace[srcI].clone();
		mapItemLarge[i] = mapItemLarge[srcI].clone();
		mapItemAffiliated[i] = mapItemAffiliated[srcI].clone();
		mapItemFocus[i] = mapItemFocus[i].clone();
		clearColumn(srcI);
	}

	private boolean isMergeColumns(int i, int srcI) {
		for (int j = 0; j < rowsSize; ++j)
			if (mapItemColor[i][j] != 0 && mapItemColor[srcI][j] != 0)
				return false;
		return true;
	}

	private void mergeColumn(int i, int srcI) {
		for (int j = 0; j < rowsSize; ++j)
			if (mapItemColor[i][j] == 0)
				moveItem(i, j, srcI, j);
		clearColumn(srcI);
	}

	// Statistics
	protected void generateCount() {
		smallCount = 0;
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j) {
				if ((mapItemFocus[i][j] && !mapItemAffiliated[i][j]))
					++smallCount;
			}
		largeCount = 0;
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j) {
				if ((mapItemFocus[i][j] && mapItemLarge[i][j]))
					++largeCount;
			}
	}

	@Override
	protected void generateScore() {
		nowScore = 0;
		nowBonusText = "";
		if (smallCount > 0) {
			nowScore += smallCount * 100;
			nowScoreText = Integer.toString(nowScore);
		}
		if (smallCount >= 20) {
			nowBonusText += "+1500";
			nowScore += 1500;
		} else if (smallCount >= 10) {
			nowBonusText += "+1000";
			nowScore += 1000;
		} else if (smallCount >= 5) {
			nowBonusText += "+500";
			nowScore += 500;
		}

		if (smallCount + largeCount == remainCount) {
			nowBonusText += (nowBonusText == "" ? "x5" : " x5");
			nowScore *= 5;
		} else if (largeCount > 0) {
			nowBonusText += (nowBonusText == "" ? "x" + (largeCount + 1) : " x"
					+ (largeCount + 1));
			nowScore *= largeCount + 1;
		}
		if (smallCount + largeCount / 4 > maxCount)
			maxCount = smallCount + largeCount / 4;
	}

	private void setTotalScore(int nowScore) {
		totalScore += nowScore;
		statusView.setTotalScore(totalScore);
	}

	@Override
	protected String remarkScore(int score) {
		if (score >= 4000) {
			Sound.play(this, Sound.VOICE_AWESOME, 0, this);
			return "Awesome";
		} else if (score >= 3000) {
			Sound.play(this, Sound.VOICE_EXCELLENT, 0, this);
			return "Excellent";
		} else if (score >= 2000) {
			Sound.play(this, Sound.VOICE_GOOD, 0, this);
			return "Good";
		}
		return "";
	}

	// GameLogic
	@Override
	public void clear() {
		clearSelectedItems();
		ensureLarge();
		clearItemsTowardsDown();
		clearItemsTowardsLeft();
		initMapItemFocus();
		statusView.setTotalRateText(remainCount, totalCount);
	}

	protected void clearSelectedItems() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemFocus[i][j]) {
					if (!mapItemAffiliated[i][j] || mapItemLarge[i][j])
						--remainCount;
					clearItem(i, j);
				}
	}

	/*
	 * The function is to prevent some unpredictable and intangible bugs.
	 * However the occurrence is rather small.
	 */
	private void ensureLarge() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemLarge[i][j]) {
					mapItemAffiliated[i][j] = true;
					setItemAffiliated(i + 1, j, i, j);
					setItemAffiliated(i, j + 1, i, j);
					setItemAffiliated(i + 1, j + 1, i, j);
				}
	}

	private void clearItemsTowardsLeft() {
		for (int i = 0; i < columnsSize; ++i) {
			int mergeI = i + 1;
			while (mergeI < columnsSize && isEmptyColumn(mergeI))
				++mergeI;
			if (mergeI >= columnsSize)
				continue;
			if (isMergeColumns(i, mergeI))
				mergeColumn(i, mergeI);
		}
	}

	private void clearItemsTowardsDown() {
		for (int j = rowsSize - 1; j >= 0; --j) {
			for (int i = 0; i < columnsSize; ++i) {
				if (mapItemColor[i][j] == 0) {
					int srcJ = j - 1;
					while (isInside(i, srcJ) && mapItemColor[i][srcJ] == 0)
						--srcJ;
					if (srcJ < 0)
						continue;
					if (!mapItemAffiliated[i][srcJ])
						moveItem(i, j, i, srcJ);
					else {
						Point largePoint = this.getItemLargePoint(i, srcJ);
						int srcI = largePoint.x;
						srcJ = largePoint.y;
						if (isEmptyColumn(srcI, srcJ + 2, j)
								&& isEmptyColumn(srcI + 1, srcJ + 2, j)) {
							moveItem(srcI, j, srcI, srcJ + 1);
							moveItem(srcI + 1, j, srcI + 1, srcJ + 1);
							moveItem(srcI, j - 1, srcI, srcJ);
							moveItem(srcI + 1, j - 1, srcI + 1, srcJ);
						}
					}
				}
			}
		}
	}

	@Override
	protected boolean judgeNewRecord(int score, int time, float complete,
			String lastDate, int lastScore, int lastTime, float lastComplete) {
		return lastDate == null
				|| complete > lastComplete + EPSILON
				|| (Math.abs(complete - lastComplete) < EPSILON && totalScore > lastScore);
	}

	@Override
	public void checkResult() {
		boolean failed = true;
		out: for (int i = 0; i < columnsSize; ++i) {
			for (int j = 0; j < rowsSize; ++j)
				if (isValidItem(i, j)) {
					failed = false;
					break out;
				}
		}
		if ((failed || remainCount == 0)) {
			isFinishing = true;
			if (!gameView.isViewAnimating())
				endGame(remainCount == 0);
		}
	}

	@Override
	public int getBackgroundAlpha() {
		return 0xFF * (totalCount - remainCount) / totalCount;
	}

	@Override
	public String getReadyText() {
		return getResources().getString(R.string.text_ready);
	}

	@Override
	public void manageMotion(float x, float y) {
		if (!gameView.isViewLocked() && !isFinishing) {
			motionI = (int) (x / gameView.getItemImageWidth());
			motionJ = (int) (y / gameView.getItemImageHeight());
			if (isValidItem(motionI, motionJ)) {
				++totalClick;
				++validClick;
				this.chooseItemColor = mapItemColor[motionI][motionJ];
				Log.d(getClass().getSimpleName(), "choose item : (" + motionI + "," + motionJ + ")");
				if (GameSetting.getOneClick(this)) {
					initMapItemFocus();
					focusItem(motionI, motionJ);
					generateCount();
					generateScore();
					setTotalScore(nowScore);
					gameView.setItemFading(true);
					Sound.play(this, Sound.SE_CLEAR, 0, this);
					if (nowScore > 0) {
						gameView.setNowScoreAnimating(true);
						gameView.setScoreTextByCenter(
								(int) (15 + Math.log(nowScore) * 10),
								nowScoreText, nowBonusText,
								remarkScore(nowScore), x, y);
					}
				} else if (isFocusing && mapItemFocus[motionI][motionJ]) {
					// Start Clearing Animation
					generateScore();
					setTotalScore(nowScore);
					gameView.setItemFading(true);
					Sound.play(this, Sound.SE_CLEAR, 0, this);
					if (nowScore > 0) {
						gameView.setNowScoreAnimating(true);
						gameView.setScoreTextByCenter(
								(int) (15 + Math.log(nowScore) * 10),
								nowScoreText, nowBonusText,
								remarkScore(nowScore), x, y);
					}
				} else {
					// Focus
					initMapItemFocus();
					isFocusing = true;
					focusItem(motionI, motionJ);
					generateCount();
				}
			} else {
				// Invalid Touch
				++totalClick;
				initMapItemFocus();
			}
		}
	}

	// Main Thread
	@Override
	public void run() {
		while (!finished && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(40);
				if (isGameViewReady && isStatusViewReady && isMapReady) {
					decreaseMapItemFaceChangeTime();
					randomSetMapItemFaceChangeTime();
					if (!isAllVisible)
						randomSetMapItemVisible();
					if (gameView.isStarted())
						checkResult();
				}
				time = gameTimer.getTime();
				statusView.setTime(time);
				gameView.postInvalidate();
				statusView.postInvalidate();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
