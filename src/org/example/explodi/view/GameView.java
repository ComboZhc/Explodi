package org.example.explodi.view;

import org.example.explodi.R;
import org.example.explodi.Utils;
import org.example.explodi.mode.AbstractMode;
import org.example.explodi.mode.ClassicMode;
import org.example.explodi.mode.FillMode;
import org.example.explodi.mode.GravityMode;
import org.example.explodi.mode.TimeMode;

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
import android.view.MotionEvent;

public class GameView extends AbstractView {
	private static final String TAG = "GameView";
	//Constant
	private static final double EPSILON = 0.000001;
	private static final int FACES_SIZE = 7;
	private static final int COLORS_SIZE = 6;
	private int[] scoreTextColors;
	private int nowScoreTextColor = 0;
	private final AbstractMode game;
	private static Bitmap bitBackgroundImage;
	private static Bitmap bitLaser;
	private static Bitmap bitWarning;
	private static DoubleBitmap bitItemBlur;
	private static DoubleBitmap[] bitItemsColorImage;
	private static DoubleBitmap bitItemImageBlur;
	private static BitItemFaceImage[] bitItemsFaceImage;

	private float itemImageWidth, itemImageHeight;
	private boolean isItemFading = false;
	private boolean isItemAppearing = false;
	private boolean isWarning = false;
	private boolean isNowScoreAnimating = false;
	private boolean isNowRemarkAnimating = false;
	private boolean isReadyGoAnimating = true;
	private String nowScoreText;
	private String nowBonusText;
	private String nowRemarkText;
	private Value itemFadingImageScale;
	private Value itemAppearingImageScale;
	private int nowScoreTextHeight;
	private int nowBonusTextHeight;
	private Value nowScoreTextScale;
	private Value nowScoreTextAlpha;
	private Value nowBonusTextScale;
	private Value nowBonusTextAlpha;
	private Value nowRemarkTextScale;
	private Value nowRemarkTextAlpha;
	private Value nowWarningAlpha;
	private String nowReadyText;
	private Value nowReadyTextAlpha;
	private Value nowGoTextAlpha;
	
	
	private Point nowScoreTextPoint = new Point();
	private Point nowBonusTextPoint = new Point();
	private Point nowRemarkTextPoint = new Point();
	private Point nowScoreTextDeltaPoint = new Point();
	private Point nowReadyTextPoint = new Point();
	private Point nowReadyTextDeltaPoint = new Point();
	private Point nowGoTextPoint = new Point();
	
