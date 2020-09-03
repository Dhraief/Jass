package ch.epfl.javass.jass;

import java.io.IOException;
import java.util.Map;
import ch.epfl.javass.jass.Card.Color;

/**
 * Player : represente un joueur
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public interface Player {

	/**
	 * @param state : l'etat du tour
	 * @param hand  : la main du joueur
	 * @return la carte que le joueur désire jouer, sachant que l'état actuel du
	 *         tour est celui décrit par state et que le joueur a les cartes hand en
	 *         main
	 */
	Card cardToPlay(TurnState state, CardSet hand);

	/**
	 * informer le joueur qu'il a l'identité ownId et que les différents joueurs
	 * (lui inclus) sont nommés selon le contenu de la table associative playerNames
	 * 
	 * @param ownId       : identité du joueur
	 * @param playerNames : noms des joueurs
	 */
	default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
	}

	/**
	 * appelée chaque fois que la main du joueur change
	 * 
	 * @param newHand : main du joueur
	 */
	default void updateHand(CardSet newHand) {
	}

	/**
	 * appelée chaque fois que l'atout change
	 * 
	 * @param trump : atout
	 * @throws IOException 
	 */
	default void setTrump(Color trump){
	}

	/**
	 * appelée chaque fois que le pli change
	 * 
	 * @param newTrick : pli
	 */
	default void updateTrick(Trick newTrick) {
	}

	/**
	 * appelée chaque fois que le score change
	 * 
	 * @param score : score des equipes
	 */
	default void updateScore(Score score) {
	}

	/**
	 * appelée une seule fois dès qu'une équipe à gagné
	 * 
	 * @param winningTeam : equipe gagnante
	 */
	default void setWinningTeam(TeamId winningTeam) {
	}

}
