package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits64;

/**
 * PackedScore: les scores empaquetés
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class PackedScore {

	/**
	 * Scores de début de partie
	 */
	public static final long INITIAL = pack(0, 0, 0, 0, 0, 0);

	// Constructeur prive
	private PackedScore() {
	}

	/**
	 * @param pkScore : scores empaquetes
	 * @return vrai la valeur donnée est un score empaqueté valide et faux sinon
	 */
	public static boolean isValid(long pkScore) {
		return Bits64.extract(pkScore, 0, 4) >= 0 && Bits64.extract(pkScore, 0, 4) <= 9
				&& Bits64.extract(pkScore, 4, 9) >= 0 && Bits64.extract(pkScore, 4, 9) <= 257
				&& Bits64.extract(pkScore, 13, 11) >= 0 && Bits64.extract(pkScore, 13, 11) <= 2000
				&& Bits64.extract(pkScore, 24, 8) == 0 && Bits64.extract(pkScore, 32, 4) >= 0
				&& Bits64.extract(pkScore, 32, 4) <= 9 && Bits64.extract(pkScore, 36, 9) >= 0
				&& Bits64.extract(pkScore, 36, 9) <= 257 && Bits64.extract(pkScore, 45, 11) >= 0
				&& Bits64.extract(pkScore, 45, 11) <= 2000 && Bits64.extract(pkScore, 56, 8) == 0;
	}

	/**
	 * @param turnTricks1 : nombre de plis remportés par l'équipe 1 dans le tour
	 *                    courant
	 * @param turnPoints1 : nombre de points remportés par l'équipe 1 dans le tour
	 *                    courant
	 * @param gamePoints1 : nombre de points remportés par l'équipe 1 dans le jeu
	 *                    courant
	 * @param turnTricks2 : nombre de plis remportés par l'équipe 2 dans le tour
	 *                    courant
	 * @param turnPoints2 : nombre de points remportés par l'équipe 2 dans le tour
	 *                    courant
	 * @param gamePoints2 : nombre de points remportés par l'équipe 2 dans le jeu
	 *                    courant
	 * @return un entier de type long dans lequel sont empaquetées les six
	 *         composantes des scores
	 */
	public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2,
			int gamePoints2) {
		assert ((turnTricks1 >= 0 && turnTricks1 <= 9 && turnTricks2 >= 0 && turnTricks2 <= 9 && turnPoints1 >= 0
				&& turnPoints2 <= 257 && turnPoints2 >= 0 && turnPoints2 <= 257 && gamePoints1 >= 0
				&& gamePoints1 <= 2000 && gamePoints2 >= 0 && gamePoints2 <= 2000));

		return Bits64.pack(Bits64.pack(Bits64.pack(turnTricks1, 4, turnPoints1, 9), 13, gamePoints1, 11), 32,
				Bits64.pack(Bits64.pack(turnTricks2, 4, turnPoints2, 9), 13, gamePoints2, 11), 32);
	}

	/**
	 * @param pkScore : le score empaqueté
	 * @param t       : l'equipe donnée
	 * @return le nombre de plis remportés par l'équipe donnée dans le tour courant
	 *         des scores empaquetés donnés
	 */
	public static int turnTricks(long pkScore, TeamId t) {
		assert isValid(pkScore);

		if (t == TeamId.TEAM_1) {
			return (int) Bits64.extract(pkScore, 0, 4);
		}
		return (int) Bits64.extract(pkScore, 32, 4);
	}

	/**
	 * @param pkScore : le score empaqueté
	 * @param t       : l'equipe donnée
	 * @return le nombre de points remportés par l'équipe donnée dans le tour
	 *         courant des scores empaquetés donnés
	 */
	public static int turnPoints(long pkScore, TeamId t) {
		assert isValid(pkScore);
		if (t == TeamId.TEAM_1) {
			return (int) Bits64.extract(pkScore, 4, 9);
		}

		return (int) Bits64.extract(pkScore, 36, 9);
	}

	/**
	 * @param pkScore : scores empaquetés
	 * @param t       : l'equipe donnée
	 * @return le nombre de points reportés par l'équipe donnée dans les tours
	 *         précédents (sans inclure le tour courant) des scores empaquetés
	 *         donnés
	 */
	public static int gamePoints(long pkScore, TeamId t) {
		assert isValid(pkScore);
		if (t == TeamId.TEAM_1) {
			return (int) Bits64.extract(pkScore, 13, 11);
		}

		return (int) Bits64.extract(pkScore, 45, 11);
	}

	/**
	 * @param pkScore : scores empaquetes
	 * @param t       : l'equipe donnée
	 * @return le nombre total de points remportés par l'équipe donnée dans la
	 *         partie courante des scores empaquetés donnés
	 */
	public static int totalPoints(long pkScore, TeamId t) {
		assert isValid(pkScore);
		return gamePoints(pkScore, t) + turnPoints(pkScore, t);
	}

	/**
	 * @param pkScore     : scores empaquetes
	 * @param winningTeam : l'equipe gagnante
	 * @param trickPoints : points remportés dans le pli
	 * @return les scores empaquetés donnés mis à jour
	 */
	public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
		assert isValid(pkScore);

		if (winningTeam == TeamId.TEAM_1) {
			if (turnTricks(pkScore, winningTeam) == 8) {
				return pack(turnTricks(pkScore, TeamId.TEAM_1) + 1,
						turnPoints(pkScore, TeamId.TEAM_1) + trickPoints + Jass.MATCH_ADDITIONAL_POINTS,
						gamePoints(pkScore, TeamId.TEAM_1),

						turnTricks(pkScore, TeamId.TEAM_2), turnPoints(pkScore, TeamId.TEAM_2),
						gamePoints(pkScore, TeamId.TEAM_2));
			}

			return pack(turnTricks(pkScore, TeamId.TEAM_1) + 1, turnPoints(pkScore, TeamId.TEAM_1) + trickPoints,
					gamePoints(pkScore, TeamId.TEAM_1),

					turnTricks(pkScore, TeamId.TEAM_2), turnPoints(pkScore, TeamId.TEAM_2),
					gamePoints(pkScore, TeamId.TEAM_2));

		}

		if (turnTricks(pkScore, winningTeam) == 8) {
			return pack(turnTricks(pkScore, TeamId.TEAM_1), turnPoints(pkScore, TeamId.TEAM_1),
					gamePoints(pkScore, TeamId.TEAM_1),

					turnTricks(pkScore, TeamId.TEAM_2) + 1,
					turnPoints(pkScore, TeamId.TEAM_2) + trickPoints + Jass.MATCH_ADDITIONAL_POINTS,
					gamePoints(pkScore, TeamId.TEAM_2));

		}

		return pack(turnTricks(pkScore, TeamId.TEAM_1), turnPoints(pkScore, TeamId.TEAM_1),
				gamePoints(pkScore, TeamId.TEAM_1),

				turnTricks(pkScore, TeamId.TEAM_2) + 1, turnPoints(pkScore, TeamId.TEAM_2) + trickPoints,
				gamePoints(pkScore, TeamId.TEAM_2));

	}

	/**
	 * @param pkScore : scores empaquete
	 * @return les scores empaquetés donnés mis à jour pour le tour prochain
	 */
	public static long nextTurn(long pkScore) {
		assert isValid(pkScore);
		return pack(0, 0, (int) (Bits64.extract(pkScore, 4, 9) + Bits64.extract(pkScore, 13, 11)), 0, 0,
				(int) (Bits64.extract(pkScore, 36, 9) + Bits64.extract(pkScore, 45, 11)));
	}

	/**
	 * @param pkScore : scores empaquete
	 * @return la représentation textuelle des scores
	 */
	public static String toString(long pkScore) {
		assert isValid(pkScore);
		return "(" + turnTricks(pkScore, TeamId.TEAM_1) + "," + turnPoints(pkScore, TeamId.TEAM_1) + ","
				+ gamePoints(pkScore, TeamId.TEAM_1) + ")/(" + turnTricks(pkScore, TeamId.TEAM_2) + ","
				+ turnPoints(pkScore, TeamId.TEAM_2) + "," + gamePoints(pkScore, TeamId.TEAM_2) + ")";
	}

}
