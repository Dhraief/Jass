package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

/**
 * PackedTrick: Pli empaquete
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */ 
public final class PackedTrick {
	
	/**
	 * Pli invalide empaquete
	 */
	public static final int INVALID = -1;
	
	//Constructeur privé
    private PackedTrick() {
    }
    
    /**
     * @param pkTrick : pli empaquete
     * @return vrai si l'entier donné représente un pli empaqueté valide et faux sinon
     */
    public static boolean isValid(int pkTrick) {
        return (Bits32.extract(pkTrick, 24, 4) >= 0
                && Bits32.extract(pkTrick, 24, 4) <= 8)
                && (   (PackedCard.isValid(Bits32.extract(pkTrick, 0, 6))
                        && PackedCard.isValid(Bits32.extract(pkTrick, 6, 6))
                        && PackedCard.isValid(Bits32.extract(pkTrick, 12, 6))
                        && PackedCard.isValid(Bits32.extract(pkTrick, 18, 6)))
                        || (PackedCard.isValid(Bits32.extract(pkTrick, 0, 6))
                                && PackedCard
                                        .isValid(Bits32.extract(pkTrick, 6, 6))
                                && PackedCard
                                        .isValid(Bits32.extract(pkTrick, 12, 6))
                                && (
                                     Bits32.extract(pkTrick, 18, 6)==PackedCard.INVALID ) )
                        || (PackedCard.isValid(Bits32.extract(pkTrick, 0, 6))
                                && PackedCard
                                        .isValid(Bits32.extract(pkTrick, 6, 6))
                                && ( Bits32.extract(pkTrick, 12, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 18, 6)==PackedCard.INVALID) )
                        
                        || (PackedCard.isValid(Bits32.extract(pkTrick, 0, 6))
                                && ( Bits32.extract(pkTrick, 6, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 12, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 18, 6)==PackedCard.INVALID) )
                        || (( Bits32.extract(pkTrick, 0, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 6, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 12, 6)==PackedCard.INVALID)
                                && ( Bits32.extract(pkTrick, 18, 6)==PackedCard.INVALID))

                );

    }

    /**
     * @param pkTrick : pli empaquete
     * @return taille du pli
     */
    public static int size(int pkTrick) {
    	assert isValid(pkTrick);
    	
    	int nb = 4;
    	for (int i=18 ; i>=0 ; i-=6) {
    		if(PackedCard.isValid(Bits32.extract(pkTrick, i, 6))) {
    			return nb;
    		}
    		nb--;
    	}
    	return nb;
    }

    /**
     * @param pkTrick : pli empaquete
     * @return atout du pli
     */
    public static Color trump(int pkTrick) {
    	assert isValid(pkTrick);
        return Card.Color.values()[Bits32.extract(pkTrick, 30, 2)];
    }

    /**
     * @param pkTrick : pli empaquete
     * @return index du pli
     */
    public static int index(int pkTrick) {
    	assert isValid(pkTrick);
        return Bits32.extract(pkTrick, 24, 4);
    }

    /**
     * @param pkTrick : pli empaquete
     * @param index : index de la carte
     * @return version empaquetée de la carte du pli à l'index donné
     */
    public static int card(int pkTrick, int index) {
    	assert isValid(pkTrick);
        return (pkTrick >>> (6 * index)) & 0b111111;
    }

    
    /**
     * @param pkTrick : pli empaquete
     * @return représentation textuelle du pli
     */
    public static String toString(int pkTrick) {
    	assert isValid(pkTrick);
        StringJoiner j = new StringJoiner(",", "{", "}");
        
        for (int i=0 ; i<size(pkTrick) ; i++) {
           Card c = Card.ofPacked(card(pkTrick, i)) ;
           j.add(c.color().toString()+c.rank().toString());
        }
        
            return j.toString();
    }

    /**
     * @param trump : atout
     * @param firstPlayer : premier joueur donné
     * @return le pli empaqueté vide sans aucune carte d'index 0 avec l'atout et le premier joueur donnés
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return (trump.ordinal() << 30 | firstPlayer.ordinal() << 28)
                | (INVALID >>> 8);
    }

    /**
     * @param pkTrick : pli empaquete
     * @return vrai ssi le pli est le dernier du tour et faux sinon
     */
    public static boolean isLast(int pkTrick) {
    	assert isValid(pkTrick);
        return Bits32.extract(pkTrick, 24, 4) == 8;
    }

