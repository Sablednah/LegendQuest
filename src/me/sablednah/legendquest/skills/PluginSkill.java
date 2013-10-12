package me.sablednah.legendquest.skills;



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
        return false;
    }

    @Override
    public void loadOptions() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CommandResult onCommand() {
        // TODO Auto-generated method stub
        return CommandResult.NOTAVAILABLE;
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