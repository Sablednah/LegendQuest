package me.sablednah.legendquest.playercharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.Skill;
import me.sablednah.legendquest.utils.SetExp;

public class PC {
	public Main						lq;

	public String					charname;
	public String					player;
	public Race						race;
	public ClassType				mainClass;
	public ClassType				subClass;
	public HashMap<String, Integer>	xpEarnt			= new HashMap<String, Integer>();
	public int						maxHP;
	public int						health;
	public int						mana;

	public boolean					raceChanged;

	public int						statStr;
	public int						statDex;
	public int						statInt;
	public int						statWis;
	public int						statCon;
	public int						statChr;

	public int						currentXP;

	public List<Skill>				skillsSelected;
	public HashMap<String, Integer>	skillsPurchased	= new HashMap<String, Integer>();

	public PC(Main plugin, String pName) {
		this.lq = plugin;

		this.player = pName;
		this.charname = pName;
		this.mainClass = this.lq.classes.defaultClass;
		this.race = this.lq.races.defaultRace;
		this.raceChanged = false;
		this.subClass = null;
		this.maxHP = 20;
		this.health = 20;
		this.mana = getMaxMana();
		this.currentXP = 0;
		if (!lq.configMain.randomStats) {
			statStr = statDex = statInt = statWis = statCon = statChr = 12;
		} else {
			int[] statline = { 16, 14, 13, 12, 11, 10 };
			Random r = new Random(pName.hashCode());
			for (int i = 0; i < statline.length; i++) {
				int position = i + r.nextInt(statline.length - i);
				int temp = statline[i];
				statline[i] = statline[position];
				statline[position] = temp;
			}
			statStr = statline[0];
			statDex = statline[1];
			statInt = statline[2];
			statWis = statline[3];
			statCon = statline[4];
			statChr = statline[5];
		}
		checkSkills();
		scheduleCheckInv();
		scheduleHealthCheck();

	}

	public void checkSkills() {
		List<Skill> potentialSkills = getUniqueSkills();
		List<Skill> activeSkills = new ArrayList<Skill>();
		int level = SetExp.getLevelOfXpAmount(currentXP);

		for (Skill s : potentialSkills) {
			if (s.levelRequired <= level && s.skillPoints < 1) {
				activeSkills.add(s);
				continue;
			}
			// skill points now :/
			if (skillsPurchased.containsValue(s.name)) {
				activeSkills.add(s);
				continue;
			}
		}
		skillsSelected = activeSkills;
	}

	public List<Skill> getUniqueSkills() {
		Set<Skill> set = new HashSet<Skill>();
		set.addAll(race.availableSkills);
		set.addAll(mainClass.availableSkills);
		if (subClass != null) {
			set.addAll(subClass.availableSkills);
		}
		List<Skill> uniques = new ArrayList<Skill>();
		uniques.addAll(set);
		return uniques;
	}

	public int getSkillPointsLeft() {
		return getMaxSkillPointsLeft() - getSkillPointsSpent();
	}

	public int getMaxSkillPointsLeft() {
		int sp, level;
		double result, perlevel;

		sp = race.skillPoints;
		sp += mainClass.skillPoints;
		level = SetExp.getExpAtLevel(currentXP);
		if (subClass != null) {
			perlevel = Math.max(mainClass.skillPointsPerLevel, subClass.skillPointsPerLevel);
		} else {
			perlevel = mainClass.healthPerLevel;
		}
		result = (sp + (level * (perlevel + race.skillPointsPerLevel)));

		return (int) result;
	}

	public int getSkillPointsSpent() {
		int result = 0;
		for (int cost : skillsPurchased.values()) {
			result += cost;
		}
		return result;
	}

