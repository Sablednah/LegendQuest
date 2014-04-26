package me.sablednah.legendquest.skills;

public class LQTime {

	private int	ticks;
	public int	TICKS_PER_DAY		= 1728000;
	public int	TICKS_PER_HOUR		= 72000;
	public int	TICKS_PER_MINUTE	= 1200;
	public int	TICKS_PER_SECOND	= 20;

	public LQTime(String time) {
		this.ticks = convert(time);
	}

	public LQTime(int ticks) {
		this.ticks = ticks;
	}

	public LQTime(int seconds, int ticks) {
		this.ticks = (seconds * TICKS_PER_SECOND + ticks);
	}

	public LQTime(int minutes, int seconds, int ticks) {
		this.ticks = (minutes * TICKS_PER_MINUTE + seconds * TICKS_PER_SECOND + ticks);
	}

	public LQTime(int hours, int minutes, int seconds, int ticks) {
		this.ticks = (hours * TICKS_PER_HOUR + minutes * TICKS_PER_MINUTE + seconds * TICKS_PER_SECOND + ticks);
	}

	public LQTime(int days, int hours, int minutes, int seconds, int ticks) {
		this.ticks = (days * TICKS_PER_DAY + hours * TICKS_PER_HOUR + minutes * TICKS_PER_MINUTE + seconds * TICKS_PER_SECOND + ticks);
	}

	public String asLongString() {
		String tmp = "";
		int time = this.ticks;
		if (time >= TICKS_PER_DAY) {
			tmp = tmp + time / TICKS_PER_DAY + " day ";
			time %= TICKS_PER_DAY;
		}
		if (time >= TICKS_PER_HOUR) {
			tmp = tmp + time / TICKS_PER_HOUR + " hour ";
			time %= TICKS_PER_HOUR;
		}
		if (time >= TICKS_PER_MINUTE) {
			tmp = tmp + time / TICKS_PER_MINUTE + " minute ";
			time %= TICKS_PER_MINUTE;
		}
		if (time >= TICKS_PER_SECOND) {
			tmp = tmp + time / TICKS_PER_SECOND + " second ";
			time %= TICKS_PER_SECOND;
		}
		if (time > 0) {
			tmp = tmp + time + " tick ";
		}
		if (tmp.equalsIgnoreCase(""))
			return "no";

		tmp = tmp.substring(0, tmp.lastIndexOf(" "));
		return tmp;
	}

	public String asString() {
		String tmp = "";
		int time = this.ticks;
		if (time >= TICKS_PER_DAY) {
			tmp = tmp + time / TICKS_PER_DAY + "d";
			time %= TICKS_PER_DAY;
		}
		if (time >= TICKS_PER_HOUR) {
			tmp = tmp + time / TICKS_PER_HOUR + "h";
			time %= TICKS_PER_HOUR;
		}
		if (time >= TICKS_PER_MINUTE) {
			tmp = tmp + time / TICKS_PER_MINUTE + "m";
			time %= TICKS_PER_MINUTE;
		}
		if (time >= TICKS_PER_SECOND) {
			tmp = tmp + time / TICKS_PER_SECOND + "s";
			time %= TICKS_PER_SECOND;
		}
		if (time > 0) {
			tmp = tmp + time + "t";
		}
		return tmp;
	}

	private int convert(String time) {
		int ticks = 0;
		if (time.contains("d")) {
			int i = time.indexOf("d");
			ticks += Integer.parseInt(time.substring(0, i)) * TICKS_PER_DAY;
			time = time.substring(i + 1);
		}
		if (time.contains("h")) {
			int i = time.indexOf("h");
			ticks += Integer.parseInt(time.substring(0, i)) * TICKS_PER_HOUR;
			time = time.substring(i + 1);
		}
		if (time.contains("m")) {
			int i = time.indexOf("m");
			ticks += Integer.parseInt(time.substring(0, i)) * TICKS_PER_MINUTE;
			time = time.substring(i + 1);
		}
		if (time.contains("s")) {
			int i = time.indexOf("s");
			ticks += Integer.parseInt(time.substring(0, i)) * TICKS_PER_SECOND;
			time = time.substring(i + 1);
		}
		if (time.contains("t")) {
			int i = time.indexOf("t");
			ticks += Integer.parseInt(time.substring(0, i));
			time = time.substring(i + 1);
		}
		return ticks;
	}

	public int toDays() {
		return this.ticks / TICKS_PER_DAY;
	}

	public int toHours() {
		return this.ticks / TICKS_PER_HOUR;
	}

	public int toMinutes() {
		return this.ticks / TICKS_PER_MINUTE;
	}

	public int toSeconds() {
		return this.ticks / TICKS_PER_SECOND;
	}

	public int toTicks() {
		return this.ticks;
	}
}