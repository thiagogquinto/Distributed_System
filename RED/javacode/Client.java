import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Client {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 12345;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("Conectado ao servidor em " + host + ":" + port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Escolha um comando: ");
                System.out.println("get_movies = 1 \nget_movies_by_actor = 2 \nget_movies_by_genre = 3 \nadd_movie = 4 \nupdate_movie = 5 \ndelete_movie = 6 \nexit = 7 \n");
                String command = reader.readLine();

                if (command.equals("1") || command.equals("2") || command.equals("3")) {
                    
                    MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
                    MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = MovieOuterClass.Movie.MovieData.newBuilder();

                    if (command.equals("1")) { 
                        requestBuilder.setOperation("get_movies");
                        
                    } else if(command.equals("3")) {
                        requestBuilder.setOperation("get_movies_by_genre");
                        String genre = getUserInput("Digite o gênero: ", reader);
                        movieDataBuilder.addGenres(genre);
                        requestBuilder.addMovies(movieDataBuilder.build());

                    } else if(command.equals("2")) {
                        requestBuilder.setOperation("get_movies_by_actor");
                        String cast = getUserInput("Digite o ator: ", reader);
                        movieDataBuilder.addCast(cast);
                        requestBuilder.addMovies(movieDataBuilder.build());
                    }
                    
                    sendRequest(socket, requestBuilder);
                    printResponse(socket);

                } else if (command.equals("4")) {
                    // Construir a mensagem protobuf
                    MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
                    requestBuilder.setOperation("add_movie");
                    
                    MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = createMovieData(reader);
                    requestBuilder.addMovies(movieDataBuilder.build());
                    
                    sendRequest(socket, requestBuilder);
                    printResponse(socket);
                    
                } else if (command.equals("5")) {
                    // Construir a mensagem protobuf
                    MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
                    requestBuilder.setOperation("update_movie");

                    String id = getUserInput("Digite o id do filme: ", reader);

                    MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = createMovieData(reader);
                    movieDataBuilder.setId(id);
                    requestBuilder.addMovies(movieDataBuilder.build());

                    sendRequest(socket, requestBuilder);
                    printResponse(socket);
                    
                } else if (command.equals("6")) {
                    // Enviar solicitação para obter filmes
                    MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
                    requestBuilder.setOperation("delete_movie");
                    
                    MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = MovieOuterClass.Movie.MovieData.newBuilder();
                    String id = getUserInput("Digite o id do filme: ", reader);
                    movieDataBuilder.setId(id);
                    
                    requestBuilder.addMovies(movieDataBuilder.build());
                    
                    sendRequest(socket, requestBuilder);
                    printResponse(socket);
                } else if (command.equals("7")) {
                    MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
                    requestBuilder.setOperation("exit");
                    sendRequest(socket, requestBuilder);
                    
                    socket.close();
                    System.out.println("Cliente encerrado. Encerrando a conexão.");
                    break;
                }
                else {
                    System.out.println("Comando inválido. Tente novamente.");
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Função para ler uma string do usuário
     * String prompt: mensagem para o usuário
     * BufferedReader reader: objeto para ler a entrada do usuário
     * retorna uma string
     */
    private static String getUserInput(String prompt, BufferedReader reader) throws IOException {
        // antes de retornar o valor lido, deve-se verificar se o usuário digitou algo
        // (se ele apertou apenas ENTER, por exemplo, não deve retornar uma string vazia)
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Entrada inválida. Tente novamente.");
        }
    }

    /*
     * Função para ler um inteiro do usuário
     * String prompt: mensagem para o usuário
     * BufferedReader reader: objeto para ler a entrada do usuário
     * retorna um inteiro
     */
    private static int getIntInput(String prompt, BufferedReader reader) throws IOException {
        
        while (true) {
            System.out.print(prompt);
            String input = reader.readLine();
            if (!input.isEmpty() && input.matches("\\d+")) {
                return Integer.parseInt(input);
            }
            System.out.println("Entrada inválida. Tente novamente.");
        }
    }

    /*
     * Imprime a resposta do servidor
     * Socket socket: socket conectado ao servidor
     */
    private static void printResponse(Socket socket) {

        try {
            // Receber resposta do servidor
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String valueStr = dis.readLine();

            // Ler o tamanho do buffer
            int sizeBuffer = Integer.valueOf(valueStr);
            byte[] buffer = new byte[sizeBuffer];

            // Ler os dados do buffer
            dis.readFully(buffer);
            
            // Parse da mensagem protobuf
            MovieOuterClass.Movie response = MovieOuterClass.Movie.parseFrom(buffer);

            if (response != null) {
                System.out.println("\nResposta do servidor: " + response.toString());
            } else {
                System.out.println("\nNão recebeu resposta válida do servidor.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /*
     * Função para enviar uma mensagem protobuf para o servidor
     * Socket socket: socket conectado ao servidor
     * MovieOuterClass.Movie.Builder requestBuilder: mensagem protobuf a ser enviada
     */
    private static void sendRequest(Socket socket,  MovieOuterClass.Movie.Builder requestBuilder) throws IOException {

        MovieOuterClass.Movie request = requestBuilder.build();

        // Marshalling
        byte[] msg = request.toByteArray();
        int size = msg.length;

        // Enviar o tamanho do buffer como um inteiro seguido pelos dados serializados
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(size);
        dos.write(msg);
        dos.flush();
    }

    /*
     * Função para criar um objeto MovieData
     * BufferedReader reader: objeto para ler a entrada do usuário
     * retorna um objeto MovieData
     */
    public static MovieOuterClass.Movie.MovieData.Builder createMovieData(BufferedReader reader) throws IOException {

        MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = MovieOuterClass.Movie.MovieData.newBuilder();
        movieDataBuilder.setPlot(getUserInput("Digite o plot do filme: ", reader));
        movieDataBuilder.addAllGenres(Arrays.asList(getUserInput("Digite os gêneros do filme (Separados por vírgula): ", reader).split(",")));
        movieDataBuilder.setRuntime(getIntInput("Digite a duração do filme: ", reader));
        movieDataBuilder.addAllCast(Arrays.asList(getUserInput("Digite os atores do filme (Separados por vírgula): ", reader).split(",")));
        movieDataBuilder.setNumMflixComments(getIntInput("Digite a quantidade de comentários: ", reader));
        movieDataBuilder.setTitle(getUserInput("Digite o título do filme: ", reader));
        movieDataBuilder.setFullplot(getUserInput("Digite o enredo do filme: ", reader));
        movieDataBuilder.addAllCountries(Arrays.asList(getUserInput("Digite os países do filme (Separados por vírgula): ", reader).split(",")));
        movieDataBuilder.setReleased(getUserInput("Digite a data de lançamento do filme: ", reader));
        movieDataBuilder.addAllDirectors(Arrays.asList(getUserInput("Digite os diretores (Separados por vírgula): ", reader).split(",")));
        movieDataBuilder.setRated(getUserInput("Digite o rated do filme: ", reader));
        movieDataBuilder.setYear(getUserInput("Digite o ano do filme: ", reader));
        movieDataBuilder.setType(getUserInput("Digite o tipo do filme: ", reader));

        return movieDataBuilder;
    }
}


// private static void printResponse(Socket socket) {
        
    //     try {
    //         // Receber resposta do servidor
    //         DataInputStream dis = new DataInputStream(socket.getInputStream());
    //         BufferedReader inFromServer = new BufferedReader(new InputStreamReader(dis));
            
    //         String valueStr = inFromServer.readLine();
            
    //         // Ler o tamanho do buffer
    //         int sizeBuffer = Integer.valueOf(valueStr);
    //         byte[] buffer = new byte[sizeBuffer];
            
    //         System.out.println("A resposta do servidor tem ");

    //         // Ler os dados do buffer
    //         dis.readFully(buffer);
            
    //         // Parse da mensagem protobuf
    //         MovieOuterClass.Movie response = MovieOuterClass.Movie.parseFrom(buffer);
            
    //         if (response != null) {
    //             System.out.println("\nResposta do servidor: " + response.toString());
    //         } else {
    //             System.out.println("\nNão recebeu resposta válida do servidor.\n");
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
        
    // }

    // private static void printResponse(Socket socket) {
    //     try {
    //         DataInputStream dis = new DataInputStream(socket.getInputStream());
    //         int sizeBuffer = dis.readInt(); // Lê o tamanho do buffer como um inteiro
    
    //         byte[] buffer = new byte[sizeBuffer];
    //         dis.readFully(buffer); // Lê os dados do buffer
    
    //         MovieOuterClass.Movie response = MovieOuterClass.Movie.parseFrom(buffer);
    
    //         if (response != null) {
    //             System.out.println("\nResposta do servidor: " + response.toString());
    //         } else {
    //             System.out.println("\nNão recebeu resposta válida do servidor.\n");
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }