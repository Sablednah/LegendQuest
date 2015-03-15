package me.sablednah.legendquest.skills;

public class SkillDefinition {

	private final Class<?>		clazz;
	private final SkillLoader	source;
	private SkillInfo			info;

	public SkillDefinition(final Class<?> clazz, final SkillLoader source, final SkillInfo info) {
		this.clazz = clazz;
		this.source = source;
		this.info = info;
	}

	public Class<?> getSkillClass() {
		return this.clazz;
	}

	public SkillInfo getSkillInfo() {
		return this.info;
	}

	public void setSkillInfo(SkillInfo si) {
		this.info = si;
	}

	public SkillLoader getSource() {
		return this.source;
	}

	public SkillInfo getSkillInfoClone() {
		SkillInfo s = new SkillInfo(info.getAuthor(), info.getName(), 
				info.getDescription(), info.getType(), info.getVersion(), 
				info.getBuildup(), info.getDelay(), info.getDuration(), info.getCooldown(), 
				info.getPay(), info.getXp(), info.getManaCost(), info.getConsumes(), info.getLevelRequired(), info.getSkillPoints(),
				info.getVars(), info.getKarmaCost(), info.getKarmaReward(), info.getKarmaRequired() );
		return s;
	}

}