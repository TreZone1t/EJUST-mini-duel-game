package schema;
import java.util.*;
public class Player {
    private String name;
    private int health;
    public List<Card> hand;
    public Monster fieldMonster; 
    public Spell fieldSpell;
    public List<Card> graveyard;
    public List<Card> deck;
    public Player(String name) {
        this.name = name;
        this.health = 2000;
        this.hand = new ArrayList<>();
        this.graveyard = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.fieldMonster = null;
        this.fieldSpell = null;
    }

    public int getHealth() {
        return health;
    }
    public String getName() {
        return name;
    }
    public List<Card> getHand() {
        return hand;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}