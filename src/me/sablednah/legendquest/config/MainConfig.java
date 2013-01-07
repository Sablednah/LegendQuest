package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class MainConfig extends Config {
	public boolean	useMySQL	= false;
	public String	sqlUsername	= "username";
	public String	sqlPassword	= "password";

	public MainConfig(Main p) {
		super(p, "config.yml");

		this.useMySQL = this.getConfigItem("useMySQL", this.useMySQL);
		this.sqlUsername = this.getConfigItem("sqlUsername", this.sqlUsername);
		this.sqlPassword = this.getConfigItem("sqlPassword", this.sqlPassword);
		
	}
}
