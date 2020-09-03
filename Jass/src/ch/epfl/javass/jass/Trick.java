package ch.epfl.javass.jass;


import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Trick : Pli
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class Trick {

	private final int pkTrick;

	// Constructeur prive
	private Trick(int pkTrick) {
		this.pkTrick = pkTrick;
	}

	/**
	 * Pli invalide
	 */
	public static final Trick INVALID = new Trick(-1);

	/**
	 * @param trump       : atout
	 * @param firstPlayer : premier joueur donné
	 * @return le pli vide sans aucune carte d'index 0 avec l'atout et le premier
	 *         joueur donnés
	 */
	public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
		return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
	}

	/**
	 * @param packed : pli empaquete
	 * @throws IllegalArgumentException si le pli empaquete packed n'est pas valide
	 * @return le pli dont la version empaquetée
	 */
	public static Trick ofPacked(int packed) {
		Preconditions.checkArgument(PackedTrick.isValid(packed));
		return new Trick(packed);
	}

	/**
	 * @return le pli empaquete
	 */
	public int packed() {
		return pkTrick;
	}

	/**
	 * @throws IllegalStateException si le pli n'est pas plein
	 * @return le pli vide suivant celui donné
	 */
	public Trick nextEmpty() {
		if (!PackedTrick.isFull(pkTrick)) {
			throw new IllegalStateException();
		}

		return new Trick(PackedTrick.nextEmpty(pkTrick));
	}

	/**
	 * @return vrai ssi le pli est vide et faux sinon
	 */
	public boolean isEmpty() {
		return PackedTrick.isEmpty(pkTrick);
	}

	/**
	 * @return vrai ssi le pli est plein et faux sinon
	 */
	public boolean isFull() {
		return PackedTrick.isFull(pkTrick);
	}

	/**
	 * @return vrai ssi le pli est le dernier du tour et faux sinon
	 */
	public boolean isLast() {
		return PackedTrick.isLast(pkTrick);
	}

	/**
	 * @return taille du pli
	 */
	public int size() {
		return PackedTrick.size(pkTrick);
	}

	/**
	 * @return atout du pli
	 */
	public Color trump() {
		return PackedTrick.trump(pkTrick);
	}

	/**
	 * @return index du pli
	 */
	public int index() {
		return PackedTrick.index(pkTrick);
	}

	/**
	 * @param index : index du joueur
	 * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0
	 *                                   (inclus) et 4 (exclus)
	 * @return le joueur d'index donné dans le pli
	 */
	public PlayerId player(int index) {
		Preconditions.checkIndex(index, 4);
		return PackedTrick.player(pkTrick, index);
	}

	/**
	 * @param index: index de la carte
	 * @throws IndexOutOfBoundsException si l'index n'est pas compris entre 0
	 *                                   (inclus) et la taille du pli (exclus)
	 * @return la carte du pli à l'index donné
	 */
	public Card card(int index) {
		Preconditions.checkIndex(index, size());
		return Card.ofPacked(PackedTrick.card(pkTrick, index));
	}

	/**
	 * @param card : carte à ajouter
	 * @throws IllegalStateException si le pli est plein
	 * @return un pli identique à celui donné, mais à laquelle la carte donnée a été
	 *         ajoutée
	 */
	public Trick withAddedCard(Card card) {
		if (isFull()) {
			throw new IllegalStateException();
		}

		return new Trick(PackedTrick.withAddedCard(pkTrick, card.packed()));
	}

	/**
	 * @throws IllegalStateException si le pli est vide
	 * @return la couleur de base du pli
	 */
	public Color baseColor() {
		if (isEmpty()) {
			throw new IllegalStateException();
		}
		return PackedTrick.baseColor(pkTrick);
	}

	/**
	 * @param hand : main du joueur
	 * @throws IllegalStateException si le pli est plein
	 * @return le sous-ensemble des cartes de la main hand qui peuvent être jouées
	 *         comme prochaine carte du pli
	 */
	public CardSet playableCards(CardSet hand) {
		if (isFull()) {
			throw new IllegalStateException();
		}

		return CardSet.ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
	}

	/**
	 * @return la valeur du pli, en tenant compte des « 5 de der »
	 */
	public int points() {
		return PackedTrick.points(pkTrick);
	}

	/**
	 * @throws IllegalStateException si le pli est vide
	 * @return l'identité du joueur menant le pli
	 */
	public PlayerId winningPlayer() {
		if (isEmpty()) {
			throw new IllegalStateException();
		}
		return PackedTrick.winningPlayer(pkTrick);
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return pkTrick;
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Trick)) {
			return false;
		}
		return ((Trick) that).pkTrick == this.pkTrick;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PackedTrick.toString(pkTrick);
	}

}



