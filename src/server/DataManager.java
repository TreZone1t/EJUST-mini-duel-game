package server;
import java.io.*;

public class  DataManager {
    
    public static void saveMatchResult(String result) {
        try (FileWriter fw = new FileWriter("data/match_history.txt", true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println(result);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}