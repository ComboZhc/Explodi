package org.example.explodi;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class GameTimer {
	private static final int ADD_TIME = 1;
	private int time = 0;
	private int lastTime = 0;
	private Timer timer = new Timer(true);
	private boolean running = false;
	private boolean paused = false;
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ADD_TIME:
				++time;
				break;
			}
			super.handleMessage(msg);
		}
	};

	private TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = ADD_TIME;
			handler.sendMessage(message);
		}
	};
	
	public void start() {
		time = 0;
		lastTime = 0;
		timer.schedule(task, 1000, 1000);
		running = true;
		paused = false;
	}
	
	public void stop() {
		timer.cancel();
		running = false;
		paused = false;
	}
	
	public void pause() {
		if (!paused) {
			lastTime = time;
			paused = true;
		}
	}
	
	public void resume() {
		if (paused) {
			time = lastTime;
			paused = false;
		}
	}
	
	public int getTime() {
		if (paused)
			return lastTime;
		else 
			return time;
	}
}
