package me.sablednah.legendquest.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.sablednah.legendquest.Main;
import me.sablednah.legendquest.playercharacters.PC;
import me.sablednah.legendquest.races.Race;
import me.sablednah.legendquest.skills.SkillDefinition;
import me.sablednah.legendquest.skills.SkillInfo;
import me.sablednah.legendquest.utils.Pair;
import me.sablednah.legendquest.utils.Utils;
import me.sablednah.legendquest.utils.WeightedProbMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Classes {

    public Main lq;
    private final Map<String, ClassType> classTypes = new HashMap<String, ClassType>();
    private final ArrayList<Pair<Integer, String>> classprobability = new ArrayList<Pair<Integer, String>>();
    public WeightedProbMap<String> wpmClasses;

    public ClassType defaultClass;

    @SuppressWarnings({ "unchecked", "deprecation" })
    public Classes(final Main p) {
        this.lq = p;

        // Find the Classes folder
        final File classDir = new File(lq.getDataFolder() + File.separator + "classes");
        // notify sanning begun
        lq.log(lq.configLang.classScan + ": " + classDir);
        
        // make it if not found
        if (!classDir.exists()) {
            lq.debug.info(classDir + " not found, installing defaults.");
            classDir.mkdir();
            try {
                if (lq.configMain.debugMode) { lq.debug.info("looking for classes zip"); }
                    
                lq.saveResource("classes.zip",true);
                File zf = new File(lq.getDataFolder() + File.separator + "classes.zip");
                ZipFile zip = new ZipFile(zf);
                
                Enumeration<? extends ZipEntry> entries = zip.entries();
                
                ZipEntry entry;
                while(entries.hasMoreElements()){
                    entry = entries.nextElement();
                    if (lq.configMain.debugMode) { lq.debug.info("Extracting "+entry.getName()); }
                    Utils.extractFile(zip.getInputStream(entry), new FileOutputStream(classDir.getPath() + File.separator + entry.getName()));
                }
                 zip.close();
                 File nf = new File(lq.getDataFolder() + File.separator + "classes.zip");
                 nf.delete();
                 
            } catch (IOException e) {
                lq.debug.info("Could not extract defaults from classes.zip");
                e.printStackTrace();
            }
        }

        final File[] classfiles = classDir.listFiles();

        for (final File classfile : classfiles) {
            // only want .yml configs here baby
            if (classfile.isFile() && classfile.getName().toLowerCase().endsWith(".yml")) {
                // found a config file - load it in time

                lq.log(lq.configLang.classScanFound + classfile.getName());
                final ClassType c = new ClassType();
                Boolean validConfig = true;
                YamlConfiguration thisConfig = null;

                // begin parsing config file
                try {
                    thisConfig = YamlConfiguration.loadConfiguration(classfile);

                    c.filename = classfile.getName();
                    c.name = thisConfig.getString("name");

                    // c.size = (float) thisConfig.getDouble("size", 1.0);
                    // c.speed = thisConfig.getDouble("speed", 1.0);
                    // c.xp = thisConfig.getInt("xp", 0);
                    /* @SuppressWarnings("unchecked") List<String> effects = (List<String>)
                     * thisConfig.getList("effects"); if (effects != null) { for (String thisEffect : effects) { //
                     * ZombieMod.logger.info("["+ZombieMod.myName+"] Effect: " + thisEffect); Effect x =
                     * Effect.valueOf(thisEffect); // ZombieMod.logger.info("["+ZombieMod.myName+"] x: " + x);
                     * c.effects.add(x); } } */
                    /* @SuppressWarnings("unchecked") List<String> abilities = (List<String>)
                     * thisConfig.getList("abilities"); c.abilities = abilities; */
                    c.frequency = thisConfig.getInt("frequency");

                    final List<String> allowedRaces = (List<String>) thisConfig.getList("allowedRaces");
                    for (int i = 0; i < allowedRaces.size(); i++) {
                        allowedRaces.set(i, allowedRaces.get(i).toLowerCase());
                    }
                    c.allowedRaces = allowedRaces;

                    final List<String> allowedGroups = (List<String>) thisConfig.getList("allowedGroups");
                    if (allowedGroups != null) {
                        for (int i = 0; i < allowedGroups.size(); i++) {
                            allowedGroups.set(i, allowedGroups.get(i).toLowerCase());
                        }
                    }
                    c.allowedGroups = allowedGroups;

                    final List<String> requiresOne = (List<String>) thisConfig.getList("requiresOne");
                    if (requiresOne != null) {
                        for (int i = 0; i < requiresOne.size(); i++) {
                            requiresOne.set(i, requiresOne.get(i).toLowerCase());
                        }
                    }
                    c.requiresOne = requiresOne;

                    final List<String> requires = (List<String>) thisConfig.getList("requires");
                    if (requires != null) {
                        for (int i = 0; i < requires.size(); i++) {
                            requires.set(i, requires.get(i).toLowerCase());
                        }
                    }
                    c.requires = requires;

                    c.defaultClass = thisConfig.getBoolean("default");
                    c.statStr = thisConfig.getInt("statmods.str");
                    c.statDex = thisConfig.getInt("statmods.dex");
                    c.statInt = thisConfig.getInt("statmods.int");
                    c.statWis = thisConfig.getInt("statmods.wis");
                    c.statCon = thisConfig.getInt("statmods.con");
                    c.statChr = thisConfig.getInt("statmods.chr");
                    c.healthPerLevel = thisConfig.getDouble("healthperlevel");

                    c.stopCrafting = thisConfig.getBoolean("stopCrafting");
                    c.stopSmelting = thisConfig.getBoolean("stopSmelting");
                    c.stopBrewing = thisConfig.getBoolean("stopBrewing");
                    c.stopEnchating = thisConfig.getBoolean("stopEnchating");
                    c.stopRepairing = thisConfig.getBoolean("stopRepairing");
                    
                    c.manaPerLevel = thisConfig.getDouble("manaPerLevel");
                    c.manaBonus = thisConfig.getInt("manaBonus");
                    c.manaPerSecond = thisConfig.getInt("manaPerSecond");

                    c.skillPointsPerLevel = thisConfig.getDouble("skillPointsPerLevel");
                    c.skillPoints = thisConfig.getInt("skillPoints");

                    c.perm = thisConfig.getString("perm");

                    // allowed lists

                    List<String> stringList;
                    List<Material> materiallist;
                    String keyName;
                    stringList = (List<String>) thisConfig.getList("allowedTools");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "tools";
                            } else if (keyName.equalsIgnoreCase("iron") || keyName.equalsIgnoreCase("gold") || keyName.equalsIgnoreCase("wood")
                                    || keyName.equalsIgnoreCase("stone") || keyName.equalsIgnoreCase("diamond")) {
                                keyName = keyName + "tools";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Allowed tool '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.allowedTools = materiallist;

                    stringList = (List<String>) thisConfig.getList("allowedArmour");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "armour";
                            } else if (keyName.equalsIgnoreCase("iron") || keyName.equalsIgnoreCase("gold") || keyName.equalsIgnoreCase("chain")
                                    || keyName.equalsIgnoreCase("leather") || keyName.equalsIgnoreCase("diamond")) {
                                keyName = keyName + "armour";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Allowed Armour '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.allowedArmour = materiallist;

                    stringList = (List<String>) thisConfig.getList("allowedWeapons");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "weapons";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Allowed Weapons '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.allowedWeapons = materiallist;

                    // build disallowed lists

                    stringList = (List<String>) thisConfig.getList("dissallowedTools");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "tools";
                            } else if (keyName.equalsIgnoreCase("iron") || keyName.equalsIgnoreCase("gold") || keyName.equalsIgnoreCase("wood")
                                    || keyName.equalsIgnoreCase("stone") || keyName.equalsIgnoreCase("diamond")) {
                                keyName = keyName + "tools";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Dissallowed tool '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.dissallowedTools = materiallist;

                    stringList = (List<String>) thisConfig.getList("dissallowedArmour");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "armour";
                            } else if (keyName.equalsIgnoreCase("iron") || keyName.equalsIgnoreCase("gold") || keyName.equalsIgnoreCase("chain")
                                    || keyName.equalsIgnoreCase("leather") || keyName.equalsIgnoreCase("diamond")) {
                                keyName = keyName + "armour";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Dissallowed Armour '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.dissallowedArmour = materiallist;

                    stringList = (List<String>) thisConfig.getList("dissallowedWeapons");
                    materiallist = new ArrayList<Material>();
                    if (stringList != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            keyName = stringList.get(i).toLowerCase();
                            if (keyName.equalsIgnoreCase("all") || keyName.equalsIgnoreCase("any")) {
                                keyName = "weapons";
                            }
                            if (!keyName.equalsIgnoreCase("none")) {
                                if (lq.configData.dataSets.containsKey(keyName)) {
                                    materiallist.addAll(lq.configData.dataSets.get(keyName));
                                } else if (Material.matchMaterial(keyName) != null) {
                                    materiallist.add(Material.matchMaterial(keyName));
                                } else if (Utils.isParsableToInt(keyName)) {
                                    materiallist.add(Material.getMaterial(Integer.parseInt(keyName)));
                                } else {
                                    lq.debug.error("Dissallowed Weapons '" + keyName + "' in " + c.filename + " not understood");
                                }
                            }
                        }
                    }
                    c.dissallowedWeapons = materiallist;

                    // skills
                    c.availableSkills = new ArrayList<SkillDefinition>();
                    final ConfigurationSection inateSkills = thisConfig.getConfigurationSection("skills");
                    if (inateSkills != null) {
                        for (final String key : inateSkills.getKeys(false)) {
                            if (lq.skills.skillList.containsKey(key.toLowerCase())) {
                            final ConfigurationSection skillInfo = inateSkills.getConfigurationSection(key);
                            SkillDefinition s;
/*                            if (skillInfo.getString("command") != null && !skillInfo.getString("command").isEmpty()) {
                                s = new PluginSkill();  //skillInfo
                            } else {
                                s = new LqSkill();  //skillInfo
                            }
*/
                            System.out.print("Key: "+key);
                            s = lq.skills.skillList.get(key.toLowerCase());
                            SkillInfo si = s.getSkillInfo();
                            si.readConfigInfo(skillInfo);
                            s.setSkillInfo(si);
                            c.availableSkills.add(s);
                            }
                        }
                    }

                    // check race or group exists.
                    final boolean hasRace = checkRaceList(allowedRaces);
                    final boolean hasGroup = checkGroupList(allowedGroups);

                    if (!(hasRace || hasGroup)) {
                        validConfig = false;
                        lq.log(lq.configLang.classScanNoRaceOrGroup + classfile.getName());
                    }

                } catch (final Exception e) {
                    validConfig = false;
                    lq.logSevere(lq.configLang.classScanInvalid + classfile.getName());
                    e.printStackTrace();
                }

                if (validConfig) {
                    classprobability.add(new Pair<Integer, String>(c.frequency, c.name));
                    classTypes.put(c.name.toLowerCase(), c);
                    if (c.defaultClass) {
                        defaultClass = c;
                    }
                }
            }
        }
        
        wpmClasses = new WeightedProbMap<String>(classprobability);

        // notify sanning ended
        lq.log(lq.configLang.classScanEnd);
        if (defaultClass == null) {
            lq.log(lq.configLang.classNoDefault);
            // set "default" to first found to stop breaking...
            defaultClass = classTypes.entrySet().iterator().next().getValue();
        }
    }

    public boolean checkGroupList(final List<String> allowedGroups) {
        if (allowedGroups == null) {
            return false;
        }
        int groupcount = 0;
        for (final String g : allowedGroups) {
            if (g.equalsIgnoreCase("all") || g.equalsIgnoreCase("any")) {
                return true;
            }
            if (lq.races.groupExists(g)) {
                groupcount++;
            } else {
                lq.logWarn(lq.configLang.classScanGroupWarning + g);
            }
        }
        if (groupcount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkRaceList(final List<String> allowedRaces) {
        if (allowedRaces == null) {
            return false;
        }

        int racecount = 0;
        for (final String r : allowedRaces) {
            if (r.equalsIgnoreCase("all") || r.equalsIgnoreCase("any")) {
                return true;
            }
            if (lq.races.raceExists(r)) {
                racecount++;
            } else {
                lq.logWarn(lq.configLang.classScanRaceWarning + r);
            }
        }
        if (racecount > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ClassType getClass(final String className) {
        return classTypes.get(className.toLowerCase());
    }

    public List<String> getClasses(final String raceName) {
        return getClasses(raceName, null);
    }

    public List<String> getClasses(final String raceName, final Player player) {

        final Race race = lq.races.getRace(raceName);
        if (race != null) {
            final List<String> groups = race.groups;
            final List<String> result = new ArrayList<String>();
            final PC pc = lq.players.getPC(player);
            for (final ClassType c : classTypes.values()) {
                if (player != null) {
                    if (!(c.perm == null || c.perm.equalsIgnoreCase("") || player.isPermissionSet(c.perm))) {
                        continue;
                    }
                }
                boolean raceCheck = false;
                if ((c.allowedRaces.contains(raceName.toLowerCase()) || c.allowedRaces.contains("all") || c.allowedRaces.contains("any"))) {
                    raceCheck = true;
                } else {
                    // check if groups and allowed groups have any common elements
                    if (groups != null && c.allowedGroups != null) {
                        if (!Collections.disjoint(groups, c.allowedGroups) || c.allowedGroups.contains("all") || c.allowedGroups.contains("any")) {
                            raceCheck = true;
                        }
                    }
                }
                if (!raceCheck) {
                    continue;
                }
                if (c.requires != null) {
                    boolean allValid = true;
                    for (final String required : c.requires) {
                        if (!pc.hasMastered(required)) {
                            allValid = false;
                        }
                        lq.logger.info("req: " + required + " - " + pc.hasMastered(required));
                    }
                    // not mastered all required - skip me!!
                    if (!allValid) {
                        continue;
                    }
                }
                if (c.requiresOne != null) {
                    boolean oneValid = false;
                    for (final String requested : c.requiresOne) {
                        if (pc.hasMastered(requested)) {
                            oneValid = true;
                        }
                        lq.logger.info("requested: " + requested + " - " + pc.hasMastered(requested));
                    }

                    // if one is mastered allow
                    if (!oneValid) {
                        continue;
                    }
                }

                result.add(c.name.toLowerCase());
            }
            return result;
        } else {
            return null;
        }
    }

    public Map<String, ClassType> getClassTypes() {
        return classTypes;
    }

}
