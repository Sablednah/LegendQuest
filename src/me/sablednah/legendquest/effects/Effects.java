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
	
	// Night Vision, Blindness, Fire Resistance, Water Breathing, Invisibility, nausia.
	NIGHT_VISION_STEALTH(EffectType.POTION, PotionEffectType.NIGHT_VISION, null,128),
	BLINDNESS_STEALTH(EffectType.POTION, PotionEffectType.BLINDNESS, null,128),
	FIRE_RESISTANCE_STEALTH(EffectType.POTION, PotionEffectType.FIRE_RESISTANCE, null,128),
	WATER_BREATHING_STEALTH(EffectType.POTION, PotionEffectType.WATER_BREATHING, null,128),
	INVISIBILITY_STEALTH(EffectType.POTION, PotionEffectType.INVISIBILITY, null,128),
	CONFUSION_STEALTH(EffectType.POTION, PotionEffectType.CONFUSION, null,128),
	
	
	SPEED_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,130),
	SPEED_I_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,128),
	SPEED_II_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,129),
	SPEED_III_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,130),
	SPEED_IV_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,131),
	SPEED_V_STEALTH(EffectType.POTION, PotionEffectType.SPEED, null,132),
	SLOW_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,130),
	SLOW_I_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,128),
	SLOW_II_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,129),
	SLOW_III_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,130),
	SLOW_IV_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,131),
	SLOW_V_STEALTH(EffectType.POTION, PotionEffectType.SLOW, null,132),
	FAST_DIGGING_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,130),
	FAST_DIGGING_I_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,128),
	FAST_DIGGING_II_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,129),
	FAST_DIGGING_III_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,130),
	FAST_DIGGING_IV_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,131),
	FAST_DIGGING_V_STEALTH(EffectType.POTION, PotionEffectType.FAST_DIGGING, null,132),
	SLOW_DIGGING_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,130),
	SLOW_DIGGING_I_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,128),
	SLOW_DIGGING_II_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,129),
	SLOW_DIGGING_III_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,130),
	SLOW_DIGGING_IV_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,131),
	SLOW_DIGGING_V_STEALTH(EffectType.POTION, PotionEffectType.SLOW_DIGGING, null,132),
	INCREASE_DAMAGE_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,130),
	INCREASE_DAMAGE_I_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,128),
	INCREASE_DAMAGE_II_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,129),
	INCREASE_DAMAGE_III_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,130),
	INCREASE_DAMAGE_IV_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,131),
	INCREASE_DAMAGE_V_STEALTH(EffectType.POTION, PotionEffectType.INCREASE_DAMAGE, null,132),
	HEAL_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,130),
	HEAL_I_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,128),
	HEAL_II_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,129),
	HEAL_III_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,130),
	HEAL_IV_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,131),
	HEAL_V_STEALTH(EffectType.POTION, PotionEffectType.HEAL, null,132),
	HARM_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,130),
	HARM_I_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,128),
	HARM_II_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,129),
	HARM_III_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,130),
	HARM_IV_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,131),
	HARM_V_STEALTH(EffectType.POTION, PotionEffectType.HARM, null,132),
	JUMP_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,130),
	JUMP_I_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,128),
	JUMP_II_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,129),
	JUMP_III_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,130),
	JUMP_IV_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,131),
	JUMP_V_STEALTH(EffectType.POTION, PotionEffectType.JUMP, null,132),
	REGENERATION_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,130),
	REGENERATION_I_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,128),
	REGENERATION_II_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,129),
	REGENERATION_III_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,130),
	REGENERATION_IV_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,131),
	REGENERATION_V_STEALTH(EffectType.POTION, PotionEffectType.REGENERATION, null,132),
	DAMAGE_RESISTANCE_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,130),
	DAMAGE_RESISTANCE_I_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,128),
	DAMAGE_RESISTANCE_II_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,129),
	DAMAGE_RESISTANCE_III_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,130),
	DAMAGE_RESISTANCE_IV_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,131),
	DAMAGE_RESISTANCE_V_STEALTH(EffectType.POTION, PotionEffectType.DAMAGE_RESISTANCE, null,132),
	HUNGER_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,130),
	HUNGER_I_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,128),
	HUNGER_II_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,129),
	HUNGER_III_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,130),
	HUNGER_IV_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,131),
	HUNGER_V_STEALTH(EffectType.POTION, PotionEffectType.HUNGER, null,132),
	WEAKNESS_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,130),
	WEAKNESS_I_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,128),
	WEAKNESS_II_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,129),
	WEAKNESS_III_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,130),
	WEAKNESS_IV_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,131),
	WEAKNESS_V_STEALTH(EffectType.POTION, PotionEffectType.WEAKNESS, null,132),
	POISON_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,130),
	POISON_I_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,128),
	POISON_II_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,129),
	POISON_III_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,130),
	POISON_IV_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,131),
	POISON_V_STEALTH(EffectType.POTION, PotionEffectType.POISON, null,132),
	WITHER_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,130),
	WITHER_I_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,128),
	WITHER_II_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,129),
	WITHER_III_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,130),
	WITHER_IV_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,131),
	WITHER_V_STEALTH(EffectType.POTION, PotionEffectType.WITHER, null,132),
	HEALTH_BOOST_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,130),
	HEALTH_BOOST_I_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,128),
	HEALTH_BOOST_II_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,129),
	HEALTH_BOOST_III_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,130),
	HEALTH_BOOST_IV_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,131),
	HEALTH_BOOST_V_STEALTH(EffectType.POTION, PotionEffectType.HEALTH_BOOST, null,132),
	ABSORPTION_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,130),
	ABSORPTION_I_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,128),
	ABSORPTION_II_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,129),
	ABSORPTION_III_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,130),
	ABSORPTION_IV_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,131),
	ABSORPTION_V_STEALTH(EffectType.POTION, PotionEffectType.ABSORPTION, null,132),
	SATURATION_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,130),
	SATURATION_I_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,128),
	SATURATION_II_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,129),
	SATURATION_III_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,130),
	SATURATION_IV_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,131),
	SATURATION_V_STEALTH(EffectType.POTION, PotionEffectType.SATURATION, null,132),

	
	
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
