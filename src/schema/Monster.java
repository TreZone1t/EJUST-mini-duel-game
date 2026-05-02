package schema;
public class Monster extends Card {
    private int attack;
    private int defense;
    public Monster(String name, int attack , int defense) {
        super(name, "Monster");
        this.attack = attack;
        this.defense = defense;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    // Overriding the abstract method
    @Override
    public String getDetails() {
        return "[Monster] " + name + " (ATK: " + attack + ", DEF: " + defense + ")";
    }
        @Override
    public String getEffect() {
        return "No special effect.";
    }

    @Override
    public void activateEffect(Player self, Player opponent) {
       if(opponent.fieldMonster != null) {
            int damage = this.attack - opponent.fieldMonster.getAttack();
            if (damage > 0) {
                opponent.takeDamage(damage);
                opponent.graveyard.add(opponent.fieldMonster);
                opponent.fieldMonster = null;
            } else if (damage < 0) {
                self.takeDamage(-damage);
                self.graveyard.add(self.fieldMonster);
                self.fieldMonster = null;
            }
        }
    }
}