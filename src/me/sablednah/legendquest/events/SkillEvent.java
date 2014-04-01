package me.sablednah.legendquest.events;

public abstract class SkillEvent {
    protected abstract void dispatch(SkillListener skillListener);
}