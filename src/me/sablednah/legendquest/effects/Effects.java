package me.sablednah.legendquest.effects;

import org.bukkit.Effect;
import org.bukkit.potion.PotionEffectType;

public enum Effects {
	SPEED(EffectType.POTION, PotionEffectType.SPEED, null),
	SLOW(EffectType.POTION, PotionEffectType.SLOW, null),
	FAST_DIGGING(EffectType.POTION, PotionEffectType.FAST_DIGGING, null),
	SLOW_DIGGING(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null),
	INCREASE_DAMAGE(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null),
	HEAL(EffectType.POTION, PotionEffectType.HEAL, null),
	HARM(EffectType.POTION, PotionEffectType.HARM, null),
	JUMP(EffectType.POTION, PotionEffectType.JUMP, null),
	CONFUSION(EffectType.POTION, PotionEffectType.CONFUSION, null),
	REGENERATION(EffectType.POTION, PotionEffectType.REGENERATION, null),
	DAMAGE_RESISTANCE(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null),
	FIRE_RESISTANCE(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null),
	WATER_BREATHING(EffectType.POTION, PotionEffectType.WATER_BREATHING, null),
	INVISIBILITY(EffectType.POTION, PotionEffectType.INVISIBILITY, null),
	BLINDNESS(EffectType.POTION, PotionEffectType.BLINDNESS, null),
	NIGHT_VISION(EffectType.POTION, PotionEffectType.NIGHT_VISION, null),
	HUNGER(EffectType.POTION, PotionEffectType.HUNGER, null),
	WEAKNESS(EffectType.POTION, PotionEffectType.WEAKNESS, null),
	POISON(EffectType.POTION, PotionEffectType.POISON, null),
	WITHER(EffectType.POTION, PotionEffectType.WITHER, null),
	HEALTH_BOOST(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null),
	ABSORPTION(EffectType.POTION, PotionEffectType.ABSORPTION, null),
	SATURATION(EffectType.POTION, PotionEffectType.SATURATION, null),
	SMOKE(EffectType.BUKKIT, null, Effect.SMOKE),
	ENDER_SIGNAL(EffectType.BUKKIT, null, Effect.ENDER_SIGNAL),
	MOBSPAWNER_FLAMES(EffectType.BUKKIT, null, Effect.MOBSPAWNER_FLAMES),
	BLEED(EffectType.CUSTOM, null, Effect.POTION_BREAK),
	SLOWBLEED(EffectType.CUSTOM, null, Effect.POTION_BREAK),
	STUNNED(EffectType.CUSTOM, PotionEffectType.SLOW, Effect.POTION_BREAK);

	private EffectType			effectType;
	private PotionEffectType	potioneffectType;
	private Effect				effect;

	Effects(EffectType effectType, PotionEffectType potioneffectType, Effect effect) {
		this.setEffectType(effectType);
		this.setPotioneffectType(potioneffectType);
		this.setEffect(effect);
	}

	@Override
	public String toString() {
		final String s = super.toString();
		return s.toLowerCase();
	}

	/**
	 * @return the effectType
	 */
	public EffectType getEffectType() {
		return effectType;
	}

	/**
	 * @param effectType
	 *            the effectType to set
	 */
	public void setEffectType(EffectType effectType) {
		this.effectType = effectType;
	}

	/**
	 * @return the potioneffectType
	 */
	public PotionEffectType getPotioneffectType() {
		return potioneffectType;
	}

	/**
	 * @param potioneffectType
	 *            the potioneffectType to set
	 */
	public void setPotioneffectType(PotionEffectType potioneffectType) {
		this.potioneffectType = potioneffectType;
	}

	/**
	 * @return the effect
	 */
	public Effect getEffect() {
		return effect;
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	public void setEffect(Effect effect) {
		this.effect = effect;
	}
}
