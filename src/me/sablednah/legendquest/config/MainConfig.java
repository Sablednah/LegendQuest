package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class MainConfig extends Config {
	public boolean	debugMode	= false;
	public boolean	useMySQL	= false;
	public String	sqlUsername	= "username";
	public String	sqlPassword	= "password";
	public String	sqlHostname	= "localhost";
	public int		sqlPort		= 3306;
	public String	sqlDatabase	= "legendquest";
	public boolean	randomStats	= true;
	public String	invalidCommand = "Sorry couldn't recognise command: ";
	//TODO add REAL help info...
	public String	helpCommand = "indepth help here";
	public String	invalidPlayerCommand = "Command only valid from ingame player.";
	public String	invalidArgumentsCommand = "Invalid arguments.";
	public String	characterNotFound = "Character not found for: ";

	public MainConfig(Main p) {
		super(p, "config.yml");

		this.debugMode = this.getConfigItem("debugMode", this.debugMode);
		this.useMySQL = this.getConfigItem("useMySQL", this.useMySQL);
		this.sqlUsername = this.getConfigItem("sqlUsername", this.sqlUsername);
		this.sqlPassword = this.getConfigItem("sqlPassword", this.sqlPassword);
		this.sqlHostname = this.getConfigItem("sqlHostname", this.sqlHostname);
		this.sqlPort = this.getConfigItem("sqlPort", this.sqlPort);
		this.sqlDatabase = this.getConfigItem("sqlDatabase", this.sqlDatabase);
		this.randomStats = this.getConfigItem("randomStats", this.randomStats);
		
		this.helpCommand = this.getConfigItem("helpCommand", this.helpCommand);
		this.invalidCommand = this.getConfigItem("invalidCommand", this.invalidCommand);
		this.invalidArgumentsCommand = this.getConfigItem("invalidArgumentsCommand", this.invalidArgumentsCommand);
		this.characterNotFound = this.getConfigItem("characterNotFound", this.characterNotFound);

//		this.config.options().copyDefaults(true);
//		saveConfig();
	}
}
