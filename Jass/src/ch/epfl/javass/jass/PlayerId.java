package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.jass.TeamId;

/**
 * PlayerId: Identification des joueurs
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public enum PlayerId {

	/**
	 * Les 4 joueurs du jeu (dans l'ordre: joueur 1, joueur 2, joueur 3, joueur 4)
	 */
	PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;
	/**
	 * liste immuable contenant toutes les valeurs du type énuméré dans leur ordre
	 * de déclaration
	 */
	public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
	/**
	 * nombre de valeurs du type énuméré
	 */
	public static final int COUNT = 4;

	/**
	 * @return l'équipe à laquelle appartient le joueur auquel on l'applique, à
	 *         savoir l'équipe 1 pour les joueurs 1 et 3, et l'équipe 2 pour les
	 *         joueurs 2 et 4
	 */
	public TeamId team() {
		switch (this) {
		case PLAYER_1:
			return TeamId.TEAM_1;
		case PLAYER_2:
			return TeamId.TEAM_2;
		case PLAYER_3:
			return TeamId.TEAM_1;
		default:
			return TeamId.TEAM_2;
		}

	}

}
