package server;
import java.net.*;
import java.io.*;


public class Server {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                System.out.println("Waiting for Player 1...");
                Socket player1 = serverSocket.accept();  
                System.out.println("Player 1 connected!");

                System.out.println("Waiting for Player 2...");
                Socket player2 = serverSocket.accept();  
                System.out.println("Player 2 connected!");

                
                Session session = new Session(player1, player2);
                new Thread(session).start();
                
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}