package me.sablednah.legendquest.cmds;

public enum Cmds {
    // constant (console allowed,min arg length)
    RACE(true, 0),
    CLASS(true, 0),
    SKILL(true, 0),
    LINK(false, 1),
    UNLINK(false, 0),
    RELOAD(true, 0),
    STATS(true, 0),
    KARMA(true, 0),
    ROLL(true, 0),
    HP(true, 0),
    PARTY(false, 0),
    HELP(true, 0), 
    ADMIN(true, 1);
    
    private Boolean canConsole;
    private int minArgLength;

    Cmds(final Boolean cc, final int mal) {
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
        final String s = super.toString();
        return s.toLowerCase();
    }
}
