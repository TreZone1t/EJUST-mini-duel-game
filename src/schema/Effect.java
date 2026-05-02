package schema;

public class Effect {
    private String[] effectParts;
    
    
    private enum Target { self, opponent, all, none };
    private enum Place { hand, field, graveyard, deck, none };
    
    private Target target;
    private Place place;

    public Effect(String effect , String targetStr, String placeStr) {
        this.effectParts = effect.split(" ");
        this.target = parseTarget(targetStr);
        this.place = parsePlace(placeStr);
    }

    private Target parseTarget(String t) {
        try { return Target.valueOf(t.trim().toLowerCase()); } 
        catch (Exception e) { return Target.none; }
    }

    private Place parsePlace(String p) {
        try { return Place.valueOf(p.trim().toLowerCase()); } 
        catch (Exception e) { return Place.none; }
    }

    public void handleEffect(Player self, Player opponent, String t, String p) {
        try {
            String action = effectParts[0].toLowerCase();
            int value = effectParts.length > 1 ? Integer.parseInt(effectParts[1]) : 1;
            
            this.target = parseTarget(t);
            this.place = parsePlace(p);
            
            switch (action) {
                case "destroy":
                    if (target == Target.self) destroy(value, self);
                    else if (target == Target.opponent) destroy(value, opponent);
                    else { destroy(value, self); destroy(value, opponent); }
                    break;
                case "draw":
                    if (target == Target.self || target == Target.none) draw(value, self);
                    else if (target == Target.opponent) draw(value, opponent);
                    break;
                case "summon":
                    if (target == Target.self || target == Target.none) specialSummon(value, self, place);
                    else if (target == Target.opponent) specialSummon(value, opponent , place);
                    else { specialSummon(value, self, place); specialSummon(value, opponent , place); }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Effect Execution Error: " + e.getMessage());
        }
    }
    
    public void destroy(int cardsToDestroy, Player targetPlayer) {
        if(targetPlayer.fieldMonster != null && cardsToDestroy > 0) {
            targetPlayer.graveyard.add(targetPlayer.fieldMonster);
            targetPlayer.fieldMonster = null;
            cardsToDestroy--;
        }
        if(cardsToDestroy > 0) {
            for(int i = 0; i < targetPlayer.hand.size() && cardsToDestroy > 0; i++) {
                targetPlayer.graveyard.add(targetPlayer.hand.get(i));
                targetPlayer.hand.remove(i);
                i--;
                cardsToDestroy--;
            }
        }
    }

    public void draw(int cardsToDraw, Player targetPlayer) {
        for(int i = 0; i < cardsToDraw; i++) {
            if(!targetPlayer.deck.isEmpty()) {
                targetPlayer.hand.add(targetPlayer.deck.remove(0));
            }
        }
    }

    public void specialSummon(int cardsToSummon, Player targetPlayer, Place place) {
        while(cardsToSummon > 0) {
            if(place == Place.graveyard && !targetPlayer.graveyard.isEmpty()) {
                Card card = targetPlayer.graveyard.remove(0);
                if(card instanceof Monster) {
                    targetPlayer.fieldMonster = (Monster) card;
                    cardsToSummon--;
                }
            } else if(place == Place.hand && !targetPlayer.hand.isEmpty()) {
                Card card = targetPlayer.hand.remove(0);
                if(card instanceof Monster) {
                    targetPlayer.fieldMonster = (Monster) card;
                    cardsToSummon--;
                }
            } else {
                break; 
            }
        }
    }
}