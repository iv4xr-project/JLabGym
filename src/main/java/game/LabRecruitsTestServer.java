/*
This program has been developed by students from the bachelor Computer Science
at Utrecht University within the Software and Game project course.

©Copyright Utrecht University (Department of Information and Computing Sciences)
*/

package game;

import environments.LabRecruitsEnvironment;
import helperclasses.PrintColor;
import helperclasses.Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Contains method to launch the Lab Recruits game. The name "TestServer" is a
 * bit misleading. We keep it for a historical reason. This class itself is not
 * a "server", but it launches the Lab Recruits game, which in turn has an open
 * TCP port that let it to be controlled by an external agent. So, the game can
 * be seen as a "server".
 */
public class LabRecruitsTestServer {

    private Process server;

    /**
     * Create an instance of this class, launching the Lab Recruits game as it does
     * so. The game is assumed to be installed in ProgramFiles (windows) or in
     * Application (Mac). Linux: use the other constructor.
     * 
     * @param useGraphics if true, the game will be launched with graphics
     *                    displayed, and else without graphics. Linux: graphics
     *                    cannot be turned on.
     */
    public LabRecruitsTestServer(Boolean useGraphics) {
        start(useGraphics, Platform.INSTALL_PATH);
    }

    /**
     * Create an instance of this class, launching the Lab Recruits game as it does
     * so. The given path is the path to the Lab Recruits executable.
     * 
     * @param useGraphics if true, the game will be launched with graphics
     *                    displayed, and else without graphics. Linux: graphics
     *                    cannot be turned on.
     */
    public LabRecruitsTestServer(Boolean useGraphics, String binaryPath) {
        start(useGraphics, binaryPath);
    }

    /**
     * Launch the Lab Recruits game.
     */
    private void start(Boolean useGraphics, String binaryPath) {
        // try to start the server

        if (Platform.isLinux())
            useGraphics = false;

        Util.verifyPath(binaryPath);

        if (server != null && server.isAlive())
            throw new IllegalCallerException(
                    "The current server is still running. Close the server first by calling Close();");

        try {
            ProcessBuilder pb = new ProcessBuilder(useGraphics ? new String[] { binaryPath }
                    : new String[] { binaryPath, "-batchmode", "-nographics" });

            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            server = pb.start();
            waitFor(Process::isAlive);

        } catch (IOException e) {
            System.out.println(PrintColor.FAILURE() + ": Cannot start LabRecruits server!\n" + e.getMessage());
        }
    }

    /**
     * Return from method when the game has loaded
     */
    public void waitForGameToLoad() {
        if (server == null)
            throw new IllegalCallerException(
                    "Cannot wait for game to load, because the server is has not started yet!");
        if (!server.isAlive())
            throw new IllegalCallerException("Cannot wait for game to load, because, the server already closed down!");

        // try to connect with an empty configuration
        new LabRecruitsEnvironment();
    }

    /**
     * Close the game-instance by destroying the process that contains it.
     */
    public void close() {
        if (server != null) {

            try {
                // server.waitFor();
                server.destroy();
                server.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check whether the game is alive. This does not actually check that, but
     * rather only check if the process that contains the game is alive.
     */
    public boolean isRunning() {
        if (server == null)
            return false;
        return server.isAlive();
    }

    // wait for a certain condition
    private void waitFor(Function<Process, Boolean> eval) {
        try {
            while (!eval.apply(server))
                server.waitFor(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(PrintColor.FAILURE() + ": Cannot run the process for " + eval.toString());
        }
    }
}
