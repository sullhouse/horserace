package com.sullhouse.horserace;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.sullhouse.gambol.EmailSender;
import com.sullhouse.gambol.HorseRaceGambol;
import com.sullhouse.gambol.Player;

public class HorseRaceRunner extends TimerTask {
	private HorseRace horseRace;
	private String emails;
	private String subject;
	private Timer timer;
	private int secondsDelay;
	private EmailSender emailSender;
	private boolean bettingOpen;
	
	public HorseRaceRunner(int length, int secondsDelay, Date startTime, List<Player> players) {
		super();
		this.horseRace = new HorseRace(length);
		this.secondsDelay = secondsDelay;
		
		this.timer = new Timer();
		
		this.emailSender = new EmailSender();
		
		this.emails = "";
		
		this.subject = "Horse Race " + horseRace.getId() + "!!!!";
		
		String configHtml = "<h3>A new horse race has been scheduled!</h3>"
				+ "Length: " + length
				+ "<br>Start Time: " + getDateString(startTime);
		
		boolean first = true;
		for (Player p : players) {
			emailSender.sendEmail(p.getEmail(), this.subject, configHtml + p.getBettingLinksHTML(horseRace.getId()) + "<br><br>" + horseRace.toHTML());
			if (first) {
				this.emails = p.getEmail();
				first = false;
			} else {
				this.emails += p.getEmail();
			}
		}
		
		this.bettingOpen = true;
		
		this.timer.schedule(this, startTime, secondsDelay * 1000);
		
		HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
		
		horseRaceDatabaseAccess.putHorseRace(this.horseRace, startTime);
	}
	
	public String getSubject() {
		return subject;
	}

	public Timer getTimer() {
		return timer;
	}

	public boolean isBettingOpen() {
		return bettingOpen;
	}

	public int getSecondsDelay() {
		return secondsDelay;
	}

	public void setSecondsDelay(int secondsDelay) {
		this.secondsDelay = secondsDelay;
	}

	public void run() {
		if (this.bettingOpen) {
			HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
			horseRaceDatabaseAccess.setBettingClosed(horseRace.getId());
			setEmails(horseRaceDatabaseAccess.getHorseRaceEmails(horseRace.getId()));
		}
		
		HorseRaceGambol horseRaceGambol = new HorseRaceGambol(horseRace.getId());
		
		horseRace.flipCard();
		emailSender.sendEmail(emails, this.subject, horseRace.toHTML() + "<br><br>" + horseRaceGambol.getPayoutsByHorse());				
		if (horseRace.isGameOver()) {
			this.stop();
		}
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public HorseRace getHorseRace() {
		return horseRace;
	}

	public EmailSender getEmailSender() {
		return emailSender;
	}
	
	public void stop() {
		this.timer.cancel();
		this.timer.purge();
	}
	
	private String getDateString(Date d) {
		SimpleDateFormat formatter = new SimpleDateFormat("E MMM d h:mm a");
		TimeZone tz = TimeZone.getTimeZone("EST");
		formatter.setTimeZone(tz);
		return formatter.format(d);
	}
}