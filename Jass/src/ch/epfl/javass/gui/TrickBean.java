package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * TrickBean: bean JavaFX contenant le pli courant
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class TrickBean {

    private final ObservableMap<PlayerId, Card> trick = FXCollections
            .observableHashMap();
    private final ObjectProperty<Color> trumpProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<PlayerId> winningPlayerProperty = new SimpleObjectProperty<>();

    /**
     * @return la propriété de l'atout
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trumpProperty;
    }

    /**
     * @return la propriété du joueur gagnant
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayerProperty;
    }

    /**
     * @return une version non modifiable du pli
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * @param newTrick
     *            Il affecte dans la map les cartes jouées au joueurs
     *            correspendants et null aux joueurs qui n'ont pas joué
     */
    public void setTrick(Trick newTrick) {
        if (!newTrick.isEmpty()) {
            winningPlayerProperty.set(newTrick.winningPlayer());
        } else {
            winningPlayerProperty.set(null);
        }

        trick.clear();
        for (int i = 0; i < newTrick.size(); i++) {
            trick.put(newTrick.player(i), newTrick.card(i));
        }
        for (int i = newTrick.size(); i < PlayerId.COUNT; i++) {
            trick.put(newTrick.player(i), null);
        }
    }

    /**
     * @param trump
     *            setter de Trump
     */
    public void setTrump(Color trump) {
        trumpProperty.set(trump);
    }
}