	private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Typeface typeface;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.game = (AbstractMode) context;
		this.typeface = Typeface.createFromAsset(this.getResources().getAssets(), "cooper.ttf");
		this.textPaint.setTypeface(typeface);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		Log.d(TAG, "onGameView");
	}
	public void setNowScoreAnimating(boolean animating) {
		this.isNowScoreAnimating = animating;
		this.isNowRemarkAnimating = animating;
		this.nowScoreTextScale = new Value(0).add(1, 3).add(1, 8).add(1, 5);
		this.nowScoreTextAlpha = new Value(0xff).add(0xff, 3).add(0xff, 8).add(0x7f, 5);
		this.nowBonusTextScale = new Value(0).add(1, 3).add(1, 8).add(1, 40);
		this.nowBonusTextAlpha = new Value(0xff).add(0xff, 3).add(0xff, 8).add(0, 10);
		this.nowRemarkTextScale = new Value(0).add(0, 3).add(0, 8).add(1, 3).add(1, 8).add(1, 5);
		this.nowRemarkTextAlpha = new Value(0xff).add(0xff, 3).add(0xff, 8).add(0xff, 3).add(0xff, 8).add(0, 5);

	}
	public void setItemFading(boolean isItemFading) {
		this.isItemFading = isItemFading;
		this.itemFadingImageScale = new Value(1).add(0, 3);
	}	
	public void setItemPopuping(boolean isItemPopuping) {
		this.isItemAppearing = isItemPopuping;
		this.itemAppearingImageScale = new Value(0).add(1, 3);
	}
	public void setWarning(boolean isWarning) {
		this.isWarning = isWarning;
		this.nowWarningAlpha = new Value(0).add(0xff, 5).add(0xff, 5).add(0, 5);
	}
	private void setReadyGoAnimating(boolean animating) {
		this.isReadyGoAnimating = animating;
		this.nowReadyTextDeltaPoint.set(0, -nowReadyTextPoint.y / 10);
		this.nowReadyTextAlpha = new Value(0).add(0xff, 3).add(0xff, 20).add(0, 10);
		this.nowGoTextAlpha = new Value(0).add(0, 3).add(0, 20).add(0xff, 3).add(0xff, 20).add(0, 3);
		this.nowReadyText = game.getReadyText();
	}
	public boolean isViewLocked() {
		return isItemFading || isItemAppearing;
	}
	public boolean isWarning() {
		return isWarning;
	}
	public boolean isViewAnimating() {
		return isNowScoreAnimating || isNowRemarkAnimating || isReadyGoAnimating || isViewLocked();
	}
	public boolean isStarted() {
		return !isReadyGoAnimating;
	}
	public float getItemImageWidth() {
		return itemImageWidth;
	}
	public float getItemImageHeight() {
		return itemImageHeight;
	}
	public void setScoreTextByCenter(int scoreHeight, String scoreText, String bonusText, String remarkText, float x, float y) {
		Paint scorePaint = new Paint();
		Paint bonusPaint = new Paint();
		nowScoreText = scoreText;
		nowBonusText = bonusText;
		nowRemarkText = remarkText;
		nowScoreTextHeight = (int)Math.min(getHeight() / 10, scoreHeight);
		nowBonusTextHeight = nowScoreTextHeight / 2;
		scorePaint.setTextSize(nowScoreTextHeight);
		bonusPaint.setTextSize(nowBonusTextHeight);
		scorePaint.setTextAlign(Align.CENTER);
		bonusPaint.setTextAlign(Align.CENTER);
		//scorePaint.setTypeface(typeface);
		//bonusPaint.setTypeface(typeface);
		FontMetrics scoreFM = scorePaint.getFontMetrics();
		FontMetrics bonusFM = bonusPaint.getFontMetrics();
		int textLength = (int)Math.max(scorePaint.measureText(nowScoreText), scorePaint.measureText(remarkText));
		textLength = (int)Math.max(textLength, bonusPaint.measureText(bonusText));
		if (bonusText.length() > 0) {
			Point nowMiddlePoint = fixPointByCenter((int)x, (int) (y), textLength, nowScoreTextHeight + nowBonusTextHeight);
			nowScoreTextPoint.set(nowMiddlePoint.x, nowMiddlePoint.y);
			nowScoreTextPoint.y = (int) (nowScoreTextPoint.y - nowScoreTextHeight / 2 - (scoreFM.ascent + scoreFM.descent) / 2);
			nowBonusTextPoint.set(nowMiddlePoint.x, nowMiddlePoint.y);
			nowBonusTextPoint.y = (int) (nowBonusTextPoint.y + nowBonusTextHeight / 2 - (bonusFM.ascent + bonusFM.descent) / 2);
			nowScoreTextDeltaPoint.set((int)(this.getWidth() - nowScoreTextPoint.x - scorePaint.measureText(nowScoreText) / 3) / 10, -nowScoreTextPoint.y / 10);
			nowRemarkTextPoint.set(nowScoreTextPoint.x, nowScoreTextPoint.y);
		} else {	
			Point nowMiddlePoint = fixPointByCenter((int)x, (int) (y), textLength, nowScoreTextHeight);
			nowScoreTextPoint.set(nowMiddlePoint.x, nowMiddlePoint.y);
			nowScoreTextPoint.y = (int) (nowScoreTextPoint.y - (scoreFM.ascent + scoreFM.descent) / 2);
			nowScoreTextDeltaPoint.set((int)(this.getWidth() - nowScoreTextPoint.x - scorePaint.measureText(nowScoreText) / 3) / 10, -nowScoreTextPoint.y / 10);
			nowRemarkTextPoint.set(nowScoreTextPoint.x, nowScoreTextPoint.y);
		}
	}
	private Point fixPointByCenter(int x, int y, int w, int h) {
		if (x - w / 2 < 0)
			x = w / 2;
		if (x + w - w / 2 > this.getWidth())
			x = this.getWidth() - w + w / 2;
		if (y - h / 2 < 0)
			y = h / 2;
		if (y + h - h / 2 > this.getHeight())
			y = this.getHeight() - h + h / 2;
		Point point = new Point(x, y);
		return point;
	}

	@Override 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//ItemBlur
		Bitmap tmpBitmap;
		tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.cblur)).getBitmap();
		
		//Initialize itemImage and mapSize
		itemImageWidth = (float)w / game.getColumnsSize();
		int rowsSize = (int)(h / (tmpBitmap.getHeight() / tmpBitmap.getWidth() * itemImageWidth));
		if (game.getClass().equals(TimeMode.class) && (rowsSize % 2) == 1)
			++rowsSize;
		game.setRowsSize(rowsSize);
		itemImageHeight = (float)h / game.getRowsSize();
		game.initValue();
		game.initMap();
		//----------------------------------Get Picture--------------------------------//
		//ItemsColorImage
		bitItemsColorImage = new DoubleBitmap[COLORS_SIZE];
		for (int i = 0; i < COLORS_SIZE; ++i) {
			tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.c1 + i)).getBitmap();
			bitItemsColorImage[i] = createDoubleBitmapByScale(tmpBitmap, (float)itemImageWidth / tmpBitmap.getWidth(), (float)itemImageHeight / tmpBitmap.getHeight());
		}
		//ScoreTextColor
		scoreTextColors = getResources().getIntArray(R.array.scoreColors);
		//ItemBlur
		tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.cblur)).getBitmap();
		bitItemBlur = createDoubleBitmapByScale(tmpBitmap, (float)itemImageWidth/ tmpBitmap.getWidth(), (float)itemImageHeight / tmpBitmap.getHeight());
		
		//ItemsFaceImage
		bitItemsFaceImage = new BitItemFaceImage[FACES_SIZE];
		for (int i = 0; i < FACES_SIZE; ++i) {
			bitItemsFaceImage[i] = new BitItemFaceImage();
			tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.ea1 + i)).getBitmap();
			bitItemsFaceImage[i].image = createDoubleBitmapByScale(tmpBitmap, (float)itemImageWidth / tmpBitmap.getWidth(), (float)itemImageHeight / tmpBitmap.getHeight());
			tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.eo1 + i)).getBitmap();
			bitItemsFaceImage[i].imageAnother = createDoubleBitmapByScale(tmpBitmap, (float)itemImageWidth  / tmpBitmap.getWidth(), (float)itemImageHeight / tmpBitmap.getHeight());
		}
		//ItemImageBlur
		tmpBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.eaf)).getBitmap();
		bitItemImageBlur = createDoubleBitmapByScale(tmpBitmap, (float)itemImageWidth  / tmpBitmap.getWidth(), (float)itemImageHeight / tmpBitmap.getHeight());
		//Warning
		bitWarning = ((BitmapDrawable)getResources().getDrawable(R.drawable.warning)).getBitmap();
		bitWarning = createBitmapByScale(bitWarning, itemImageHeight * 2 / bitWarning.getHeight(), itemImageHeight * 2 / bitWarning.getHeight());
		//Laser
		bitLaser = ((BitmapDrawable)getResources().getDrawable(R.drawable.laser)).getBitmap();
		//Background
		int backgroundID = 0;
		if (game.getClass().equals(ClassicMode.class))
			backgroundID = R.drawable.background_classic;
		else if (game.getClass().equals(TimeMode.class))
			backgroundID = R.drawable.background_time;
		else if (game.getClass().equals(FillMode.class))
			backgroundID = R.drawable.background_fill;
		else if (game.getClass().equals(GravityMode.class))
			backgroundID = R.drawable.background_gravity;
		bitBackgroundImage = ((BitmapDrawable)getResources().getDrawable(backgroundID)).getBitmap();
		//--------------------------------Get Picture END----------------------------//
		Log.d(TAG, "background loaded");
		nowGoTextPoint.set(w / 2, h / 2);
		nowReadyTextPoint.set(w / 2, h / 2);
		setReadyGoAnimating(true);
		game.setGameViewReady(true);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		//Background
		alphaPaint.setAlpha(game.getBackgroundAlpha());
		drawImageByCorner(canvas, bitBackgroundImage, 
				0, 0, bitBackgroundImage.getWidth(), bitBackgroundImage.getHeight(), 
				0, 0, this.getWidth(), this.getHeight(), 
				alphaPaint);
		//FillMode.Background
		for (int i = 0; i < game.getColumnsSize(); ++i)
			for (int j = 0; j < game.getRowsSize(); ++j) 
				if (!game.getPositionFilled(i, j)) {
					alphaPaint.setAlpha(0xFF);
					canvas.drawRect(i * itemImageWidth, j * itemImageHeight, (i + 1) * itemImageWidth, (j + 1) * itemImageHeight, alphaPaint);
				}
		
		//Fade & Appear
		if (isItemFading && !itemFadingImageScale.next()) {
			game.clear();
			isItemFading = false;
		}
		if (isItemAppearing && !itemAppearingImageScale.next()) {
			game.endAppear();
			isItemAppearing = false;
		}
		//Items
		for (int i = 0; i < game.getColumnsSize(); ++i)
			for (int j = 0; j < game.getRowsSize(); ++j) {
				if (game.getItemColor(i, j) != 0 && (!game.getItemAffiliated(i, j) || game.getItemLarge(i, j))) {
					int k = game.getItemLarge(i, j) ? DoubleBitmap.LARGE : DoubleBitmap.SMALL;
					int x = game.getItemLarge(i, j) ? (int)((i + 1) * itemImageWidth) : (int)((i + 0.5) * itemImageWidth);
					int y = game.getItemLarge(i, j) ? (int)((j + 1) * itemImageHeight) : (int)((j + 0.5) * itemImageHeight);
					if (isItemFading && game.getItemFocus(i, j)) {
						nowScoreTextColor = scoreTextColors[game.getItemColor(i, j) - 1];
						if (itemFadingImageScale.getDouble() > EPSILON) {
							Bitmap tmpBitmap = createBitmapByScale(bitItemsColorImage[game.getItemColor(i, j) - 1].getBitmap(k), itemFadingImageScale.getDouble(), itemFadingImageScale.getDouble());
							drawImageByCenter(canvas, tmpBitmap,
									0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(),
									x, y, tmpBitmap.getWidth(), tmpBitmap.getHeight(), null);
						}
					} else if (isItemAppearing && game.getItemAppear(i, j)) {
						if (itemAppearingImageScale.getDouble() > EPSILON) {
							Bitmap tmpBitmap = createBitmapByScale(bitItemsColorImage[game.getItemColor(i, j) - 1].getBitmap(k), itemAppearingImageScale.getDouble(), itemAppearingImageScale.getDouble());
							drawImageByCenter(canvas, tmpBitmap,
									0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(),
									x, y, tmpBitmap.getWidth(), tmpBitmap.getHeight(), null);
						}
					} else {
						canvas.drawBitmap(bitItemsColorImage[game.getItemColor(i, j) - 1].getBitmap(k), i * itemImageWidth, j * itemImageHeight, null);	
						if (game.getItemFocus(i, j)) {
							alphaPaint.setAlpha(0x7F);
							canvas.drawBitmap(bitItemImageBlur.getBitmap(k),  i * itemImageWidth, j * itemImageHeight, null);
							canvas.drawBitmap(bitItemBlur.getBitmap(k),  i * itemImageWidth, j * itemImageHeight, alphaPaint);
						}
						else if (game.getItemFaceChanged(i, j))
							canvas.drawBitmap(bitItemsFaceImage[game.getItemFace(i, j)].imageAnother.getBitmap(k), i * itemImageWidth, j * itemImageHeight, null);
						else
							canvas.drawBitmap(bitItemsFaceImage[game.getItemFace(i, j)].image.getBitmap(k), i * itemImageWidth, j * itemImageHeight, null);
					}
				}
			}
		//Laser
		if (game.getClass().equals(TimeMode.class)) {
			alphaPaint.setAlpha(Utils.random.nextInt(0x7F) + 0x80);
			drawImageByCenter(canvas, bitLaser,
					0, 0, bitLaser.getWidth(), bitLaser.getHeight(),
					getWidth() / 2, getHeight() / 2, getWidth(), (int)itemImageHeight * 2,
					alphaPaint);
		}
		//Value
		if (this.isNowScoreAnimating) {
			this.nowScoreTextScale.next();
			if (!this.nowScoreTextAlpha.next())
				this.isNowScoreAnimating = false;
			int textSize = (int)(this.nowScoreTextHeight * this.nowScoreTextScale.getDouble());
			int alpha = this.nowScoreTextAlpha.getInt();
			if (alpha < 0xff)
				this.nowScoreTextPoint.offset(nowScoreTextDeltaPoint.x, nowScoreTextDeltaPoint.y);
			if (textSize > 0)
				drawTextByBaseline(canvas, this.nowScoreText, this.nowScoreTextPoint, textSize, nowScoreTextColor, Color.WHITE, alpha, textPaint);
			this.nowBonusTextScale.next();
			this.nowBonusTextAlpha.next();
			textSize = (int)(this.nowBonusTextHeight * this.nowBonusTextScale.getDouble());
			alpha = this.nowBonusTextAlpha.getInt();
			if (textSize > 0)
				drawTextByBaseline(canvas, this.nowBonusText, this.nowBonusTextPoint, textSize, nowScoreTextColor, Color.WHITE, alpha, textPaint);
		}
		if (this.isNowRemarkAnimating) {
			this.nowRemarkTextScale.next();
			if (!this.nowRemarkTextAlpha.next())
				this.isNowRemarkAnimating = false;
			int textSize = (int)(this.nowScoreTextHeight * this.nowRemarkTextScale.getDouble());
			int alpha = this.nowRemarkTextAlpha.getInt();
			if (textSize > 0) 
				drawTextByBaseline(canvas, this.nowRemarkText, this.nowRemarkTextPoint, textSize, nowScoreTextColor, Color.WHITE, alpha, textPaint);
		}
		if (this.isWarning) {
			if (!this.nowWarningAlpha.next())
				this.isWarning = false;
			int alpha = this.nowWarningAlpha.getInt();
			alphaPaint.setAlpha(alpha);
			drawImageByCenter(canvas, bitWarning, 
						0, 0, bitWarning.getWidth(), bitWarning.getHeight(), 
						getWidth() / 2, getHeight() / 2, (int)itemImageWidth * 2, (int)itemImageHeight * 2, 
						alphaPaint);
		}
		//Ready & Go
		if (isReadyGoAnimating) {
			if (nowGoTextAlpha != null && nowReadyTextAlpha != null) {
				if (nowGoTextAlpha.next())
					drawTextByBaseline(canvas, getResources().getString(R.string.text_go), nowGoTextPoint, getHeight() / 5, Color.WHITE, Color.BLACK, nowGoTextAlpha.getInt(), textPaint);
				else {
					game.startGame();
					isReadyGoAnimating = false;
				}
				if (nowGoTextAlpha.getInt() > 0)
					nowReadyTextPoint.offset(nowReadyTextDeltaPoint.x, nowReadyTextDeltaPoint.y);
				if (nowReadyTextAlpha.next())
					drawTextByBaseline(canvas, nowReadyText, nowReadyTextPoint, getHeight() / 10, Color.WHITE, Color.BLACK, nowReadyTextAlpha.getInt(), textPaint);
			}
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent @ GameView");
		if (event.getAction() == MotionEvent.ACTION_UP && !isReadyGoAnimating)
			game.manageMotion(event.getX(), event.getY());
		return true;
	}
	
}
