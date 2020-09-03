package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Bits64 : Manipulation d'entiers de 64 bits 
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class Bits64 {

	// Constructeur privé de Bits64
	private Bits64() {
	}
	/**
	 * @param start: debut donné
	 * @param size : taille donnée
	 * @throws IllegalArgumentException si start et size ne désignent pas une plage
	 *                                  de bits valide
	 * @return un entier dont les bits d'index allant de start (inclus) à start +
	 *         size (exclus) valent 1, les autres valant 0
	 * 
	 */
	public static long mask(int start, int size) {
		Preconditions.checkArgument(validPlage(start, size));

		if (size == Long.SIZE)
			return -1L;
		return ((1L << size) - 1L) << start;
	}

	/**
	 * @param bits: entier où on va faire l'extraction
	 * @param start : debut donné
	 * @param size : taille donnée
	 * @throws IllegalArgumentException si start et size ne désignent pas une plage
	 *                                  de bits valide
	 * @return une valeur dont les size bits de poids faible sont égaux à ceux de
	 *         bits allant de l'index start (inclus) à l'index start + size (exclus)
	 */
	public static long extract(long bits, int start, int size) {
		Preconditions.checkArgument(validPlage(start, size));
		return (bits & Bits64.mask(start, size)) >>> (start);
	}



	/**
	 * @param v1 : entier donné
	 * @param s1 : nombre de bits occupés par v1
	 * @param v2 : entier donné
	 * @param s2 : nombre de bits occupés par v2
	 * @return entier où les valeurs v1 et v2 sont empaquetées tel que v1 occupe les
	 *         s1 bits de poids faible, et v2 occupant les s2 bits suivants, tous
	 *         les autres bits valant 0
	 */
	public static long pack(long v1, int s1, long v2, int s2) {
		Preconditions.checkArgument(checkPack(v1, s1) && checkPack(v2, s2) && s1 + s2 <= Long.SIZE);
		return (v2 << s1) | v1;
	}

	// Methode qui verifier que start et size sont positifs ou nuls et que leur somme ne dépasse pas 64
	private static boolean validPlage(int start, int size) {
		return ((start >= 0) && (size >= 0) && (start + size <= Long.SIZE));
	}

	// méthdode auxiliaire qui vérifie si la taille est entre 1 et 63
	// et aussi que la représentation binaire de v est de taille inférieur (ou égale) à s
	private static boolean checkPack(long v, int s) {
		return s >= 1 && s <= (Long.SIZE - 1) && Long.toBinaryString(v).length() <= s;
	}
}


