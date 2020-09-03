package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Score : Scores de la partie de Jass
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class Score {

	private final long pkScore;
	/**
	 * Score initial de la partie
	 */
	public static final Score INITIAL = new Score(0);

	// Constructeur prive
	private Score(long c) {
		pkScore = c;
	}

	/**
	 * @param pkScore : scores empaquetes
	 * @throws IllegalArgumentException si pkScore ne représente pas des scores
	 *                                  empaquetés valides
	 * @return les scores dont pkScore est la version empaquetée
	 */
	public static Score ofPacked(long pkScore) {
		Preconditions.checkArgument(PackedScore.isValid(pkScore));
		return new Score(pkScore);
	}

	/**
	 * @return version empaquete des scores
	 */
	public long packed() {
		return pkScore;
	}

	/**
	 * @param t : equipe donnee
	 * @return le nombre de plis remportés par l'équipe donnée dans le tour courant
	 *         du récepteur
	 */
	public int turnTricks(TeamId t) {
		return PackedScore.turnTricks(pkScore, t);
	}

	/**
	 * @param t : equipe donnee
	 * @return le nombre de points remportés par l'équipe donnée dans le tour
	 *         courant du récepteur
	 */
	public int turnPoints(TeamId t) {
		return PackedScore.turnPoints(pkScore, t);
	}

	/**
	 * @param t : equipe donnee
	 * @return le nombre de points reportés par l'équipe donnée dans les tours
	 *         précédents (sans inclure le tour courant) du récepteur
	 */
	public int gamePoints(TeamId t) {
		return PackedScore.gamePoints(pkScore, t);
	}

	/**
	 * @param t : equipe donnee
	 * @return le nombre total de points remportés par l'équipe donnée dans la
	 *         partie courante du récepteur
	 */
	public int totalPoints(TeamId t) {
		return PackedScore.totalPoints(pkScore, t);
	}

	/**
	 * @param winningTeam : equipe gagnante
	 * @param trickPoints : valeur du pli remporté par l'equipe gagnante
	 * @throws IllegalArgumentException si le nombre de points donné est inférieur à 0
	 * @return les scores donnés mis à jour
	 */
	public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
		Preconditions.checkArgument(trickPoints >= 0);
		return new Score(PackedScore.withAdditionalTrick(pkScore, winningTeam, trickPoints));
	}

	/**
	 * @return les scores mis à jour pour le tour prochain
	 */
	public Score nextTurn() {
		return new Score(PackedScore.nextTurn(pkScore));
	}

	/* 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(pkScore);
	}

	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PackedScore.toString(pkScore);
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object thatO) {
		if (!(thatO instanceof Score)) {
			return false;
		}
		return ((Score) thatO).pkScore == this.pkScore;
	}

}
