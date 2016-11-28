package com.sullhouse.horserace;

import java.util.Timer;

public class HorseRaceTask {
	private int intervalSeconds;
	@SuppressWarnings("unused")
	private HorseRaceRunner horseRaceRunner;
	private Timer timer;
	
	public HorseRaceTask(int intervalSeconds, HorseRaceRunner horseRaceRunner) {
		super();
		this.intervalSeconds = intervalSeconds;
		
		this.timer = new Timer();
		
		this.horseRaceRunner = horseRaceRunner;
		
		this.timer.scheduleAtFixedRate(horseRaceRunner, 0, intervalSeconds * 1000);
	}

	public int getInvervalSeconds() {
		return intervalSeconds;
	}

	public void setInvervalSeconds(int intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}
	
	public void stop() {
		this.timer.cancel();
		this.timer.purge();
	}
}