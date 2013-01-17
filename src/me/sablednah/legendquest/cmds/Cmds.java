package me.sablednah.legendquest.cmds;

public enum Cmds {
	// constant (console allowed,min arg length)
	RACE(true, 0),
	CLASS(true, 0),
	RELOAD(true, 0),
	STATS(true, 0),
	HELP(true, 0);

	private Boolean	canConsole;
	private int		minArgLength;

	Cmds(Boolean cc, int mal) {
		this.canConsole = cc;
		this.minArgLength = mal;
	}

	public Boolean canConsole() {
		return canConsole;
	}

	public int minArgLength() {
		return minArgLength;
	}

	@Override
	public String toString() {
		String s = super.toString();
		return s.toLowerCase();
	}
}
