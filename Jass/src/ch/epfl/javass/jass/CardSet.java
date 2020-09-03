package ch.epfl.javass.jass;

import java.util.List;
import ch.epfl.javass.Preconditions;

/**
 * CardSet: ensemble de cartes
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class CardSet {

	private final long pkCardSet;

	// Constructeur prive
	private CardSet(long packed) {
		this.pkCardSet = packed;
	}

	/**
	 * l'ensemble de cartes vide
	 */
	public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);

	/**
	 * l'ensemble des 36 cartes du jeu de Jass
	 */
	public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

	/**
	 * @param cards : liste de cartes
	 * @return l'ensemble de cartes contenues dans la liste donnée
	 */
	public static CardSet of(List<Card> cards) {
		long setOfCards = 0;
		for (int i = 0; i < cards.size(); i++) {
			setOfCards = PackedCardSet.add(setOfCards, cards.get(i).packed());
		}

		return new CardSet(setOfCards);
	}

	/**
	 * @param packed : ensemble empaquete
	 * @throws IllegalArgumentException si l'argument packed ne représente pas un
	 *                                  ensemble empaqueté valide
	 * @return l'ensemble de cartes dont packed est la version empaquetée
	 */
	public static CardSet ofPacked(long packed) {
		Preconditions.checkArgument(PackedCardSet.isValid(packed));
		return new CardSet(packed);
	}

	/**
	 * @return la version empaquetée de l'ensemble de cartes appelé
	 */
	public long packed() {
		return pkCardSet;
	}

	/**
	 * @return vrai ssi l'ensemble de carte est vide et faux sinon
	 */
	public boolean isEmpty() {
		return pkCardSet == 0;
	}

	/**
	 * @return le nombre de cartes dans l'ensemble
	 */
	public int size() {
		return PackedCardSet.size(pkCardSet);
	}

	/**
	 * @param index : index donné
	 * @return la carte (non-empaquetée) d'index (index) dans l'ensemble de cartes
	 *         appelant
	 */
	public Card get(int index) {
		return Card.ofPacked(PackedCardSet.get(pkCardSet, index));
	}

	/**
	 * @param card : carte à ajouter
	 * @return un ensemble de cartes après ajout de la carte (card)
	 */
	public CardSet add(Card card) {
		return new CardSet(PackedCardSet.add(pkCardSet, card.packed()));
	}

	/**
	 * @param card : carte à enlever
	 * @return un ensemble de cartes après suppression de la carte (card)
	 */
	public CardSet remove(Card card) {
		return new CardSet(PackedCardSet.remove(pkCardSet, card.packed()));
	}

	/**
	 * @param card : carte donnée
	 * @return vrai ssi l'ensemble de cartes contient la carte donnée et faux sinon
	 */
	public boolean contains(Card card) {
		return PackedCardSet.contains(pkCardSet, card.packed());
	}

	/**
	 * @return le complement à l'ensemble de cartes appelant
	 */
	public CardSet complement() {
		return new CardSet(PackedCardSet.complement(pkCardSet));
	}

	/**
	 * @param that: ensemble de cartes donné
	 * @return l'ensemble de cartes résultant entre l'union de l'ensemble de cartes
	 *         appelant et that
	 */
	public CardSet union(CardSet that) {
		return new CardSet(PackedCardSet.union(pkCardSet, that.pkCardSet));
	}

	/**
	 * @param that : ensemble de cartes donné
	 * @return l'ensemble de cartes résultant entre l'intersection de l'ensemble de
	 *         cartes appelant et that
	 */
	public CardSet intersection(CardSet that) {
		return new CardSet(PackedCardSet.intersection(pkCardSet, that.pkCardSet));
	}

	/**
	 * @param that : ensemble de cartes donné
	 * @return l'ensemble de cartes présents dans l'ensemble de cartes appelant et
	 *         non dans that
	 */
	public CardSet difference(CardSet that) {
		return new CardSet(PackedCardSet.difference(pkCardSet, that.pkCardSet));
	}

	/**
	 * @param color : couleur donnée
	 * @return le sous ensemble de cartes de couleur color, le sous ensemble étant
	 *         issu de l'ensemble de cartes appelant
	 */
	public CardSet subsetOfColor(Card.Color color) {
		return new CardSet(PackedCardSet.subsetOfColor(pkCardSet, color));
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(pkCardSet);
	}

	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof CardSet)) {
			return false;
		}
		return ((CardSet) that).pkCardSet == this.pkCardSet;
	}

	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PackedCardSet.toString(pkCardSet);
	}

}
