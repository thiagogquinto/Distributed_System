/**
 * Descrição: Cliente que manda a operação requerida além das informações a serem preenchidas
 * de acordo com a solicitação
 * 
 * Autor: Thiago Gariani Quinto, Marcos Vinicius de Quadros
 * 
 * Data de criação: 10/10/2023
 * Data de atualização: 11/10/2023, 12/10/2023, 13/10/2023, 14/10/2023, 15/10/2023/, 16/10/2023/ 17/10/023, 18/10/2023, 19/10/2023,
 * 23/10/2023, 24/10/2023
 * 
 */

import java.io.*;
import java.net.*;
import java.util.Arrays;
import com.proto.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
 
public class Client {
     
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 12345;

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        MovieServiceGrpc.MovieServiceBlockingStub stub = MovieServiceGrpc.newBlockingStub(channel);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while(true){
            System.out.println("Escolha um comando: ");
            System.out.println("get_movies = 1 \nget_movies_by_actor = 2 \nget_movies_by_genre = 3 \nadd_movie = 4 \nupdate_movie = 5 \ndelete_movie = 6 \nexit = 7 \n");
            String command = reader.readLine();

            if(command.equals("1")){
                MovieOuterClass.GetMoviesRequest.Builder request = MovieOuterClass.GetMoviesRequest.newBuilder();
                request.setOperation("get_movies");
                request.setParameter("todos");
                MovieOuterClass.GetMoviesResponse response = stub.getMovies(request);

                

            } else if (command.equals("2")){
                MovieOuterClass.GetMoviesRequest.Builder request = MovieOuterClass.GetMoviesRequest.newBuilder();
                String actor = getUserInput("Digite o ator: ", reader);
                request.setOperation("get_movies_by_actor");
                request.setParameter(actor);
                GetMoviesResponse response = stub.getMovies(request);
            } else if (command.equals("3")){
                MovieOuterClass.GetMoviesRequest.Builder request = MovieOuterClass.GetMoviesRequest.newBuilder();
                String genre = getUserInput("Digite o gênero: ", reader);
                request.setOperation("get_movies_by_genre");
                request.setParameter(genre);
                GetMoviesResponse response = stub.getMovies(request);
            } else if (command.equals("4")){
                MovieOuterClass.Movie.Builder request = MovieOuterClass.Movie.newBuilder();
                request.setOperation("add_movie");
                MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = createMovieData(reader);
                request.addMovies(movieDataBuilder.build());
            } else if (command.equals("5")){
                MovieOuterClass.Movie.Builder request = MovieOuterClass.Movie.newBuilder();
                request.setOperation("update_movie");
                MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = createMovieData(reader);
                request.addMovies(movieDataBuilder.build());
            } else if (command.equals("6")){
                MovieOuterClass.DeleteMovieRequest.Builder request = MovieOuterClass.DeleteMovieRequest.newBuilder();
                request.setOperation("delete_movie");
                String movie_id = getUserInput("Digite o id do filme: ", reader);
                request.setMovieId(movie_id);
            } else if (command.equals("7")){
                channel.shutdown();
                break;
            } else {
                System.out.println("Comando inválido");
            }
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