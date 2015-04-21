package me.sablednah.legendquest.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.sablednah.legendquest.Main;

public class LangConfig extends Config {

	public String					startup					= "The sky above the port was the color of television, tuned to a dead channel.";
	public String					shutdown				= "=== END OF LINE ===";

	public String					adminName				= "GM";

	public String					invalidCommand			= "Sorry couldn't recognise command: ";
	// TODO add more help info...
	public String					helpCommand				= "/lq help : Displays this list\n" + "/lq stats [player] : Display character stats\n" + "/lq hp [player] : Display player health (and mana)\n"
																	+ "/lq karma [player] : Display player Karma Level\n"
																	+ "/lq roll [player] [str|dex|con|int|wis|chr] [integer|very_easy|easy|average|tough|challenging] : Roll the dice to perform a test for roleplay purposes.\n"
																	+ "/lq race : Display your race\n" + "/lq race [list|racename] : List the races you are allowed or select race [racename]\n" + "/lq class : Display your class and (subclass if any)\n"
																	+ "/lq class list : Display classes you are allowed.  Including classes unlocked by mastery of dependent class.\n"
																	+ "/lq class {classname} : Select class name as your name class.  Will warn if you will loose XP.\n"
																	+ "/lq class sub {classname} : Select class name as your sub class.  Will warn if you will loose XP.";

	public HashMap<String, String>	cmdHelp					= new HashMap<String, String>();

	public String					invalidPlayerCommand	= "Command only valid from ingame player.";
	public String					invalidArgumentsCommand	= "Invalid arguments.";
	public String					characterNotFound		= "Character not found for: ";
	public String					commandReloaded			= "Settings Reloaded";

	public String					commandRollSucess		= "Test Succeeded";
	public String					commandRollFail			= "Test Failed";

	public String					invalidWorld			= "Sorry you are not in a valid LegendQuest world";

	public String					raceScan				= "Scanning for race files in folder: ";
	public String					raceScanFound			= "Found race: ";
	public String					raceScanEnd				= "Scanning for race files completed.";
	public String					raceScanInvalid			= "Invalid race file: ";
	public String					raceInvalid				= "Invalid race: ";

	public String					classScan				= "Scanning for class files in folder: ";
	public String					classScanFound			= "Found class: ";
	public String					classScanEnd			= "Scanning for class files completed.";
	public String					classScanInvalid		= "Invalid class file: ";
	public String					classInvalid			= "Invalid class: ";

	public String					classScanRaceWarning	= "Warning Found unknown Race: ";
	public String					classScanNoRaceOrGroup	= "No Valid race or group found in: ";
	public String					classScanGroupWarning	= "No Valid group found in: ";

	public String					raceNoDefault			= "Sorry no default Race found - using first available.";
	public String					classNoDefault			= "Sorry no default Class found - using first available.";

	public String					youAreCurrently			= "You are currently ";
	public String					raceChanged				= "Your race has been changed to ";
	public String					raceChangedAdmin		= "GM has changed your race to ";
	public String					raceChangeNotAllowed	= "Sorry you have already selected a race. ";
	public String					raceList				= "-------- Races --------";
	public String					raceNotAllowed			= "Sorry, you can't select that race.";
	public String					raceChangeWarnXpLoss	= "Warning: Changing race will lose your accumulated XP";
	public String					raceConfirm				= "Repeat with confirm appended  e.g. /race human confirm";

	public String					classSelectRaceFirst	= "Please Select Race before class.";
	public String					classChanged			= "You have changed your class to: ";
	public String					classChangedAdmin		= "GM has changed your class to: ";
	public String					classList				= "------- Classes -------";
	public String					classChangeWarnXpLoss	= "Warning: Changing class will lose your accumulated XP";
	public String					classConfirm			= "Repeat with confirm appended  e.g. /class mage confirm";
	public String					classNotAllowed			= "Sorry, you can't select that class.";

	public String					playerStats				= "----- Player Info -----";
	public String					statSTR					= "STR";
	public String					statDEX					= "DEX";
	public String					statINT					= "INT";
	public String					statWIS					= "WIS";
	public String					statCON					= "CON";
	public String					statCHR					= "CHR";
	public String					statHealth				= "HP";
	public String					statMana				= "MP";

	public String					statLevel				= "Level";
	public String					statLevelShort			= "Lvl";
	public String					statSkillPoints			= "Skill Points";
	public String					statSp					= "SP";
	public String					statRace				= "Race";
	public String					statClass				= "Class";
	public String					playerName				= "Name";
	public String					statKarma				= "Karma";
	public String					statXP					= "Exp";
	public String					storedExperience		= "-- Stored Experience --";

