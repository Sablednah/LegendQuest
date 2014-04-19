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
    
    public int karmaDamagePlayer = -100;
    public int karmaDamageVillager = -50;
    public int karmaDamagePet = -20;
    public int karmaDamageAnimal = -5;
    public int karmaDamageMonster = 15;
    public int karmaDamageSlime = 10;
    
    public int karmaKillPlayer = -10000;
    public int karmaKillVillager = -5000;
    public int karmaKillPet = -2000;
    public int karmaKillAnimal = -500;
    public int karmaKillMonster = 2000;
    public int karmaKillSlime = 1000;
    
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
    }
}
