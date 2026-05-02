package schema;

public abstract class Card {
    protected String name;
    protected String type;
    protected String effect;
    protected String target;
    protected String place;
    public Card(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public abstract String getDetails();

    public abstract String getEffect();
    public abstract void activateEffect(Player self, Player opponent);
 
    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}