	public String					cantEquipArmour			= "Sorry you can't wear that armour.";
	public String					cantUseTool				= "Sorry you can't use that tool.";
	public String					cantUseWeapon			= "Sorry you can't wield that weapon.";
	public String					cantCraft				= "Sorry you can't craft.";
	public String					cantSmelt				= "Sorry you can't smelt.";
	public String					cantEnchant				= "Sorry you can't enchant.";
	public String					cantRepair				= "Sorry you can't repair.";
	public String					cantBrew				= "Sorry you can't brew potions";
	public String					cantTame				= "Sorry you can't tame";
	public String					renameWarning			= "&cWarning &f";
	public String					renameWarning2			= "&c attempted to rename item as: ";

	public String					noSkills				= "You have no skills to use.";
	public String					hasSkills				= "You have the following usable skills:";
	public String					skillPoints				= "Skill Points: ";
	public String					skillList				= "You have the following skills available.";
	public String					skillListHeader			= "-- Skill Name --,-- Level --,--Cost--";
	public String					skillsList				= "------- SkillPool --------";
	public String					skillsListPasive		= "--- Passive Skills ---";
	public String					skillsListActive		= "--- Active Skills ----";

	public String					combatHit				= "You hit your target.";
	public String					combatMissed			= "Your target evaded your attack.";
	public String					combatDodged			= "You evaded an attack.";
	public String					combatDodgefail			= "You failed to evade.";

	public String					karmaPositive			= "Neutral,Kind , Good,Samaritan, Saintly";
	public String					karmaNegative			= "Neutral., Rascal,Rogue,villainous , Diabolic";

	public List<String>				karmaPositiveItems		= null;
	public List<String>				karmaNegativeItems		= null;

	public String					skillBuildupDisturbed	= "cancelled due to movement! Stay still...";
	public String					skillLackOfMana			= "Sorry not enough mana.";
	public String					skillLackOfItem			= "Sorry you can't use that skill, you need ";
	public String					skillLackOfPay			= "Sorry you can't use that skill, you need to pay:";
	public String					skillLackOfKarma		= "Sorry you can't use that skill, you need karma over:";
	public String					skillLackOfKarmaEvil	= "Sorry you can't use that skill, you need karma below:";
	public String					skillLackOfPerm			= "Sorry you are not permitted to use that skill.";

	public String					skillCooldown			= "Skill cooling down.";
	public String					skillDelayed			= "Skill Delayed.";
	public String					skillBuilding			= "Skill Building...";
	public String					skillActive				= "Skill Already Active.";

	public String					skillLinkEmptyHand		= "Cannot link Skill to empty hand";
	public String					skillLinked				= " skill linked to item ";
	public String					skillUnlinked			= "Skill unlinked from item ";
	public String					skillLinkList			= "You have the following skill links: ";
	public String					skillPointsBought		= "You have learnt a skill: ";
	public String					skillPointsOwned		= " already learnt.";
	public String					skillPointsMissing		= "Sorry you don't have enough skill points to buy ";
	public String					skillPointsNoSkill		= "Please specify a skill to buy";
	public String					skillInfoNoSkill		= "Please specify a skill to get info";
	public String					skillRequiresOne		= "Skill requires one of these skill: ";
	public String					skillRequires			= "Skill requires all these skills: ";
	public String					skillCommandLineUse		= "Using skill: ";
	public String					skillLinkUse			= "Using Linked skill: ";
	public String					skillInvalid			= "Sorry you can't use: ";
	public String					skillInvalidTarget		= "Sorry, you need to look at a target...";
	
	public String					skillInvalidPassive		= " is a passive or triggered skill, not usable via a command";
	public String					skillStunned			= "Sorry you're stuned, you can't use: ";

	public String					xpChange				= "You gained Exp: ";
	public String					xpChangeAdmin			= "You gave Exp: ";

	public String					ecoPaid					= " paid.";
	public String					ecoDeclined				= "Card Declined: Insufficient funds.";

	public String					partyXpChange			= "Your party gained Exp: ";

