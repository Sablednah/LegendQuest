package me.sablednah.legendquest.config;

import java.util.ArrayList;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.mechanics.Difficulty;

public class MainConfig extends Config {

	public boolean				debugMode					= false;

	public boolean				useMySQL					= false;
	public String				sqlUsername					= "username";
	public String				sqlPassword					= "password";
	public String				sqlHostname					= "localhost";
	public int					sqlPort						= 3306;
	public String				sqlDatabase					= "legendquest";
	public String				sqlPrefix					= "lq_";
	public int					sqlKeepAliveInterval		= 60;
	
	public boolean				backupSQlite				= true;
	public int					SQLiteSaves					= 3;
	public boolean				backupLQlog					= true;
	public int					LQLogSaves					= 5;

	public boolean				logSQL						= true;
	public String				logLevel					= "ALL";
	public boolean				useServerLogOnly			= false;

	public ArrayList<String>	worlds						= new ArrayList<String>();
	public boolean				randomStats					= true;

	public ArrayList<String>	nameBlacklist				= new ArrayList<String>();
	public ArrayList<String>	nameBlacklistParts			= new ArrayList<String>();
	public boolean				broadcastRename				= true;

	public boolean				useScoreBoard				= true;
	public boolean				usePlayerSlot				= true;
	public boolean				showPlayerHealth			= true;
	public boolean				showListLevels				= true;

	public boolean				allowRaceSwap				= false;
	public double				percentXpKeepRaceChange		= 10.00D;
	public double				percentXpKeepClassChange	= 10.00D;
	public double				percentXpLossRespawn		= 10.00D;
	public int					max_level					= 150;
	public boolean				hardLevelCap				= false;
	public int					mastery_level				= 0;

	public int					max_xp						= 58245;
	public boolean				XPnotify					= true;
	public double				scaleXP						= 100.00D;

	public boolean				useSkillTestForCombat		= true;
	public boolean				verboseCombat				= true;
	public boolean				hideHitMessage				= false;
	public boolean				useSizeForCombat			= true;
	public int					rangedHitBonus				= 10;
	public int					healthScale					= 40;

	public int					karmaDamagePlayer			= -10;
	public int					karmaDamageVillager			= -5;
	public int					karmaDamagePet				= -2;
	public int					karmaDamageAnimal			= -1;
	public int					karmaDamageMonster			= 2;
	public int					karmaDamageSlime			= 1;

	public int					karmaKillPlayer				= -1000;
	public int					karmaKillVillager			= -500;
	public int					karmaKillPet				= -200;
	public int					karmaKillAnimal				= -50;
	public int					karmaKillMonster			= 200;
	public int					karmaKillSlime				= 100;
	public int					karmaScale					= 500;

	public double				skillBuildupMoveAllowed		= 2.0D;
	public long					skillTickInterval			= 10L;

	public double				scaleFallDamagePlayers		= 100.0D;
	public double				scaleFallDamageMobs			= 100.0D;

	public boolean				manageHealthNonLqWorlds		= true;

	public boolean				chatUsePrefix				= true;
	public boolean				chatProcessPrefix			= true;
	public String				chatPrefix					= "[{race}|{class} L:{lvl}] {current}";

	public boolean				attributesModifyBaseStats	= false;
	public boolean				disableStats				= false;
	public boolean				verboseStats				= true;
	public boolean				itemsAllMeansAll			= false;

	public int					ecoRaceSwap					= 100000;
	public int					ecoClassSwap				= 10000;
	public int					ecoPartyTeleport			= 100;

	public int					partyRange					= 32;
	public boolean				useParties					= true;
	public int					partyBonus					= 50;
	public boolean				blockPartyPvP				= true;
	public boolean				allowPartyTeleport			= true;

	public int					heightBonus					= 2;

	public String				hitchance					= "AVERAGE";
	public String				dodgechance					= "AVERAGE";
	public String				blockchance					= "TOUGH";

	public Difficulty			hitchanceenum				= Difficulty.AVERAGE;
	public Difficulty			dodgechanceenum				= Difficulty.AVERAGE;
	public Difficulty			blockchanceenum				= Difficulty.TOUGH;

	public boolean				givePermForLevels			= true;
	public boolean				givePermForAttributes		= false;
	public boolean				givePermForKarma			= false;

	public boolean				blockRepairXPloss			= true;
	public double				adjustRepairXP				= 50.0D;
	public boolean				useAlternateRepairExpCost	= false;

	public int					manaCostPerRepairLevel		= 10;
	public int					ecoCostPerRepairLevel		= 1000;
	public String				materialRepairCost			= "LAPIS_BLOCK";
	public int					materialRepairQtyPerLevel	= 1;

	public boolean				manaTickEvent				= true;
	public boolean				manaRegen					= true;
	public int					noticeInterval				= 5;
	public int					manaTickInterval			= 20;
	
