package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class MainConfig extends Config {

    public boolean debugMode = false;

    public boolean useMySQL = false;
    public String sqlUsername = "username";
    public String sqlPassword = "password";
    public String sqlHostname = "localhost";
    public int sqlPort = 3306;
    public String sqlDatabase = "legendquest";

    public boolean randomStats = true;
    public double percentXpKeepClassChange = 10.00D;
    public double percentXpLossRespawn = 10.00D;

    public boolean useSkillTestForCombat = true;

    public boolean verboseCombat = true;

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
        
    }
}
