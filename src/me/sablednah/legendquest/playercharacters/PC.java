package me.sablednah.legendquest.playercharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
import me.sablednah.legendquest.utils.SetExp;
import me.sablednah.legendquest.mechanics.Difficulty;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.mechanics.Attribute;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PC {

	public class DelayedCheck implements Runnable {

		public void run() {
			healthCheck();
		}
	}

	public class DelayedInvCheck implements Runnable {

		public void run() {
			checkInv();
		}
	}

	public UUID								uuid;
	public Main								lq;
	public String							charname;
	public String							player;

	public Race								race;
	public boolean							raceChanged;
	public ClassType						mainClass;
	public ClassType						subClass;

	public HashMap<String, Integer>			xpEarnt			= new HashMap<String, Integer>();
	public double							maxHP;
	public int								currentXP;

	public double							health;
	public int								mana;
	public long								karma;

	public int								statStr;
	public int								statDex;
	public int								statInt;
	public int								statWis;
	public int								statCon;
	public int								statChr;

	public HashMap<String, SkillDataStore>	skillSet		= null;
	public Map<String, Boolean>				skillsSelected;
	public HashMap<String, Integer>			skillsPurchased	= new HashMap<String, Integer>();

	// private boolean skillsEnabled = true;

	/**
	 * Create PC by name
	 * 
	 * @deprecated use {@link PC(Main, UUID)} instead.
	 */

	@Deprecated
	public PC(Main plugin, String pName) {
		this(plugin, plugin.getServer().getPlayer(pName).getUniqueId());
	}

	/**
	 * Create PC by UUID
	 **/
	public PC(Main plugin, UUID uuid) {
		String pName;
		pName = plugin.getServer().getOfflinePlayer(uuid).getName();
		this.lq = plugin;
		this.uuid = uuid;
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
		skillSet = getUniqueSkills(true);
		checkSkills();
		scheduleCheckInv();
		scheduleHealthCheck();

	}

	public boolean allowedArmour(Material id) {
		Boolean valid = false;
		if (id == null) {
			valid = true;
			lq.debug.fine("Naked is valid armour");
		}
		if (mainClass.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid armour for class: " + mainClass.name);
		}
		if (race.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid armour for race: " + race.name);
		}
		if (subClass != null && subClass.allowedArmour.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid armour for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid armour for class: " + mainClass.name);
		}
		if (race.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid armour for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedArmour.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid armour for sub-class: " + subClass.name);
		}
		return valid;
	}

	public boolean allowedTool(Material id) {
		Boolean valid = false;

		if (id == null) {
			valid = true;
			lq.debug.fine("Air/fist is valid tool");
		}
		if (mainClass.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid tool for class: " + mainClass.name);
		}
		if (race.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid tool for race: " + race.name);
		}
		if (subClass != null && subClass.allowedTools.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid tool for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid tool for class: " + mainClass.name);
		}
		if (race.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid tool for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedTools.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid tool for sub-class: " + subClass.name);
		}
		return valid;
	}

	public boolean allowedWeapon(Material id) {
		Boolean valid = false;

		if (id == null) {
			valid = true;
			lq.debug.fine("Air/Fist is valid weapon");
		}
		if (mainClass.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid weapon for class: " + mainClass.name);
		}
		if (race.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid weapon for race: " + race.name);
		}
		if (subClass != null && subClass.allowedWeapons.contains(id)) {
			valid = true;
			lq.debug.fine(id.toString() + " is valid weapon for sub-class: " + subClass.name);
		}
		if (mainClass.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid weapon for class: " + mainClass.name);
		}
		if (race.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid weapon for race: " + race.name);
		}
		if (subClass != null && subClass.dissallowedWeapons.contains(id)) {
			valid = false;
			lq.debug.fine(id.toString() + " is Invalid weapon for sub-class: " + subClass.name);
		}
		return valid;
	}

	@SuppressWarnings("deprecation")
	public void checkInv() {
		Player p = lq.getServer().getPlayer(uuid);
		if (p != null && p.isOnline()) {
			PlayerInventory i = p.getInventory();

			ItemStack helm = i.getHelmet();
			ItemStack chest = i.getChestplate();
			ItemStack legs = i.getLeggings();
			ItemStack boots = i.getBoots();

			if (helm != null && !(allowedArmour(helm.getType()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed helmet " + (helm.getType().toString()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), helm);
				i.setHelmet(null);
			}
			if (chest != null && !(allowedArmour(chest.getType()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed chestplate " + (chest.getType().toString()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), chest);
				i.setChestplate(null);
			}
			if (legs != null && !(allowedArmour(legs.getType()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed leggings " + (legs.getType().toString()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), legs);
				i.setLeggings(null);
			}
			if (boots != null && !(allowedArmour(boots.getType()))) {
				p.sendMessage(lq.configLang.cantEquipArmour);
				lq.debug.fine("Removed boots " + (boots.getType().toString()) + " from " + p.getName() + ".");
				p.getWorld().dropItemNaturally(p.getLocation(), boots);
				i.setBoots(null);
			}
			p.updateInventory();
		}
	}

	// Couldn't resist...
	public Double getMaxHeadroom() {
		return race.size;
	}

	public double getMaxHealth() {
		int hp, level, con;
		double result, perlevel;
		con = getAttributeModifier(Attribute.CON);
		hp = race.baseHealth + con;
		if (hp < 1) {
			hp = 1;
		}
		level = SetExp.getLevelOfXpAmount(currentXP);
		if (subClass != null) {
			perlevel = Math.max(mainClass.healthPerLevel, subClass.healthPerLevel);
		} else {
			perlevel = mainClass.healthPerLevel;
		}
		double conBonus = ((con * 10) + 100) / 100.00D; // percent per level bonus of +/-50%
		perlevel *= conBonus;
		result = (hp + (level * perlevel));
		this.maxHP = result;

		return this.maxHP;
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

	public int getSkillPointsLeft() {
		return getMaxSkillPointsLeft() - getSkillPointsSpent();
	}

	public int getSkillPointsSpent() {
		int result = 0;
		for (int cost : skillsPurchased.values()) {
			result += cost;
		}
		return result;
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

	public void checkSkills() {
		HashMap<String, SkillDataStore> potentialSkills = getUniqueSkills();
		Map<String, Boolean> activeSkills = new HashMap<String, Boolean>();
		int level = SetExp.getLevelOfXpAmount(currentXP);
System.out.print("Checking skills for level: "+level);
		for (SkillDataStore s : potentialSkills.values()) {
System.out.print("Checking skill: "+ s.name + " - "+ s.levelRequired);

			if (s.levelRequired <= level && s.skillPoints < 1) {
				// activeSkills.put(lq.skills.instantiateSkill(s),true);
				activeSkills.put(s.name, true);
System.out.print("adding skill: "+ s.name);
				continue;
			}
			// skill points now :/
			if (skillsPurchased.containsValue(s.name)) {
				activeSkills.put(s.name, true);
				continue;
			}
		}
		skillsSelected = activeSkills;
	}


	public HashMap<String, SkillDataStore> getUniqueSkills() {
		return getUniqueSkills(false);
	}

	public HashMap<String, SkillDataStore> getUniqueSkills(boolean rescan) {
		// use cached set if present unless told otherwise
		if (!rescan && this.skillSet != null && !skillSet.isEmpty()) {
System.out.print("using cached skills");
			return skillSet;
		}

		Set<SkillDataStore> set = new HashSet<SkillDataStore>();
		set.addAll(race.availableSkills);
		set.addAll(race.outsourcedSkills);
		set.addAll(mainClass.availableSkills);
		set.addAll(mainClass.outsourcedSkills);
		if (subClass != null) {
			set.addAll(subClass.availableSkills);
			set.addAll(subClass.outsourcedSkills);
		}
		List<SkillDataStore> uniques = new ArrayList<SkillDataStore>();
		uniques.addAll(set);
		return makeMap(uniques);
	}

	public HashMap<String, SkillDataStore> makeMap(List<SkillDataStore> in) {
		HashMap<String, SkillDataStore> out = new HashMap<String, SkillDataStore>();
		for (SkillDataStore item : in) {
			out.put(item.name, item);
		}
		return out;
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

	public void healthCheck() {
		Player p = Bukkit.getServer().getPlayer(uuid);
		if (p != null) {
			getMaxHealth();

			this.health = p.getHealth();
			if (this.health > this.maxHP) {
				this.health = this.maxHP;
			}
			p.setMaxHealth(this.maxHP);
			p.setHealth(this.health);
			double scale = this.maxHP;
			if (scale > 40.0D) {
				scale = 40.0D;
			}
			p.setHealthScale(scale);
			p.setHealthScaled(true);
			if (lq.configMain.debugMode) {
				lq.debug.fine("SHC � HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
				if (p.getName().equalsIgnoreCase("sablednah")) {
					p.sendMessage("SHC � HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
				}
			}
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

	public void scheduleCheckInv() {
		Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedInvCheck(), 2L);
	}

	public void scheduleHealthCheck() {
		Bukkit.getServer().getScheduler().runTaskLater(lq, new DelayedCheck(), 2L);
	}

	public void setXP(int newXP) {
		xpEarnt.put(mainClass.name.toLowerCase(), newXP);
		if (subClass != null) {
			xpEarnt.put(subClass.name.toLowerCase(), newXP);
		}

		// lq.debug.fine("newXP:"+newXP);

		currentXP = newXP;

		// lq.debug.fine("currentXP:"+currentXP);

		Player p = Bukkit.getServer().getPlayer(uuid);
		if (p != null) {
			SetExp.setTotalExperience(p, newXP);
		}
	}

	public boolean canCraft() {
		if (race.stopCrafting || mainClass.stopCrafting) {
			return false;
		}
		if (subClass != null && subClass.stopCrafting) {
			return false;
		}
		return true;
	}

	public boolean canSmelt() {
		if (race.stopSmelting || mainClass.stopSmelting) {
			return false;
		}
		if (subClass != null && subClass.stopSmelting) {
			return false;
		}
		return true;
	}

	public boolean canBrew() {
		if (race.stopBrewing || mainClass.stopBrewing) {
			return false;
		}
		if (subClass != null && subClass.stopBrewing) {
			return false;
		}
		return true;
	}

	public boolean canEnchant() {
		if (race.stopEnchating || mainClass.stopEnchating) {
			return false;
		}
		if (subClass != null && subClass.stopEnchating) {
			return false;
		}
		return true;
	}

	public boolean canRepair() {
		if (race.stopRepairing || mainClass.stopRepairing) {
			return false;
		}
		if (subClass != null && subClass.stopRepairing) {
			return false;
		}
		return true;
	}

	public boolean validSkill(String name) {
		checkSkills();
		Boolean hasSkill = skillsSelected.get(name);
		if (skillsSelected != null && name != null && hasSkill != null) {
			return hasSkill;
		}
		return false;
	}

	public void useSkill(String name) {
		if (validSkill(name)) {
			if (getSkillPhase(name)==SkillPhase.READY) {
				SkillDataStore skill = skillSet.get(name);
				
				skill.lastUse=System.currentTimeMillis();
				Player p = lq.getServer().getPlayer(uuid);
				if (p != null && p.isOnline()) {
					skill.lastUseLoc=p.getLocation().clone();
				}
				skillSet.put(name,skill);
			}
		}
	}

	public SkillPhase getSkillPhase(String name) {
		if (skillSet.containsKey(name)) {
			if (validSkill(name)) {
				return skillSet.get(name).checkPhase();
			}
		}
		return null;
	}

	public int getStat(Attribute attr) {
		switch (attr) {
			case STR:
				return getStatStr();
			case DEX:
				return getStatDex();
			case CON:
				return getStatCon();
			case WIS:
				return getStatWis();
			case INT:
				return getStatInt();
			case CHR:
				return getStatChr();
			default:
				throw new IllegalStateException();
		}
	}

	public int getAttributeModifier(Attribute attr) {
		return Mechanics.getPlayersAttributeModifier(this, attr);
	}

	public int skillTest(int dif, Attribute attr) {
		return Mechanics.skillTest(dif, attr, this);
	}

	public int skillTest(Difficulty dif, Attribute attr, PC pc) {
		return Mechanics.skillTest(dif, attr, this);
	}

	public boolean skillTestB(int dif, Attribute attr, PC pc) {
		return Mechanics.skillTestB(dif, attr, this);
	}

	public boolean skillTestB(Difficulty dif, Attribute attr, PC pc) {
		return Mechanics.skillTestB(dif, attr, this);
	}

	public double logOfBase(int base, long num) {
		return Math.log(num) / Math.log(base);
	}

	public String karmaName() {
		return karmaName(this.karma);
	}

	public String karmaName(long number) {
		int x = (int) logOfBase(lq.configMain.karmaScale, Math.abs(number));
		String karma = null;
		// x is index
		if (number > 0) {
			if (x > lq.configLang.karmaPositiveItems.size() - 1) {
				x = lq.configLang.karmaPositiveItems.size() - 1;
			}
			karma = lq.configLang.karmaPositiveItems.get(x);
		} else {
			if (x > lq.configLang.karmaNegativeItems.size() - 1) {
				x = lq.configLang.karmaNegativeItems.size() - 1;
			}
			karma = lq.configLang.karmaNegativeItems.get(x);
		}

		return karma;
	}
}
