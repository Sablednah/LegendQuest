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

	public MainConfig(Main p) {
		super(p, "config.yml");

		this.debugMode = this.getConfigItem("debugMode", this.debugMode);
		this.useMySQL = this.getConfigItem("useMySQL", this.useMySQL);
		this.sqlUsername = this.getConfigItem("sqlUsername", this.sqlUsername);
		this.sqlPassword = this.getConfigItem("sqlPassword", this.sqlPassword);
		this.sqlHostname = this.getConfigItem("sqlHostname", this.sqlHostname);
		this.sqlPort = this.getConfigItem("sqlPort", this.sqlPort);
		this.sqlDatabase = this.getConfigItem("sqlDatabase", this.sqlDatabase);

//		this.config.options().copyDefaults(true);
//		saveConfig();
	}
}
