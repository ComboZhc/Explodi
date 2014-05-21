package org.example.explodi.mode;

import org.example.explodi.Difficulty;
import org.example.explodi.Mode;
import org.example.explodi.Utils;

public class FillMode extends ClassicMode {
	@Override
	public Mode getMode() {
		return Mode.FILL;
	}

	private boolean[][] mapPositionFilled;
	private boolean[][] mapItemAppear;
	private int fillCount = 0;
	private int timeLimit = 0;

	@Override
	protected void setDifficulty(Difficulty difficulty) {
		switch (difficulty) {
		case EASY:
			columnsSize = 8;
			itemColorsOrdinaryKind = 4;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 60;
			itemLargeSize = Utils.random.nextInt(6);
			break;
		case NORMAL:
			columnsSize = 9;
			itemColorsOrdinaryKind = 5;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 120;
			itemLargeSize = Utils.random.nextInt(7);
			break;
		case HARD:
			columnsSize = 10;
			itemColorsOrdinaryKind = 6;
			itemFacesKind = 7;
			itemLargeSize = 0;
			timeLimit = 180;
			itemLargeSize = Utils.random.nextInt(8);
			break;
		}
	}

	@Override
	public int getItemColorsSize() {
		return itemColorsOrdinaryKind;
	}

	@Override
	public boolean getPositionFilled(int i, int j) {
		return mapPositionFilled[i][j];
	}

	@Override
	public boolean getItemAppear(int i, int j) {
		return mapItemAppear[i][j];
	}

	@Override
	protected void generateScore() {
		nowScore = 0;
		nowBonusText = "";
		if (fillCount > 0) {
			nowScore += fillCount * 150;
			nowScoreText = Integer.toString(nowScore);
		}
		if (fillCount >= 20) {
			nowBonusText += "+1500";
			nowScore += 1500;
		} else if (fillCount >= 10) {
			nowBonusText += "+1000";
			nowScore += 1000;
		} else if (fillCount >= 5) {
			nowBonusText += "+500";
			nowScore += 500;
		}

		if (fillCount == remainCount) {
			nowBonusText += (nowBonusText == "" ? "x5" : " x5");
			nowScore *= 5;
		}
		if (fillCount > maxCount)
			maxCount = fillCount;
	}

	@Override
	protected void generateCount() {
		fillCount = 0;
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemFocus[i][j] && !mapPositionFilled[i][j]) {
					++fillCount;
				}
	}

	private void fillPosition() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemFocus[i][j] && !mapPositionFilled[i][j]) {
					mapPositionFilled[i][j] = true;
				}
		remainCount -= fillCount;
	}

	private void initMapPositionFilled() {
		mapPositionFilled = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapPositionFilled[i][j] = false;
	}

	private void initMapItemAppear() {
		isAppearing = false;
		mapItemAppear = new boolean[columnsSize][rowsSize];
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				mapItemAppear[i][j] = false;
	}

	@Override
	public void initValue() {
		remainCount = columnsSize * rowsSize;
		totalCount = remainCount;
	}

	@Override
	public void initMap() {
		initMapPositionFilled();
		initMapItemAppear();
		super.initMap();
	}

	@Override
	public void appear() {
		isAppearing = true;
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemColor[i][j] == 0) {
					mapItemColor[i][j] = Utils.random
							.nextInt(itemColorsOrdinaryKind) + 1;
					mapItemFace[i][j] = Utils.random.nextInt(itemFacesKind);
					mapItemAppear[i][j] = true;
				}
	}

	@Override
	public void endAppear() {
		initMapItemAppear();
	}

	@Override
	protected void clearSelectedItems() {
		for (int i = 0; i < columnsSize; ++i)
			for (int j = 0; j < rowsSize; ++j)
				if (mapItemFocus[i][j]) {
					clearItem(i, j);
				}
	}

	@Override
	public void clear() {
		fillPosition();
		super.clear();
		appear();
		gameView.setItemPopuping(true);
	}

	@Override
	public void checkResult() {
		boolean failed = true;
		fail_break: for (int i = 0; i < columnsSize; ++i) {
			for (int j = 0; j < rowsSize; ++j)
				if (isValidItem(i, j)) {
					failed = false;
					break fail_break;
				}
		}
		failed |= time >= timeLimit;
		if ((failed || remainCount == 0)) {
			isFinishing = true;
			if (!gameView.isViewAnimating())
				endGame(remainCount == 0);
		}
	}

	@Override
	protected boolean judgeNewRecord(int score, int time, float complete,
			String lastDate, int lastScore, int lastTime, float lastComplete) {
		return lastDate == null
				|| complete > lastComplete
				|| ((Math.abs(complete - lastComplete) < EPSILON) && (time < lastTime || (time == lastTime && totalScore > lastScore)));
	}

	@Override
	public int getBackgroundAlpha() {
		return 0xFF;
	}

	@Override
	public String getReadyText() {
		return Utils.getTimeBySecond(timeLimit);
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
