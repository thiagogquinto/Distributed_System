package RED;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        // Configurações do cliente
        String host = "127.0.0.1";
        int port = 12345;

        try (Socket clientSocket = new Socket(host, port);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            System.out.println("Conectado ao servidor em " + host + ":" + port);

            while (true) {
                String command = 
                getUserInput("Digite um comando (add_movie, get_movies, get_movies_by_actor, get_movies_by_genre, update_movie, delete_movie, exit): ", userInput);

                if (command.equals("exit")) {
                    outToServer.println("{\"operation\": \"exit\"}");
                    break;
                }

                if (command.equals("add_movie") || command.equals("delete_movie")) {
                    String title = getUserInput("Digite o título do filme: ", userInput);

                    if (command.equals("add_movie")) {
                        int year = Integer.parseInt(getUserInput("Digite o ano do filme: ", userInput));
                        String genre = getUserInput("Digite o gênero do filme: ", userInput);
                        String jsonCommand = String.format("{\"operation\": \"%s\", \"movie\": {\"title\": \"%s\", \"year\": %d, \"genre\": \"%s\"}}", command, title, year, genre);
                        System.out.println(jsonCommand);
                        outToServer.println(jsonCommand);
                    }

                    String jsonCommand = String.format("{\"operation\": \"%s\", \"title\": \"%s\"}", command, title);
                    outToServer.println(jsonCommand);

                    String response = inFromServer.readLine();
                    System.out.println("\n" + response + "\n\n");

                } else if (command.equals("update_movie")) {
                    String title = getUserInput("Digite o título do filme: ", userInput);
                    String newTitle = getUserInput("Digite o novo título do filme: ", userInput);
                    int newYear = Integer.parseInt(getUserInput("Digite o novo ano do filme: ", userInput));
                    String newGenre = getUserInput("Digite o novo gênero do filme: ", userInput);
                    
                    String jsonCommand = String.format("{\"operation\": \"%s\", \"title\": \"%s\" \"movie\": {\"new_title\": \"%s\", \"new_year\": %d, \"new_genre\": \"%s\"}}", command, title, newTitle, newYear, newGenre);
                    outToServer.println(jsonCommand);

                    String response = inFromServer.readLine();
                    System.out.println("\n" + response + "\n\n");

                } else if (command.equals("get_movies_by_actor") || command.equals("get_movies_by_genre")) {
                    String prompt = (command.equals("get_movies_by_actor")) ? "Digite o nome do ator: " : "Digite o gênero do filme: ";
                    String value = getUserInput(prompt, userInput);

                    String jsonCommand = String.format("{\"operation\": \"%s\", \"%s\": \"%s\"}", 
                    command, (command.equals("get_movies_by_actor")) ? "actor" : "genre", value);
                    outToServer.println(jsonCommand);

                    String response = inFromServer.readLine();
                    System.out.println("\n" + response + "\n\n");

                } else if (command.equals("get_movies")) {
                    outToServer.println("{\"operation\": \"get_movies\"}");

                    String response = inFromServer.readLine();
                    System.out.println("\n" + response + "\n\n");

                } else {
                    System.out.println("Comando inválido. Tente novamente.");
                }
            }

        } catch (IOException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
        }
    }

    private static String getUserInput(String prompt, BufferedReader reader) throws IOException {
        System.out.print(prompt);
        return reader.readLine();
    }
}