    /**
     * @param pkTrick : pli empaquete
     * @return vrai ssi le pli est vide et faux sinon
     */
    public static boolean isEmpty(int pkTrick) {
    	assert isValid(pkTrick);
        return (Bits32.extract(pkTrick, 0, 6) == PackedCard.INVALID);
    }

    /**
     * @param pkTrick : pli empaquete
     * @return vrai ssi le pli est plein et faux sinon
     */
    public static boolean isFull(int pkTrick) {
    	assert isValid(pkTrick);
        return PackedCard.isValid(Bits32.extract(pkTrick, 18, 6));
    }

    /**
     * @param pkTrick : pli empaquete
     * @param index : index du joueur
     * @return le joueur d'index donné dans le pli
     */
    public static PlayerId player(int pkTrick, int index) {
    	assert isValid(pkTrick);
        return PlayerId.values()[(Bits32.extract(pkTrick, 28, 2) + index) % 4];
    }

    /**
     * @param pkTrick : pli empaquete
     * @return la couleur de base du pli
     */
    public static Color baseColor(int pkTrick) {
    	assert isValid(pkTrick);
        return PackedCard.color(Bits32.extract(pkTrick, 0, 6));
    }

    /**
     * @param pkTrick : pli empaquete
     * @return la valeur du pli, en tenant compte des « 5 de der »
     */
    public static int points(int pkTrick) {
    	assert isValid(pkTrick);
        int points = 0;
        for (int i = 0; i < 4; i++) {
           if(PackedCard.isValid(Bits32.extract(pkTrick, i * 6, 6))) {
               points += PackedCard.points(trump(pkTrick),  Bits32.extract(pkTrick, i * 6, 6));
           }
                  
        }
        if (isLast(pkTrick))
            points += 5;

        return points;
    }

