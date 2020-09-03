package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TeamId : Identification des equipes
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public enum TeamId {
	/**
	 * Les 2 equipes du jeu (dans l'ordre: equipe 1 , equipe 2)
	 */
	TEAM_1, TEAM_2;
	/**
	 * liste immuable contenant toutes les valeurs du type énuméré dans leur ordre
	 * de déclaration
	 */
	public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
	/**
	 * nombre de valeurs du type énuméré
	 */
	public static final int COUNT = 2;

	/**
	 * @return l'autre équipe que celle à laquelle on l'applique (TEAM_2 pour
	 *         TEAM_1, et inversément)
	 */
	public TeamId other() {
		if (this == TEAM_1) {
			return TEAM_2;
		}
		return TEAM_1;

	}

}
