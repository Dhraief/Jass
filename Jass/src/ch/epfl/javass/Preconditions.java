package ch.epfl.javass;

/**
 * Preconditions: Validation d'arguments
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah(284592)
 *
 */
public final class Preconditions {

	// Constructeur prive
	private Preconditions() {
	}

	/**
	 * Verifie si b est true
	 * 
	 * @param b : condition à verifier
	 * @throws IllegalArgumentException si la valeur b est fausse
	 */
	public static void checkArgument(boolean b) {
		if (b == false) {
			throw new IllegalArgumentException();
		}

	}

	/**
	 * @param index : index donné
	 * @param size  : taille donnée
	 * @throws IndexOutOfBoundsException si l'index est négatif ou l'index est plus
	 *                                   grand que size
	 * @return l'index si les arguments sont cohérants
	 */
	public static int checkIndex(int index, int size) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}

		return index;

	}

}
