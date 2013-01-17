package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class LangConfig extends Config {
	public String	startup					= "The sky above the port was the color of television, tuned to a dead channel.";
	public String	shutdown				= "=== END OF LINE ===";

	public String	raceScan				= "Scanning for race files in folder: ";
	public String	raceScanFound			= "Found race: ";
	public String	raceScanEnd				= "Scanning for race files completed.";
	public String	raceScanInvalid			= "Invalid race: ";

	public String	classScan				= "Scanning for class files in folder: ";
	public String	classScanFound			= "Found class: ";
	public String	classScanEnd			= "Scanning for class files completed.";
	public String	classScanInvalid		= "Invalid class: ";
	public String	classScanRaceWarning	= "Warning Found unknon Race: ";
	public String	classScanNoRaceOrGroup	= "No Valid race or group found in: ";
	public String	classScanGroupWarning	= "No Valid group found in: ";

	public String	raceNoDefault			= "Sorry no default Race found - using first available.";
	public String	classNoDefault			= "Sorry no default Class found - using first available.";
	public String	youAreCurrently			= "You are currently ";
	public String	raceChanged				= "Your race has been changed to ";
	public String	raceChangeNotAllowed	= "Sorry you have already slected a race. ";
	public String	raceList				= "-------- Races --------";

	public String	classSelectRaceFirst	= "Please Select Race before class.";
	public String	classChanged			= "You have changed your class to: ";
	public String	classList				= "------- Classes -------";

	public LangConfig(Main p) {
		super(p, "lang.yml");

		this.startup = this.getConfigItem("startup", this.startup);
		this.shutdown = this.getConfigItem("shutdown", this.shutdown);

		this.raceScan = this.getConfigItem("raceScan", this.raceScan);
		this.raceScanFound = this.getConfigItem("raceScanFound", this.raceScanFound);
		this.raceScanEnd = this.getConfigItem("raceScanEnd", this.raceScanEnd);
		this.raceScanInvalid = this.getConfigItem("raceScanInvalid", this.raceScanInvalid);

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

		// this.config.options().copyDefaults(true);
		// saveConfig();

	}
}
