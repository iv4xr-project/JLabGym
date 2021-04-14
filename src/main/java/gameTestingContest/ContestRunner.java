package gameTestingContest;

import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import environments.LabRecruitsConfig;
import environments.LabRecruitsEnvironment;
import environments.SocketReaderWriter;
import game.LabRecruitsTestServer;
import game.Platform;
import helperclasses.CSVExport;
import nl.uu.cs.aplib.utils.Pair;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


public class ContestRunner implements Callable<Integer> {
    
    // ========================================================================
    // ===== this section specifies avaibale command-line options for this contest runner
    // =====
    @Option(names = "--ng", description = "Will surpress the game graphics.") 
    boolean noGraphic ;
    
    @Option(names = "--time",  
            description = "Alocated time budget to run (in seconds)")
    int timeBudget = 60 ;
    
    @Option(names = "--xroot", required = true, 
            description = "The root directory where Lab Recruit executables are placed.")
    String labRecruitesExeRootDir ;
    
    @Option(names = "--ldir", required = true, 
            description = "The directory where the levels' definition files are placed.")
    String levelsDir ;
    
    @Option(names = "--rdir", required = true, 
            description = "The directory to put the resulting report file.")
    String reportDir ;
    
    @Parameters(paramLabel = "levelname", description = "The name of the game level to load",
                index = "0") 
    String levelName ;
    
    @Option(names = "--help", usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;
    
    // ===== end of section
    // ========================================================================

    
    // a flag to indicate that the testing-AI manages to complete its work
    private boolean aiThreadHasTerminatedNormally = false ;
    
    // a function used to create an instance of MyTestingAI. By default this
    // will just call the constructor of MyTestingAI. But through this field
    // we can change it to something for the purpose of testing this Runner.
    static Supplier<MyTestingAI> mkAnInstanceOfMyTestingAI = () -> new MyTestingAI() ;
    
    /**
     * The stream used to produce console-output. The default is System.out.
     */
    public static PrintStream out = System.out ;
    
    // ========================================================================
    
    /**
     * This method implements the actual logic of this contest runner.
     */
    @Override
    public Integer call() throws Exception { 
        out.println("** START JLabGym Testing-AI Contest. ") ;
        out.println("** Time-stamp        : " + java.time.LocalDate.now() + ", " + java.time.LocalTime.now()) ;
        out.println("** LR executable root: " + labRecruitesExeRootDir) ;
        out.println("** Levels dir        : " + levelsDir) ;
        out.println("** Level to load     : " + levelName) ;
        out.println("** Allocated time    : " + timeBudget) ;
        if(noGraphic) {
            out.println("** No graphics.") ;
        }
        else {
            out.println("** With graphics.") ;
        }
        
        LabRecruitsTestServer LRbinding = launchLabRecruits() ;
        var config = new LabRecruitsConfig(levelName,levelsDir) ;
        LabRecruitsEnvironment environment = new LabRecruitsEnvironment(config);
        
        final Thread parentThread = Thread.currentThread() ;
        Thread aiThread = new Thread(() -> { 
            long startTime = System.currentTimeMillis() ;
            MyTestingAI myTestingAI = mkAnInstanceOfMyTestingAI.get() ;
            try {
                Set<Pair<String,String>> report = myTestingAI.exploreLRLogic(environment) ;
                long endTime = System.currentTimeMillis() ;
                long runTime = endTime - startTime ;
                // write the report to a file:
                List<String[]> data = new LinkedList<>() ;
                String[] row0 = { "" + runTime } ; // the first row is the runtime
                data.add(row0) ;
                for(Pair<String,String> connection : report) {
                    String[] row = { connection.fst , connection.snd } ;
                    data.add(row) ;
                }
                
                String outputFile = reportDir + FileSystems.getDefault().getSeparator() 
                        + "report_" + levelName + ".csv" ;
                
                CSVExport.exportToCSV(data,outputFile) ;
                
                aiThreadHasTerminatedNormally = true ;
            }
            catch(Exception e) {
               out.println("## your instance of MyTestingAI crashed: " + e.getClass().getName()) ;
               e.printStackTrace(out) ;
               // the AI has crashed. Swallow the exception, but we don't set the 
               // successful-termination flag to true.
            }
            parentThread.interrupt() ;
        }) ;
        aiThread.run(); 
        int  returnCode = 0 ; // 0 means successful run of the AI, -1 means it fails or runs out of time
        try {
            // wait for the time budget
            Thread.sleep(timeBudget * 1000);
            // time budget is exhausted. Kill the AI-thread:
            aiThread.interrupt();
            // give 10 secs for the AI-thread to cleanly finish, else kill it:
            Thread.sleep(10000) ;
            aiThread.stop();
            returnCode = -1 ;
        }
        catch(InterruptedException e) {
            // well the AI-thread has interrupted the wait...
            if (!aiThreadHasTerminatedNormally) returnCode = -1 ;
        }
        finally {
            environment.close() ;
            LRbinding.close() ;
        }
        out.println("** END. Return-code: " + returnCode) ;
        return returnCode ;
    }
    
    
    LabRecruitsTestServer launchLabRecruits() {
        var useGraphics = true ; // set this to false if you want to run the game without graphics
        SocketReaderWriter.debug = false ; 
        LabRecruitsTestServer labRecruitsBinding =new LabRecruitsTestServer(
                useGraphics,
                Platform.PathToLabRecruitsExecutable(labRecruitesExeRootDir));
        labRecruitsBinding.waitForGameToLoad();
        return labRecruitsBinding ;
    }

    // ========================================================================
    
    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new ContestRunner()).execute(args);
    }

}
