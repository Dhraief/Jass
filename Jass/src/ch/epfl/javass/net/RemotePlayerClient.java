package ch.epfl.javass.net;

import java.io.BufferedReader;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * RemotePlayerClient : représente le client d'un joueur
 * 
 * @author Amine Atallah (284592)
 * @author Mohamed Ali Dhraief (283509)
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final int TCP_PORT = 5108;

    /**
     * Constructeur de RemotePlayerClient
     * 
     * @param hostName
     *            : nom de l'hôte sur lequel s'exécute le serveur du joueur
     *            distant
     * @throws IOException
     */
    public RemotePlayerClient(String hostName) throws IOException {
        socket = new Socket(hostName, TCP_PORT);
        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), US_ASCII));
        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
    }

    /*
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws IOException {
        socket.close();
        writer.close();
        reader.close();
    }

    /*
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card card;
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.CARD.name(),
                    StringSerializer.combine(',',
                            StringSerializer.serializeLong(state.packedScore()),
                            StringSerializer
                                    .serializeLong(state.packedUnplayedCards()),
                            StringSerializer.serializeInt(state.packedTrick())),
                    StringSerializer.serializeLong(hand.packed())));
            newLineAndFlush();
            card = Card.ofPacked(StringSerializer.deserializeInt(reader.readLine()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return card;
    }

    /*
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        try {
            String[] playerNamesSerialized = new String[playerNames.size()];
            for (int i=0 ; i<playerNamesSerialized.length; i++) {
                playerNamesSerialized[i] = StringSerializer.serializeString(playerNames.get(PlayerId.values()[i]));
            }
            writer.write(StringSerializer.combine(' ', JassCommand.PLRS.name(),
                    StringSerializer.serializeInt(ownId.ordinal()),
                    StringSerializer.combine(',', playerNamesSerialized) ));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.HAND.name(),
                    StringSerializer.serializeLong(newHand.packed())));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.TRMP.name(),
                    StringSerializer.serializeInt(trump.ordinal())));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.TRCK.name(),
                    StringSerializer.serializeInt(newTrick.packed())));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.SCOR.name(),
                    StringSerializer.serializeLong(score.packed())));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
     * @see
     * ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        try {
            writer.write(StringSerializer.combine(' ', JassCommand.WINR.name(),
                    StringSerializer.serializeInt(winningTeam.ordinal())));
            newLineAndFlush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void newLineAndFlush() throws IOException {
        writer.write('\n');
        writer.flush();
    }

}