    /**
     * @param pkTrick : pli empaquete
     * @param pkCard : carte empaquete
     * @return un pli identique à celui donné, mais à laquelle la carte donnée a été ajoutée
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
    	assert isValid(pkTrick);
        for (int i = 0; i < 3; i++) {
            if (Bits32.extract(pkTrick, i * 6, 6) == PackedCard.INVALID) {

                int t = ~Bits32.mask(i * 6, 6);
                return (pkTrick & t) | (pkCard << i * 6);
            }

        }
        
        int l = ~Bits32.mask(18, 6);
        return (pkTrick & l) | (pkCard << 18);
    }

    /**
     * @param pkTrick : pli empaquete
     * @return l'identité du joueur menant le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        Card cardMax, card;
        int cardIndexMax, cardIndex;
        int maxIndice = 0;

        for (int i = 1; i <= 3; i++) {

            cardIndexMax = Bits32.extract(pkTrick, maxIndice * 6, 6);
            cardIndex = Bits32.extract(pkTrick, i * 6, 6);

            if (!PackedCard.isValid(cardIndex)) {
                break;
            }
            cardMax = Card.ofPacked(cardIndexMax);
            card = Card.ofPacked(cardIndex);
            if (card.isBetter(trump(pkTrick), cardMax)) {             
                maxIndice = i;
            }

        }

        return player(pkTrick, maxIndice);
    }

    /**
     * @param pkTrick : pli empaquete
     * @return le pli empaqueté vide suivant celui donné 
     */
    public static int nextEmpty(int pkTrick) {
    	assert isValid(pkTrick);
        if (isLast(pkTrick)) {
            return INVALID;
        }

        return (trump(pkTrick).ordinal() << 30)
                | (winningPlayer(pkTrick).ordinal() << 28)
                | ((index(pkTrick) + 1) << 24) | (Bits32.mask(0, 24));

    }

    
	/**
	 * @param pkTrick : pli empaquete
	 * @param pkHand  : main du joueur
	 * @return le sous-ensemble empaqueté des cartes de la main pkHand qui peuvent
	 *         être jouées comme prochaine carte du pli pkTrick
	 */
	public static long playableCards(int pkTrick, long pkHand) {
		assert isValid(pkTrick);

		// Le premier joueur peut jouer n'importe quelle carte
		if (isEmpty(pkTrick)) {
			return pkHand;
		}

		Card.Color trump = trump(pkTrick);
		Card.Color couleurDeBase = baseColor(pkTrick);

		// cas où la couleur de base est egale au trump et le joueur n'a que la carte
		// valet comme carte de couleur atout
		if (couleurDeBase == trump
				&& PackedCardSet.subsetOfColor(pkHand, trump) == (0b100000L << trump.ordinal() * 16)) {
			return pkHand;
		}

		// cas où le joueur n'a ni une carte de couleur de base ni de couleur atout
		if (PackedCardSet.subsetOfColor(pkHand, couleurDeBase) == 0L
				&& PackedCardSet.subsetOfColor(pkHand, trump) == 0L) {

			return pkHand;
		}

		// cas où le joueur n'a pas de carte de couleur de base
		if (PackedCardSet.subsetOfColor(pkHand, couleurDeBase) == 0L) {

			// si aucune carte d'atout posee retourne la main complete
			if (maxAtoutPose(pkTrick) == -1) {
				return pkHand;
			}

			long playableCards = PackedCardSet.EMPTY;
			for (int i = 0; i < PackedCardSet.size(pkHand); i++) {

				// voir si la carte itérée est de couleur atout pour savoir si elle est jouable
				// ou non
				if (PackedCard.color(PackedCardSet.get(pkHand, i)) == trump) {
					// voir si la carte dans sa main est plus forte que la carte qui a déjà coupé
					if (PackedCard.isBetter(trump, PackedCardSet.get(pkHand, i), maxAtoutPose(pkTrick))) {
						playableCards = PackedCardSet.add(playableCards, PackedCardSet.get(pkHand, i));

						// voir s'il a le droit de sous couper
					} else if (rightToSousCouper(pkTrick, pkHand)) {
						return pkHand;
					}
				} else {
					playableCards = PackedCardSet.add(playableCards, PackedCardSet.get(pkHand, i));
				}
			}

			return playableCards;

		}

		// tous les autres cas (cas normale)
		long playableCards = PackedCardSet.EMPTY;
		for (int i = 0; i < PackedCardSet.size(pkHand); i++) {

			if (PackedCard.color(PackedCardSet.get(pkHand, i)) == couleurDeBase) {
				playableCards = PackedCardSet.add(playableCards, PackedCardSet.get(pkHand, i));
			}

			else if (PackedCard.color(PackedCardSet.get(pkHand, i)) == trump) {

				boolean verif = true;
				for (int j = 0; j < size(pkTrick); j++) {
					if (PackedCard.color(card(pkTrick, j)) == trump
							&& PackedCard.isBetter(trump, card(pkTrick, j), PackedCardSet.get(pkHand, i))) {
						verif = false;
						break;
					}
				}
				if (verif) {

					playableCards = PackedCardSet.add(playableCards, PackedCardSet.get(pkHand, i));
				}

			}
		}

		return playableCards;

	}
    
    //verifie si on a le droit de sous-couper
    private static boolean rightToSousCouper(int pkTrick, long pkHand) {
        //cette boucle verifie que toutes les cartes en main sont de couleur atout
        for (int i = 0; i < PackedCardSet.size(pkHand); i++) {
            if (!(PackedCard
                    .color(PackedCardSet.get(pkHand, i)) == trump(pkTrick))) {
                return false;
            }
        }
        //cette boucle verifie que toutes les cartes en main sont 
        //moins fortes que la plus fort carte atout deja posee
        for (int i = 0; i < PackedCardSet.size(pkHand); i++) {
            if (PackedCard.isBetter(trump(pkTrick),
                    PackedCardSet.get(pkHand, i), maxAtoutPose(pkTrick))) {
                return false;
            }
        }

        return true;
    }
    
    //Retourne la carte atout posée la plus forte dans le pli (empaquetee) sinon si aucune
    //carte atout n'a ete posee dans le pli retourne -1
    private static int maxAtoutPose(int pkTrick) {
        int maxAtoutPose = -1;
        for (int i = 0; i < size(pkTrick); i++) {
            if (PackedCard.color(card(pkTrick, i)) == trump(pkTrick)) {
                maxAtoutPose = card(pkTrick, i);
                break;
            }
        }
        if (maxAtoutPose == -1) {
            return -1;
        }

        for (int i = 0; i < size(pkTrick); i++) {
            if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, i),
                    maxAtoutPose)) {
                maxAtoutPose = card(pkTrick, i);
            }
        }

        return maxAtoutPose;
    }

}



