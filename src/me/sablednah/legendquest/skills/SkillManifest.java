package me.sablednah.legendquest.skills;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SkillManifest {
	public abstract String name();

	public abstract double version();

	public abstract SkillType type();

	public abstract String author() default "";

	public abstract String description() default "";

	public abstract int buildup() default 0;

	public abstract int delay() default 0;

	public abstract int duration() default 0;

	public abstract int cooldown() default 0;

	public abstract int manaCost() default 0;

	public abstract int pay() default 0;

	public abstract int xp() default 0;

	public abstract int karmaReward() default 0;

	public abstract int karmaRequired() default 0;

	public abstract int karmaCost() default 0;

	public abstract String consumes() default "";

	public abstract int levelRequired() default 0;

	public abstract int skillPoints() default 0;

	public abstract String[] strvarnames() default {};

	public abstract String[] strvarvalues() default {};

	public abstract String[] intvarnames() default {};

	public abstract int[] intvarvalues() default {};

	public abstract String[] dblvarnames() default {};

	public abstract double[] dblvarvalues() default {};
}
