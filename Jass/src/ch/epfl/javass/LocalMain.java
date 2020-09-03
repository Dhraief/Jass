package ch.epfl.javass;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * LocalMain : programme principal permettant de jouer une partie locale
 * 
 * @author Amine Atallah(284592)
 * @author Mohamed Ali Dhraief (283509)
 *
 */
public final class LocalMain extends Application {
    private final String[] DEFAULT_NAMES = { "Aline", "Bastien", "Colette",
    "David" };
    private final int DEFAULT_ITERATIONS = 10_000;
    private final String DEFAULT_HOST = "localhost";
    private final int MINTIME = 2;
    private final int MIN_ITERATIONS = 10;
    
    public static void main(String[] args) {
        launch(args);
    }

    /*
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Random seed = null;
        List<String> arguments = getParameters().getRaw();

        // GESTION DES ERREURS
        if (arguments.size() > 5 || arguments.size() < 4) {
            showHelpMessage();
            System.exit(1);
        } else if (arguments.size() == 5) {
            checkSeed(arguments.get(4));
            seed = new Random(Long.parseLong(arguments.get(4)));
            for (int i = 0; i < arguments.size(); i++) {
                if (!checkArgument(arguments.get(i), i).getKey()) {
                    System.err.println(
                            checkArgument(arguments.get(i), i).getValue());
                    System.exit(1);
                }
            }
        } else {
            seed = new Random();
            for (int i = 0; i < arguments.size(); i++) {
                if (!checkArgument(arguments.get(i), i).getKey()) {
                    System.err.println(
                            checkArgument(arguments.get(i), i).getValue());
                    System.exit(1);
                }
            }
        }
        // GENERATION DES GRAINES ALEATOIRES
        long[] seeds = new long[5];
        for (int i = 0; i < seeds.length; i++) {
            seeds[i] = seed.nextLong();
        }

        // CREATION DES JOUEURS
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> names = new EnumMap<>(PlayerId.class);

        for (int i = 0; i < arguments.size(); i++) {
            String argument = arguments.get(i);
            String[] argSeparator = argument.split(":");
            char playerType = argument.charAt(0);
            if (playerType == 'h') {
                players.put(PlayerId.values()[i], new GraphicalPlayerAdapter());
                setName(argSeparator, i, names, DEFAULT_NAMES);
            }

            else if (playerType == 's') {
                int iterations = argSeparator.length == 3
                        ? Integer.parseInt(argSeparator[2])
                                : DEFAULT_ITERATIONS;
                        players.put(PlayerId.values()[i],
                                new PacedPlayer(new MctsPlayer(PlayerId.values()[i],
                                        seeds[i + 1], iterations), MINTIME));
                        setName(argSeparator, i, names, DEFAULT_NAMES);
            } else if (playerType == 'r') {
                setName(argSeparator, i, names, DEFAULT_NAMES);
                String hostName = argSeparator.length == 3 ? argSeparator[2]
                        : DEFAULT_HOST;
                try {
                    players.put(PlayerId.values()[i],
                            new RemotePlayerClient(hostName));
                } catch (IOException e) {
                    System.err.println(
                            "Erreur de connexion pour le joueur distant "
                                    + (i + 1));
                    System.exit(1);
                }
            }
        }
        {
            // FIL D'EXECUTION
            Thread gameThread = new Thread(() -> {
                JassGame g = new JassGame(seeds[0], players, names);
                while (!g.isGameOver()) {
                    g.advanceToEndOfNextTrick();
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println("Exception");
                    }
                }

            });
           
            gameThread.setDaemon(true);
            gameThread.start();
            }

        
    }
    // Retourne la paire (true,"") si aucune erreur n'est signalée sinon
    // retourne faux avec
    // le message correspondant (false, ErrorMessage)
    private Pair<Boolean, String> checkArgument(String s, int i) {
        char playerType = s.charAt(0);
        String[] argSeparator = s.split(":");
        if (playerType != 's' && playerType != 'h' && playerType != 'r') {
            return new Pair<>(false,
                    "Erreur : type du joueur invalide dans le joueur "
                            + (i + 1));
        }
        if (argSeparator.length > 3)
            return new Pair<>(false,
                    "Erreur : nombre d'arguments invalide dans le joueur "
                            + (i + 1));
        if (playerType == 'h') {
            if (argSeparator.length > 2) {
                return new Pair<>(false,
                        "Erreur : nombre d'arguments invalide dans le joueur humain "
                                + (i + 1));
            }
        }
        if (playerType == 's') {
            if (argSeparator.length == 3)
                try {
                    int iterations = Integer.parseInt(argSeparator[2]);
                    if (iterations < MIN_ITERATIONS) {
                        return new Pair<>(false,
                                "Erreur : nombre d'itérations trop petit dans le joueur simule "
                                        + (i + 1));
                    }
                } catch (NumberFormatException e) {
                    return new Pair<>(false,
                            "Erreur : nombre d'itérations invalide dans le joueur simule "
                                    + (i + 1));
                }

        }

        return new Pair<>(true, "");
    }

    // met le nom du joueur dans names
    private void setName(String[] argSeparator, int counter,
            Map<PlayerId, String> names, String[] defaultNames) {
        if (argSeparator.length >= 2 && argSeparator[1] != "") {
            names.put(PlayerId.values()[counter], argSeparator[1]);
        } else {
            names.put(PlayerId.values()[counter], defaultNames[counter]);
        }
    }

    // Vérifie que la graine donnée est valide
    private void checkSeed(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.err.println(
                    "Erreur : la graine aléatoire n'est pas un long valide ");
            System.exit(1);
        }
    }

    private void showHelpMessage() {
        System.err.println("Utilisation: java ch.epfl.javass.LocalMain :\n"
                + "Pour les arguments, on doit introduire les 4 joueurs, et une graine (optionnel) \n"
                + " a) Chaque joueur est spécifier de la manière qui suit:\n"
                + " 1) D'abord le type de joueur : h, s, r désigne respectivement humain, simulé et"
                + " distant.\n"
                + "2)Puis, il est possible d'insérer son nom sous la forme d une chaine. Dans "
                + "  le cas échéant, un nom par défaut lui sera attribuer.\n"
                + "  3) Il est possible d'introduire un troisième argument dans les deux cas suivants:\n"
                + " - si le joueur est simulé, le troisième argument désigne le nombre d'itérations. "
                + "Celle ci est par défaut fixé à 100 000.\n"
                + " -si le joueur est distant, le troisième argument désigne l'adresse IP."
                + "Celle-ci par défaut  est fixé à localhost\n"
                + " Veuillez noter que au sein d'un joueur, les joueurs sont séparés par deux points "
                + ":\n"
                + " b) Pour insérer une graine il suffit d'insérer un nombre qui soit un long valable");
    }
}
