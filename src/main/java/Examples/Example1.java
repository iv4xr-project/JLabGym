package Examples;
import java.util.Scanner;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import environments.SocketReaderWriter;
import eu.iv4xr.framework.spatial.Vec3;
import game.LabRecruitsTestServer;
import game.Platform;
import world.LabEntity;
import world.LabWorldModel;

/**
 * A simple example of controlling the Lab Recruits game using JLabGym. See
 * the main-method of this class.
 */
public class Example1 {
    
    /**
     * Specify where the root location of the Lab Recruits executable. By default, 
     * it is in the ./gym directory within this project. If so, you don't need to
     * change the code below.
     */
    static String labRecruitesExeRootDir= System.getProperty("user.dir") ;
     
    /**
     * This will launch the Lab Recruits game. It assumes the game to be located in
     * under a root directory specified by the variable labRecruitesExeRootDir.
     * The method returns an instance of a Java object that binds to the game.
     * For a historical reason, this binder is called "LabRecruitsTestServer", though
     * the actual server is the Lab Recruits game itself, that has an open port that
     * listens to external commands.
     */
    static LabRecruitsTestServer launchLabRecruits() {
        var useGraphics = true ; // set this to false if you want to run the game without graphics
        SocketReaderWriter.debug = false ; 
        LabRecruitsTestServer labRecruitsBinding =new LabRecruitsTestServer(
                useGraphics,
                Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));
        labRecruitsBinding.waitForGameToLoad();
        return labRecruitsBinding ;
    }
    
    /**
     * Run this main-method to see how it launches and control the Lab Recruits game.
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        // (1) launch the Lab Recruits game:
        LabRecruitsTestServer labrecruitsBinding = launchLabRecruits() ;
        
        // (2) create an "Environment" to interface with Lab Recruits, and specify which game-level
        // is to be loaded. Available game levels are by default placed in src/test/resources/levels.
        // We will load a simple level called "moveButton". It defines a small room, with the player
        // character and a single button in the room.
        var config = new LabRecruitsConfig("moveToButton") ;
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        hit_RETURN()  ;
        
        // (3) we will now control the player character to move towards the button. Each primitive
        // command will also return what the agent observes. We will demonstrate this by printing
        // the agent's own position, and the distance to the button, when it becomes visible.
        environment.observe("agent0") ;
        int i = 0 ;
        i = guide(environment,i,new Vec3(1.5f,0,4f)) ;
        i = guide(environment,i,new Vec3(5,0,4f)) ;
        i = guide(environment,i,new Vec3(5,0,1.8f)) ;
        
        hit_RETURN() ;
        
        // (4) Let's now instruct the player character to push the button:
        String agent   = "agent0" ;
        String button0 = "button0" ;
        LabWorldModel wom = environment.observe(agent) ;
        System.out.println("*** Button0 state before being interacted: " + wom.getElement(button0).getBooleanProperty("isOn")) ;
        wom = environment.interact(agent,button0,"") ;
        System.out.println("*** Button0 state AFTER being interacted: " + wom.getElement(button0).getBooleanProperty("isOn")) ;
        
        hit_RETURN() ;
        // (5) Demo is finished. We close both the environment and the Lab Recruits:
        environment.close();
        labrecruitsBinding.close(); 
    }
    
    /**
     * Guide the player character to go in a straight line to the given destination. The path
     * to the destination must be clear.
     */
    public static int guide(LabRecruitsEnvironment environment, int currentStepCount, Vec3 destination) throws InterruptedException {
        String agent   = "agent0" ;
        String button0 = "button0" ;
        // ask the agent to report what it observes; the observation is given as an instance of a
        // LabWorldModel.
        //LabWorldModel wom = environment.observe(agent) ;
        while (true) {
            Thread.sleep(30);
            // Instruct the agent to move towards the given destination. How far it will advance
            // towards the destination depends on the agent speed. This is set in the LabRecruitsConfig
            // that you set earlier. The default is 0.13 distance unit.
            // The agent also returns what it observes. For "move", it is what it observes at the 
            // beginning of the move. For the default speed 0.13, most of the time this will be the
            // same as the observation at the end of the move.
            // In the instruction was "interact", the observation sent back is what the agent sees
            // after the interaction.
            LabWorldModel wom = environment.observe(agent) ;
            wom = environment.moveToward(agent, wom.position, destination) ;
            // let's see if button0 is observed:
            LabEntity button = wom.getElement(button0) ;
            boolean buttonVisible =  button!= null ;
            
            // Let's print back some of the observed facts to the console:
            System.out.print("*** [" + currentStepCount + "] Agent pos.: " + wom.getFloorPosition()) ;
            if (buttonVisible) {
                float distance = Vec3.dist(button.getFloorPosition(), wom.getFloorPosition()) ;
                System.out.println(". Button0 is observed, dist.: " + distance);
            }
            else {
                System.out.println("") ;
            }
            // stop if the agent is now close enough to the destination:
            if (currentStepCount>90 || Vec3.dist(wom.getFloorPosition(), destination)  <= 0.3) {
                break ;
            }
            currentStepCount++ ;
        }
        return currentStepCount ;
    }

    static void hit_RETURN() {
        System.out.println("Hit RETURN to continue.") ;
        new Scanner(System.in) . nextLine() ;
    }

}
