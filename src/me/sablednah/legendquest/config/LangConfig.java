package me.sablednah.legendquest.config;

import me.sablednah.legendquest.Main;

public class LangConfig extends Config {
	public String	startup					= "The sky above the port was the color of television, tuned to a dead channel.";
	public String	shutdown				= "=== END OF LINE ===";

	public String	raceScan				= "Scanning for race files in folder: ";
	public String	raceScanFound			= "Found race: ";
	public String	raceScanEnd				= "Scanning for race files completed.";
	public String	raceScanInvalid			= "Invalid race file: ";

	public String	classScan				= "Scanning for class files in folder: ";
	public String	classScanFound			= "Found class: ";
	public String	classScanEnd			= "Scanning for class files completed.";
	public String	classScanInvalid		= "Invalid class file: ";
	public String	classScanRaceWarning	= "Warning Found unknon Race: ";
	public String	classScanNoRaceOrGroup	= "No Valid race or group found in: ";
	public String	classScanGroupWarning	= "No Valid race or group found in: ";

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

	}
}
