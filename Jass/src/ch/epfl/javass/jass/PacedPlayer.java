package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * PacedPlayer : Permet de s'assurer qu'un joueur met un temps minimum pour
 * jouer
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class PacedPlayer implements Player {

	private final Player underlyingPlayer;
	private final double minTime;

	/**
	 * Construit un joueur qui se comporte exactement comme le joueur sous-jacent
	 * donné
	 * 
	 * @param underlyingPlayer : joueur sous-jacent donné
	 * @param minTime          : temps minimum en secondes
	 */
	public PacedPlayer(Player underlyingPlayer, double minTime) {
		this.underlyingPlayer = underlyingPlayer;
		this.minTime = minTime;
	}

	/*
	 * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
	 */
	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {

		long current = System.currentTimeMillis();
		Card card = underlyingPlayer.cardToPlay(state, hand);
		long duration = System.currentTimeMillis() - current;
		if ((minTime * 1000) - duration > 0) {
			try {
				Thread.sleep((long) ((minTime * 1000) - duration));
			} catch (InterruptedException e) {
				/* ignore */ }
		}

		return card;
	}

	/* 
	 * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
	 */
	@Override
	public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		underlyingPlayer.setPlayers(ownId, playerNames);
	}

	/*
	 * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
	 */
	@Override
	public void updateHand(CardSet newHand) {
		underlyingPlayer.updateHand(newHand);
	}

	/*
	 * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
	 */
	@Override
	public void setTrump(Color trump) {
		underlyingPlayer.setTrump(trump);
	}

	/*
	 * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
	 */
	@Override
	public void updateTrick(Trick newTrick) {
		underlyingPlayer.updateTrick(newTrick);
	}

	/*
	 * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
	 */
	@Override
	public void updateScore(Score score) {
		underlyingPlayer.updateScore(score);
	}

	/* 
	 * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
	 */
	@Override
	public void setWinningTeam(TeamId winningTeam) {
		underlyingPlayer.setWinningTeam(winningTeam);
	}

}
