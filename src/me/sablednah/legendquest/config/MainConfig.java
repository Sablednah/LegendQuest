package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class MainConfig extends Config {

	public boolean	debugMode					= false;

	public boolean	useMySQL					= false;
	public String	sqlUsername					= "username";
	public String	sqlPassword					= "password";
	public String	sqlHostname					= "localhost";
	public int		sqlPort						= 3306;
	public String	sqlDatabase					= "legendquest";

	public boolean	randomStats					= true;
	public double	percentXpKeepClassChange	= 10.00D;
	public double	percentXpLossRespawn		= 10.00D;

	public boolean	useSkillTestForCombat		= true;

	public boolean	verboseCombat				= true;

	public int		karmaDamagePlayer			= -10;
	public int		karmaDamageVillager			= -5;
	public int		karmaDamagePet				= -2;
	public int		karmaDamageAnimal			= -1;
	public int		karmaDamageMonster			= 2;
	public int		karmaDamageSlime			= 1;

	public int		karmaKillPlayer				= -1000;
	public int		karmaKillVillager			= -500;
	public int		karmaKillPet				= -200;
	public int		karmaKillAnimal				= -50;
	public int		karmaKillMonster			= 200;
	public int		karmaKillSlime				= 100;

	public int		karmaScale					= 500;

	public double	skillBuildupMoveAllowed		= 2.0D;

	public long		skillTickInterval			= 10L;

	public MainConfig(final Main p) {
		super(p, "config.yml");

		this.debugMode = this.getConfigItem("debugMode", this.debugMode);

		this.useMySQL = this.getConfigItem("useMySQL", this.useMySQL);
		this.sqlUsername = this.getConfigItem("sqlUsername", this.sqlUsername);
		this.sqlPassword = this.getConfigItem("sqlPassword", this.sqlPassword);
		this.sqlHostname = this.getConfigItem("sqlHostname", this.sqlHostname);
		this.sqlPort = this.getConfigItem("sqlPort", this.sqlPort);
		this.sqlDatabase = this.getConfigItem("sqlDatabase", this.sqlDatabase);

		this.randomStats = this.getConfigItem("randomStats", this.randomStats);
		this.percentXpKeepClassChange = this.getConfigItem("percentXpKeepClassChange", this.percentXpKeepClassChange);
		this.percentXpLossRespawn = this.getConfigItem("percentXpLossRespawn", this.percentXpLossRespawn);

		this.useSkillTestForCombat = this.getConfigItem("useSkillTestForCombat", this.useSkillTestForCombat);
		this.verboseCombat = this.getConfigItem("verboseCombat", this.verboseCombat);

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
		
		
	}
}
