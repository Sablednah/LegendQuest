package me.sablednah.legendquest.events;

import me.sablednah.legendquest.listeners.SkillListener;
import me.sablednah.legendquest.skills.Skill;

public class SkillDisableEvent extends SkillEvent {
    
    private final Skill skill;
    
    public SkillDisableEvent(Skill skill) {
        this.skill = skill;
    }
    
    public void dispatch(SkillListener eventListener) {
        eventListener.onSkillDisable(this);
    }
    
    public Skill getSkill() {
        return this.skill;
    }
}