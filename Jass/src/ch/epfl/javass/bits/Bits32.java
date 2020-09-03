package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Bits32 : Manipulation d'entiers de 32 bits 
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class Bits32 {

	// Constructeur prive
	private Bits32() {
	}

	/**
	 * @param start : debut donné
	 * @param size  : taille donnée
	 * @throws IllegalArgumentException si start et size ne désignent pas une plage
	 *                                  de bits valide
	 * @return un entier dont les bits d'index allant de start (inclus) à start +
	 *         size (exclus) valent 1, les autres valant 0
	 * 
	 */
	public static int mask(int start, int size) {
		Preconditions.checkArgument(start >= 0 && size >= 0 && start + size <= Integer.SIZE);

		if (size == Integer.SIZE)
			return -1;
		return ((1 << size) - 1) << (start);
	}

	/**
	 * @param bits: entier où on va faire l'extraction
	 * @param start : début donné
	 * @param size  : taille donnée
	 * @throws IllegalArgumentException si start et size ne désignent pas une plage
	 *                                  de bits valide
	 * @return une valeur dont les size bits de poids faible sont égaux à ceux de
	 *         bits allant de l'index start (inclus) à l'index start + size (exclus)
	 */
	public static int extract(int bits, int start, int size) {
		Preconditions.checkArgument(start >= 0 && size >= 0 && start + size <= Integer.SIZE);
		return (bits & Bits32.mask(start, size)) >>> (start);

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
	public static int pack(int v1, int s1, int v2, int s2) {
		Preconditions.checkArgument(check(v1, s1) && check(v2, s2) && s1 + s2 <= Integer.SIZE);
		return (v2 << s1) | v1;
	}

	/**
	 * @param v1 : entier donné
	 * @param s1 : nombre de bits occupés par v1
	 * @param v2 : entier donné
	 * @param s2 : nombre de bits occupés par v2
	 * @param v3 : entier donné
	 * @param s3 : nombre de bits occupés par v3
	 * @return la version pack à 3 arguments (v1 occupe s1 de poids faible, v2
	 *         occupe s2 bits suivants et v3 s3 bits suivants)
	 */
	public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
		return pack(pack(v1, s1, v2, s2), s2 + s1, v3, s3);
	}

	/**
	 * @param v1 : entier donné
	 * @param s1 : nombre de bits occupés par v1
	 * @param v2 : entier donné
	 * @param s2 : nombre de bits occupés par v2
	 * @param v3 : entier donné
	 * @param s3 : nombre de bits occupés par v3
	 * @param v4 : entier donné
	 * @param s4 : nombre de bits occupés par v4
	 * @param v5 : entier donné
	 * @param s5 : nombre de bits occupés par v5
	 * @param v6 : entier donné
	 * @param s6 : nombre de bits occupés par v6
	 * @param v7 : entier donné
	 * @param s7 : nombre de bits occupés par v7
	 * @return la version de pack à 7 arguments (v1 occupe s1 de poids faible, v2
	 *         occupe s2 bits suivants...etc jusqu'à v7)
	 */
	public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6,
			int s6, int v7, int s7) {
		return pack(pack(v1, s1, v2, s2, v3, s3), s2 + s1 + s3, pack(v4, s4, v5, s5, v6, s6), s4 + s5 + s6, v7, s7);
	}

	// méthode qui vérifie si la taille est entre 1 et 31
	// et aussi que la représentation binaire de v1 est de taille inférieur (ou
	// égale) à s1
	private static boolean check(int v1, int s1) {
		return s1 >= 1 && s1 <= (Integer.SIZE - 1) && Integer.toBinaryString(v1).length() <= s1;
	}

}
