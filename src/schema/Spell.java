package schema;

public class Spell extends Card {
    private String effect;
    private String target;
    private String place;
    private Effect spellEffect;
    public Spell(String name, String effect, String target, String place) {
        super(name, "Spell");
        this.effect = effect;
        this.target = target;
        this.place = place;
        this.spellEffect = new Effect(effect, target, place); 
    }
    @Override
    public String getEffect() {
        return effect;
    }

    @Override
    public String getDetails() {
        return "[Spell] " + name + " (Effect: " + effect + ")";
    }
    @Override
    public void activateEffect(Player self, Player opponent) {
            spellEffect.handleEffect(self, opponent, target, place);
    }
    
}
