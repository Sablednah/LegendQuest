package me.sablednah.legendquest.events;

import java.util.EventListener;


public abstract interface SkillListener extends EventListener {
  public abstract void onSkillCommand(SkillCommandEvent commandEvent);
  public abstract void onSkillEnable(SkillEnableEvent skillEnableEvent);
  public abstract void onSkillDisable(SkillDisableEvent skillDisableEvent);
}