package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * JassGame : représente une partie de jass
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class JassGame {

	private final Map<PlayerId, Player> players;
	private final Map<PlayerId, String> playerNames;
	private final Random shuffleRng, trumpRng;
	private Map<PlayerId, CardSet> hands = new HashMap<>(); //les mains des differents joueurs
	private List<Card> deck; //liste contenant les differentes cartes du jeu 
	private TurnState tour; //etat du tour
	private int nbTurn = 0; //nombre de tours
	private boolean firstTrickOfGame = true; //premier pli de toute la partie
	private boolean firstTrickOfTurn = true; //premier pli du tour
	private PlayerId firstPlayerOfGame; //premier joueur de la partie
	private PlayerId winningPlayer; //joueur qui a gagné le pli
	private TeamId winningTeam; //equipe qui gagné la partie

	/**
	 * construit une partie de Jass avec la graine aléatoire et les joueurs donnés
	 * 
	 * @param rngSeed : graine aleatoire donnée
	 * @param players : identite des joueurs 
	 * @param playerNames : nom des joueurs
	 */
	public JassGame(long rngSeed, Map<PlayerId, Player> players,
			Map<PlayerId, String> playerNames) {
		Random rng = new Random(rngSeed);
		this.shuffleRng = new Random(rng.nextLong());
		this.trumpRng = new Random(rng.nextLong());
		this.players = Collections.unmodifiableMap(new EnumMap<>(players));
		this.playerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames));
		//initialisation du premier tour
		DealsTheCard();
		firstPlayerOfGame = firstPlayerOfTrick();
		tour = TurnState.initial(trumpChoose(), Score.INITIAL,firstPlayerOfGame);
		setPlayers();
        setTrump();
        updatePli();
        updateScore();
        updateMain();
	}

	/**
	 * fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait rien si la partie est terminée
	 */
	public void advanceToEndOfNextTrick() {
		if (isGameOver()) return;

		//Si c'est le premier pli de la partie
		if (firstTrickOfGame) {
			
			playsATrick(firstPlayerOfGame);
			firstTrickOfGame = false;
			firstTrickOfTurn = false;
		} else {
			//Si le dernier pli joué etait le dernier du tour
			if (tour.trick().isLast()) {
				firstTrickOfTurn = true;
				nbTurn++;
				tour = tour.withTrickCollected();
				DealsTheCard();
				PlayerId firstPlayer = firstPlayerOfTrick();
				tour = TurnState.initial(trumpChoose(),tour.score().nextTurn(), firstPlayer);

				if (isGameOver()) {
					updateScore();
					setWinningTeam();
					return;
				}
				setTrump();
				updatePli();
				updateScore();
				updateMain();
				playsATrick(firstPlayer);
				firstTrickOfTurn = false;

			} else {
				tour = tour.withTrickCollected();
				if (isGameOver()) {
					updateScore();
					setWinningTeam();
					return;
				} 
				updatePli();
				updateScore();
				PlayerId firstPlayer = firstPlayerOfTrick();
				playsATrick(firstPlayer);

			}
		}
	}


	/**
	 * @return vrai ssi la partie est terminée et faux sinon
	 */
	public boolean isGameOver() {
		Set<Map.Entry<PlayerId, Player>> vue = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vue) {
			if(tour.score().totalPoints(e.getKey().team()) >= Jass.WINNING_POINTS ) {
				winningTeam = e.getKey().team();
				return true;   
			}
		}
		return false;
	}

	// Le déroulement d'un pli
	private void playsATrick(PlayerId player) {

		Player p = players.get(player);
		CardSet hand = hands.get(player);
		Card playedCard = p.cardToPlay(tour, hand);
		tour = tour.withNewCardPlayed(playedCard);
		updatePli();
		hands.put(player, hand.remove(playedCard));
		hand = hands.get(player);
		updateMain();
		PlayerId playerWillPlay;
		for (int i = 0; i < 3; i++) {
			playerWillPlay = tour.nextPlayer();
			p = players.get(playerWillPlay);
			hand = hands.get(playerWillPlay);
			playedCard = p.cardToPlay(tour, hand);
			tour = tour.withNewCardPlayed(playedCard);
			updatePli();
			hand = hand.remove(playedCard);
			hands.put(playerWillPlay, hand.remove(playedCard));
			hand = hands.get(player);
			updateMain();
		}

		winningPlayer = tour.trick().winningPlayer();
	}

	// liste des cartes non melangees
	private List<Card> deck() {
		CardSet allCards = CardSet.ALL_CARDS;
		List<Card> liste = new ArrayList<>();
		for (int i = 0; i < allCards.size(); i++) {
			liste.add(allCards.get(i));
		}
		return liste;
	}

	//premier joueur du pli
	private PlayerId firstPlayerOfTrick() {
		if(firstTrickOfGame) {
			return firstPlayerOfGame();
		} 
		if (firstTrickOfTurn) {
			return PlayerId.values()[(firstPlayerOfGame.ordinal() + nbTurn)% 4];
		}
		return winningPlayer;
	}

	// choix de l'atout au début par hasard
	private Color trumpChoose() {
		return Color.ALL.get(trumpRng.nextInt(4));
	}

	// Premier joueur de toute la partie
	private PlayerId firstPlayerOfGame() {
		int s = 0;
		for (int i = 0; i < 36; i++) {
			if (deck.get(i).equals(Card.of(Color.DIAMOND, Rank.SEVEN))) {
				s = i;
				break;
			}
		}
		return PlayerId.values()[s / 9];
	}

	// Melange et Distribution des cartes
	private void DealsTheCard() {
		deck=deck();
		Collections.shuffle(deck, shuffleRng);
		hands.put(PlayerId.PLAYER_1, CardSet.of(deck.subList(0, 9)));
		hands.put(PlayerId.PLAYER_2, CardSet.of(deck.subList(9, 18)));
		hands.put(PlayerId.PLAYER_3, CardSet.of(deck.subList(18, 27)));
		hands.put(PlayerId.PLAYER_4, CardSet.of(deck.subList(27, 36)));

	}

	//Annoncer l'atout
	private void setTrump() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().setTrump(tour.trick().trump());
		}
	}
	//Mettre à jour le pli
	private void updatePli() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().updateTrick(tour.trick());
		}
	}
	//Mettre à jour la main des joueurs
	private void updateMain() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().updateHand(hands.get(e.getKey()));
		}
	}
	//Mettre à jour les scores
	private void updateScore() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().updateScore(tour.score());
		}
	}
	//Annoncer l'equipe qui a gagné
	private void setWinningTeam() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().setWinningTeam(winningTeam);
		}
	}
	//Annoncer les joueurs de la partie
	private void setPlayers() {
		Set<Map.Entry<PlayerId, Player>> vu = players.entrySet();
		for (Map.Entry<PlayerId, Player> e : vu) {
			e.getValue().setPlayers(e.getKey(), playerNames);
		}
	}

}



