package game;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import environments.SocketReaderWriter;
import eu.iv4xr.framework.extensions.pathfinding.SurfaceNavGraph;
import eu.iv4xr.framework.spatial.Vec3;
import world.LabEntity;
import world.LabWorldModel;

/**
 * The nav-mesh sent by Unity can be broken, where a single node (a corner of a triangle in the mesh)
 * could be split into a pair of nodes, which are unreachable from each other. We have implemented
 * a fix for this by force-merging such twins. This test run on a level where it is know that Unity
 * produces such a broken nav-mesh. We test if our fix works.
 */
public class NavMeshFixer_Test {
    
    private static LabRecruitsTestServer labRecruitsTestServer;

    @BeforeAll
    static void start() {
        // set this to true to make the game's graphic visible:
        var useGraphics = false ;
        // SocketReaderWriter.debug = true ;
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
    
    @Test
    public void test1() throws InterruptedException {
        
        var config = new LabRecruitsConfig("samira_8room") ;
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        SurfaceNavGraph nav = new SurfaceNavGraph(environment.worldNavigableMesh, 0.01f) ;
        nav.perfect_memory_pathfinding = true ;
        assertTrue(nav.findPath(new Vec3(3,0,70), new Vec3(3,0,77.5f), 0.1f) != null) ;
        
        environment.close() ;
 
    }

}
