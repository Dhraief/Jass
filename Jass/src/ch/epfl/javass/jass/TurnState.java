package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;

/**
 * TurnState : etat du tour
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class TurnState {

	private final long pkScore;
	private final long pkUnplayedCards;
	private final int pkTrick;

	// Constructeur prive
	private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
		this.pkScore = pkScore;
		this.pkUnplayedCards = pkUnplayedCards;
		this.pkTrick = pkTrick;
	}

	/**
	 * @param trump       : atout
	 * @param score       : scores
	 * @param firstPlayer : premier joueur du tour
	 * @return l'état initial correspondant à un tour de jeu dont l'atout, le score
	 *         initial et le joueur initial sont ceux donnés
	 */
	public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
		int pkTrick = (trump.ordinal() << 30) | (firstPlayer.ordinal() << 28) | (PackedCard.INVALID << 18)
				| (PackedCard.INVALID << 12) | (PackedCard.INVALID << 6) | (PackedCard.INVALID);

		long pkUnPlayedCards = Bits64.mask(0, 9) | Bits64.mask(16, 9) | Bits64.mask(32, 9) | Bits64.mask(48, 9);
		return new TurnState(score.packed(), pkUnPlayedCards, pkTrick);
	}

	/**
	 * @param pkScore         : scores empaquetes
	 * @param pkUnplayedCards : ensemble de cartes non jouees empaquete
	 * @param pkTrick         : pli courant empaquete
	 * @throws IllegalArgumentException si l'une d'entre elles est invalide
	 * @return l'état dont les composantes (empaquetées) sont celles données
	 */
	public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {

		Preconditions.checkArgument(
				PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));

		return new TurnState(pkScore, pkUnplayedCards, pkTrick);
	}

	/**
	 * @return la version empaquetée du score courant
	 */
	public long packedScore() {
		return pkScore;
	}

	/**
	 * @return la version empaquetée de l'ensemble des cartes pas encore jouées
	 */
	public long packedUnplayedCards() {
		return pkUnplayedCards;
	}

	/**
	 * @return la version empaquetée du pli courant
	 */
	public int packedTrick() {
		return pkTrick;
	}

	/**
	 * @return le score courant
	 */
	public Score score() {
		return Score.ofPacked(pkScore);
	}

	/**
	 * @return l'ensemble des cartes pas encore jouées
	 */
	public CardSet unplayedCards() {
		return CardSet.ofPacked(pkUnplayedCards);
	}

	/**
	 * @return le pli courant
	 */
	public Trick trick() {
		return Trick.ofPacked(pkTrick);
	}

	/**
	 * @return vrai ssi l'état est terminal et faux sinon
	 */
	public boolean isTerminal() {
		return pkTrick == PackedTrick.INVALID;
	}

	/**
	 * @throws IllegalStateException si le pli courant est plein
	 * @return l'identité du joueur devant jouer la prochaine carte
	 */
	public PlayerId nextPlayer() {
		if (PackedTrick.isFull(pkTrick)) {
			throw new IllegalStateException();
		}
		return trick().player(trick().size());
	}

	/**
	 * @param card : carte donnée
	 * @throws IllegalStateException si le pli courant est plein
	 * @return l'état correspondant à celui auquel on l'applique après que le
	 *         prochain joueur ait joué la carte donnée
	 */
	public TurnState withNewCardPlayed(Card card) {
		if (PackedTrick.isFull(pkTrick)) {
			throw new IllegalStateException();
		}

		int pkTrick = PackedTrick.withAddedCard(this.pkTrick, card.packed());
		return new TurnState(this.pkScore, PackedCardSet.remove(this.pkUnplayedCards, card.packed()), pkTrick);
	}

	/**
	 * @throws IllegalStateException si le pli courant n'est pas terminé
	 * @return l'état correspondant à celui auquel on l'applique après que le pli
	 *         courant ait été ramassé
	 */
	public TurnState withTrickCollected() {
		if (!PackedTrick.isFull(pkTrick)) {
			throw new IllegalStateException();
		}
		int pkTrick = PackedTrick.nextEmpty(this.pkTrick);
		long score = PackedScore.withAdditionalTrick(pkScore, PackedTrick.winningPlayer(this.pkTrick).team(),
				PackedTrick.points(this.pkTrick));

		return new TurnState(score, this.pkUnplayedCards, pkTrick);
	}

	/**
	 * @param card : carte donnée
	 * @throws IllegalStateException si le pli courant est plein
	 * @return l'état correspondant à celui auquel on l'applique après que le
	 *         prochain joueur ait joué la carte donnée, et que le pli courant ait
	 *         été ramassé s'il est alors plein
	 */
	public TurnState withNewCardPlayedAndTrickCollected(Card card) {
		if (PackedTrick.isFull(pkTrick)) {
			throw new IllegalStateException();
		}
		if (PackedTrick.size(pkTrick) == 3) {
			return withNewCardPlayed(card).withTrickCollected();
		}
		return withNewCardPlayed(card);
	}
}
