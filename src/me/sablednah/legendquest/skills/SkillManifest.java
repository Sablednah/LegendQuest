package me.sablednah.legendquest.skills;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SkillManifest
{
    
    public abstract String author();
    
    public abstract String concept();
    
    public abstract String description();
    
    public abstract String name();
    
    public abstract String[] requiredPowers();
    
    public abstract SkillType type();
    
    public abstract double version();
}