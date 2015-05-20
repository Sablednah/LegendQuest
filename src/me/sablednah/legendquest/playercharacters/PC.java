package me.sablednah.legendquest.playercharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
// import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.classes.ClassType;
import me.sablednah.legendquest.db.HealthStore;
import me.sablednah.legendquest.events.CoreSkillCheckEvent;
import me.sablednah.legendquest.events.HealthCheck;
import me.sablednah.legendquest.events.ManaCheck;
import me.sablednah.legendquest.events.SpeedCheckEvent;
import me.sablednah.legendquest.experience.ExperienceSource;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDataStore;
import me.sablednah.legendquest.skills.SkillPhase;
import me.sablednah.legendquest.skills.SkillType;
import me.sablednah.legendquest.utils.plugins.PluginUtils;
import me.sablednah.legendquest.loadout.Loadout;
import me.sablednah.legendquest.mechanics.Difficulty;
import me.sablednah.legendquest.mechanics.Mechanics;
import me.sablednah.legendquest.mechanics.Attribute;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

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

	public class DelayedXPSave implements Runnable {
		public void run() {
			saveXP();
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

	private double							health;
	public int								mana;
	public long								karma;

	public int								statStr;
	public int								statDex;
	public int								statInt;
	public int								statWis;
	public int								statCon;
	public int								statChr;

	public int								statPoints;
	public int								statPointsSpent;

	public HashMap<String, SkillDataStore>	skillSet		= null;
	public Map<String, Boolean>				skillsSelected;
	public HashMap<String, Integer>			skillsPurchased	= new HashMap<String, Integer>();
	public HashMap<String, String>			dataStore		= new HashMap<String, String>();
	public HashMap<Material, String>		skillLinkings	= new HashMap<Material, String>();

	public List<Loadout>					loadouts		= new ArrayList<Loadout>();

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
		// System.out.print("op-uuid: "+uuid);
		OfflinePlayer op = plugin.getServer().getOfflinePlayer(uuid);
		pName = op.getName();

		// System.out.print("pName: "+pName);

		// System.out.print("e: "+op.toString());
		// System.out.print("eType: "+op.getType());
		// System.out.print("e.id: "+e.getEntityId());

		if (pName == null) {
			if (op.isOnline()) {
				pName = op.getPlayer().getName();
				// System.out.print("isonline: " + pName);
			} else {
				// System.out.print("isoffline getting from uuid: " + uuid);
				Entity e = getEntityFromUUID(uuid);
				if (e != null) {
					if (e instanceof Player) {
						pName = ((Player) e).getName();
						// System.out.print("isoffline e is player : " + pName);
					} else {
						if (e instanceof LivingEntity) {
							pName = ((LivingEntity) e).getCustomName();
						} else {
							pName = "Herobrine";
						}
						// System.out.print("isoffline e is NOT player : " + pName);

					}
				} else {
					pName = "Unkn0wn";
					// System.out.print("Unkn0wn!!");
				}
			}
		}

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
		this.mana = getMaxMana(true);
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
		loadouts = getLoadouts();
	}

	public boolean allowedArmour(Material id) {
		Boolean valid = false;
		if (id == null) {
			valid = true;
			lq.debug.fine("Naked is valid armour");
		} else {
			if (id == Material.AIR) {
				valid = true;
				lq.debug.fine("'Air' is always valid armour");
			} else {

				if (lq.configMain.itemsAllMeansAll) {
					if (mainClass.allowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour set for class: " + mainClass.name);
						valid = true;
					}
					if (race.allowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour set for race: " + race.name);
						valid = true;
					}
					if (subClass != null && subClass.allowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour set for subClass: " + subClass.name);
						valid = true;
					}
					if (mainClass.dissallowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour disallowed for class: " + mainClass.name);
						valid = false;
					}
					if (race.dissallowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour disallowed for race: " + race.name);
						valid = false;
					}
					if (subClass != null && subClass.dissallowedArmour.contains(Material.PISTON_MOVING_PIECE)) {
						lq.debug.fine("ALL armour disallowed for subClass: " + subClass.name);
						valid = false;
					}
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

				int level = SetExp.getLevelOfXpAmount(currentXP);

				List<Object> perlevelallow = race.levelUp.getList("allowarmour", level);
				perlevelallow.addAll(mainClass.levelUp.getList("allowarmour", level));
				if (subClass != null) {
					perlevelallow.addAll(subClass.levelUp.getList("allowarmour", level));
				}
				if (StringUtils.join(perlevelallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
					valid = true;
				}

				List<Object> perleveldisallow = race.levelUp.getList("disallowarmour", level);
				perleveldisallow.addAll(mainClass.levelUp.getList("disallowarmour", level));
				if (subClass != null) {
					perleveldisallow.addAll(subClass.levelUp.getList("disallowarmour", level));
				}
				if (StringUtils.join(perleveldisallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
					valid = false;
				}

				CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.ARMOUR, valid, id);
				Bukkit.getServer().getPluginManager().callEvent(e);
				valid = e.isValid();
			}
		}
		return valid;
	}

	public boolean allowedTool(Material id) {
		Boolean valid = false;
		if (id == null) {
			valid = true;
			// System.out.print("Air/fist is valid tool");
		} else {
			if (lq.configMain.itemsAllMeansAll) {
				if (mainClass.allowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools set for class: " + mainClass.name);
					valid = true;
				}
				if (race.allowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools set for race: " + race.name);
					valid = true;
				}
				if (subClass != null && subClass.allowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools set for subClass: " + subClass.name);
					valid = true;
				}
				if (mainClass.dissallowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools disallowed for class: " + mainClass.name);
					valid = false;
				}
				if (race.dissallowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools disallowed for race: " + race.name);
					valid = false;
				}
				if (subClass != null && subClass.dissallowedTools.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL tools disallowed for subClass: " + subClass.name);
					valid = false;
				}
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

			int level = SetExp.getLevelOfXpAmount(currentXP);

			List<Object> perlevelallow = race.levelUp.getList("allowtool", level);
			perlevelallow.addAll(mainClass.levelUp.getList("allowtool", level));
			if (subClass != null) {
				perlevelallow.addAll(subClass.levelUp.getList("allowtool", level));
			}
			if (StringUtils.join(perlevelallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
				valid = true;
			}

			List<Object> perleveldisallow = race.levelUp.getList("disallowtool", level);
			perleveldisallow.addAll(mainClass.levelUp.getList("disallowtool", level));
			if (subClass != null) {
				perleveldisallow.addAll(subClass.levelUp.getList("disallowtool", level));
			}
			if (StringUtils.join(perleveldisallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
				valid = false;
			}

			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.TOOL, valid, id);
			Bukkit.getServer().getPluginManager().callEvent(e);
			valid = e.isValid();
		}
		return valid;
	}

	public boolean allowedWeapon(Material id) {
		Boolean valid = false;

		if (id == null) {
			valid = true;
			// lq.debug.fine("Air/Fist is valid weapon");
		} else {
			if (lq.configMain.itemsAllMeansAll) {
				if (mainClass.allowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons set for class: " + mainClass.name);
					valid = true;
				}
				if (race.allowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons set for race: " + race.name);
					valid = true;
				}
				if (subClass != null && subClass.allowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons set for subClass: " + subClass.name);
					valid = true;
				}
				if (mainClass.dissallowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons disallowed for class: " + mainClass.name);
					valid = false;
				}
				if (race.dissallowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons disallowed for race: " + race.name);
					valid = false;
				}
				if (subClass != null && subClass.dissallowedWeapons.contains(Material.PISTON_MOVING_PIECE)) {
					lq.debug.fine("ALL weapons disallowed for subClass: " + subClass.name);
					valid = false;
				}
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

			int level = SetExp.getLevelOfXpAmount(currentXP);

			List<Object> perlevelallow = race.levelUp.getList("allowweapon", level);
			perlevelallow.addAll(mainClass.levelUp.getList("allowweapon", level));
			if (subClass != null) {
				perlevelallow.addAll(subClass.levelUp.getList("allowweapon", level));
			}
			
			if (StringUtils.join(perlevelallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
				valid = true;
			}

			List<Object> perleveldisallow = race.levelUp.getList("disallowweapon", level);
			perleveldisallow.addAll(mainClass.levelUp.getList("disallowweapon", level));
			if (subClass != null) {
				perleveldisallow.addAll(subClass.levelUp.getList("disallowweapon", level));
			}
			if (StringUtils.join(perleveldisallow, ",").toLowerCase().contains(id.toString().toLowerCase())) {
				valid = false;
			}

			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.WEAPON, valid, id);
			Bukkit.getServer().getPluginManager().callEvent(e);
			valid = e.isValid();
		}
		return valid;
	}

	@SuppressWarnings("deprecation")
	public void checkInv() {
		Player p = lq.getServer().getPlayer(uuid);
		if (p != null && p.isOnline()) {
			if (!lq.validWorld(p.getWorld().getName())) {
				return;
			}

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
		int hp, level, con, bonus;
		double result, perlevel;
		con = getAttributeModifier(Attribute.CON);
		hp = race.baseHealth + con;
		if (hp < 1) {
			hp = 1;
		}
		level = SetExp.getLevelOfXpAmount(currentXP);
		if (level > lq.configMain.max_level) {
			level = lq.configMain.max_level;
		}

		double levelbonuses = race.levelUp.getTotal("hp", level);
		if (subClass != null) {
			perlevel = Math.max(mainClass.healthPerLevel, subClass.healthPerLevel);
			bonus = Math.max(mainClass.healthMod, subClass.healthMod);
			levelbonuses += Math.max(mainClass.levelUp.getTotal("hp", level), subClass.levelUp.getTotal("hp", level));
		} else {
			perlevel = mainClass.healthPerLevel;
			bonus = mainClass.healthMod;
			levelbonuses += mainClass.levelUp.getTotal("hp", level);
		}

		// System.out.print("levelbonuses: "+levelbonuses + " ("+level+")");
		double conBonus = ((con * 10) + 100) / 100.00D; // percent per level bonus of +/-50%
		perlevel *= conBonus;
		levelbonuses *= conBonus;
		// System.out.print("levelbonuses:"+levelbonuses);
		double base = hp + bonus;

		if (lq.configMain.attributesModifyBaseStats) {
			base *= conBonus;
		}
		result = (base + (level * perlevel) + levelbonuses);
		result = (Math.round(result * 10.0) / 10.0);

		this.maxHP = result;

		return this.maxHP;
	}

	public int getMaxMana() {
		return getMaxMana(false);
	}

	public int getMaxMana(boolean skipMods) {
		double result = 0;
		int mana, level, bonus, wis;
		double perlevel;
		wis = getAttributeModifier(Attribute.WIS, skipMods);

		mana = race.baseMana;

		level = SetExp.getLevelOfXpAmount(currentXP);
		if (level > lq.configMain.max_level) {
			level = lq.configMain.max_level;
		}

		double levelbonuses = race.levelUp.getTotal("mana", level);
		if (subClass != null) {
			perlevel = Math.max(mainClass.manaPerLevel, subClass.manaPerLevel);
			bonus = Math.max(mainClass.manaBonus, subClass.manaBonus);
			levelbonuses += Math.max(mainClass.levelUp.getTotal("mana", level), subClass.levelUp.getTotal("mana", level));
		} else {
			perlevel = mainClass.manaPerLevel;
			bonus = mainClass.manaBonus;
			levelbonuses += mainClass.levelUp.getTotal("mana", level);
		}
		double wisBonus = ((wis * 10) + 100) / 100.00D; // percent per level bonus of +/-50%
		perlevel *= wisBonus;
		levelbonuses *= wisBonus;

		double base = mana + bonus;
		if (lq.configMain.attributesModifyBaseStats) {
			base *= wisBonus;
		}
		result = (base + (level * perlevel) + levelbonuses);

		ManaCheck e = new ManaCheck(this, result);
		Bukkit.getServer().getPluginManager().callEvent(e);		
		result = e.getMaxMana();
		
		return (int) result;
	}

	public int getMaxSkillPointsLeft() {
		int sp, level, intel;
		double result, perlevel;
		intel = getAttributeModifier(Attribute.INT);
		sp = race.skillPoints;
		sp += mainClass.skillPoints;
		level = SetExp.getLevelOfXpAmount(currentXP);
		if (level > lq.configMain.max_level) {
			level = lq.configMain.max_level;
		}

		double levelbonuses = race.levelUp.getTotal("sp", level);
		if (subClass != null) {
			perlevel = Math.max(mainClass.skillPointsPerLevel, subClass.skillPointsPerLevel);
			levelbonuses += Math.max(mainClass.levelUp.getTotal("sp", level), subClass.levelUp.getTotal("sp", level));
		} else {
			perlevel = mainClass.skillPointsPerLevel;
			levelbonuses += mainClass.levelUp.getTotal("sp", level);
		}
		perlevel += race.skillPointsPerLevel;
		double intBonus = ((intel * 10) + 100) / 100.00D; // percent per level bonus of +/-50%
		perlevel *= intBonus;
		levelbonuses *= intBonus;
		double base = sp;
		if (lq.configMain.attributesModifyBaseStats) {
			base *= intBonus;
		}
		result = (base + (level * (perlevel)) + levelbonuses);

		return (int) result;
	}

	public float getSpeed() {
		float sp;
		int level = SetExp.getLevelOfXpAmount(currentXP);
		sp = race.baseSpeed;
		sp += mainClass.speedMod;
		sp += mainClass.levelUp.getTotal("speed", level);
				
		float before = sp;
		Entity e = getEntity();
		if (e != null && e.hasMetadata("cursetimeout")) {
			long cursetime = e.getMetadata("cursetimeout").get(0).asLong();
			if (System.currentTimeMillis() > cursetime) {
				e.removeMetadata("cursetimeout", lq);
			} else {
				List<MetadataValue> mods = null;
				mods = e.getMetadata("speed");
				for (MetadataValue metamod : mods) {
					Float mod = metamod.asFloat();
					lq.debug.fine("Curse: speed - " + mod);
					sp += mod;
				}
			}
		}
		
// System.out.print("SpeedCheckEvent: " + sp );

		SpeedCheckEvent ev = new SpeedCheckEvent(this, sp);
		Bukkit.getServer().getPluginManager().callEvent(ev);
		
// System.out.print("SpeedCheckEvent: " + ev.getSpeed() );

		if (ev.isCancelled()) {
			sp = before;
		} else {
			sp = (float) ev.getSpeed();
		}
		
// System.out.print("SpeedCheckEvent: " + sp );
		
		if (sp > 1.0f) {
			sp = 1.0f;
		}
		return sp;
	}

	public double getXPMod(ExperienceSource es) {
		double xp;
		switch (es) {
			case KILL:
				if (subClass != null) {
					xp = race.xpAdjustKill + mainClass.xpAdjustKill + subClass.xpAdjustKill;
				} else {
					xp = race.xpAdjustKill + mainClass.xpAdjustKill;
				}
				break;
			case MINE:
				if (subClass != null) {
					xp = race.xpAdjustMine + mainClass.xpAdjustMine + subClass.xpAdjustMine;
				} else {
					xp = race.xpAdjustMine + mainClass.xpAdjustMine;
				}
				break;
			case SMELT:
				if (subClass != null) {
					xp = race.xpAdjustSmelt + mainClass.xpAdjustSmelt + subClass.xpAdjustSmelt;
				} else {
					xp = race.xpAdjustSmelt + mainClass.xpAdjustSmelt;
				}
				break;
			default:
				xp = 0;
		}
		return xp;
	}

	public int getSkillPointsLeft() {
		return getMaxSkillPointsLeft() - getSkillPointsSpent();
	}

	public int getSkillPointsSpent() {
		int result = 0;
		for (Entry<String, Integer> cost : skillsPurchased.entrySet()) {
			String lKey = cost.getKey().toLowerCase();
			if (lKey.startsWith(mainClass.name.toLowerCase()) || lKey.startsWith(race.name.toLowerCase()) || (subClass != null && lKey.startsWith(subClass.name.toLowerCase()))) {
				result += cost.getValue();
			}
		}
		return result;
	}

	/**
	 * @return the statChr
	 */
	public int getStatChr() {
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statChr;
		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("chr", level);
			stat += race.statChr;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("chr", level);
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("chr", level);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statCon;

		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("con", level);
			stat += race.statCon;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("con", level);
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("con", level);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statDex;

		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("dex", level);
			stat += race.statDex;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("dex", level);
			// System.out.print("dexbonuses: "+mainClass.levelUp.getTotal("dex", level) + " ("+level+")");
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("dex", level);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statInt;

		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("int", level);
			stat += race.statInt;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("int", level);
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("int", level);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statStr;

		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("str", level);
			stat += race.statStr;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("str", level);
			// System.out.print("strbonuses: "+mainClass.levelUp.getTotal("str", level) + " ("+level+")");
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("str", level);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
		int stat;
		stat = statWis;

		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (race != null) {
			stat += (int) race.levelUp.getTotal("wis", level);
			stat += race.statWis;
		}
		if (mainClass != null) {
			stat += (int) mainClass.levelUp.getTotal("wis", level);
			if (subClass != null) {
				stat += (int) subClass.levelUp.getTotal("wis", level);
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
		for (Entry<String, SkillDataStore> s : potentialSkills.entrySet()) {
			if (isValidSkill(s.getKey())) {
				activeSkills.put(s.getValue().name, true);
			}
		}
		skillsSelected = activeSkills;
	}

	public boolean isValidSkill(String skill) {
		int level = SetExp.getLevelOfXpAmount(currentXP);
		HashMap<String, SkillDataStore> potentialSkills = getUniqueSkills();
		if (potentialSkills.containsKey(skill)) {
			SkillDataStore s = potentialSkills.get(skill);
			boolean valid = false;
			if (s.levelRequired <= level && s.skillPoints < 1) {
				valid = true;
			}
			if ((skillsPurchased.containsKey(mainClass.name + "|" + s.name) || skillsPurchased.containsKey(race.name + "|" + s.name) || (subClass != null && skillsPurchased.containsKey(subClass.name + "|" + s.name))) && (s.levelRequired <= level)) {
				valid = true;
			}
			if (s.needPerm != null && !s.needPerm.isEmpty()) {
				// System.out.print("needPerm: " + s.needPerm + " = " + getPlayer().hasPermission(s.needPerm));
				if (getPlayer()!=null) {
					if (!getPlayer().hasPermission(s.needPerm)) {
						valid = false;
					}
				} else {
					valid = false;
				}
			}
			if (!valid) {
				return false;
			}
			valid = checkSkillDeps(skill);
			if (valid) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSkillDeps(String skill) {
		boolean valid = false;
		HashMap<String, SkillDataStore> potentialSkills = getUniqueSkills();
		if (potentialSkills.containsKey(skill)) {
			SkillDataStore s = potentialSkills.get(skill);
			int level = SetExp.getLevelOfXpAmount(currentXP);
			if (s.levelRequired > level) {
				return false; 
			}
			
			if (s.requiresOne != null && !s.requiresOne.isEmpty()) {
				for (String skillname : s.requiresOne) {
					if (isValidSkill(skillname)) {
						valid = true;
					}
				}
			} else {
				valid = true;
			}

			if (valid) {
				if (s.requires != null && !s.requires.isEmpty()) {
					for (String skillname : s.requires) {
						if (!isValidSkill(skillname)) {
							valid = false;
						}
					}
				}
			}
			
			if (s.needPerm!=null || !s.needPerm.isEmpty()) {
				Player p = getPlayer();
				if (p!=null) {
					if (p.isOnline()) {
						if (p.hasPermission(s.needPerm)) {
							valid = true;
						} else {
							valid = false;
						}
					}else {
						valid = false;
					}
				} else {
					valid = false;
				}
			}
			
			if ((s.requires == null || s.requires.isEmpty())) {
				if (s.requiresOne == null || s.requiresOne.isEmpty()) {
					if (s.needPerm==null || s.needPerm.isEmpty()) {
						return true;
					}
				}
			}
		}
		return valid;
	}

	public HashMap<String, SkillDataStore> getUniqueSkills() {
		return getUniqueSkills(false);
	}

	public HashMap<String, SkillDataStore> getUniqueSkills(boolean rescan) {
		// use cached set if present unless told otherwise
		if (!rescan && this.skillSet != null && !skillSet.isEmpty()) {
			return skillSet;
		}

		Set<SkillDataStore> set = new HashSet<SkillDataStore>();
		if (race.availableSkills != null) {
			// set.addAll(race.availableSkills);
			for (SkillDataStore as : race.availableSkills) {
				SkillDataStore tempskill = new SkillDataStore(as);
				set.add(tempskill);
			}
		}
		if (race.outsourcedSkills != null) {
			// set.addAll(race.outsourcedSkills);
			for (SkillDataStore as : race.outsourcedSkills) {
				SkillDataStore tempskill = new SkillDataStore(as);
				set.add(tempskill);
			}
		}
		if (subClass != null) {
			if (subClass.availableSkills != null) {
				// set.addAll(subClass.availableSkills);
				for (SkillDataStore as : subClass.availableSkills) {
					SkillDataStore tempskill = new SkillDataStore(as);
					set.add(tempskill);
				}
			}
			if (subClass.outsourcedSkills != null) {
				// set.addAll(subClass.outsourcedSkills);
				for (SkillDataStore as : subClass.outsourcedSkills) {
					SkillDataStore tempskill = new SkillDataStore(as);
					set.add(tempskill);
				}
			}
		}
		if (mainClass.availableSkills != null) {
			// set.addAll(mainClass.availableSkills);
			for (SkillDataStore as : mainClass.availableSkills) {
				SkillDataStore tempskill = new SkillDataStore(as);
				set.add(tempskill);
			}
		}
		if (mainClass.outsourcedSkills != null) {
			// set.addAll(mainClass.outsourcedSkills);
			for (SkillDataStore as : mainClass.outsourcedSkills) {
				SkillDataStore tempskill = new SkillDataStore(as);
				set.add(tempskill);
			}
		}
		List<SkillDataStore> uniques = new ArrayList<SkillDataStore>();
		if (set != null) {
			uniques.addAll(set);
		}
		return makeMap(uniques);
	}

	public HashMap<String, SkillDataStore> makeMap(List<SkillDataStore> in) {
		HashMap<String, SkillDataStore> out = new HashMap<String, SkillDataStore>();
		Player p = getPlayer();
		for (SkillDataStore item : in) {
			if (item.needPerm !=null && !item.needPerm.isEmpty()) {
				if (p==null || !p.isOnline()) {
					continue;
				}
				if (!p.hasPermission(item.needPerm)) {
					continue;
				}
			}
			out.put(item.name, item);
		}
		return out;
	}

	public String getSkillsource(String skillName) {
		String source = null;
		for (SkillDataStore s : race.availableSkills) {
			if (s.name.equalsIgnoreCase(skillName)) {
				return race.name;
			}
		}
		for (SkillDataStore s : race.outsourcedSkills) {
			if (s.name.equalsIgnoreCase(skillName)) {
				return race.name;
			}
		}

		for (SkillDataStore s : mainClass.availableSkills) {
			if (s.name.equalsIgnoreCase(skillName)) {
				return mainClass.name;
			}
		}
		for (SkillDataStore s : mainClass.outsourcedSkills) {
			if (s.name.equalsIgnoreCase(skillName)) {
				return mainClass.name;
			}
		}

		if (subClass != null) {
			for (SkillDataStore s : subClass.availableSkills) {
				if (s.name.equalsIgnoreCase(skillName)) {
					return subClass.name;
				}
			}
			for (SkillDataStore s : subClass.outsourcedSkills) {
				if (s.name.equalsIgnoreCase(skillName)) {
					return subClass.name;
				}
			}
		}

		return source;
	}

	public boolean hasMastered(String className) {
		if (lq.configMain.debugMode) {
			lq.logger.info("className (" + className + ")...");
		}
		if (xpEarnt.containsKey(className.toLowerCase())) {
			if (lq.configMain.debugMode) {
				lq.logger.info("className (" + className + "): " + xpEarnt.get(className.toLowerCase()));
			}
			if (xpEarnt.get(className.toLowerCase()) >= lq.configMain.max_xp) {
				return true;
			}
		}
		return false;
	}

	public void healthCheck() {
		Player p = Bukkit.getServer().getPlayer(uuid);
		if (p != null) {

			if (!lq.validWorld(p.getWorld().getName())) {
				if (lq.configMain.manageHealthNonLqWorlds) {
					HealthStore hs = lq.datasync.getAltHealthStore(p.getUniqueId());
					if (hs == null || hs.getMaxhealth() < 1) {
						double hp = p.getHealth();
						if (hp > 20.0D) {
							hp = 20.0D;
							p.setHealth(hp);
							p.setMaxHealth(20.0D);
							p.setHealthScale(20.0D);
						}
					} else {
						p.setHealth(hs.getHealth());
						p.setMaxHealth(hs.getMaxhealth());
						p.setHealthScale(20.0D);
					}
				}
				return;
			}

			getMaxHealth();
			this.health = p.getHealth();

			try {
				if (this.health > 0.0D) {
					if (this.health > this.maxHP) {
						this.health = this.maxHP;
					}
					
					HealthCheck e = new HealthCheck(this, this.health, this.maxHP);
					Bukkit.getServer().getPluginManager().callEvent(e);
					
					double hp = e.getHealth();
					double mhp = e.getMaxHealth();
					if (hp>mhp) { hp=mhp; } 
					
					this.health = hp;
					this.maxHP = mhp;
					
					p.setHealth(Math.min(this.health, p.getMaxHealth()));

					p.setMaxHealth(this.maxHP);
					// p.setHealth(this.health);
					double scale = this.maxHP;

					double scaleLimit = (lq.configMain.healthScale + 0.0D);
					if (scale > scaleLimit) {
						scale = scaleLimit;
					}
					p.setHealthScale(scale);
					p.setHealthScaled(true);
					if (lq.configMain.debugMode) {
						lq.debug.fine("SHC - HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.print("Health error: check legendquest.log for details.");
				lq.debug.fine("Healtherror - HP: " + p.getHealth() + " | pHP: " + this.health + " | p.max: " + p.getMaxHealth() + " | pc.max: " + this.maxHP);
				lq.debug.thrown("PC", "healthCheck", e);
			}
		}
	}

	public boolean manaGain() {
		double gain;
		gain = race.manaPerSecond;
		int level = SetExp.getLevelOfXpAmount(currentXP);
		if (subClass != null) {
			gain += (Math.max(mainClass.manaPerSecond, subClass.manaPerSecond));
			gain += (int) (Math.max(race.levelUp.getTotal("manaregen", level), race.levelUp.getTotal("manaregen", level)));
		} else {
			gain += mainClass.manaPerSecond;
			gain += (int) race.levelUp.getTotal("manaregen", level);
		}

		int wis = getAttributeModifier(Attribute.WIS);
		double wisBonus = ((wis * 10) + 100) / 100.00D; // percent per level bonus of +/-50%
		if (wisBonus < -50.00D) {
			wisBonus = -50.00D;
		}

		gain *= wisBonus;

		return manaGain(gain);
	}

	public boolean manaGain(double gain) {
		double manaNow = this.mana;
		this.mana += Math.round(gain);
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

	public void scheduleXPSave() {
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lq, new DelayedXPSave(), 2L);
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

	public void saveXP() {
		Player p = getPlayer();
		if (p != null) {
			currentXP = SetExp.getTotalExperience(p);
			xpEarnt.put(mainClass.name.toLowerCase(), SetExp.getTotalExperience(getPlayer()));
			if (subClass != null) {
				xpEarnt.put(subClass.name.toLowerCase(), SetExp.getTotalExperience(getPlayer()));
			}
		}
	}

	public void giveXP(int XPGain) {
		PlayerExpChangeEvent event = new PlayerExpChangeEvent(getPlayer(), XPGain);
		Bukkit.getPluginManager().callEvent(event);
		XPGain = event.getAmount();
		this.setXP(currentXP + XPGain);
	}

	public boolean canCraft() {
		return canCraft(true);
	}

	public boolean canCraft(boolean eventfull) {
		boolean cando = false;
		if (race.allowCrafting || mainClass.allowCrafting) {
			cando = true;
			System.out.print("race " + race.name + " or main " + mainClass.name +" can craft ");

		}
		if (subClass != null && subClass.allowCrafting) {
			cando = true;
			System.out.print("subclass " + subClass.name +" can craft ");
		}

		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.CRAFT, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canCraft(Material m) {
		boolean cando = canCraft(false);
// System.out.print("craft cando:  " + cando);
		if (cando) {
			if (race.disallowedCrafting != null && race.disallowedCrafting.contains(m)) {
				cando = false;
// System.out.print("race " + race.name + " cant craft " + m.toString());
			}
			if (mainClass.disallowedCrafting != null && mainClass.disallowedCrafting.contains(m)) {
				cando = false;
// System.out.print("mainClass " + mainClass.name + " cant craft " + m.toString());
			}
			if (subClass != null) {
				if (subClass.disallowedCrafting != null && subClass.disallowedCrafting.contains(m)) {
					cando = false;
// System.out.print("subClass " + subClass.name + " cant craft " + m.toString());
				}
			}
		}
		
		Boolean corecheck = checkCoreability("craft",m.toString());
		if (corecheck!=null) { cando = corecheck; }

// System.out.print("craft corecheck cando:  " + cando);
		
		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.CRAFT, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

// System.out.print("craft event cando:  " + cando);

		return cando;
	}

	public boolean canSmelt() {
		return canSmelt(true);
	}

	public boolean canSmelt(boolean eventfull) {
		boolean cando = false;
		if (race.allowSmelting || mainClass.allowSmelting) {
			cando = true;
		}
		if (subClass != null && subClass.allowSmelting) {
			cando = true;
		}

		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.SMELT, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canSmelt(Material m) {
		boolean cando = canSmelt(false);
		if (cando) {
			if (race.disallowedSmelting != null && race.disallowedSmelting.contains(m)) {
				cando = false;
			}
			if (mainClass.disallowedSmelting != null && mainClass.disallowedSmelting.contains(m)) {
				cando = false;
			}
			if (subClass != null) {
				if (subClass.disallowedSmelting != null && subClass.disallowedSmelting.contains(m)) {
					cando = false;
				}
			}
		}

		Boolean corecheck = checkCoreability("smelt",m.toString());
		if (corecheck!=null) { cando = corecheck; }

		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.SMELT, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

		return cando;
	}

	public boolean canBrew() {
		return canBrew(true);
	}

	public boolean canBrew(boolean eventfull) {
		boolean cando = false;
		if (race.allowBrewing || mainClass.allowBrewing) {
			cando = true;
		}
		if (subClass != null && subClass.allowBrewing) {
			cando = true;
		}
		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.BREW, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canBrew(Material m) {
		boolean cando = canBrew(false);
		if (cando) {
			if (race.disallowedBrewing != null && race.disallowedBrewing.contains(m)) {
				cando = false;
			}
			if (mainClass.disallowedBrewing != null && mainClass.disallowedBrewing.contains(m)) {
				cando = false;
			}
			if (subClass != null) {
				if (subClass.disallowedBrewing != null && subClass.disallowedBrewing.contains(m)) {
					cando = false;
				}
			}
		}

		Boolean corecheck = checkCoreability("brew",m.toString());
		if (corecheck!=null) { cando = corecheck; }
		
		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.BREW, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

		return cando;
	}

	public boolean canEnchant() {
		return canEnchant(true);
	}

	public boolean canEnchant(boolean eventfull) {
		boolean cando = false;
		if (race.allowEnchating || mainClass.allowEnchanting) {
			cando = true;
		}
		if (subClass != null && subClass.allowEnchanting) {
			cando = true;
		}
		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.ENCHANT, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canEnchant(Material m) {
		boolean cando = canEnchant(false);
		if (cando) {
			if (race.disallowedEnchanting != null && race.disallowedEnchanting.contains(m)) {
				cando = false;
			}
			if (mainClass.disallowedEnchanting != null && mainClass.disallowedEnchanting.contains(m)) {
				cando = false;
			}
			if (subClass != null) {
				if (subClass.disallowedEnchanting != null && subClass.disallowedEnchanting.contains(m)) {
					cando = false;
				}
			}
		}
		
		Boolean corecheck = checkCoreability("enchant",m.toString());
		if (corecheck!=null) { cando = corecheck; }

		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.ENCHANT, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

		return cando;
	}

	public boolean canRepair() {
		return canRepair(true);
	}

	public boolean canRepair(boolean eventfull) {
		boolean cando = false;
		if (race.allowRepairing || mainClass.allowRepairing) {
			cando = true;
		}
		if (subClass != null && subClass.allowRepairing) {
			cando = true;
		}
		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.REPAIR, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canRepair(Material m) {
		boolean cando = canRepair(false);
		if (cando) {
			if (race.disallowedRepairing != null && race.disallowedRepairing.contains(m)) {
				cando = false;
			}
			if (mainClass.disallowedRepairing != null && mainClass.disallowedRepairing.contains(m)) {
				cando = false;
			}
			if (subClass != null) {
				if (subClass.disallowedRepairing != null && subClass.disallowedRepairing.contains(m)) {
					cando = false;
				}
			}
		}

		Boolean corecheck = checkCoreability("repair",m.toString());
		if (corecheck!=null) { cando = corecheck; }
		
		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.REPAIR, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

		return cando;
	}

	public boolean canTame() {
		return canTame(true);
	}

	public boolean canTame(boolean eventfull) {
		boolean cando = false;
		if (race.allowTaming || mainClass.allowTaming) {
			cando = true;
		}
		if (subClass != null && subClass.allowTaming) {
			cando = true;
		}
		if (eventfull) {
			CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.TAME, cando);
			Bukkit.getServer().getPluginManager().callEvent(e);
			cando = e.isValid();
		}
		return cando;
	}

	public boolean canTame(EntityType m) {
		boolean cando = canTame(false);
		if (cando) {
			if (race.disallowedTaming != null && race.disallowedTaming.contains(m)) {
				cando = false;
			}
			if (mainClass.disallowedTaming != null && mainClass.disallowedTaming.contains(m)) {
				cando = false;
			}
			if (subClass != null) {
				if (subClass.disallowedTaming != null && subClass.disallowedTaming.contains(m)) {
					cando = false;
				}
			}
		}

		
		Boolean corecheck = checkCoreability("tame",m.toString());
		if (corecheck!=null) { cando = corecheck; }		

		CoreSkillCheckEvent e = new CoreSkillCheckEvent(this, CoreSkillCheckEvent.CoreSkill.TAME, cando, m);
		Bukkit.getServer().getPluginManager().callEvent(e);
		cando = e.isValid();

		return cando;
	}

	public boolean validSkill(String name) {
		Player p = getPlayer();
		if (p != null) {
			if (p.isOnline()) {
				if (!lq.validWorld(p.getWorld().getName())) {
					return false;
				}
			}
		}
		checkSkills();
		if (skillsSelected != null && name != null) {
			Boolean hasSkill = skillsSelected.get(name);
			if (hasSkill != null) {
				return hasSkill;
			}
		}
		return false;
	}

	public void useSkill(String name, String[] args) {
		if (validSkill(name)) {
			SkillPhase phase = getSkillPhase(name);
			int time = 0;
			Player p = getPlayer();
			SkillDataStore skill = skillSet.get(name);
			time = (int) skill.getTimeLeft();
			if (phase == SkillPhase.READY) {
				if (skill.type != null && (!skill.type.equals(SkillType.ACTIVE))) {
					p.sendMessage(name + lq.configLang.skillInvalidPassive);
					return;
				}
				// if ((skill.name.equalsIgnoreCase("aura") || skill.name.equalsIgnoreCase("might"))) {
				// System.out.print("start might: "+p.getName() + " - " + skill.getLastUse()); }
				skill.setlastArgs(args);
				skill.setLastUse(System.currentTimeMillis());
				// if ((skill.name.equalsIgnoreCase("aura") || skill.name.equalsIgnoreCase("might"))) {
				// System.out.print("start Might: "+p.getName() + " - " + skill.getLastUse()); }
				if (p != null && p.isOnline()) {
					skill.setLastUseLoc(p.getLocation().clone());
				}
				if (skill.delay < 1 && skill.buildup < 1) {
					skill.startperms(lq, p);
					skill.start(lq, this);
				}
			} else if (phase == SkillPhase.COOLDOWN) {
				if (p != null && p.isOnline()) {
					p.sendMessage(name + " " + lq.configLang.skillCooldown + " " + time + "s");
				}
			} else if (phase == SkillPhase.DELAYED) {
				if (p != null && p.isOnline()) {
					p.sendMessage(name + " " + lq.configLang.skillDelayed + " " + time + "s");
				}
			} else if (phase == SkillPhase.BUILDING) {
				if (p != null && p.isOnline()) {
					p.sendMessage(name + " " + lq.configLang.skillBuilding + " " + time + "s");
				}
			} else if (phase == SkillPhase.ACTIVE) {
				if (p != null && p.isOnline()) {
					p.sendMessage(name + " " + lq.configLang.skillActive + " " + time + "s");
				}
			}
		} else {
			Player p = getPlayer();
			p.sendMessage(lq.configLang.skillInvalid + name);
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
		if (lq.configMain.disableStats) {
			return 10;
		}
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
		return getAttributeModifier(attr, false);
	}

	public int getAttributeModifier(Attribute attr, boolean skipMods) {
		return Mechanics.getPlayersAttributeModifier(this, attr, skipMods);
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
		} else if (number < 0) {
			if (x > lq.configLang.karmaNegativeItems.size() - 1) {
				x = lq.configLang.karmaNegativeItems.size() - 1;
			}
			karma = lq.configLang.karmaNegativeItems.get(x);
		} else {
			karma = lq.configLang.karmaPositiveItems.get(0);
		}

		return karma;
	}

	public Player getPlayer() {
		return lq.getServer().getPlayer(uuid);
	}

	public Entity getEntity() {
		for (World w : lq.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) { 
				if (e.getUniqueId().equals(uuid)) {
					return e;
				}
			}
		}
		return null;
	}

	public boolean payMana(int cost) {
		if (mana < cost) {
			return false;
		} else {
			mana = mana - cost;
			return true;
		}
	}

	public boolean canPay(int pay) {
		if (lq.hasVault) {
			Player p = getPlayer();
			if (p == null) {
				return false;
			}
			return PluginUtils.canPay(pay, p);
		} else {
			return true; // economy disabled
		}
	}

	public boolean payCash(int pay) {
		if (lq.hasVault) {
			Player p = getPlayer();
			if (p == null) {
				return false;
			}
			return PluginUtils.payCash(pay, p);
		} else {
			return true; // economy disabled
		}
	}

	public Double getBalance() {
		if (lq.hasVault) {
			Player p = getPlayer();
			if (p == null) {
				return null;
			}
			return PluginUtils.balance(p);
		} else {
			return null; // economy disabled
		}
	}

	public boolean payItem(ItemStack item) {
		int amount = item.getAmount();
		Player p = getPlayer();
		if (p == null) {
			return false;
		}
		if (!p.isOnline()) {
			return false;
		}
		PlayerInventory inv = p.getInventory();
		Material payment = item.getType();
		if (inv == null) {
			return false;
		}
		if (!inv.contains(payment)) {
			return false;
		} else {
			for (ItemStack i : inv.getContents()) {
				if (i != null && i.getType() != null && i.getType().equals(payment)) {
					if (i.getAmount() == amount) {
						inv.remove(i);
						return true;
					} else if (i.getAmount() > amount) {
						ItemStack replacement = new ItemStack(i.getType(), i.getAmount() - amount);
						inv.setItem(inv.first(i), replacement);
						return true;
					}
				}
			}
		}
		return false;
	}

	// skillLinkings
	public boolean hasLink(Material m) {
		return (skillLinkings.containsKey(m));
	}

	public String getLink(Material m) {
		return (skillLinkings.get(m));
	}

	public String addLink(Material m, String s) {
		return skillLinkings.put(m, s);
	}

	public String RemoveLink(Material m) {
		return skillLinkings.remove(m);
	}

	public boolean buySkill(String skillname) {
		SkillDataStore skill = getSkillData(skillname);
		if (skill != null) {
			getPlayer().sendMessage("Skill " + skillname + " found");
			int cost = skill.skillPoints;
			if (getSkillPointsLeft() >= cost) {
				if (checkSkillDeps(skillname)) {
					String classname = this.getSkillsource(skillname);
					skillsPurchased.put(classname + "|" + skillname, cost);
					return true;
				} else {
					if (skill.requires != null && !skill.requires.isEmpty()) {
						getPlayer().sendMessage(lq.configLang.skillRequires + skill.requires.toString());
					}
					if (skill.requiresOne != null && !skill.requiresOne.isEmpty()) {
						getPlayer().sendMessage(lq.configLang.skillRequiresOne + skill.requiresOne.toString());
					}
					int level = SetExp.getLevelOfXpAmount(currentXP);
					if (skill.levelRequired>0 && level<skill.levelRequired) {
						getPlayer().sendMessage(lq.configLang.skillRequiresLevel + skill.levelRequired);
					}
					return false;
				}
			} else {
				getPlayer().sendMessage(lq.configLang.skillPointsMissing + "'" + skillname + "': " + getSkillPointsLeft() + " (" + getSkillPointsSpent() + "/" + getMaxSkillPointsLeft() + ")");
				return false;
			}
		} else {
			getPlayer().sendMessage("Skill " + skillname + " not found");
			return false;
		}
	}

	public SkillDataStore getSkillData(String name) {
		SkillDataStore s = skillSet.get(name);
		return s;
	}

	public boolean changeClass(ClassType cl, Boolean sub) {
		return changeClass(cl,sub,false);		
	}
	public boolean changeClass(ClassType cl, Boolean sub, boolean admin) {
		if (cl == null) {
			lq.debug.fine(lq.configLang.classInvalid);
			return false;
		}

		if (Main.debugMode) {
			System.out.print("Admin/nopay: " + admin);
			System.out.print("lq.configMain.ecoClassSwap: " + lq.configMain.ecoClassSwap);
		}
		if (lq.hasVault && lq.configMain.ecoClassSwap > 0 && !admin) {
			if ((!sub && this.mainClass != lq.classes.defaultClass) || (sub && this.subClass == null)) {
				boolean payCheck = this.payCash(lq.configMain.ecoClassSwap);
				if (!payCheck) {
					return false;
				}
			}
		}

		Player p = getPlayer();
		final int xpNow = SetExp.getTotalExperience(p);

		int newxp = 0;
		if (p.getLevel() > 1 && xpNow < lq.configMain.max_xp) {
			lq.debug.fine("Level is: " + p.getLevel());
			if ((!sub && this.mainClass != lq.classes.defaultClass) || (sub && this.subClass != null)) {
				lq.debug.fine("resetting " + p.getName() + " XP: " + p.getTotalExperience() + " - " + ((int) (p.getTotalExperience() * (lq.configMain.percentXpKeepClassChange / 100))));
				// reset XP
				newxp = (int) (xpNow * (lq.configMain.percentXpKeepClassChange / 100));
				this.setXP(newxp);
				lq.players.savePlayer(this);
			}
			if (sub && this.subClass == null) {
				// moving from no subclass to subclass
				newxp = xpNow;
			}
		}

		String oldClassname;
		if (sub) {
			if (this.subClass != null) {
				oldClassname = this.subClass.name.toLowerCase();
			} else {
				oldClassname = null;
			}
			this.subClass = cl;
		} else {
			oldClassname = this.mainClass.name.toLowerCase();
			this.mainClass = cl;
		}
		int newclassxp = 0;
		if (this.xpEarnt.containsKey(cl.name.toLowerCase())) {
			newclassxp = this.xpEarnt.get(cl.name.toLowerCase());
		} else {
			newclassxp = newxp;
		}

		// if mastered class - save this xp and check if target class is mastered.
		if (xpNow > lq.configMain.max_xp) {
			if (oldClassname != null) {
				this.xpEarnt.put(oldClassname, xpNow);
			}

			if (newclassxp > lq.configMain.max_xp) {
				this.setXP(newclassxp);
			} else {
				this.setXP(0);
			}
		} else {
			// old class was not masteted - xp loss if any was done above.
			this.setXP(newclassxp);
		}

		lq.players.addPlayer(uuid, this);
		lq.players.savePlayer(this);
		this.scheduleHealthCheck();
		lq.players.scheduleUpdate(uuid);
		this.checkInv();
		this.skillSet = this.getUniqueSkills(true);
		lq.debug.fine(lq.configLang.classChanged + ": " + cl.name + " - " + p.getName());
		lq.players.scoreboards.put(this.uuid, null);

		this.loadouts = getLoadouts();
		lq.players.setLqPerms(this);
		return true;
	}

	public boolean changeRace(Race r) {
		return changeRace(r,false);		
	}
	public boolean changeRace(Race r, boolean admin) {
		if (r == null) {
			lq.debug.fine(lq.configLang.raceInvalid);
			return false;
		}

		if (raceChanged) {
			if (lq.hasVault && lq.configMain.ecoRaceSwap > 0 && !admin) {
				boolean payCheck = this.payCash(lq.configMain.ecoRaceSwap);
				if (!payCheck) {
					return false;
				}
			}
			Player p = getPlayer();
			final int xpNow = SetExp.getTotalExperience(p);
			int newxp = (int) (xpNow * (lq.configMain.percentXpKeepRaceChange / 100));
			this.setXP(newxp);
		}

		this.race = r;
		this.raceChanged = true;
		lq.players.addPlayer(uuid, this);
		lq.players.savePlayer(uuid);
		this.scheduleHealthCheck();
		this.checkInv();
		this.skillSet = this.getUniqueSkills(true);
		lq.players.scoreboards.put(this.uuid, null);

		this.loadouts = getLoadouts();
		
		lq.players.setLqPerms(this);

		return true;
	}

	public void damage(double dmg) {
		damage(dmg, null);
	}

	public void damage(double dmg, Entity source) {
		if (source != null) {
			getPlayer().damage(dmg, source);
		} else {
			getPlayer().damage(dmg);
		}
		this.scheduleHealthCheck();
	}

	public void heal(double health) {
		heal(health, null);
	}

	public void heal(double health, Entity source) {
		Player p = getPlayer();
		double h = p.getHealth();
		h = h + health;
		if (h > p.getMaxHealth()) {
			h = p.getMaxHealth();
		}
		p.setHealth(h);
		this.health = h;
		this.scheduleHealthCheck();
	}

	public void setStatStr(int statStr) {
		this.statStr = statStr;
	}

	public void setStatDex(int statDex) {
		this.statDex = statDex;
	}

	public void setStatInt(int statInt) {
		this.statInt = statInt;
	}

	public void setStatWis(int statWis) {
		this.statWis = statWis;
	}

	public void setStatCon(int statCon) {
		this.statCon = statCon;
	}

	public void setStatChr(int statChr) {
		this.statChr = statChr;
	}

	public Entity getEntityFromUUID(UUID uuid) {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e.getUniqueId().equals(uuid)) {
					return e;
				}
			}
		}
		return null;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public String putData(String key, String value) {
		return dataStore.put(key, value);
	}

	public String getData(String key) {
		return dataStore.get(key);
	}

	public void reset() {
		Player p = getPlayer();
		this.player = p.getName();
		this.charname = p.getName();
		this.mainClass = this.lq.classes.defaultClass;
		this.race = this.lq.races.defaultRace;
		this.raceChanged = false;
		this.subClass = null;
		this.maxHP = 20;
		this.health = 20;
		this.mana = getMaxMana();
		this.currentXP = 0;
		dataStore = new HashMap<String, String>();
		skillLinkings = new HashMap<Material, String>();
		if (!lq.configMain.randomStats) {
			statStr = statDex = statInt = statWis = statCon = statChr = 12;
		} else {
			int[] statline = { 16, 14, 13, 12, 11, 10 };
			Random r = new Random(p.getName().hashCode());
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
		xpEarnt = new HashMap<String, Integer>();
		dataStore = new HashMap<String, String>();
		skillLinkings = new HashMap<Material, String>();
		p.setExp(0);
		this.setXP(0);
		this.currentXP = 0;

	}

	public List<Loadout> getLoadouts() {
		if (Main.debugMode) {
			System.out.print("getting loadouts");
		}

		List<Loadout> list = new ArrayList<Loadout>();
		if (mainClass.classLoadouts != null) {
			if (Main.debugMode) {
				System.out.print("adding loadouts");
			}
			for (Loadout l : mainClass.classLoadouts) {
				if (Main.debugMode) {
					System.out.print("adding loadout: " + l.name);
				}
				try {
					list.add((Loadout) l.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			if (Main.debugMode) {
				System.out.print("no class loadouts :( ");
			}
		}
		if (subClass != null) {
			if (subClass.classLoadouts != null) {
				for (Loadout l : subClass.classLoadouts) {
					try {
						list.add((Loadout) l.clone());
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}

	public Loadout getLoadout() {
		return getLoadout(null, false);
	}

	public Loadout getLoadout(boolean matchActivator) {
		return getLoadout(null, matchActivator);
	}

	public Loadout getLoadout(ItemStack hold, boolean matchActivator) {
		if (hold == null) {
			Player p = getPlayer();
			ItemStack holding = p.getItemInHand();
			if (holding == null) {
				return null;
			}
			hold = holding;
		}

		for (Loadout l : loadouts) {
			if (Main.debugMode) {
				System.out.print("checking load:" + l.name);
			}

			ItemStack match = null;
			if (matchActivator) {
				match = l.activator;
			} else {
				match = l.repository;
			}
			if (Main.debugMode) {
				System.out.print("hold type:" + hold.getType().toString());
			}
			if (Main.debugMode) {
				System.out.print("match type:" + match.getType().toString());
			}

			if (match.getType().equals(hold.getType())) {
				if (Main.debugMode) {
					System.out.print("match types");
				}

				ItemMeta mdata = match.getItemMeta();
				ItemMeta hdata = hold.getItemMeta();

				boolean namematch = false;
				if (mdata.getDisplayName() == null && hdata.getDisplayName() == null) {
					namematch = true;
				}
				if (mdata.getDisplayName() != null && hdata.getDisplayName() != null) {
					if ((mdata.getDisplayName().equals(hdata.getDisplayName()))) {
						namematch = true;
					}
				}
				if (Main.debugMode) {
					System.out.print("customname match: " + namematch);
				}

				boolean lorematch = false;
				if (Main.debugMode) {
					System.out.print("checking lore: --------------------------");
				}
				if ((mdata.getLore() == null || mdata.getLore().isEmpty()) && (hdata.getLore() == null || hdata.getLore().isEmpty())) {
					lorematch = true;
					if (Main.debugMode) {
						System.out.print("lores are both null/empty");
					}
				}
				if (Main.debugMode) {
					System.out.print("mlore: " + mdata.getLore());
				}
				if (Main.debugMode) {
					System.out.print("hlore: " + hdata.getLore());
				}
				if (mdata.getLore() != null && hdata.getLore() != null) {
					List<String> mlore = mdata.getLore();
					List<String> hlore = hdata.getLore();
					if (mlore.size() == hlore.size()) {
						if (Main.debugMode) {
							System.out.print("lores are same size");
						}
						lorematch = true;
						for (int i = 0; i < mlore.size(); i++) {
							if (!mlore.get(i).equals(hlore.get(i))) {
								if (Main.debugMode) {
									System.out.print("lores are diff text: " + mlore.get(i) + " - " + hlore.get(i));
								}
								lorematch = false;
							}
						}
					} else {
						if (Main.debugMode) {
							System.out.print("lores are diff sizes");
						}
					}
				} else {
					if (Main.debugMode) {
						System.out.print("lores are diff nullstates");
					}
				}
				if (Main.debugMode) {
					System.out.print("lore match: " + lorematch);
				}

				if (Main.debugMode) {
					System.out.print("lore: ----------------------------------");
				}

				if (lorematch && namematch) {
					if (Main.debugMode) {
						System.out.print("returning load type - " + l.name);
					}
					// item is a match = return this load out.
					return l;
				} else {
					if (Main.debugMode) {
						System.out.print("item match fail");
					}
				}
			} else {
				if (Main.debugMode) {
					System.out.print("match types failed :( ");
				}

			}
		}
		return null;
	}

	public void setLoadouts() {
		if (loadouts == null || loadouts.size() < 1) {
			loadouts = getLoadouts();
		}
	}

	public String getCharname() {
		return charname;
	}
	public void setCharname(String charname) {
		this.charname = charname;
	}

	public int getStatPoints() {
		return statPoints;
	}
	public void setStatPoints(int statPoints) {
		this.statPoints = statPoints;
	}


	public int getStatPointsSpent() {
		return statPointsSpent;
	}
	public void setStatPointsSpent(int statPointsSpent) {
		this.statPointsSpent = statPointsSpent;
	}

	public int getStatPointsLeft() {
		return statPoints-statPointsSpent;
	}

	
	
	@SuppressWarnings("unchecked")
	public Boolean checkCoreability(String abilityname, String target) {
		Boolean cando = null;
//System.out.print("Core Skill Levels Check: " + abilityname+" | "+ target );		
		int level = SetExp.getLevelOfXpAmount(currentXP);

		List<String> potentials = null;
		List<Object> pl = null;

		potentials = new ArrayList<String>();
		pl = race.levelUp.getList("allow"+abilityname, level);
		pl.addAll(mainClass.levelUp.getList("allow"+abilityname, level));
		if (subClass != null) {
			pl.addAll(subClass.levelUp.getList("allow"+abilityname, level));
		}
		for (Object obj : pl) {
			for (String item : (List<String>)obj) {
				potentials.add(item.toLowerCase());

// System.out.print("Core Skill Levels Check item: " +item.toLowerCase());
			}
		}		
		if (potentials.contains(target.toLowerCase()) || potentials.contains("any") || potentials.contains("all")) {
// System.out.print("Core Skill Levels Check: " + abilityname+" | "+ target + "= true - (hasany?:"+potentials.contains("any")+")" );
			cando = true;
		}

		potentials = new ArrayList<String>();
		pl = race.levelUp.getList("disallow"+abilityname, level);
		pl.addAll(mainClass.levelUp.getList("disallow"+abilityname, level));
		if (subClass != null) {
			pl.addAll(subClass.levelUp.getList("disallow"+abilityname, level));
		}
		for (Object obj : pl) {
			for (String item : (List<String>)obj) {
// System.out.print("Core Skill Levels Check dis-item: " +item.toLowerCase());
				potentials.add(item.toLowerCase());
			}
		}
		if (potentials.contains(target.toLowerCase()) || potentials.contains("any") || potentials.contains("all")) {
// System.out.print("Core Skill Levels Check: " + abilityname+" | "+ target + "= false - (hasany?:"+potentials.contains("any")+")" );
			cando = false;
		}
		
		//return null if not affected.
		return cando;
	}
	
}
