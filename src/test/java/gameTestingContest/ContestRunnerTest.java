package gameTestingContest;


import static examples.Example1.*;
import static org.junit.jupiter.api.Assertions.* ;

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
        public List<Pair<String, String>> checkLRLogic(LabRecruitsEnvironment environment) throws Exception {
            int i = 0 ;
            i = Example1.guide(environment,i,new Vec3(1.5f,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,4f)) ;
            i = Example1.guide(environment,i,new Vec3(5,0,1.8f)) ;
            
            // producing a mock report:
            List<Pair<String, String>> report = new LinkedList<>() ;
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
    public void test1() throws Exception {
        
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
       assertTrue(Util.fileExists(System.getProperty("user.home") + "/tmp/report_moveToButton.csv")) ;
    }

}
