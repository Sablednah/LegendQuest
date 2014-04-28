package me.sablednah.legendquest.listeners;

import java.util.EventListener;

import me.sablednah.legendquest.events.SkillCommandEvent;
import me.sablednah.legendquest.events.SkillEnableEvent;
import me.sablednah.legendquest.events.SkillDisableEvent;

public abstract interface SkillListener extends EventListener {
  public abstract void onSkillCommand(SkillCommandEvent paramSkillCommandEvent);
  public abstract void onSkillEnable(SkillEnableEvent paramSkillEnableEvent);
  public abstract void onSkillDisable(SkillDisableEvent paramSkillDisableEvent);
}