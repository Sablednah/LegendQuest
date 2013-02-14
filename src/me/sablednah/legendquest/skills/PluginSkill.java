package me.sablednah.legendquest.skills;

import org.bukkit.configuration.ConfigurationSection;

public class PluginSkill extends Skill {

    public String command;
    public String commandPermission;

    public PluginSkill(final ConfigurationSection skillInfo) {
        super(skillInfo);
        command = skillInfo.getString("command");
        commandPermission = skillInfo.getString("perm");
    }

}

/*cooldown: 1200
 * duration: 1200
 * delay:
 * regent: 288
 * perm: essentials.fly
 * command: fly on
 * cost:
 * level: */