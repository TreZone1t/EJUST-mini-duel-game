package client;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {
            PrintWriter out    = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner    = new Scanner(System.in);

            System.out.println("Connecting to the Duel Server...");

            Thread listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        if (msg.startsWith("WELCOME:")) {
                            System.out.println("\n=== " + msg.split(":")[1] + " ===");
                        } else if (msg.equals("GAME_START")) {
                            System.out.println("\n*** THE DUEL HAS STARTED! ***\n");
                        } else if (msg.startsWith("YOUR_TURN:")) {
                            System.out.println("\n>>> " + msg.split(":")[1] + " <<<");
                            printMenu();
                            System.out.print("Enter command: ");
                        } else if (msg.startsWith("WAIT:")) {
                            System.out.println("\n[-] " + msg.split(":")[1]);
                        } else if (msg.startsWith("SUCCESS:")) {
                            System.out.println("[+] " + msg.split(":")[1]);
                        } else if (msg.startsWith("ERROR:")) {
                            System.out.println("[!] " + msg.split(":")[1]);
                        } else if (msg.startsWith("OPPONENT_ACTION:")) {
                            System.out.println("[Opponent] " + msg.split(":")[1]);
                        } else if (msg.startsWith("SYSTEM_MSG:")) {
                            System.out.println(msg.substring(11)); 
                        } else if (msg.startsWith("HAND_START:")) {
                            System.out.println("\n" + msg.substring(11)); 
                        } else if (msg.startsWith("HAND_CARD:")) {
                            System.out.println("  " + msg.substring(10));
                        } else if (msg.startsWith("HAND_END:")) {
                            System.out.println("---------------------------------");
                        } else {
                            System.out.println("[System]: " + msg);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("\nDisconnected from server.");
                    System.exit(0);
                }
            });
            listener.start();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                out.println(line);
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("---------------------------------");
        System.out.println(" Available Commands:");
        System.out.println(" 1. SUMMON <index>  (e.g., SUMMON 0)");
        System.out.println(" 2. SPELL <index>   (e.g., SPELL 1)");
        System.out.println(" 3. ATTACK          (Attacks opponent)");
        System.out.println(" 4. END_TURN        (Ends your turn)");
        System.out.println("---------------------------------");
    }
}