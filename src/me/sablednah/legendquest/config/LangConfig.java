package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class LangConfig extends Config {

    public String startup = "The sky above the port was the color of television, tuned to a dead channel.";
    public String shutdown = "=== END OF LINE ===";

    public String invalidCommand = "Sorry couldn't recognise command: ";
    // TODO add REAL help info...
    public String helpCommand = "indepth help here";
    public String invalidPlayerCommand = "Command only valid from ingame player.";
    public String invalidArgumentsCommand = "Invalid arguments.";
    public String characterNotFound = "Character not found for: ";
    public String commandReloaded = "Settings Reloaded";;

    public String raceScan = "Scanning for race files in folder: ";
    public String raceScanFound = "Found race: ";
    public String raceScanEnd = "Scanning for race files completed.";
    public String raceScanInvalid = "Invalid race: ";

    public String classScan = "Scanning for class files in folder: ";
    public String classScanFound = "Found class: ";
    public String classScanEnd = "Scanning for class files completed.";
    public String classScanInvalid = "Invalid class: ";
    public String classScanRaceWarning = "Warning Found unknon Race: ";
    public String classScanNoRaceOrGroup = "No Valid race or group found in: ";
    public String classScanGroupWarning = "No Valid group found in: ";

    public String raceNoDefault = "Sorry no default Race found - using first available.";
    public String classNoDefault = "Sorry no default Class found - using first available.";

    public String youAreCurrently = "You are currently ";
    public String raceChanged = "Your race has been changed to ";
    public String raceChangeNotAllowed = "Sorry you have already slected a race. ";
    public String raceList = "-------- Races --------";
    public String raceNotAllowed = "Sorry, you can't select that race.";

    public String classSelectRaceFirst = "Please Select Race before class.";
    public String classChanged = "You have changed your class to: ";
    public String classList = "------- Classes -------";
    public String classChangeWarnXpLoss = "Warning: Changing class will lose your acumulated XP";
    public String classConfirm = "Repeat with confirm appended  e.g. /class mage confirm";
    public String classNotAllowed = "Sorry, you can't select that class.";

    public String playerStats = "----- Player Info -----";
    public String statSTR = "STR";
    public String statDEX = "DEX";
    public String statINT = "INT";
    public String statWIS = "WIS";
    public String statCON = "CON";
    public String statCHR = "CHR";
    public String statHealth = "HP";
    public String statMana = "MP";

    public String statLevel = "Level";
    public String statLevelShort = "Lvl";
    public String statSkillPoints = "Skill Points";
    public String statSp = "SP";
    public String statRace = "Race";
    public String statClass = "Class";
    public String playerName = "Name";

    public String cantEquipArmour = "Sory you can't wear that armour.";
    public String cantUseTool = "Sory you can't use that tool.";
    public String cantUseWeapon = "Sory you can't weild that weapon.";
    public String cantCraft = "Sory you can't craft.";
    public String cantSmelt = "Sory you can't smelt.";
    public String cantEnchant = "Sory you can't enchat.";
    public String cantRepair = "Sory you can't repair.";
    public String cantBrew = "Sory you can't brew potions";
    
    public String noSkills = "You have no skills to use.";
    public String hasSkills = "You have the following usable skills:";
    public String skillPoints = "Skill Points: ";
    public String skillList = "You have the following skills available.";
    public String skillListHeader = "-- Skill Name --,-- Level --,--Cost--";
    public String skillsList = "------- SkillPool --------";
    

    public LangConfig(final Main p) {
        super(p, "lang.yml");

        this.startup = this.getConfigItem("startup", this.startup);
        this.shutdown = this.getConfigItem("shutdown", this.shutdown);

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

        this.raceScan = this.getConfigItem("raceScan", this.raceScan);
        this.raceScanFound = this.getConfigItem("raceScanFound", this.raceScanFound);
        this.raceScanEnd = this.getConfigItem("raceScanEnd", this.raceScanEnd);
        this.raceScanInvalid = this.getConfigItem("raceScanInvalid", this.raceScanInvalid);
        this.raceNotAllowed = this.getConfigItem("raceNotAllowed", this.raceNotAllowed);

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

        this.classSelectRaceFirst = this.getConfigItem("classSelectRaceFirst", this.classSelectRaceFirst);
        this.classChanged = this.getConfigItem("classChanged", this.classChanged);
        this.classList = this.getConfigItem("classList", this.classList);
        this.classNotAllowed = this.getConfigItem("classNotAllowed", this.classNotAllowed);

        this.helpCommand = this.getConfigItem("helpCommand", this.helpCommand);
        this.invalidCommand = this.getConfigItem("invalidCommand", this.invalidCommand);
        this.invalidArgumentsCommand = this.getConfigItem("invalidArgumentsCommand", this.invalidArgumentsCommand);
        this.characterNotFound = this.getConfigItem("characterNotFound", this.characterNotFound);
        this.commandReloaded = this.getConfigItem("commandReloaded", this.commandReloaded);

        this.cantEquipArmour = this.getConfigItem("cantEquipArmour", this.cantEquipArmour);
        this.cantUseTool = this.getConfigItem("cantUseTool", this.cantUseTool);
        this.cantUseWeapon = this.getConfigItem("cantUseWeapon", this.cantUseWeapon);
        this.cantCraft = this.getConfigItem("cantCraft", this.cantCraft);
        this.cantSmelt = this.getConfigItem("cantSmelt", this.cantSmelt);
        this.cantBrew = this.getConfigItem("cantBrew", this.cantBrew);
        this.cantEnchant = this.getConfigItem("cantEnchant", this.cantEnchant);
        this.cantRepair = this.getConfigItem("cantRepair", this.cantRepair);

        this.noSkills = this.getConfigItem("noSkills", this.noSkills);
        this.hasSkills = this.getConfigItem("hasSkills", this.hasSkills);
        this.skillPoints = this.getConfigItem("skillPoints", this.skillPoints);
        this.skillList = this.getConfigItem("skillList", this.skillList);
        this.skillListHeader = this.getConfigItem("skillListHeader", this.skillListHeader);
        this.skillsList = this.getConfigItem("skillsList", this.skillsList);

    }
}
