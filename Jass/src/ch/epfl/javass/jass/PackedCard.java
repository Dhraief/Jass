package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * PackedCard : Carte empaquetee
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class PackedCard {

	/**
	 * Carte empaquetee invalide
	 */
	public static final int INVALID = 0b111111;

	// Constructeur prive
	private PackedCard() {
	};

	/**
	 * @param pkCard : carte empaquetee
	 * @return vrai ssi la valeur donnée est une carte empaquetée valide et faux
	 *         sinon
	 */
	public static boolean isValid(int pkCard) {
		return Bits32.extract(pkCard, 0, 4) <= 8 && Bits32.extract(pkCard, 6, 26) == 0;
	}

	/**
	 * @param color : couleur donnée
	 * @param rank  : rang donné
	 * @return la carte empaquetée de couleur et rang donnés
	 */
	public static int pack(Card.Color color, Card.Rank rank) {
		return Bits32.pack(rank.ordinal(), 4, color.ordinal(), 2);
	}

	/**
	 * @param pkCard : carte empaquetee
	 * @return la couleur de la carte empaquetée donnée
	 */
	public static Card.Color color(int pkCard) {
		assert isValid(pkCard);
		return Color.values()[Bits32.extract(pkCard, 4, 2)];
	}

	/**
	 * @param pkCard : carte empaquetée
	 * @returnle rang de la carte empaquetée donnée
	 */
	public static Card.Rank rank(int pkCard) {
		assert isValid(pkCard);
		return Rank.values()[Bits32.extract(pkCard, 0, 4)];
	}

	/**
	 * @param trump   : atout
	 * @param pkCardL : premiere carte empaquetee donnée
	 * @param pkCardR : seconde carte empaquetée donnée
	 * @return vrai ssi la première carte donnée est supérieure à la seconde et
	 *         retourne faux sinon sachant que l'atout est trump
	 */
	public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
		assert isValid(pkCardL) && isValid(pkCardR);

		if ((color(pkCardL) != color(pkCardR)) && ((color(pkCardL) == trump) || (color(pkCardR) == trump))) {
			return color(pkCardL) == trump;
		}

		if (color(pkCardL) == color(pkCardR)) {
			if (color(pkCardL) == trump) {
				return (rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal());
			} else {
				return (rank(pkCardL).ordinal() > rank(pkCardR).ordinal());
			}
		}

		return false;
	}

	/**
	 * @param trump  : atout
	 * @param pkCard : carte empaquetée
	 * @return la valeur de la carte empaquetée donnée, sachant que l'atout est
	 *         trump
	 */
	public static int points(Card.Color trump, int pkCard) {
		assert isValid(pkCard);

		if (color(pkCard) == trump) {
			switch (rank(pkCard).trumpOrdinal()) {
			case 0:
				return 0;
			case 1:
				return 0;
			case 2:
				return 0;
			case 3:
				return 10;
			case 4:
				return 3;
			case 5:
				return 4;
			case 6:
				return 11;
			case 7:
				return 14;

			}
			return 20;
		}

		switch (rank(pkCard).ordinal()) {
		case 0:
			return 0;
		case 1:
			return 0;
		case 2:
			return 0;
		case 3:
			return 0;
		case 4:
			return 10;
		case 5:
			return 2;
		case 6:
			return 3;
		case 7:
			return 4;

		}
		return 11;

	}

	/**
	 * @param pkCard : carte empaquetée
	 * @return représentation de la carte empaquetée donnée sous forme de chaîne de
	 *         caractères composée du symbole de la couleur et du nom abrégé du rang
	 */
	public static String toString(int pkCard) {
		return color(pkCard).toString() + rank(pkCard).toString();
	}

}
