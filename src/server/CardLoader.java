package server;
import java.io.*;
import java.util.*;
import schema.*;

public class CardLoader {
    public static List<Card> loadCards(String filePath) {
        List<Card> cards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                
                String type = parts[0].replace("\"", "").trim();
                String name = parts[1].replace("\"", "").trim();
                
                if (type.equalsIgnoreCase("monster")) {
                    int attack = Integer.parseInt(parts[2].replace("\"", "").trim());
                    int defense = Integer.parseInt(parts[3].replace("\"", "").trim());
                    cards.add(new Monster(name, attack, defense));
                } else if (type.equalsIgnoreCase("spell")) {
                    String effect = parts[4].replace("\"", "").trim();
                    String target = parts[5].replace("\"", "").trim();
                    String place = parts[6].replace("\"", "").trim();
                    cards.add(new Spell(name, effect, target, place));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading cards: " + e.getMessage());
        }
        return cards;
    }
}