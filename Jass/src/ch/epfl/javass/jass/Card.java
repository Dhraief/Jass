package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Carte d'un jeu
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class Card {

	private final int pkCard;

	// Constructeur prive
	private Card(int c) {
		pkCard = c;
	}

	/**
	 * Color : couleur de la carte
	 *
	 * @author Mohamed Ali Dhraief (283509)
     * @author Amine Atallah (284592)
	 */
	public enum Color {

		/**
		 * Les differentes couleurs des cartes de Jass (dans l'ordre: pique, coeur ,
		 * carreaux, trefle)
		 */
		SPADE, HEART, DIAMOND, CLUB;
		/**
		 * liste immuable contenant toutes les valeurs du type énuméré dans leur ordre
		 * de déclaration
		 */
		public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));

		/**
		 * nombre de valeurs du type énuméré
		 */
		public static final int COUNT = 4;

		/* 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this) {
			case SPADE:
				return "\u2660";
			case HEART:
				return "\u2665";
			case DIAMOND:
				return "\u2666";
			default:
				return "\u2663";

			}

		}
	}

	/**
	 * Rank : Rang de la carte
	 * @author Mohamed Ali Dhraief (283509)
     * @author Amine Atallah (284592)
	 */
	public enum Rank {
		/**
		 * Les rangs des differentes cartes de Jass
		 */
		SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

		/**
		 * liste immuable contenant toutes les valeurs du type énuméré dans leur ordre
		 * de déclaration
		 */
		public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
		/**
		 * nombre de valeurs du type énuméré
		 */
		public static final int COUNT = 9;

		/**
		 * @return la position, comprise entre 0 et 8, de la carte d'atout ayant ce rang
		 */
		public int trumpOrdinal() {
			switch (this) {
			case SIX:
				return 0;
			case SEVEN:
				return 1;
			case EIGHT:
				return 2;
			case TEN:
				return 3;
			case QUEEN:
				return 4;
			case KING:
				return 5;
			case ACE:
				return 6;
			case NINE:
				return 7;
			default:
				return 8;
			}
		}

		/*
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {

			switch (this) {
			case SIX:
				return "6";
			case SEVEN:
				return "7";
			case EIGHT:
				return "8";
			case TEN:
				return "10";
			case QUEEN:
				return "Q";
			case KING:
				return "K";
			case ACE:
				return "A";
			case NINE:
				return "9";
			default:
				return "J";

			}

		}

	}

	/**
	 * @param c : couleur de la carte
	 * @param r : rang de la carte
	 * @return la carte de couleur et de rang donnés
	 */
	public static Card of(Color c, Rank r) {
		Card card = new Card(PackedCard.pack(c, r));
		return card;
	}

	/**
	 * @param packed : carte empaquetee
	 * @throws IllegalArgumentException si l'argument packed ne représente pas une
	 *                                  carte empaquetée valide
	 * @return la carte dont packed est la version empaquetée
	 */
	public static Card ofPacked(int packed) {
		Preconditions.checkArgument(PackedCard.isValid(packed));
		return new Card(packed);
	}

	/**
	 * @return la version empaquetée de la carte
	 */
	public int packed() {
		return pkCard;
	}

	/**
	 * @return la couleur de la carte
	 */
	public Color color() {
		return PackedCard.color(pkCard);
	}

	/**
	 * @return le rang de la carte
	 */
	public Rank rank() {
		return PackedCard.rank(pkCard);
	}

	/**
	 * @param trump : atout
	 * @param that  : carte empaquetee
	 * @return vrai ssi le récepteur (c-à-d la carte à laquelle on applique la
	 *         méthode) est supérieur à la carte passée en argument, sachant que
	 *         l'atout est trump et retourne faux sinon
	 */
	public boolean isBetter(Color trump, Card that) {
		return PackedCard.isBetter(trump, pkCard, that.pkCard);
	}

	/**
	 * @param trump : atout
	 * @return la valeur de la carte, sachant que l'atout est trump
	 */
	public int points(Color trump) {
		return PackedCard.points(trump, pkCard);
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object thatO) {
		if (!(thatO instanceof Card)) {
			return false;
		}
		return ((Card) thatO).pkCard == this.pkCard;
	}

	/* 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return packed();
	}

	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return PackedCard.toString(pkCard);
	}

}