	@SuppressWarnings("unchecked")
	public MainConfig(final Main p) {
		super(p, "config.yml");

		this.debugMode = this.getConfigItem("debugMode", this.debugMode);

		this.useMySQL = this.getConfigItem("useMySQL", this.useMySQL);
		this.sqlUsername = this.getConfigItem("sqlUsername", this.sqlUsername);
		this.sqlPassword = this.getConfigItem("sqlPassword", this.sqlPassword);
		this.sqlHostname = this.getConfigItem("sqlHostname", this.sqlHostname);
		this.sqlPort = this.getConfigItem("sqlPort", this.sqlPort);
		this.sqlDatabase = this.getConfigItem("sqlDatabase", this.sqlDatabase);
		this.logSQL = this.getConfigItem("logSQL", this.logSQL);
		this.logLevel = this.getConfigItem("logLevel", this.logLevel);
		this.useServerLogOnly = this.getConfigItem("useServerLogOnly", this.useServerLogOnly);
		this.sqlPrefix = this.getConfigItem("sqlPrefix", this.sqlPrefix);
		this.sqlKeepAliveInterval = this.getConfigItem("sqlKeepAliveInterval", this.sqlKeepAliveInterval);

		this.backupSQlite = this.getConfigItem("backupSQlite", this.backupSQlite);
		this.SQLiteSaves = this.getConfigItem("SQLiteSaves", this.SQLiteSaves);
		this.backupLQlog = this.getConfigItem("backupLQlog", this.backupLQlog);
		this.LQLogSaves = this.getConfigItem("LQLogSaves", this.LQLogSaves);
		
		this.worlds = (ArrayList<String>) this.getConfigItem("worlds", this.worlds);
		this.manageHealthNonLqWorlds = this.getConfigItem("manageHealthNonLqWorlds", this.manageHealthNonLqWorlds);

		this.scaleFallDamagePlayers = this.getConfigItem("scaleFallDamagePlayers", this.scaleFallDamagePlayers);
		this.scaleFallDamageMobs = this.getConfigItem("scaleFallDamageMobs", this.scaleFallDamageMobs);

		this.nameBlacklist = (ArrayList<String>) this.getConfigItem("nameBlacklist", this.nameBlacklist);
		this.nameBlacklistParts = (ArrayList<String>) this.getConfigItem("nameBlacklistParts", this.nameBlacklistParts);
		this.broadcastRename = this.getConfigItem("broadcastRename", this.broadcastRename);

		this.useScoreBoard = this.getConfigItem("useScoreBoard", this.useScoreBoard);
		this.usePlayerSlot = this.getConfigItem("usePlayerSlot", this.usePlayerSlot);
		this.showPlayerHealth = this.getConfigItem("showPlayerHealth", this.showPlayerHealth);
		this.showListLevels = this.getConfigItem("showListLevels", this.showListLevels);

		this.randomStats = this.getConfigItem("randomStats", this.randomStats);
		this.percentXpKeepClassChange = this.getConfigItem("percentXpKeepClassChange", this.percentXpKeepClassChange);
		this.percentXpKeepRaceChange = this.getConfigItem("percentXpKeepRaceChange", this.percentXpKeepRaceChange);

		this.percentXpLossRespawn = this.getConfigItem("percentXpLossRespawn", this.percentXpLossRespawn);
		this.max_level = this.getConfigItem("max_level", this.max_level);
		this.hardLevelCap = this.getConfigItem("hardLevelCap", this.hardLevelCap);
		this.mastery_level = this.getConfigItem("mastery_level", this.mastery_level);

		if (this.mastery_level > 0) {
			this.max_xp = SetExp.getExpToLevel(this.mastery_level);
		} else if (max_level > 0) {
			this.max_xp = SetExp.getExpToLevel(this.max_level);
		} else {
			this.max_xp = Integer.MAX_VALUE; // never master!
		}
		this.XPnotify = this.getConfigItem("XPnotify", this.XPnotify);
		this.scaleXP = this.getConfigItem("scaleXP", this.scaleXP);

		this.allowRaceSwap = this.getConfigItem("allowRaceSwap", this.allowRaceSwap);

		this.useSkillTestForCombat = this.getConfigItem("useSkillTestForCombat", this.useSkillTestForCombat);
		this.useSizeForCombat = this.getConfigItem("useSizeForCombat", this.useSizeForCombat);
		this.verboseCombat = this.getConfigItem("verboseCombat", this.verboseCombat);
		this.hideHitMessage = this.getConfigItem("hideHitMessage", this.hideHitMessage);

		this.rangedHitBonus = this.getConfigItem("rangedHitBonus", this.rangedHitBonus);
		this.heightBonus = this.getConfigItem("heightBonus", this.heightBonus);
		this.hitchance = this.getConfigItem("hitchance", this.hitchance);
		this.dodgechance = this.getConfigItem("dodgechance", this.dodgechance);
		this.blockchance = this.getConfigItem("blockchance", this.blockchance);
		this.healthScale = this.getConfigItem("healthScale", this.healthScale);

		this.hitchanceenum = Difficulty.valueOf(hitchance);
		this.dodgechanceenum = Difficulty.valueOf(dodgechance);
		this.blockchanceenum = Difficulty.valueOf(blockchance);

		this.partyRange = this.getConfigItem("partyRange", this.partyRange);
		this.useParties = this.getConfigItem("useParties", this.useParties);
		this.partyBonus = this.getConfigItem("partyBonus", this.partyBonus);
		this.blockPartyPvP = this.getConfigItem("blockPartyPvP", this.blockPartyPvP);

		this.karmaDamagePlayer = this.getConfigItem("karmaDamagePlayer", this.karmaDamagePlayer);
		this.karmaDamageVillager = this.getConfigItem("karmaDamageVillager", this.karmaDamageVillager);
		this.karmaDamagePet = this.getConfigItem("karmaDamagePet", this.karmaDamagePet);
		this.karmaDamageAnimal = this.getConfigItem("karmaDamageAnimal", this.karmaDamageAnimal);
		this.karmaDamageMonster = this.getConfigItem("karmaDamageMonster", this.karmaDamageMonster);
		this.karmaDamageSlime = this.getConfigItem("karmaDamageSlime", this.karmaDamageSlime);

		this.karmaKillPlayer = this.getConfigItem("karmaKillPlayer", this.karmaKillPlayer);
		this.karmaKillVillager = this.getConfigItem("karmaKillVillager", this.karmaKillVillager);
		this.karmaKillPet = this.getConfigItem("karmaKillPet", this.karmaKillPet);
		this.karmaKillAnimal = this.getConfigItem("karmaKillAnimal", this.karmaKillAnimal);
		this.karmaKillMonster = this.getConfigItem("karmaKillMonster", this.karmaKillMonster);
		this.karmaKillSlime = this.getConfigItem("karmaKilleSlime", this.karmaKillSlime);

		this.karmaScale = this.getConfigItem("karmaScale", this.karmaScale);

		this.skillBuildupMoveAllowed = this.getConfigItem("skillBuildupMoveAllowed", this.skillBuildupMoveAllowed);
		this.skillTickInterval = (long) this.getConfigItem("skillTickInterval", this.skillTickInterval);

		this.chatUsePrefix = this.getConfigItem("chatUsePrefix", this.chatUsePrefix);
		this.chatProcessPrefix = this.getConfigItem("chatProcessPrefix", this.chatProcessPrefix);
		this.chatPrefix = this.getConfigItem("chatPrefix", this.chatPrefix);

		this.attributesModifyBaseStats = this.getConfigItem("attributesModifyBaseStats", this.attributesModifyBaseStats);
		this.disableStats = this.getConfigItem("disableStats", this.disableStats);
		this.verboseStats = this.getConfigItem("verboseStats", this.verboseStats);
		this.itemsAllMeansAll = this.getConfigItem("itemsAllMeansAll", this.itemsAllMeansAll);

		this.ecoClassSwap = this.getConfigItem("ecoClassSwap", this.ecoClassSwap);
		this.ecoRaceSwap = this.getConfigItem("ecoRaceSwap", this.ecoRaceSwap);

		this.givePermForLevels = this.getConfigItem("givePermForLevels", this.givePermForLevels);
		this.givePermForAttributes = this.getConfigItem("givePermForAttributes", this.givePermForAttributes);
		this.givePermForKarma = this.getConfigItem("givePermForKarma", this.givePermForKarma);

		this.blockRepairXPloss = this.getConfigItem("blockRepairXPloss", this.blockRepairXPloss);
		this.adjustRepairXP = this.getConfigItem("adjustRepairXP", this.adjustRepairXP);
		this.useAlternateRepairExpCost = this.getConfigItem("useAlternateRepairExpCost", this.useAlternateRepairExpCost);

		this.manaCostPerRepairLevel = this.getConfigItem("manaCostPerRepairLevel", this.manaCostPerRepairLevel);
		this.ecoCostPerRepairLevel = this.getConfigItem("ecoCostPerRepairLevel", this.ecoCostPerRepairLevel);
		this.materialRepairCost = this.getConfigItem("materialRepairCost", this.materialRepairCost);
		this.materialRepairQtyPerLevel = this.getConfigItem("materialRepairQtyPerLevel", this.materialRepairQtyPerLevel);

		this.manaRegen = this.getConfigItem("manaRegen", this.manaRegen);
		this.manaTickEvent = this.getConfigItem("manaTickEvent", this.manaTickEvent);
		this.noticeInterval = this.getConfigItem("noticeInterval", this.noticeInterval);
		this.manaTickInterval = this.getConfigItem("manaTickInterval", this.manaTickInterval);

		
	}
}
