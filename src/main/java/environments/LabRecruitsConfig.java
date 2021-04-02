package environments;

import helperclasses.Util;
import nl.uu.cs.aplib.utils.Pair;
import game.Platform;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This defines some configuration parameters for the Lab-Recruits game, as far
 * as they can be configured. This configuration will be sent to the Lab
 * Recruits game when we "connect" an instance of {@link LabRecruitsEnvironment}
 * to the game.
 * 
 * For example, in this configuration we specify which level that is supposed to
 * be loaded to the controlled Lab Recruits game.
 */
public class LabRecruitsConfig {

    /**
     * The IP location where the Lab Recruits runs. The default is localhost.
     */
    public transient String host = "localhost";

    /**
     * The port which Lab Recruits opens for communication with an external
     * environment. The default is 8053.
     */
    public transient int port = 8053;

    // configurations
    public int seed = 1;

    /**
     * The path of the directory where the level definition file is to be found.
     */
    public String level_path = "";

    /**
     * The name of the level. There should exists a csv file with the same name that
     * defines the said level.
     */
    public String level_name = "";

    /**
     * Set the speed of the agent in the game. The default is 0.13. This is a
     * reasonable speed. Lower speed is ok, but if the speed is set to be too large,
     * it might not be possible for the game.
     */
    public float agent_speed = 0.13f;

    public float npc_speed = 0.11f;

    /**
     * Currently has no effect.
     */
    public float fire_spread = 0f;

    /**
     * Currently has no effect.
     */
    public float jump_force = 0.18f;

    /**
     * Set the view distance of the agent. The default is 10.
     */
    public float view_distance = 10f;

    public float light_intensity = 0.5f;

    /**
     * Extra links between switches and doors that we want to explicitly add through
     * this configuration.
     */
    public ArrayList<Pair<String, String>> add_links = new ArrayList<>();

    /**
     * Links between switches and doors that we want to explicitly remove through
     * this configuration.
     */
    public ArrayList<Pair<String, String>> remove_links = new ArrayList<>();

    /**
     * Construct a default configuration. The path to the level-file and the name of
     * the level are left blank though.
     */
    public LabRecruitsConfig() {
    }

    /**
     * Create a default configuration, with the given level-name. The level-file is
     * assumed to be stored in the standard location defined by Platform.LEVEL_PATH
     * (which points to projectdir/src/test/resources/levels".
     */
    public LabRecruitsConfig(String levelName) {
        useLevel(levelName, Platform.LEVEL_PATH);
    }

    /**
     * Create a default configuration, with the given level-name. The level-file is
     * assumed to be stored in the given folder-path.
     */
    public LabRecruitsConfig(String levelName, String levelFolder) {
        useLevel(levelName, levelFolder);
    }

    private LabRecruitsConfig useLevel(String levelName, String levelFolder) {
        String fullPath = Paths.get(levelFolder, levelName + ".csv").toAbsolutePath().toString();
        Util.verifyPath(fullPath);
        this.level_path = fullPath;
        this.level_name = levelName;
        return this;
    }

    /**
     * Add explicit links between switches and doors. Each link is represented by an
     * instance of Pair (s,d).
     */
    public LabRecruitsConfig addSwitchToDoorLinks(Pair<String, String>... links) {
        this.add_links.addAll(Arrays.asList(links));
        return this;
    }

    /**
     * Add these links between switches and doors to be removed (so, if a switch s
     * is connected to a door d in the level definition, and it is to be removed,
     * then they will no longer be connected. Each link is represented by an
     * instance of Pair (s,d).
     */
    public LabRecruitsConfig removeSwitchToDoorLinks(Pair<String, String>... links) {
        this.remove_links.addAll(Arrays.asList(links));
        return this;
    }

}
