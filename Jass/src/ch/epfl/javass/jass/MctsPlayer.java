package ch.epfl.javass.jass;

import java.util.SplittableRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ch.epfl.javass.Preconditions;

/**
 * MctsPlayer : représente un joueur simulé au moyen de l'algorithme MCTS
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 */
public final class MctsPlayer implements Player {
	private final PlayerId ownId;
	private final int iterations;
	private final SplittableRandom seed;

	/**
	 * construit un joueur simulé avec l'identité, la graine aléatoire et le nombre
	 * d'itérations donnés
	 * 
	 * @param ownId      : joueur simulé
	 * @param rngSeed    : graine aleatoire
	 * @param iterations : nombre d'iterations
	 * @throws IllegalArgumentException si le nombre d'itérations est inférieur à 9
	 */
	public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
		Preconditions.checkArgument(iterations >= 9);
		this.iterations = iterations;
		this.ownId = ownId;
		SplittableRandom rng = new SplittableRandom(rngSeed);
		this.seed = new SplittableRandom(rng.nextLong());
	}

	/*
	 * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
	 * ch.epfl.javass.jass.CardSet)
	 */
	@Override
	public Card cardToPlay(TurnState state, CardSet hand) {

		Node bigRoot = new Node(state, hand, ownId, seed);
		Node root;
		int s = 0;
		while (!bigRoot.sature()) {
			root = bigRoot.addNode();
			root.MCSimulation();
			root.updateScores(root.path);
			s++;
		}

		while (s < iterations) {
			bigRoot.setVnOfChildren();
			root = bigRoot.childToExpand();
			Node directFather = root.directFather();
			Node added = directFather.addNode();
			added.MCSimulation();
			added.updateScores(added.path);

			s++;
		}
		return bigRoot.chosenCard();
	}

	private static final class Node {
		private TurnState turnStateNode;
		private List<Node> childrenNode; // liste de tous les noeuds fils
		private CardSet nextPlayableCards; // ensemble de toutes les cartes jouables
		private int Sn; // S(n)
		private int Nn; // N(n)
		private final int C = 40; // constante c
		private double Vn; // V(n)
		private CardSet hand; // main du joueur
		private PlayerId ownId;
		private SplittableRandom seed;
		private List<Node> path = new ArrayList<>();
		private CardSet nonPlayedChildren;
		private Score scores; //les scores apres simulation d'un noeud

		//construit un noeud
		private Node(TurnState turnStateNode, CardSet hand, PlayerId ownId, SplittableRandom seed) {
			this.ownId = ownId;
			this.seed = seed;
			this.turnStateNode = turnStateNode;
			nextPlayableCards = this.playableCards(turnStateNode, hand);
			childrenNode = new ArrayList<>();
			this.hand = hand;
			nonPlayedChildren = this.playableCards(turnStateNode, hand);
		}

		// retourne le joueur qui va jouer à partir du TurnState state
		private PlayerId nextPlayer(TurnState state) {
			return state.trick().isFull() ? state.withTrickCollected().nextPlayer() : state.nextPlayer();
		}

		/*
		 * retourne les cartes qui peuvent être joués à partir de state Si le prochain
		 * joueur est le joueur représenté par Mcts Player (ownId), la méthode retourne
		 * les cartes pas encore joué qui sont dans hand Sinon, la méthode retourne les
		 * carte pas dans hand et pas encore jouées
		 */
		private CardSet playableCards(TurnState state, CardSet hand) {

			TurnState other = TurnState.ofPackedComponents(state.packedScore(), state.packedUnplayedCards(),
					state.packedTrick());

			if (other.trick().isLast() && other.trick().isFull()) {
				return null;
			}
			PlayerId nextPlayer = nextPlayer(other);
			other = other.trick().isFull() ? other.withTrickCollected() : other;
			nextPlayer = nextPlayer(other);

			if (this.ownId == nextPlayer) {
				return other.trick().playableCards(other.unplayedCards().intersection(hand));
			}
			return other.trick().playableCards(other.unplayedCards().difference(hand));
			

		}

		// la méthode retourne le noeud qui a le plus grand Vn
		private Node childToExpand() {
			int maxIndice = 0;
			for (int i = 1; i < childrenNode.size(); i++) {
				if (childrenNode.get(i).Vn > childrenNode.get(maxIndice).Vn) {
					maxIndice = i;
				}
			}
			return childrenNode.get(maxIndice);
		}

		//retourne le noeud pere du noeud appelant
		private Node directFather() {
			return this.bonEndroit().get(this.bonEndroit().size() - 1);
		}

		/*
		 * Appel forcement par le parent mets à jour les Vn des noeuds fils
		 */
		private void setVnOfChildren() {

			for (int i = 0; i < this.nextPlayableCards.size(); i++) {
				Node childNode = childrenNode.get(i);
				if (childNode.Nn == 0) {
					childNode.setVn(Double.POSITIVE_INFINITY);
				} else {
					double toBeSet;
					toBeSet = ((double) childNode.Sn) / childNode.Nn;
					toBeSet += C * Math.sqrt(2 * (Math.log(this.Nn)) / childNode.Nn);

					childNode.setVn(toBeSet);
				}
			}
		}

		// retourne vrai ssi tous les noeuds fils ont été crées
		private boolean sature() {
			return childrenNode.size() == nextPlayableCards.size();
		}

		// retourne la carte qui a fait gagne le plus de point a l'equipe
		private double averageScore() {
			return ((double) Sn) / Nn;
		}

		// retourne la carte à jouer: la carte qui a fait un meilleur score pour
		// l'équipe en question
		private Card chosenCard() {

			if (turnStateNode.isTerminal())
				return null;

			Node maxNode = childrenNode.get(0);
			for (int i = 1; i < childrenNode.size(); i++) {
				Node currentNode = childrenNode.get(i);
				if (currentNode.averageScore() > maxNode.averageScore()) {
					maxNode = currentNode;
				}

			}
			Trick selectedTrick = maxNode.turnStateNode.trick();
			return selectedTrick.card(selectedTrick.size() - 1);
		}

		// appelé par un noeud
		// retourne une carte jouable possible a partir de ce noeud
		private Card randomCard(TurnState state) {
			if (state.isTerminal())
				return null;

			TurnState other = TurnState.ofPackedComponents(state.packedScore(), state.packedUnplayedCards(),
					state.packedTrick());

			int random = seed.nextInt(playableCards(state, hand).size());
			Card randomCard = playableCards(other, hand).get(random);
			return randomCard;
		}

		// retourne le score d'une simulation
		private Score finalScoreOfTurn(TurnState state, CardSet hand) {
			TurnState other = TurnState.ofPackedComponents(state.packedScore(), state.packedUnplayedCards(),
					state.packedTrick());

			while (!other.isTerminal()) {
				while (!other.trick().isFull()) {

					Card addedCard = randomCard(other);
					other = other.withNewCardPlayed(addedCard);

				}
				other = other.withTrickCollected();
			}
			return other.score();
		}

		// appele par le noeud ou la simulation aura lieu
		// mets a jour les Sn et les Nn
		private void MCSimulation() {
			scores = finalScoreOfTurn(turnStateNode, hand);
			setSn(Sn + scores.turnPoints(turnStateNode.trick().player(turnStateNode.trick().size()-1).team()));
			setNn(Nn + 1);
		}

		// appele par le noeud ou on veut ajouter un fils
		// retourne une liste des noeuds parents du plus eleves au plus bas
		private List<Node> bonEndroit() {
			List<Node> list = new LinkedList<>();
			list.add(this);
			if (this.isLeaf()) {
				return list;

			}
			Node theChild;
			if (!this.sature()) {
				return list;
			}
			if (this.sature()) {
				this.setVnOfChildren();
				theChild = this.childToExpand();
				list.addAll(theChild.bonEndroit());
				return list;
			}
			return null;
		}

		// appelé par le noeud où la modification a eu lieu
		// update tous ses parents en fonction de l'equipe de chaque parent
		private void updateScores(List<Node> path) {
			for (int i = 0; i < path.size(); i++) {
				path.get(i).setSn(path.get(i).Sn + scores.turnPoints(path.get(i).turnStateNode.trick()
						.player(path.get(i).turnStateNode.trick().size()-1).team()));
				path.get(i).setNn(path.get(i).Nn + 1);
			}
		}

		// ajoute un noeud
		private Node addNode() {
			if (this.isLeaf()) {
				return this;
			}
			TurnState other = TurnState.ofPackedComponents(this.turnStateNode.packedScore(),
					turnStateNode.packedUnplayedCards(), turnStateNode.packedTrick());

			Node resNode;
			TurnState state;
			other = other.trick().isFull() ? other.withTrickCollected() : other;
			state = other.withNewCardPlayed(nonPlayedChildren.get(0));
			nonPlayedChildren = nonPlayedChildren.remove(nonPlayedChildren.get(0));
			resNode = new Node(state, hand, ownId, seed);
			childrenNode.add(resNode);
			if(!this.turnStateNode.trick().isEmpty()) {
				resNode.path.add(this);
			}
			resNode.path.addAll(this.path);

			return resNode;
		}

		// retourne vrai si le noeud appelant ne peut pas avoir d'enfants
		private boolean isLeaf() {
			return (turnStateNode.trick().isLast() && turnStateNode.trick().isFull());
		}

		private void setNn(int a) {
			this.Nn = a;
		}

		private void setSn(int a) {
			this.Sn = a;
		}

		private void setVn(double Vn) {
			this.Vn = Vn;
		}

	}// end node

}
