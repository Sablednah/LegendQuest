package me.sablednah.legendquest.skills;

public class SkillDefinition {
    
    private final Class<?> clazz;
    private final SkillLoader source;
    private SkillInfo info;
    
    public SkillDefinition(final Class<?> clazz, final SkillLoader source, final SkillInfo info)
    {
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
}