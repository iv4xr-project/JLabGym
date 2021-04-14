package gameTestingContest;

import java.util.*;
import java.util.function.Supplier;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import environments.SocketReaderWriter;
import game.LabRecruitsTestServer;
import game.Platform;
import nl.uu.cs.aplib.utils.Pair;

/**
 * A simple runner of your test-algorithm/AI (which is expected to be
 * implemented in the class {@link gameTestingContest.MyTestingAI}). You can
 * invoke the main method of this class, which will then create an instance of
 * your MyTestingAI, and run it. The resulting findings will be printed to the
 * console.
 * 
 * You have to configure several things in the fields
 * {@link #labRecruitesExeRootDir}, {@link #levelsDir}, and {@link levelName}.
 */
public class RawContestRunner {

    /**
     * Specify here the path to the "root" directory where the Lab Recruits
     * executable is placed. E.g. if it is in the directory
     * bar/foo/gym/Windows/bin/LabRecruits.exe, then the root directory is
     * bar/foo/gym.
     */
    static String labRecruitesExeRootDir = null;

    /**
     * Specify here the name of the level that you want to load. if the name is
     * "xyz", then there should be a text-file named xyz.csv that contains the
     * definition of this level. This csv file will be loaded to Lab Recruits, which
     * in turn will generate the corresponding game content.
     */
    static String levelName = null;

    /**
     * Specify hefre the path to the directory where the level-file referred to by
     * {@link #levelName} above is stored.
     */
    static String levelsDir = null;

    static LabRecruitsTestServer labRecruitsBinding;

    // a convenience method to launch Lab Recruits:
    static void launchLabRcruits() {
        var useGraphics = true; // set this to false if you want to run the game without graphics
        SocketReaderWriter.debug = false;
        labRecruitsBinding = new LabRecruitsTestServer(useGraphics,
                Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));
        labRecruitsBinding.waitForGameToLoad();
    }

    // a help method to create an instance of your MyTestingAI.
    static Supplier<MyTestingAI> mkAnInstanceOfMyTestingAI = () -> new MyTestingAI();

    /**
     * Invoke this main method to run your MyTestingAI on a game-lavel you specified
     * above.
     */
    public static void main(String[] args) throws Exception {
        // launch an instance of Lab Recruits
        launchLabRcruits();
        // create an instance of LabRecruitsEnvironment; it will bind to the
        // Lab Recruits instance you launched above. It will also load the
        // level specified below:
        var config = new LabRecruitsConfig(levelName, levelsDir);
        LabRecruitsEnvironment env = new LabRecruitsEnvironment(config);

        // let's now instantiate your test-algorithm/AI, and run it:
        MyTestingAI myTestingAI = mkAnInstanceOfMyTestingAI.get();
        Set<Pair<String, String>> report = myTestingAI.exploreLRLogic(env);
        // printing the findings:
        System.out.println("** The level has the following logic:");
        for (Pair<String, String> connection : report) {
            System.out.println("   Button " + connection.fst + " toggles " + connection.snd);
        }
        env.close() ;
        labRecruitsBinding.close();
    }

}
