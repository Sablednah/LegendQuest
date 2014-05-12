package me.sablednah.legendquest.skills;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SkillManifest {
	public abstract String name();

	public abstract double version();

	public abstract SkillType type();

	public abstract String author();

	public abstract String description();

	public abstract int buildup();

	public abstract int delay();

	public abstract int duration();

	public abstract int cooldown();

	public abstract int manaCost();

	public abstract String consumes();

	public abstract int levelRequired();

	public abstract int skillPoints();

	public abstract String[] strvarnames();

	public abstract String[] strvarvalues();

	public abstract String[] intvarnames();

	public abstract int[] intvarvalues();

	public abstract String[] dblvarnames();

	public abstract double[] dblvarvalues();
}
