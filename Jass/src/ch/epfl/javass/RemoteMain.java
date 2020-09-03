package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * RemoteMain : contient le programme principal permettant de jouer à une partie
 * distante
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class RemoteMain extends Application {

    /**
     * methode main de RemoteMain
     * 
     * @param args
     *            : arguments donnés
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        RemotePlayerServer server = new RemotePlayerServer(
                new GraphicalPlayerAdapter());
        Thread remoteThread = new Thread(() -> {
            server.run();
        });
        System.out.println("La partie commencera à la connexion du client…");
        remoteThread.setDaemon(true);
        remoteThread.start();
    }
}