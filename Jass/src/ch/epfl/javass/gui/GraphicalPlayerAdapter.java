package ch.epfl.javass.gui;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;
import javafx.application.Platform;

public final class GraphicalPlayerAdapter implements Player  {
   
    private ScoreBean scoreBean = new ScoreBean();
    private TrickBean trickBean = new TrickBean();
    private HandBean handBean = new HandBean();
    private BlockingQueue <Card> queue = new ArrayBlockingQueue<Card> (1);
    private GraphicalPlayer graphicalPlayer;


    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater( 
                () -> { handBean.setPlayableCards(state.trick().playableCards(hand));}
                );

        Card card = null;
        try {
            card = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater( 
                () -> { handBean.setPlayableCards(CardSet.EMPTY);}
                );

        return card;
    }



    /**
     * informer le joueur qu'il a l'identité ownId et que les différents joueurs
     * (lui inclus) sont nommés selon le contenu de la table associative playerNames
     * 
     * @param ownId       : identité du joueur
     * @param playerNames : noms des joueurs
     */
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer= new GraphicalPlayer(ownId, playerNames, scoreBean  , trickBean, handBean , queue);
        Platform.runLater(
                () -> { graphicalPlayer.createStage().show(); }
                );

    }

    /**
     * appelée chaque fois que la main du joueur change
     * 
     * @param newHand : main du joueur
     */
    public void updateHand(CardSet newHand) {
        Platform.runLater(
                () -> {handBean.setHand(newHand);});
    }

    /**
     * appelée chaque fois que l'atout change
     * 
     * @param trump : atout
     * @throws IOException 
     */
    public void setTrump (Color trump){
        Platform.runLater(
                () -> {  trickBean.setTrump(trump);
                });
    }


    /**
     * appelée chaque fois que le pli change
     * 
     * @param newTrick : pli
     */
    public void updateTrick(Trick newTrick) {
        Platform.runLater(
                () -> {  trickBean.setTrick(newTrick);

                });
    }


    /**
     * appelée chaque fois que le score change
     * 
     * @param score : score des equipes
     */
    public void updateScore(Score score) {

        Platform.runLater(
                () -> { 

                    scoreBean.setTurnPoints(TeamId.TEAM_1, score.turnPoints(TeamId.TEAM_1));   
                    scoreBean.setTurnPoints(TeamId.TEAM_2, score.turnPoints(TeamId.TEAM_2));

                    scoreBean.setGamePoints(TeamId.TEAM_1, score.gamePoints(TeamId.TEAM_1));   
                    scoreBean.setGamePoints(TeamId.TEAM_2, score.gamePoints(TeamId.TEAM_2));   

                    scoreBean.setTotalPoints(TeamId.TEAM_1, score.totalPoints(TeamId.TEAM_1));   
                    scoreBean.setTotalPoints(TeamId.TEAM_2,  score.totalPoints(TeamId.TEAM_2));

                });
    }

    /**
     * appelée une seule fois dès qu'une équipe à gagné
     * 
     * @param winningTeam
     *            : equipe gagnante
     */
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }


}