	public LangConfig(final Main p) {
		super(p, "lang.yml");

		this.startup = this.getConfigItem("startup", this.startup);
		this.shutdown = this.getConfigItem("shutdown", this.shutdown);

		this.invalidWorld = this.getConfigItem("invalidWorld", this.invalidWorld);
		this.adminName = this.getConfigItem("adminName", this.adminName);

		this.playerStats = this.getConfigItem("playerStats", this.playerStats);
		this.statSTR = this.getConfigItem("statSTR", this.statSTR);
		this.statDEX = this.getConfigItem("statDEX", this.statDEX);
		this.statINT = this.getConfigItem("statINT", this.statINT);
		this.statWIS = this.getConfigItem("statWIS", this.statWIS);
		this.statCON = this.getConfigItem("statCON", this.statCON);
		this.statCHR = this.getConfigItem("statCHR", this.statCHR);
		this.statHealth = this.getConfigItem("statHealth", this.statHealth);
		this.statMana = this.getConfigItem("statMana", this.statMana);
		this.statRace = this.getConfigItem("statRace", this.statRace);
		this.statClass = this.getConfigItem("statClass", this.statClass);
		this.statKarma = this.getConfigItem("statKarma", this.statKarma);
		this.storedExperience = this.getConfigItem("storedExperience", this.storedExperience);

		this.raceScan = this.getConfigItem("raceScan", this.raceScan);
		this.raceScanFound = this.getConfigItem("raceScanFound", this.raceScanFound);
		this.raceScanEnd = this.getConfigItem("raceScanEnd", this.raceScanEnd);
		this.raceScanInvalid = this.getConfigItem("raceScanInvalid", this.raceScanInvalid);
		this.raceNotAllowed = this.getConfigItem("raceNotAllowed", this.raceNotAllowed);
		
		this.renameWarning = this.getConfigItem("renameWarning", this.renameWarning);
		this.renameWarning2 = this.getConfigItem("renameWarning2", this.renameWarning2);

		this.classScan = this.getConfigItem("classScan", this.classScan);
		this.classScanFound = this.getConfigItem("classScanFound", this.classScanFound);
		this.classScanEnd = this.getConfigItem("classScanEnd", this.classScanEnd);
		this.classScanInvalid = this.getConfigItem("classScanInvalid", this.classScanInvalid);
		this.classScanRaceWarning = this.getConfigItem("classScanRaceWarning", this.classScanRaceWarning);
		this.classScanNoRaceOrGroup = this.getConfigItem("classScanNoRaceOrGroup", this.classScanNoRaceOrGroup);
		this.classScanGroupWarning = this.getConfigItem("classScanGroupWarning", this.classScanGroupWarning);

		this.raceNoDefault = this.getConfigItem("raceNoDefault", this.raceNoDefault);
		this.classNoDefault = this.getConfigItem("classNoDefault", this.classNoDefault);

		this.youAreCurrently = this.getConfigItem("youAreCurrently", this.youAreCurrently);
		this.raceChanged = this.getConfigItem("raceChanged", this.raceChanged);
		this.raceChangeNotAllowed = this.getConfigItem("raceChangeNotAllowed", this.raceChangeNotAllowed);
		this.raceList = this.getConfigItem("raceList", this.raceList);
		this.raceChangeWarnXpLoss = this.getConfigItem("raceChangeWarnXpLoss", this.raceChangeWarnXpLoss);
		this.raceConfirm = this.getConfigItem("raceConfirm", this.raceConfirm);

		this.classSelectRaceFirst = this.getConfigItem("classSelectRaceFirst", this.classSelectRaceFirst);
		this.classChanged = this.getConfigItem("classChanged", this.classChanged);
		this.classList = this.getConfigItem("classList", this.classList);
		this.classNotAllowed = this.getConfigItem("classNotAllowed", this.classNotAllowed);
		this.classChangeWarnXpLoss = this.getConfigItem("classChangeWarnXpLoss", this.classChangeWarnXpLoss);
		this.classConfirm = this.getConfigItem("classConfirm", this.classConfirm);

		this.helpCommand = this.getConfigItem("helpCommand", this.helpCommand);

		ConfigurationSection commandHelpSection = this.getConfigItem("cmdHelp");
		Map<String, Object> vals = commandHelpSection.getValues(false);
		Set<Entry<String, Object>> eset = vals.entrySet();

		final Iterator<Entry<String, Object>> entries = eset.iterator();
		while (entries.hasNext()) {
			final Entry<String, Object> entry = entries.next();
			String data = (String) entry.getValue();
			cmdHelp.put(entry.getKey(), data);
		}

		this.invalidCommand = this.getConfigItem("invalidCommand", this.invalidCommand);
		this.invalidArgumentsCommand = this.getConfigItem("invalidArgumentsCommand", this.invalidArgumentsCommand);
		this.characterNotFound = this.getConfigItem("characterNotFound", this.characterNotFound);
		this.commandReloaded = this.getConfigItem("commandReloaded", this.commandReloaded);
		this.commandRollSucess = this.getConfigItem("commandRollSucess", this.commandRollSucess);
		this.commandRollFail = this.getConfigItem("commandRollFail", this.commandRollFail);

		this.cantEquipArmour = this.getConfigItem("cantEquipArmour", this.cantEquipArmour);
		this.cantUseTool = this.getConfigItem("cantUseTool", this.cantUseTool);
		this.cantUseWeapon = this.getConfigItem("cantUseWeapon", this.cantUseWeapon);
		this.cantCraft = this.getConfigItem("cantCraft", this.cantCraft);
		this.cantSmelt = this.getConfigItem("cantSmelt", this.cantSmelt);
		this.cantBrew = this.getConfigItem("cantBrew", this.cantBrew);
		this.cantEnchant = this.getConfigItem("cantEnchant", this.cantEnchant);
		this.cantRepair = this.getConfigItem("cantRepair", this.cantRepair);
		this.cantTame = this.getConfigItem("cantTame", this.cantTame);

		this.noSkills = this.getConfigItem("noSkills", this.noSkills);
		this.hasSkills = this.getConfigItem("hasSkills", this.hasSkills);
		this.skillPoints = this.getConfigItem("skillPoints", this.skillPoints);
		this.skillList = this.getConfigItem("skillList", this.skillList);
		this.skillListHeader = this.getConfigItem("skillListHeader", this.skillListHeader);
		this.skillsList = this.getConfigItem("skillsList", this.skillsList);
		this.skillsListPasive = this.getConfigItem("skillsListPasive", this.skillsListPasive);
		this.skillsListActive = this.getConfigItem("skillsListActive", this.skillsListActive);

		this.combatHit = this.getConfigItem("combatHit", this.combatHit);
		this.combatMissed = this.getConfigItem("combatMissed", this.combatMissed);
		this.combatDodged = this.getConfigItem("combatDodged", this.combatDodged);
		this.combatDodgefail = this.getConfigItem("combatDodgefail", this.combatDodgefail);

		this.karmaPositive = this.getConfigItem("karmaPositive", this.karmaPositive);
		this.karmaNegative = this.getConfigItem("karmaNegative", this.karmaNegative);

		this.karmaPositiveItems = Arrays.asList(this.karmaPositive.split("\\s*,\\s*"));
		this.karmaNegativeItems = Arrays.asList(this.karmaNegative.split("\\s*,\\s*"));

		this.skillBuildupDisturbed = this.getConfigItem("skillBuildupDisturbed", this.skillBuildupDisturbed);

		this.skillCooldown = this.getConfigItem("skillCooldown", this.skillCooldown);
		this.skillDelayed = this.getConfigItem("skillDelayed", this.skillDelayed);
		this.skillBuilding = this.getConfigItem("skillBuilding", this.skillBuilding);
		this.skillActive = this.getConfigItem("skillActive", this.skillActive);

		this.skillLackOfPay = this.getConfigItem("skillLackOfPay", this.skillLackOfPay);
		this.skillLackOfMana = this.getConfigItem("skillLackOfMana", this.skillLackOfMana);
		this.skillLackOfItem = this.getConfigItem("skillLackOfItem", this.skillLackOfItem);
		this.skillLackOfPerm = this.getConfigItem("skillLackOfPerm", this.skillLackOfPerm);
		this.skillLinkEmptyHand = this.getConfigItem("skillLinkEmptyHand", this.skillLinkEmptyHand);
		this.skillLinked = this.getConfigItem("skillLinked", this.skillLinked);
		this.skillUnlinked = this.getConfigItem("skillUnlinked", this.skillUnlinked);

		this.skillLinkList = this.getConfigItem("skillLinkList", this.skillLinkList);
		this.skillPointsBought = this.getConfigItem("skillPointsBought", this.skillPointsBought);
		this.skillPointsMissing = this.getConfigItem("skillPointsMissing", this.skillPointsMissing);
		this.skillPointsNoSkill = this.getConfigItem("skillPointsNoSkill", this.skillPointsNoSkill);
		this.skillInfoNoSkill = this.getConfigItem("skillInfoNoSkill", this.skillInfoNoSkill);
		this.skillRequiresOne = this.getConfigItem("skillRequiresOne", this.skillRequiresOne);
		this.skillRequires = this.getConfigItem("skillRequires", this.skillRequires);

		this.skillCommandLineUse = this.getConfigItem("skillCommandLineUse", this.skillCommandLineUse);
		this.skillLinkUse = this.getConfigItem("skillLinkUse", this.skillLinkUse);
		this.skillInvalid = this.getConfigItem("skillInvalid", this.skillInvalid);
		this.skillInvalidTarget = this.getConfigItem("skillInvalidTarget", this.skillInvalidTarget);

		this.xpChange = this.getConfigItem("xpChange", this.xpChange);
		this.xpChangeAdmin = this.getConfigItem("xpChangeAdmin", this.xpChangeAdmin);

		this.ecoPaid = this.getConfigItem("ecoPaid", this.ecoPaid);
		this.ecoDeclined = this.getConfigItem("ecoDeclined", this.ecoDeclined);

		this.partyXpChange = this.getConfigItem("partyXpChange", this.partyXpChange);

	}
}
