package org.example.explodi;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;

import android.content.Context;

public class Utils {

	public static final Random random = new Random();
	private static DecimalFormat doubleDigitFormat = new DecimalFormat("00");

	public static String getNowDateString() {
		return Calendar.getInstance().getTime().toLocaleString();
	}

	public static String getKey(Mode m, Difficulty difficulty) {
		return Explodi.TAG_DB + m.name() + "MODE_" + difficulty.name() + "_";
	}

	public static String getKey(Mode m) {
		return Explodi.TAG_DB + m.name() + "MODE";
	}

	public static String getTimeBySecond(int time) {
		return (time / 60) + ":" + doubleDigitFormat.format(time % 60);
	}

	private static DecimalFormat percentDF = new DecimalFormat("#0.0%");

	public static String getPercentByDouble(double d) {
		return percentDF.format(d);
	}

	public static String format(Context context, int id, Object... args) {
		return String.format(context.getResources().getString(id), args);
	}
}
