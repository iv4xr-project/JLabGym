package gameTestingContest;


import static examples.Example1.*;
import static org.junit.jupiter.api.Assertions.* ;

import java.io.File;
import java.util.*;

import org.junit.jupiter.api.Test;

import environments.LabRecruitsEnvironment;
import eu.iv4xr.framework.spatial.Vec3;
import examples.Example1;
import game.Platform;
import helperclasses.Util;
import nl.uu.cs.aplib.utils.Pair;

public class ContestRunnerTest {
    
    // let's create a mock instance of MyTestingAI, which will only work on the level
    // "moveToButton"; but this will do for testing.
    // We will use the guide-method provided by Example1.
    static class XXXtestAI extends MyTestingAI {
        
        // a mock-AI, just for testing.
        @Override
        public Set<Pair<String, String>> exploreLRLogic(LabRecruitsEnvironment environment) throws Exception {
            int i = 0 ;
            i = Example1.guide(environment,i,new Vec3(1.5f,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,1.8f)) ;
            
            // producing a mock report:
            Set<Pair<String, String>> report = new HashSet<>() ;
            report.add(new Pair("button0","door0")) ; // just a pretend ... there is no door in the given level.
            return report ;
        }
    }
    
    // As XXXtestAI but we add 30-sec sleep to test breaking of the execution:
    static class XXXtestAI_WithSleep extends MyTestingAI {
        
        // a mock-AI, just for testing.
        @Override
        public Set<Pair<String, String>> exploreLRLogic(LabRecruitsEnvironment environment) throws Exception {
            int i = 0 ;
            i = Example1.guide(environment,i,new Vec3(1.5f,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,1.8f)) ;
            
            // sleep:
            System.out.println(">>> MyAI starts to sleep, 30s") ;
            Thread.sleep(30000);
            System.out.println(">>> MyAI awakes again ") ;
            
            // producing a mock report:
            Set<Pair<String, String>> report = new HashSet<>() ;
            report.add(new Pair("button0","door0")) ; // just a pretend ... there is no door in the given level.
            return report ;
        }
    }
    
    // A variation of XXXtestAI_WithSleep: will sleep 30s but then ignores thread-interrupt
    static class XXXtestAI_WithSleep_IgnoringInterrupt extends MyTestingAI {
        
        // a mock-AI, just for testing.
        @Override
        public Set<Pair<String, String>> exploreLRLogic(LabRecruitsEnvironment environment) throws Exception {
            int i = 0 ;
            i = Example1.guide(environment,i,new Vec3(1.5f,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,1.8f)) ;
            
            // sleep:
            int k = 0 ;
            while(k<10) {
                try {
                    System.out.println(">>> MyAI starts to sleep, 30s") ;
                    Thread.sleep(30000);
                    System.out.println(">>> MyAI awakes again ") ;
                }
                catch(Exception e) {
                    
                }
                k++ ;
            }
            
            // producing a mock report:
            Set<Pair<String, String>> report = new HashSet<>() ;
            report.add(new Pair("button0","door0")) ; // just a pretend ... there is no door in the given level.
            return report ;
        }
    }
    
    /**
     * Run the ContestRunner using a mock TestingAI defined above. To test the runner we check
     * if the runner manages to get the report produced by the mock-AI and write it to a report
     * file.
     */
    @Test
    public void test_ContestRunner() throws Exception {
        
        String reportFile = System.getProperty("user.home") + "/tmp/report_moveToButton.csv" ;
        // delete the report file if it exists:
        if (Util.fileExists(reportFile))  {
            new File(reportFile) .delete() ;
        }
 
        
        // override the MyAI factory to use the above "xxxAI" :
        ContestRunner.mkAnInstanceOfMyTestingAI = () -> new XXXtestAI() ;
        String[] args = { 
           "--xroot", System.getProperty("user.dir") ,
           "--ldir", Platform.LEVEL_PATH, 
           "--rdir", System.getProperty("user.home") + "/tmp" ,
           "moveToButton" // level name
        } ;
        try {
           ContestRunner.main(args);
        }
        catch(Error e) { }
        // test if the report file is indeed created:
       assertTrue(Util.fileExists(reportFile)) ;
       System.out.println("Report file: " + reportFile) ;
    }
    
