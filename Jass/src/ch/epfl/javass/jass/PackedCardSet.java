package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;

/**
 * PackedCardSet : ensemble de cartes empaquete
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class PackedCardSet {

	/**
	 * l'ensemble de cartes empaquete vide
	 */
	public static final long EMPTY = 0;
	/**
	 * l'ensemble des 36 cartes du jeu de Jass empaquete
	 */
	public static final long ALL_CARDS = Bits64.mask(0, 9) | Bits64.mask(16, 9) | Bits64.mask(32, 9)
			| Bits64.mask(48, 9);
	// Tableau contenant les valeurs de retours pour la methode trumpAbove (pour une
	// execution rapide)
	private final static long[][] tabTrumpAbove = tabTrumpAbove();
	// Tableau contenant les valeurs de retours pour la methode subsetOfColor (pour
	// une
	// execution rapide)
	private final static long[] tabSubsetOfColor = tabSubsetOfColor();

	// Constructeur privé
	private PackedCardSet() {
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @return vrai si l'ensemble de carte empaqueté est valide et faux sinon
	 */
	public static boolean isValid(long pkCardSet) {
		return (((pkCardSet & (~ALL_CARDS)) == 0));
	}

	/**
	 * @param pkCard: carte atout à comparer sous forme empaquetée
	 * @return l'ensemble de cartes atout supérieur à la carte à comparer
	 */
	public static long trumpAbove(int pkCard) {
		return tabTrumpAbove[Card.ofPacked(pkCard).color().ordinal()][Card.ofPacked(pkCard).rank().ordinal()];
	}

	/**
	 * @param pkCard : carte empaquetée
	 * @return l'ensemble de cartes empaqueté contenant uniquement la carte
	 *         empaquetée donnée
	 */
	public static long singleton(int pkCard) {
		return (1L << Card.ofPacked(pkCard).rank().ordinal()) << (16 * Card.ofPacked(pkCard).color().ordinal());
	}

	/**
	 * @param pkCardSet : l'ensemble de cartes empaqueté
	 * @return vrai ssi l'ensemble de cartes empaqueté donné est vide et faux sinon
	 */
	public static boolean isEmpty(long pkCardSet) {
		return pkCardSet == EMPTY;
	}

	/**
	 * @param pkCardSet : l'ensemble de cartes empaqueté
	 * @return la taille de l'ensemble de cartes empaqueté donné (nombre de cartes)
	 */
	public static int size(long pkCardSet) {
		return Long.bitCount(pkCardSet);
	}

	/**
	 * @param       pkCardSet: ensemble de cartes empaqueté
	 * @param index : index donné
	 * @return la version empaquetée de la carte d'index donné de l'ensemble de
	 *         cartes empaqueté donné
	 */
	public static int get(long pkCardSet, int index) {

		int pos = Long.numberOfTrailingZeros(pkCardSet);
		for (int i = 0; i < index; i++) {
			pkCardSet = pkCardSet & Bits64.mask(pos + 1, 63 - pos);
			pos = Long.numberOfTrailingZeros(pkCardSet);
		}
		int colorOrdinal = pos / 16;
		int rankOrdinal = pos % 16;

		return PackedCard.pack(Card.Color.values()[colorOrdinal], Card.Rank.values()[rankOrdinal]);
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @param pkCard    : carte empaquetée à ajouter
	 * @return l'ensemble de cartes données avec pkCard ajoutée
	 */
	public static long add(long pkCardSet, int pkCard) {
		return pkCardSet | singleton(pkCard);
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @param pkCard    : carte empaqueté donnée
	 * @return l'ensemble de cartes empaqueté donné après supression de la carte
	 *         empaquetée donnée
	 */
	public static long remove(long pkCardSet, int pkCard) {
		return pkCardSet & (~singleton(pkCard));
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @param pkCard    : carte empaquetee donnée
	 * @return vrai ssi l'ensemble de cartes empaqueté donné contient la carte
	 *         donnée et faux sinon
	 */
	public static boolean contains(long pkCardSet, int pkCard) {
		return (pkCardSet | singleton(pkCard)) == pkCardSet;
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @return le complément de l'ensemble de cartes empaqueté donné
	 */
	public static long complement(long pkCardSet) {
		return (pkCardSet ^ ALL_CARDS);
	}

	/**
	 * @param pkCardSet1 : ensemble de cartes empaquete
	 * @param pkCardSet2 : ensemble de cartes empaquete
	 * @return l'union des deux ensembles de cartes empaquetées donnés
	 */
	public static long union(long pkCardSet1, long pkCardSet2) {
		return pkCardSet1 | pkCardSet2;
	}

	/**
	 * @param pkCardSet1 : ensemble de cartes empaquete
	 * @param pkCardSet2 : ensemble de cartes empaquete
	 * @return l'intersection des deux ensembles de cartes empaquetées donnés
	 */
	public static long intersection(long pkCardSet1, long pkCardSet2) {
		return pkCardSet1 & pkCardSet2;
	}

	/**
	 * @param pkCardSet1 : ensemble de cartes empaquete
	 * @param pkCardSet2 : ensemble de cartes empaquete
	 * @return l'ensemble des cartes empaquetées qui se trouvent dans pkCardSet1 et
	 *         pas dans pkCardSet2
	 */
	public static long difference(long pkCardSet1, long pkCardSet2) {
		return (pkCardSet1 & ~pkCardSet2);
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @param color     : couleur donnée
	 * @return le sous ensemble de pkCardSet de couleur color donnée
	 */
	public static long subsetOfColor(long pkCardSet, Card.Color color) {
		return intersection(tabSubsetOfColor[color.ordinal()], pkCardSet);
	}

	/**
	 * @param pkCardSet : ensemble de cartes empaquete
	 * @return la représentation textuelle de l'ensemble de cartes empaqueté donné
	 */
	public static String toString(long pkCardSet) {
		StringJoiner j = new StringJoiner(",", "{", "}");

		for (int i = 0; i < size(pkCardSet); i++) {
			Card c = Card.ofPacked(get(pkCardSet, i));
			j.add(c.color().toString() + c.rank().toString());
		}
		return j.toString();
	}

	// Methode auxiliaire pour remplir l'attribut prive tabTrumpAbove necessaire
	// pour la
	// methode trumpAbove
	private static long[][] tabTrumpAbove() {
		long[][] tab = new long[4][9];
		for (int i = 0; i < Card.Color.COUNT; i++) {
			for (int j = 0; j < Card.Rank.COUNT; j++) {
				long s = 0L;
				for (int k = 0; k < Card.Rank.COUNT; k++) {
					if (Card.Rank.values()[k].trumpOrdinal() > Card.Rank.values()[j].trumpOrdinal()) {
						s = add(s, PackedCard.pack(Card.Color.values()[i], Card.Rank.values()[k]));
					}
				}
				tab[i][j] = s;
			}
		}
		return tab;
	}

	// Methode auxiliaire pour remplir l'attribut prive tabSubsetOfColor necessaire
	// pour la
	// methode subsetOfColor
	private static long[] tabSubsetOfColor() {
		long[] tabSubsetOfColor = new long[Card.Color.COUNT];
		for (int i = 0; i < Card.Color.COUNT; i++) {
			long s = 0L;
			for (int j = 0; j < Card.Rank.COUNT; j++) {
				s = add(s, PackedCard.pack(Card.Color.values()[i], Card.Rank.values()[j]));
			}
			tabSubsetOfColor[i] = s;
		}
		return tabSubsetOfColor;
	}

}
