package me.sablednah.legendquest.effects;

import org.bukkit.Effect;
import org.bukkit.potion.PotionEffectType;

public enum Effects {
	SPEED(EffectType.POTION, PotionEffectType.SPEED, null,2),
	SPEED_I(EffectType.POTION, PotionEffectType.SPEED, null,0),
	SPEED_II(EffectType.POTION, PotionEffectType.SPEED, null,1),
	SPEED_III(EffectType.POTION, PotionEffectType.SPEED, null,2),
	SPEED_IV(EffectType.POTION, PotionEffectType.SPEED, null,3),
	SPEED_V(EffectType.POTION, PotionEffectType.SPEED, null,4),
	SLOW(EffectType.POTION, PotionEffectType.SLOW, null,2),
	SLOW_I(EffectType.POTION, PotionEffectType.SLOW, null,0),
	SLOW_II(EffectType.POTION, PotionEffectType.SLOW, null,1),
	SLOW_III(EffectType.POTION, PotionEffectType.SLOW, null,2),
	SLOW_IV(EffectType.POTION, PotionEffectType.SLOW, null,3),
	SLOW_V(EffectType.POTION, PotionEffectType.SLOW, null,4),
	FAST_DIGGING(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,2),
	FAST_DIGGING_I(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,0),
	FAST_DIGGING_II(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,1),
	FAST_DIGGING_III(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,2),
	FAST_DIGGING_IV(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,3),
	FAST_DIGGING_V(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,4),
	SLOW_DIGGING(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,2),
	SLOW_DIGGING_I(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,0),
	SLOW_DIGGING_II(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,1),
	SLOW_DIGGING_III(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,2),
	SLOW_DIGGING_IV(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,3),
	SLOW_DIGGING_V(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,4),
	INCREASE_DAMAGE(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,2),
	INCREASE_DAMAGE_I(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,0),
	INCREASE_DAMAGE_II(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,1),
	INCREASE_DAMAGE_III(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,2),
	INCREASE_DAMAGE_IV(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,3),
	INCREASE_DAMAGE_V(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,4),
	HEAL(EffectType.POTION, PotionEffectType.HEAL, null,2),
	HEAL_I(EffectType.POTION, PotionEffectType.HEAL, null,0),
	HEAL_II(EffectType.POTION, PotionEffectType.HEAL, null,1),
	HEAL_III(EffectType.POTION, PotionEffectType.HEAL, null,2),
	HEAL_IV(EffectType.POTION, PotionEffectType.HEAL, null,3),
	HEAL_V(EffectType.POTION, PotionEffectType.HEAL, null,4),
	HARM(EffectType.POTION, PotionEffectType.HARM, null,2),
	HARM_I(EffectType.POTION, PotionEffectType.HARM, null,0),
	HARM_II(EffectType.POTION, PotionEffectType.HARM, null,1),
	HARM_III(EffectType.POTION, PotionEffectType.HARM, null,2),
	HARM_IV(EffectType.POTION, PotionEffectType.HARM, null,3),
	HARM_V(EffectType.POTION, PotionEffectType.HARM, null,4),
	JUMP(EffectType.POTION, PotionEffectType.JUMP, null,2),
	JUMP_I(EffectType.POTION, PotionEffectType.JUMP, null,0),
	JUMP_II(EffectType.POTION, PotionEffectType.JUMP, null,1),
	JUMP_III(EffectType.POTION, PotionEffectType.JUMP, null,2),
	JUMP_IV(EffectType.POTION, PotionEffectType.JUMP, null,3),
	JUMP_V(EffectType.POTION, PotionEffectType.JUMP, null,4),
	CONFUSION(EffectType.POTION, PotionEffectType.CONFUSION, null,2),
	CONFUSION_I(EffectType.POTION, PotionEffectType.CONFUSION, null,0),
	CONFUSION_II(EffectType.POTION, PotionEffectType.CONFUSION, null,1),
	CONFUSION_III(EffectType.POTION, PotionEffectType.CONFUSION, null,2),
	CONFUSION_IV(EffectType.POTION, PotionEffectType.CONFUSION, null,3),
	CONFUSION_V(EffectType.POTION, PotionEffectType.CONFUSION, null,4),
	REGENERATION(EffectType.POTION, PotionEffectType.REGENERATION, null,2),
	REGENERATION_I(EffectType.POTION, PotionEffectType.REGENERATION, null,0),
	REGENERATION_II(EffectType.POTION, PotionEffectType.REGENERATION, null,1),
	REGENERATION_III(EffectType.POTION, PotionEffectType.REGENERATION, null,2),
	REGENERATION_IV(EffectType.POTION, PotionEffectType.REGENERATION, null,3),
	REGENERATION_V(EffectType.POTION, PotionEffectType.REGENERATION, null,4),
	DAMAGE_RESISTANCE(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,2),
	DAMAGE_RESISTANCE_I(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,0),
	DAMAGE_RESISTANCE_II(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,1),
	DAMAGE_RESISTANCE_III(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,2),
	DAMAGE_RESISTANCE_IV(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,3),
	DAMAGE_RESISTANCE_V(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,4),
	FIRE_RESISTANCE(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,2),
	FIRE_RESISTANCE_I(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,0),
	FIRE_RESISTANCE_II(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,1),
	FIRE_RESISTANCE_III(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,2),
	FIRE_RESISTANCE_IV(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,3),
	FIRE_RESISTANCE_V(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,4),
	WATER_BREATHING(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,2),
	WATER_BREATHING_I(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,0),
	WATER_BREATHING_II(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,1),
	WATER_BREATHING_III(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,2),
	WATER_BREATHING_IV(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,3),
	WATER_BREATHING_V(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,4),
	INVISIBILITY(EffectType.POTION, PotionEffectType.INVISIBILITY, null,2),
	INVISIBILITY_I(EffectType.POTION, PotionEffectType.INVISIBILITY, null,0),
	INVISIBILITY_II(EffectType.POTION, PotionEffectType.INVISIBILITY, null,1),
	INVISIBILITY_III(EffectType.POTION, PotionEffectType.INVISIBILITY, null,2),
	INVISIBILITY_IV(EffectType.POTION, PotionEffectType.INVISIBILITY, null,3),
	INVISIBILITY_V(EffectType.POTION, PotionEffectType.INVISIBILITY, null,4),
	BLINDNESS(EffectType.POTION, PotionEffectType.BLINDNESS, null,2),
	BLINDNESS_I(EffectType.POTION, PotionEffectType.BLINDNESS, null,0),
	BLINDNESS_II(EffectType.POTION, PotionEffectType.BLINDNESS, null,1),
	BLINDNESS_III(EffectType.POTION, PotionEffectType.BLINDNESS, null,2),
	BLINDNESS_IV(EffectType.POTION, PotionEffectType.BLINDNESS, null,3),
	BLINDNESS_V(EffectType.POTION, PotionEffectType.BLINDNESS, null,4),
	NIGHT_VISION(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,2),
	NIGHT_VISION_I(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,0),
	NIGHT_VISION_II(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,1),
	NIGHT_VISION_III(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,2),
	NIGHT_VISION_IV(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,3),
	NIGHT_VISION_V(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,4),
	HUNGER(EffectType.POTION, PotionEffectType.HUNGER, null,2),
	HUNGER_I(EffectType.POTION, PotionEffectType.HUNGER, null,0),
	HUNGER_II(EffectType.POTION, PotionEffectType.HUNGER, null,1),
	HUNGER_III(EffectType.POTION, PotionEffectType.HUNGER, null,2),
	HUNGER_IV(EffectType.POTION, PotionEffectType.HUNGER, null,3),
	HUNGER_V(EffectType.POTION, PotionEffectType.HUNGER, null,4),
	WEAKNESS(EffectType.POTION, PotionEffectType.WEAKNESS, null,2),
	WEAKNESS_I(EffectType.POTION, PotionEffectType.WEAKNESS, null,0),
	WEAKNESS_II(EffectType.POTION, PotionEffectType.WEAKNESS, null,1),
	WEAKNESS_III(EffectType.POTION, PotionEffectType.WEAKNESS, null,2),
	WEAKNESS_IV(EffectType.POTION, PotionEffectType.WEAKNESS, null,3),
	WEAKNESS_V(EffectType.POTION, PotionEffectType.WEAKNESS, null,4),
	POISON(EffectType.POTION, PotionEffectType.POISON, null,2),
	POISON_I(EffectType.POTION, PotionEffectType.POISON, null,0),
	POISON_II(EffectType.POTION, PotionEffectType.POISON, null,1),
	POISON_III(EffectType.POTION, PotionEffectType.POISON, null,2),
	POISON_IV(EffectType.POTION, PotionEffectType.POISON, null,3),
	POISON_V(EffectType.POTION, PotionEffectType.POISON, null,4),
	WITHER(EffectType.POTION, PotionEffectType.WITHER, null,2),
	WITHER_I(EffectType.POTION, PotionEffectType.WITHER, null,0),
	WITHER_II(EffectType.POTION, PotionEffectType.WITHER, null,1),
	WITHER_III(EffectType.POTION, PotionEffectType.WITHER, null,2),
	WITHER_IV(EffectType.POTION, PotionEffectType.WITHER, null,3),
	WITHER_V(EffectType.POTION, PotionEffectType.WITHER, null,4),
	HEALTH_BOOST(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,2),
	HEALTH_BOOST_I(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,0),
	HEALTH_BOOST_II(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,1),
	HEALTH_BOOST_III(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,2),
	HEALTH_BOOST_IV(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,3),
	HEALTH_BOOST_V(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,4),
	ABSORPTION(EffectType.POTION, PotionEffectType.ABSORPTION, null,2),
	ABSORPTION_I(EffectType.POTION, PotionEffectType.ABSORPTION, null,0),
	ABSORPTION_II(EffectType.POTION, PotionEffectType.ABSORPTION, null,1),
	ABSORPTION_III(EffectType.POTION, PotionEffectType.ABSORPTION, null,2),
	ABSORPTION_IV(EffectType.POTION, PotionEffectType.ABSORPTION, null,3),
	ABSORPTION_V(EffectType.POTION, PotionEffectType.ABSORPTION, null,4),
	SATURATION(EffectType.POTION, PotionEffectType.SATURATION, null,2),
	SATURATION_I(EffectType.POTION, PotionEffectType.SATURATION, null,0),
	SATURATION_II(EffectType.POTION, PotionEffectType.SATURATION, null,1),
	SATURATION_III(EffectType.POTION, PotionEffectType.SATURATION, null,2),
	SATURATION_IV(EffectType.POTION, PotionEffectType.SATURATION, null,3),
	SATURATION_V(EffectType.POTION, PotionEffectType.SATURATION, null,4),
	SMOKE(EffectType.BUKKIT, null, Effect.SMOKE,0),
	ENDER_SIGNAL(EffectType.BUKKIT, null, Effect.ENDER_SIGNAL,0),
	MOBSPAWNER_FLAMES(EffectType.BUKKIT, null, Effect.MOBSPAWNER_FLAMES,0),
	BLEED(EffectType.CUSTOM, null, Effect.POTION_BREAK,0),
	SLOWBLEED(EffectType.CUSTOM, null, Effect.POTION_BREAK,0),
	STUNNED(EffectType.CUSTOM, PotionEffectType.SLOW, Effect.POTION_BREAK,0),
	SNEAK(EffectType.LABEL, null, null,0);

	private EffectType			effectType;
	private PotionEffectType	potioneffectType;
	private Effect				effect;
	private int					amp;

	Effects(EffectType effectType, PotionEffectType potioneffectType, Effect effect, int amp) {
		this.setEffectType(effectType);
		this.setPotioneffectType(potioneffectType);
		this.setEffect(effect);
		this.setAmp(amp);
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

	/**
	 * @return the effect
	 */
	public int getAmp() {
		return amp;
	}

	/**
	 * @param effect
	 *            the effect to set
	 */
	public void setAmp(int amp) {
		this.amp = amp;
	}
}
