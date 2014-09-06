package me.sablednah.legendquest.skills;

import org.bukkit.entity.Player;

@SkillManifest(
		name = "Brawler", 
		type = SkillType.TRIGGERED, 
		author = "sablednah", 
		version = 1.0D, 
		description = "Attacks while barehanded have a chance to deal extra damage.",  
		buildup = 0, 
		consumes = "", 
		cooldown = 0, 
		delay = 0, 
		duration = 0, 
		levelRequired = 0, 
		manaCost = 0, 
		skillPoints = 0,
		dblvarnames = { "chance" }, dblvarvalues = { 75.5 }, 
		intvarnames = { "dammage" }, intvarvalues = { 5 }, 
		strvarnames = { "" }, strvarvalues = { "" }
	)
public class PluginSkill extends Skill {
    
    public String command;
    public String commandPermission;
    
/*
     public PluginSkill(final ConfigurationSection skillInfo) {
 
        super(skillInfo);
        this.command = skillInfo.getString("command");
        this.commandPermission = skillInfo.getString("perm");
    }
*/
    
    @Override
    public boolean onEnable() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public CommandResult onCommand(Player player) {
        // TODO Auto-generated method stub
        return CommandResult.NOTAVAILABLE;
    }

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
    
}

/* cooldown: 1200
 * duration: 1200
 * delay:
 * regent: 288
 * perm: essentials.fly
 * command: fly on
 * cost:
 * level: */