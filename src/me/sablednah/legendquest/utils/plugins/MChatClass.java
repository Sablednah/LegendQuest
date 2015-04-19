package me.sablednah.legendquest.utils.plugins;

import ca.q0r.mchat.variables.Var;
import ca.q0r.mchat.variables.VariableManager;
import ca.q0r.mchat.yml.locale.LocaleType;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.party.Party;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class MChatClass {
	
    public static void addVars() {
        VariableManager.addVars(new Var[]{new KarmaVar(), new RaceVar(), new ClassVar(), new ExpVar(), new HealthVar(),
                new HealthBarVar(), new LevelVar(), new ManaVar(), new ManaBarVar(), new MasteredVar(),
                new PartyVar(), new SecClassVar()});
    }

    private static class KarmaVar extends Var {
        @Keys(keys = {"LQKarma", "LQK"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {
                return pc.karmaName();
            }
            return "";
        }
    }

    
    private static class RaceVar extends Var {
        @Keys(keys = {"LQRace", "LQR"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {
                return pc.race.name;
            }
            return "";
        }
    }

    private static class ClassVar extends Var {
        @Keys(keys = {"LQClass", "LQC"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {
                return pc.mainClass.name;
            }
            return "";
        }
    }

    private static class ExpVar extends Var {
        @Keys(keys = {"LQExp", "LQEx"})
        public String getValue(UUID uuid) {
        	Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
            	return Integer.toString(player.getTotalExperience());
            }
            return "";
        }
    }


    private static class HealthVar extends Var {
        @Keys(keys = {"LQHealth", "LQHP" ,"LQhp"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            DecimalFormat df = new DecimalFormat("#.0");
            if (pc != null) {    			
                return df.format(pc.getHealth());
            } else {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    return df.format(player.getHealth());
                }            	
            }
            return "";
        }
    }

    private static class HealthBarVar extends Var {
        @Keys(keys = {"LQHBar", "LQHB"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {    			
                return Utils.barGraph(pc.getHealth(), pc.getMaxHealth(), 20, lq.configLang.statHealth, "");
            } else {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    return Utils.barGraph(player.getHealth(), player.getMaxHealth(), 20, lq.configLang.statHealth, "");
                }            	
            }
            return "";
        }
    }

    private static class LevelVar extends Var {
        @Keys(keys = {"LQLevel", "LQL", "LQLvl"})
        public String getValue(UUID uuid) {
        	Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
            	return Integer.toString(player.getLevel());
            }
            return "";
        }
    }

    private static class ManaVar extends Var {
        @Keys(keys = {"LQMana", "LQMn", "LQMP"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            DecimalFormat df = new DecimalFormat("#");
            if (pc != null) {    			
                return df.format(pc.mana);
            }
            return "";
        }
    }

    private static class ManaBarVar extends Var {
        @Keys(keys = {"LQMBar", "LQMb"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {    			
                return Utils.barGraph(pc.mana, pc.getMaxMana(), 20, lq.configLang.statMana, "");
            }            
            return "";
        }
    }

    private static class MasteredVar extends Var {
        @Keys(keys = {"LQMastered", "LQMa"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
        	Player player = Bukkit.getPlayer(uuid);
        	PC pc = lq.getPlayers().getPC(uuid);
            if (player != null) {
            	return (pc.hasMastered(pc.mainClass.name)) ? LocaleType.MESSAGE_HEROES_TRUE.getVal() : LocaleType.MESSAGE_HEROES_FALSE.getVal();
            }
            return "";
        }
    }

    private static class PartyVar extends Var {
        @Keys(keys = {"LQParty", "LQPa", "LQPty"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            Party p = lq.partyManager.getParty(uuid);
            if (p != null) {
                return p.partyName;
            }

            return "";
        }
    }

    private static class SecClassVar extends Var {
        @Keys(keys = {"LQSubClass", "LQSecClass", "LQSC"})
        public String getValue(UUID uuid) {
        	Main lq = (Main)Bukkit.getPluginManager().getPlugin("LegendQuest");        	
            PC pc = lq.getPlayers().getPC(uuid);
            if (pc != null) {
            	if (pc.subClass!=null)
                return pc.subClass.name;
            }
            return "";
        }
    }

}