	/**
	 * @return the statStr
	 */
	public int getStatStr() {
		int stat;
		stat = statStr;
		if (race != null) {
			stat += race.statStr;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statStr > -1 && subClass.statStr > -1) {
					// both positive (ok 0+)
					classboost = Math.max(mainClass.statStr, subClass.statStr);
				} else if (mainClass.statStr < 1 && subClass.statStr < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statStr, subClass.statStr);
				} else {
					classboost = mainClass.statStr + subClass.statStr;
				}
				stat += classboost;
			} else {
				stat += mainClass.statStr;
			}
		}
		return stat;
	}

	/**
	 * @return the statDex
	 */
	public int getStatDex() {
		int stat;
		stat = statDex;
		if (race != null) {
			stat += race.statDex;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statDex > -1 && subClass.statDex > -1) {
					// both positive (ok 0+)
					classboost = Math.max(mainClass.statDex, subClass.statDex);
				} else if (mainClass.statDex < 1 && subClass.statDex < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statDex, subClass.statDex);
				} else {
					classboost = mainClass.statDex + subClass.statDex;
				}
				stat += classboost;
			} else {
				stat += mainClass.statDex;
			}
		}
		return stat;
	}

	/**
	 * @return the statInt
	 */
	public int getStatInt() {
		int stat;
		stat = statInt;
		if (race != null) {
			stat += race.statInt;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statInt > -1 && subClass.statInt > -1) {
					// both positive (ok 0+)
					classboost = Math.max(mainClass.statInt, subClass.statInt);
				} else if (mainClass.statInt < 1 && subClass.statInt < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statInt, subClass.statInt);
				} else {
					classboost = mainClass.statInt + subClass.statInt;
				}
				stat += classboost;
			} else {
				stat += mainClass.statInt;
			}
		}
		return stat;
	}

	/**
	 * @return the statWis
	 */
	public int getStatWis() {
		int stat;
		stat = statStr;
		if (race != null) {
			stat += race.statWis;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statWis > -1 && subClass.statWis > -1) {
					// both positive (ok 0+)
					classboost = Math.max(mainClass.statWis, subClass.statWis);
				} else if (mainClass.statWis < 1 && subClass.statWis < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statWis, subClass.statWis);
				} else {
					classboost = mainClass.statWis + subClass.statWis;
				}
				stat += classboost;
			} else {
				stat += mainClass.statWis;
			}
		}
		return stat;
	}

	/**
	 * @return the statCon
	 */
	public int getStatCon() {
		int stat;
		stat = statStr;
		if (race != null) {
			stat += race.statCon;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statCon > -1 && subClass.statCon > -1) {
					// both positive (ok 0+)
					classboost = Math.max(mainClass.statCon, subClass.statCon);
				} else if (mainClass.statCon < 1 && subClass.statCon < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statCon, subClass.statCon);
				} else {
					classboost = mainClass.statCon + subClass.statCon;
				}
				stat += classboost;
			} else {
				stat += mainClass.statCon;
			}
		}
		return stat;
	}

	/**
	 * @return the statChr
	 */
	public int getStatChr() {
		int stat;
		stat = statStr;
		if (race != null) {
			stat += race.statChr;
		}
		if (mainClass != null) {
			if (subClass != null) {
				int classboost = 0;
				if (mainClass.statChr > -1 && subClass.statChr > -1) {
					// both positive (ok 0+)statChr
					classboost = Math.max(mainClass.statChr, subClass.statStr);
				} else if (mainClass.statChr < 1 && subClass.statChr < 1) {
					// both negative (ok 0+)
					classboost = Math.max(mainClass.statChr, subClass.statChr);
				} else {
					classboost = mainClass.statChr + subClass.statChr;
				}
				stat += classboost;
			} else {
				stat += mainClass.statChr;
			}
		}
		return stat;
	}

	public int getMaxHealth() {
		int hp, level;
		double result, perlevel;

		hp = race.baseHealth;
		level = SetExp.getLevelOfXpAmount(currentXP);
		if (subClass != null) {
			perlevel = Math.max(mainClass.healthPerLevel, subClass.healthPerLevel);
		} else {
			perlevel = mainClass.healthPerLevel;
		}
		result = (hp + (level * perlevel));
		this.maxHP = (int) result;

		return this.maxHP;
	}

	// Couldn't resist...
	public Double getMaxHeadroom() {
		return race.size;
	}

	public int getMaxMana() {
		double result = 0;
		int mana, level, bonus;
		double perlevel;

		mana = race.baseMana;

		level = SetExp.getLevelOfXpAmount(currentXP);
		if (subClass != null) {
			perlevel = Math.max(mainClass.healthPerLevel, subClass.healthPerLevel);
			bonus = Math.max(mainClass.manaBonus, subClass.manaBonus);
		} else {
			perlevel = mainClass.healthPerLevel;
			bonus = mainClass.manaBonus;
		}
		result = (mana + bonus + (level * perlevel));

		return (int) result;
	}

	public boolean manaGain(int gain) {
		int manaNow = this.mana;
		this.mana += gain;
		if (this.mana > getMaxMana()) {
			this.mana = getMaxMana();
		}
		return (manaNow != this.mana);
	}

	public void manaLoss(int loss) {
		this.mana -= loss;
		if (this.mana < 0) {
			this.mana = 0;
		}
	}

	public boolean manaGain() {
		int gain;
		gain = race.manaPerSecond;
		if (subClass != null) {
			gain += (Math.max(mainClass.manaPerSecond, subClass.manaPerSecond));
		} else {
			gain += mainClass.manaPerSecond;
		}
		return manaGain(gain);
	}

	public void healthCheck() {
		Player p = Bukkit.getServer().getPlayer(this.player);
		if (p != null) {
			getMaxHealth();

			this.health = p.getHealth();
			if (this.health > this.maxHP) {
				this.health = this.maxHP;
			}
			p.setMaxHealth(this.maxHP);
			p.setHealth(this.health);

			lq.debug.fine("SHC ¦ HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
			p.sendMessage("SHC ¦ HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
		}
	}

	public void scheduleHealthCheck() {
		Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedCheck(), 2L);
	}

	public class DelayedCheck implements Runnable {
		@Override
		public void run() {
			healthCheck();
		}
	}

	public void setXP(int newXP) {
		xpEarnt.put(mainClass.name.toLowerCase(), newXP);
		if (subClass != null) {
			xpEarnt.put(subClass.name.toLowerCase(), newXP);
		}
		
//		lq.debug.fine("newXP:"+newXP);
		
		currentXP = newXP;
		
//		lq.debug.fine("currentXP:"+currentXP);
		
		Player p = Bukkit.getServer().getPlayer(player);
		if (p != null) {
			SetExp.setTotalExperience(p, newXP);
		}
	}

	public boolean hasMastered(String className) {
		lq.logger.info("className (" + className + ")...");
		if (xpEarnt.containsKey(className.toLowerCase())) {
			lq.logger.info("className (" + className + "): " + xpEarnt.get(className.toLowerCase()));
			if (xpEarnt.get(className.toLowerCase()) >= Main.MAX_XP) {
				return true;
			}
		}
		return false;
	}

	public boolean allowedArmour(int id) {
		Boolean valid = false;
		if (id == 0) {
			valid = true;
			lq.debug.fine("Naked is valid armour");
		}
		if (mainClass.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid armour for class: " + mainClass.name);
		}
		if (race.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid armour for race: " + race.name);
		}
		if (subClass != null && subClass.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid armour for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid armour for class: " + mainClass.name);
		}
		if (race.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid armour for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid armour for sub-class: " + subClass.name);
		}
		return valid;
	}

	public boolean allowedWeapon(int id) {
		Boolean valid = false;

		if (id == 0) {
			valid = true;
			lq.debug.fine("fist is valid weapon");
		}
		if (mainClass.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid weapon for class: " + mainClass.name);
		}
		if (race.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid weapon for race: " + race.name);
		}
		if (subClass != null && subClass.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid weapon for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid weapon for class: " + mainClass.name);
		}
		if (race.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid weapon for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid weapon for sub-class: " + subClass.name);
		}
		return valid;
	}

	public boolean allowedTool(int id) {
		Boolean valid = false;

		if (id == 0) {
			valid = true;
			lq.debug.fine("fist is valid tool");
		}
		if (mainClass.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid tool for class: " + mainClass.name);
		}
		if (race.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid tool for race: " + race.name);
		}
		if (subClass != null && subClass.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id + " is valid tool for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid tool for class: " + mainClass.name);
		}
		if (race.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid tool for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id + " is INvalid tool for sub-class: " + subClass.name);
		}
		return valid;
	}

	@SuppressWarnings("deprecation")
	public void checkInv() {
		Player p = lq.getServer().getPlayer(player);
		if (p.isOnline()) {
			PlayerInventory i = p.getInventory();

			ItemStack helm = i.getHelmet();
			ItemStack chest = i.getChestplate();
			ItemStack legs = i.getLeggings();
			ItemStack boots = i.getBoots();

			if (helm != null && !(allowedArmour(helm.getTypeId()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed helmet " + (helm.getTypeId()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), helm);
				i.setHelmet(null);
			}
			if (chest != null && !(allowedArmour(chest.getTypeId()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed chestplate " + (chest.getTypeId()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), chest);
				i.setChestplate(null);
			}
			if (legs != null && !(allowedArmour(legs.getTypeId()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed leggings " + (legs.getTypeId()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), legs);
				i.setLeggings(null);
			}
			if (boots != null && !(allowedArmour(boots.getTypeId()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed boots " + (boots.getTypeId()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), boots);
				i.setBoots(null);
			}
			p.updateInventory();
		}
	}

	public void scheduleCheckInv() {
		Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedInvCheck(), 2L);
	}

	public class DelayedInvCheck implements Runnable {
		@Override
		public void run() {
			checkInv();
		}
	}

}