    /**
     * Test no-graphic feature. You have to see visually, if the game graphic is not launched.
     * @throws Exception
     */
    @Test
    public void test_ContestRunner_WithoutGraphic() throws Exception {
        
        String reportFile = System.getProperty("user.home") + "/tmp/report_moveToButton.csv" ;
        // delete the report file if it exists:
        if (Util.fileExists(reportFile))  {
            new File(reportFile) .delete() ;
        }
 
        
        // override the MyAI factory to use the above "xxxAI" :
        ContestRunner.mkAnInstanceOfMyTestingAI = () -> new XXXtestAI() ;
        String[] args = { 
           "--xroot", System.getProperty("user.dir") ,
           "--ldir", Platform.LEVEL_PATH, 
           "--rdir", System.getProperty("user.home") + "/tmp" ,
           "--ng",
           "moveToButton" // level name
        } ;
        try {
           ContestRunner.main(args);
        }
        catch(Error e) { }
        // test if the report file is indeed created:
       assertTrue(Util.fileExists(reportFile)) ;
    }
    
    /**
     * Test time-out, that the contestant thread is interrupted when it takes too long.
     * @throws Exception
     */
    @Test
    public void test_ContestRunner_TimeoutFeature_1() throws Exception {
        
        String reportFile = System.getProperty("user.home") + "/tmp/report_moveToButton.csv" ;
        // delete the report file if it exists:
        if (Util.fileExists(reportFile))  {
            new File(reportFile) .delete() ;
        }
 
        
        // override the MyAI factory to use the above "xxxAI" :
        ContestRunner.mkAnInstanceOfMyTestingAI = () -> new XXXtestAI_WithSleep() ; // will sleep 30sec
        String[] args = { 
           "--xroot", System.getProperty("user.dir") ,
           "--ldir", Platform.LEVEL_PATH, 
           "--rdir", System.getProperty("user.home") + "/tmp" ,
           "--time", "5", // set timeout to 5 secs
           "moveToButton" // level name
        } ;
        try {
           ContestRunner.main(args);
        }
        catch(Error e) { }
    }
    
    /**
     * Test time-out, that the contestant thread is interrupted when it takes too long.
     * We test with a contestant that will just ignore the interrupt.
     * @throws Exception
     */
    @Test
    public void test_ContestRunner_TimeoutFeature_2() throws Exception {
        
        String reportFile = System.getProperty("user.home") + "/tmp/report_moveToButton.csv" ;
        // delete the report file if it exists:
        if (Util.fileExists(reportFile))  {
            new File(reportFile) .delete() ;
        }
 
        
        // override the MyAI factory to use the above "xxxAI" :
        ContestRunner.mkAnInstanceOfMyTestingAI = () -> new XXXtestAI_WithSleep_IgnoringInterrupt() ; // will sleep 30sec
        String[] args = { 
           "--xroot", System.getProperty("user.dir") ,
           "--ldir", Platform.LEVEL_PATH, 
           "--rdir", System.getProperty("user.home") + "/tmp" ,
           "--time", "5", // set timeout to 5 secs
           "moveToButton" // level name
        } ;
        try {
           ContestRunner.main(args);
        }
        catch(Error e) { }
    }
    
    @Test
    public void test_RawContestRunner() throws Exception {
        RawContestRunner.labRecruitesExeRootDir = System.getProperty("user.dir") ;
        RawContestRunner.levelName = "moveToButton" ;
        RawContestRunner.levelsDir = Platform.LEVEL_PATH ;
        RawContestRunner.mkAnInstanceOfMyTestingAI = () -> new XXXtestAI() ;
        RawContestRunner.main(null);
        // no oracle... just checking that this does not crash        
    }

}
