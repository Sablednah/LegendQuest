package me.sablednah.legendquest.events;

import me.sablednah.legendquest.listeners.SkillListener;
import me.sablednah.legendquest.skills.Skill;

public class SkillEnableEvent extends SkillEvent {
    
    private final Skill skill;
    
    public SkillEnableEvent(Skill skill) {
        this.skill = skill;
    }
    
    public void dispatch(SkillListener eventListener) {
        eventListener.onSkillEnable(this);
    }
    
    public Skill getPower() {
        return this.skill;
    }
}