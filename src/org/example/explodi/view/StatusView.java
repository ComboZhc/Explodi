package org.example.explodi.view;

import org.example.explodi.R;
import org.example.explodi.Utils;
import org.example.explodi.mode.AbstractMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;

public class StatusView extends AbstractView {
	private static String TAG = "StatusView";
	private final AbstractMode game;
	
	private Bitmap bitTotalRateImage;
	private Bitmap bitBackground;
	private int totalScore = 0;
	private int nowTotalScore = 0;
	private String totalRateText;
	private String totalScoreText;
	private String timeText;
	private boolean isValuesReady;
	private Point totalRatePoint = new Point();
	private Point totalScorePoint = new Point();
	private Point timePoint = new Point();
	private FontMetrics fontMetrics;
	private Paint textPaint = new Paint();
	private Typeface typeface;
	
	public boolean isValuesReady() {
		return isValuesReady;
	}
	public void setValuesReady(boolean isReady) {
		this.isValuesReady = isReady;
	}
	public StatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.game = (AbstractMode)context;
		this.typeface = Typeface.createFromAsset(this.getResources().getAssets(), "cooper.ttf");
		this.textPaint.setTypeface(typeface);
		Log.d(TAG, "onStatusView");
	}
	@Override 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Bitmap tmpBitmap;
		tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.st)).getBitmap();
		bitTotalRateImage = createBitmapByScale(tmpBitmap, (float)(h) / tmpBitmap.getHeight(), (float)(h) /tmpBitmap.getHeight());
		tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.status_bar)).getBitmap();
		bitBackground = Bitmap.createBitmap(tmpBitmap);
		//ScoreText
		Paint tmpPaint = new Paint();
		tmpPaint.setTextSize(h);
		tmpPaint.setTextAlign(Align.CENTER);
		fontMetrics = tmpPaint.getFontMetrics();
		timePoint.x = w / 2;
		timePoint.y = (int) (h / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2);
		totalRatePoint.x = bitTotalRateImage.getWidth();
		totalRatePoint.y = (int) (h / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2);
		totalScorePoint.x = w;
		totalScorePoint.y = (int) (h / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2);
		game.setStatusViewReady(true);
	}

	
	public void setTime(int time) {
		this.timeText = Utils.getTimeBySecond(time);
	}
	
	public void setTotalRateText(int remainCount, int totalCount) {
		this.totalRateText = remainCount + "/" + totalCount;
	}
	public void setTotalScore(int score) {
		this.totalScore = score;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		drawImageByCorner(canvas, bitBackground,
				0, 0, bitBackground.getWidth(), bitBackground.getHeight(),
				0, 0, getWidth(), getHeight(), null);
		drawImageByCorner(canvas, bitTotalRateImage, 
				0, 0, bitTotalRateImage.getWidth(), bitTotalRateImage.getHeight(), 
				0, 0, bitTotalRateImage.getWidth(), bitTotalRateImage.getHeight(), 
				null);
		if (isValuesReady){
			
			nowTotalScore += Math.ceil((totalScore - nowTotalScore) * 0.2);
			totalScoreText = Integer.toString(nowTotalScore);
			textPaint.setTextSize(this.getHeight());
			textPaint.setTextAlign(Align.CENTER);
			totalRatePoint.x = (int) (bitTotalRateImage.getWidth() + textPaint.measureText(this.totalRateText) / 2);
			drawTextByBaseline(canvas, totalRateText, totalRatePoint, this.getHeight(), Color.WHITE, Color.BLACK, 0xFF, textPaint);
			totalScorePoint.x = (int) (this.getWidth() - textPaint.measureText(this.totalScoreText) / 2 - 1);
			drawTextByBaseline(canvas, totalScoreText, totalScorePoint, this.getHeight(), Color.WHITE, Color.BLACK, 0xFF, textPaint);
			timePoint.x = (int) (this.getWidth() / 2);
			drawTextByBaseline(canvas, timeText, timePoint, this.getHeight(), Color.WHITE, Color.BLACK, 0xFF, textPaint);
		}
	}
}
