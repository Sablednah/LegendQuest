package me.sablednah.legendquest.events;

import me.sablednah.legendquest.listeners.SkillListener;

public abstract class SkillEvent {
  protected abstract void dispatch(SkillListener paramPowerListener);
}