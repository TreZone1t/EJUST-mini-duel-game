package server;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Collections;
import schema.*;

public class Session implements Runnable {
    private Socket p1Socket, p2Socket;
    private PrintWriter out1, out2;
    private BufferedReader in1, in2;
    private Player player1, player2;

    public Session(Socket p1, Socket p2) throws IOException {
        this.p1Socket = p1;
        this.p2Socket = p2;

        out1 = new PrintWriter(p1.getOutputStream(), true);
        out2 = new PrintWriter(p2.getOutputStream(), true);
        in1  = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        in2  = new BufferedReader(new InputStreamReader(p2.getInputStream()));

        
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");

        
        List<Card> allCards = CardLoader.loadCards("data/card.csv");
        Collections.shuffle(allCards);

        
        int half = allCards.size() / 2;
        for (int i = 0; i < half; i++) {
            player1.deck.add(allCards.get(i));
            player2.deck.add(allCards.get(i + half));
        }

        
        for(int i=0; i<5; i++) {
            if(!player1.deck.isEmpty()) player1.hand.add(player1.deck.remove(0));
            if(!player2.deck.isEmpty()) player2.hand.add(player2.deck.remove(0));
        }
    }

    @Override
    public void run() {
        try {
            out1.println("WELCOME:You are Player 1");
            out2.println("WELCOME:You are Player 2");
            playGame();
        } catch (IOException e) {
            System.err.println("Session error: " + e.getMessage());
        } finally {
            closeAll();
        }
    }

    private void sendGameState(Player currentPlayer, Player opponentPlayer, PrintWriter currentOut) {
        currentOut.println("SYSTEM_MSG:====================================");
        currentOut.println("SYSTEM_MSG: [Health] You: " + currentPlayer.getHealth() + " | Opponent: " + opponentPlayer.getHealth());
        
        String myMonster = currentPlayer.fieldMonster != null ? currentPlayer.fieldMonster.getName() + " (ATK: " + currentPlayer.fieldMonster.getAttack() + ")" : "[Empty]";
        String oppMonster = opponentPlayer.fieldMonster != null ? opponentPlayer.fieldMonster.getName() + " (ATK: " + opponentPlayer.fieldMonster.getAttack() + ")" : "[Empty]";
        
        currentOut.println("SYSTEM_MSG: [Field]  Your Monster: " + myMonster + " | Opponent's Monster: " + oppMonster);
        currentOut.println("SYSTEM_MSG:====================================");

        currentOut.println("HAND_START:--- Your Hand ---");
        for (int i = 0; i < currentPlayer.hand.size(); i++) {
            Card c = currentPlayer.hand.get(i);
            currentOut.println("HAND_CARD:[" + i + "] " + c.getDetails()); 
        }
        currentOut.println("HAND_END:");
    }

    private void handleSummon(Player player, int cardIndex, PrintWriter currentOut, PrintWriter opponentOut) {
        if (cardIndex < 0 || cardIndex >= player.hand.size()) {
            currentOut.println("ERROR: Invalid card index.");
            return;
        }
        Card card = player.hand.get(cardIndex);
        if (card instanceof Monster) {
            player.fieldMonster = (Monster) card;
            player.hand.remove(cardIndex);
            currentOut.println("SUCCESS: Summoned " + card.getName());
            opponentOut.println("OPPONENT_ACTION: Opponent summoned [" + card.getName() + "] to the field!"); 
        } else {
            currentOut.println("ERROR: This is not a monster card.");
        }
    }

    private void handleSpell(Player self, Player opponent, int cardIndex, PrintWriter currentOut, PrintWriter opponentOut) {
        if (cardIndex < 0 || cardIndex >= self.hand.size()) {
            currentOut.println("ERROR: Invalid card index.");
            return;
        }
        Card card = self.hand.get(cardIndex);
        if (card instanceof Spell) {
            self.hand.remove(card); 
            card.activateEffect(self, opponent); 
            self.graveyard.add(card); 
            currentOut.println("SUCCESS: Activated spell " + card.getName());
            opponentOut.println("OPPONENT_ACTION: Opponent activated spell [" + card.getName() + "]!");
        } else {
            currentOut.println("ERROR: This is not a spell card.");
        }
    }

    private void playGame() throws IOException {
        out1.println("GAME_START");
        out2.println("GAME_START");

        boolean isPlayer1Turn = true;

        while (player1.getHealth() > 0 && player2.getHealth() > 0) {
            Player currentPlayer = isPlayer1Turn ? player1 : player2;
            Player opponentPlayer = isPlayer1Turn ? player2 : player1;
            BufferedReader currentIn = isPlayer1Turn ? in1 : in2;
            PrintWriter currentOut = isPlayer1Turn ? out1 : out2;
            PrintWriter opponentOut = isPlayer1Turn ? out2 : out1;

            
            if (!currentPlayer.deck.isEmpty()) {
                Card drawnCard = currentPlayer.deck.remove(0);
                currentPlayer.hand.add(drawnCard);
                currentOut.println("SUCCESS: You drew a card!");
                opponentOut.println("WAIT: Opponent drew a card.");
            } else {
                currentOut.println("ERROR: Deck is empty! No cards to draw.");
            }

            
            sendGameState(currentPlayer, opponentPlayer, currentOut);

            currentOut.println("YOUR_TURN: It's your turn!");
            opponentOut.println("WAIT: Opponent is playing...");

            boolean turnEnded = false;
            while (!turnEnded) {
                String command = currentIn.readLine();
                if (command == null) break;
                
                String[] parts = command.split(" ");
                String action = parts[0].toUpperCase();

                switch (action) {
                    case "SUMMON":
                        if(parts.length > 1) {
                            handleSummon(currentPlayer, Integer.parseInt(parts[1]), currentOut, opponentOut);
                            sendGameState(currentPlayer, opponentPlayer, currentOut);
                        } else {
                            currentOut.println("ERROR: Missing card index. (e.g., SUMMON 0)");
                        }
                        break;
                    case "SPELL":
                        if(parts.length > 1) {
                            handleSpell(currentPlayer, opponentPlayer, Integer.parseInt(parts[1]), currentOut, opponentOut);
                            sendGameState(currentPlayer, opponentPlayer, currentOut);
                        } else {
                            currentOut.println("ERROR: Missing card index. (e.g., SPELL 1)");
                        }
                        break;
                    case "ATTACK":
                        currentOut.println("SUCCESS: Attack logic is coming soon!");
                        break;
                    case "END_TURN":
                        turnEnded = true; 
                        currentOut.println("SUCCESS: Turn ended.");
                        break;
                    default:
                        currentOut.println("ERROR: Unknown command");
                }
            }
            isPlayer1Turn = !isPlayer1Turn;
        }

        if (player1.getHealth() <= 0) {
            out1.println("GAME_OVER: You lost!");
            out2.println("GAME_OVER: You won!");
        } else {
            out1.println("GAME_OVER: You won!");
            out2.println("GAME_OVER: You lost!");
        }
    }

    private void closeAll() {
        try {
            if (p1Socket != null) p1Socket.close();
            if (p2Socket != null) p2Socket.close();
        } catch (IOException e) { }
    }
}