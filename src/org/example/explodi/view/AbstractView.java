package org.example.explodi.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class DoubleBitmap {
	public static int SMALL = 0;
	public static int LARGE = 1;
	Bitmap small;
	Bitmap large;

	Bitmap getBitmap(int k) {
		if (k == SMALL)
			return small;
		if (k == LARGE)
			return large;
		return null;
	}
}

class BitItemFaceImage {
	public DoubleBitmap image;
	public DoubleBitmap imageAnother;
};

class Value {
	private double value = 0.00;
	private List<Double> values = null;
	private List<Integer> steps = null;
	private int nowPos = 1;
	private int nowStep = 0;

	public Value(double startValue) {
		value = startValue;
		values = new ArrayList<Double>();
		steps = new ArrayList<Integer>();
		values.add(startValue);
		steps.add(0);
	}

	public double getDouble() {
		return value;
	}

	public int getInt() {
		return (int) value;
	}

	public Value add(double endValue, int step) {
		values.add(endValue);
		steps.add(step);
		return this;
	}

	public boolean next() {
		if (nowPos < 0 || nowPos >= steps.size())
			return false;
		else {
			++nowStep;
			if (nowStep > steps.get(nowPos)) {
				nowStep = 0;
				++nowPos;
			}
			if (nowPos < 0 || nowPos >= steps.size())
				return false;
			value = (values.get(nowPos) * nowStep + values.get(nowPos - 1)
					* (steps.get(nowPos) - nowStep))
					/ steps.get(nowPos);
		}
		return true;
	}
}

public abstract class AbstractView extends View {

	public AbstractView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Matrix matrix = new Matrix();

	public DoubleBitmap createDoubleBitmapByScale(Bitmap bitmap,
			double widthScale, double heightScale) {
		DoubleBitmap ret = new DoubleBitmap();
		matrix.reset();
		matrix.postScale((float) widthScale, (float) heightScale);
		ret.small = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		matrix.reset();
		matrix.postScale((float) widthScale * 2, (float) heightScale * 2);
		ret.large = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return ret;
	}

	public Bitmap createBitmapByScale(Bitmap bitmap, double widthScale,
			double heightScale) {
		matrix.reset();
		matrix.postScale((float) widthScale, (float) heightScale);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	public void drawTextByBaseline(Canvas canvas, String str, Point pointBase,
			int textSize, int colorFill, int colorStroke, int alpha, Paint paint) {
		paint.setTextSize(textSize);
		paint.setColor(colorStroke);
		paint.setAlpha(alpha);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawText(str, 0, str.length(), pointBase.x, pointBase.y, paint);
		paint.setColor(colorFill);
		paint.setStyle(Style.FILL);
		paint.setAlpha(alpha);
		canvas.drawText(str, 0, str.length(), pointBase.x, pointBase.y, paint);
	}

	public void drawImageByCorner(Canvas canvas, Bitmap bitmap, int srcX,
			int srcY, int srcW, int srcH, int dstX, int dstY, int dstW,
			int dstH, Paint paint) {
		Rect src = new Rect();
		Rect dst = new Rect();
		src.set(srcX, srcY, srcX + srcW, srcY + srcH);
		dst.set(dstX, dstY, dstX + dstW, dstY + dstH);
		canvas.drawBitmap(bitmap, src, dst, paint);
	}

	public void drawImageByCenter(Canvas canvas, Bitmap bitmap, int srcX,
			int srcY, int srcW, int srcH, int dstX, int dstY, int dstW,
			int dstH, Paint paint) {
		Rect src = new Rect();
		Rect dst = new Rect();
		src.set(srcX, srcY, srcX + srcW, srcY + srcH);
		dst.set(dstX - dstW / 2, dstY - dstH / 2, dstX + dstW - dstW / 2, dstY
				+ dstH - dstH / 2);
		canvas.drawBitmap(bitmap, src, dst, paint);
	}

}
