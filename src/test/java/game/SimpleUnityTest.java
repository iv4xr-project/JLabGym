/*
This program has been developed by students from the bachelor Computer Science
at Utrecht University within the Software and Game project course.

©Copyright Utrecht University (Department of Information and Computing Sciences)
*/

package game;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import environments.SocketReaderWriter;
import eu.iv4xr.framework.spatial.Vec3;
import helperclasses.QArrayList;
import world.LabEntity;
import world.LabWorldModel;

import org.junit.jupiter.api.Assertions ;
import static org.junit.jupiter.api.Assertions.* ;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import java.util.Scanner;
import java.util.function.Predicate;


public class SimpleUnityTest {

    private static LabRecruitsTestServer labRecruitsTestServer;

    @BeforeAll
    static void start() {
        // set this to true to make the game's graphic visible:
        var useGraphics = false ;
        SocketReaderWriter.debug = true ;
        String labRecruitesExeRootDir = System.getProperty("user.dir") ;
        labRecruitsTestServer =new LabRecruitsTestServer(
                useGraphics,
                Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));
        labRecruitsTestServer.waitForGameToLoad();
    }

    @AfterAll
    static void close() {
        SocketReaderWriter.debug = false ;
        if(labRecruitsTestServer!=null) labRecruitsTestServer.close(); 
    }
    
    void hit_RETURN() {
        System.out.println("You can drag then game window elsewhere for beter viewing. Then hit RETURN to continue.") ;
        new Scanner(System.in) . nextLine() ;
    }

    /**
     * Test that we can obtain an agent position.
     */
    @Test
    public void observePositionTest() {

        var config = new LabRecruitsConfig("observePositionTest") ;
        config.seed = 500 ;

        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        //environment.registerInstrumenter(new JsonLoggerInstrument()).turnOnDebugInstrumentation();

        String agent = "agent0" ;
        assertTrue(environment.worldNavigableMesh != null) ;
        LabWorldModel wom = environment.observe(agent) ;
        assertTrue(wom != null) ;
        assertTrue(wom.position != null) ;
        System.out.println("position = " + wom.getFloorPosition()) ;
        System.out.println("dist = " + Vec3.dist(wom.getFloorPosition(), new Vec3(1f,0,2f))) ;
        assertTrue(Vec3.dist(wom.getFloorPosition(), new Vec3(1f,0,2f)) <= 0.01) ;

        environment.close();
    }

    /**
     * Test that we can observe a game-entity, its position, and property.
     */
    @Test
    public void observeSwitchTest() {

        var config = new LabRecruitsConfig("observeSwitchTest");
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        String agent = "agent0" ;
        assertTrue(environment.worldNavigableMesh != null) ;
        LabWorldModel wom = environment.observe(agent) ;
        assertTrue(wom != null) ;
        
        LabEntity e = wom.getElement("button0") ;
        assertTrue(e != null) ;
        System.out.println("button position: " + e.getFloorPosition()) ;
        assertTrue(Vec3.dist(e.getFloorPosition(), new Vec3(1,0,1)) <= 0.1) ;
        assertFalse(e.getBooleanProperty("isOn")) ;
 
        environment.close();
    }
    

    /**
     * The agent should only be able to see 1 of the 5 switches. The other 4 are hidden behind walls;
     * these should not be vissible.
     */
    @Test
    public void observeVisibleSwitches() {

        var config = new LabRecruitsConfig("observeVisibleSwitches");
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);

        String agent = "agent0" ;
        assertTrue(environment.worldNavigableMesh != null) ;
        LabWorldModel wom = environment.observe(agent) ;
        assertTrue(wom != null) ;
        
        assertTrue(wom.getElement("button1") != null) ;
        assertTrue(wom.getElement("button0") == null) ;
        assertTrue(wom.getElement("button2") == null) ;
        assertTrue(wom.getElement("button3") == null) ;
        assertTrue(wom.getElement("button4") == null) ;

        environment.close();
    }
    
    /**
     * Test that we can guide an agent to the location of a button and interact with it.
     */
    @Test
    public void moveToButton() throws InterruptedException {
        
        SocketReaderWriter.debug = false ;
        var config = new LabRecruitsConfig("moveToButton");
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        // hit_RETURN() ;
        
        String agent = "agent0" ;
        assertTrue(environment.worldNavigableMesh != null) ;
        LabWorldModel wom = environment.observe(agent) ;
        assertTrue(wom != null) ;
        
        String button0 = "button0" ;
        
        // guide the agent to button0:
        int i = 0 ;
        i = guide(environment,i,new Vec3(1.5f,0,4f)) ;
        i = guide(environment,i,new Vec3(5,0,4f)) ;
        wom = environment.observe(agent) ;
        assertTrue(wom.getElement(button0) != null) ;
        i = guide(environment,i,new Vec3(5,0,1.8f)) ;
        wom = environment.observe(agent) ;
        LabEntity b0 = wom.getElement(button0) ;
        assertTrue(b0 != null) ;
        System.out.println("Button 0 @" + b0.getFloorPosition()) ;
        assertTrue(Vec3.dist(wom.getFloorPosition(), b0.getFloorPosition()) <= 0.1) ;
        assertFalse(b0.getBooleanProperty("isOn")) ;
        
        // now interact with the button:
        wom = environment.interact(agent,button0,"") ;
        b0 = wom.getElement(button0) ;
        assertTrue(b0.getBooleanProperty("isOn")) ;

        environment.close();
    }
    
    private int guide(LabRecruitsEnvironment environment, int currentStepCount, Vec3 destination) throws InterruptedException {
        String agent = "agent0" ;
        String button0 = "button0" ;
        while (true) {
            Thread.sleep(30);
            LabWorldModel wom = environment.observe(agent) ;
            wom = environment.moveToward(agent, wom.position, destination) ;
            System.out.println("*** step " + currentStepCount + ": agent @" + wom.getFloorPosition()) ;
            boolean buttonVisible = wom.getElement(button0) != null ;
            System.out.println("      Button visioble: " + buttonVisible);
            if (currentStepCount>90 || Vec3.dist(wom.getFloorPosition(), destination) <= 0.3) {
                   break ;
            }
            
            currentStepCount++ ;
        }
        return currentStepCount ;
    }
    
}
