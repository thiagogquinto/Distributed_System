import java.io.*;
import java.net.*;
import java.util.Arrays;
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
                Movie.GetMoviesRequest.Builder request = Movie.GetMoviesRequest.newBuilder();
                request.setOperation("get_movies");
                request.setParameter("todos");
                Movie.MovieList response = stub.getMovies(request);

               if (movieList.getMoviesCount() == 0) {
                    System.out.println("Nenhum filme encontrado");
                } else {
                    for (Movie.MoviesData movieData : response.getMoviesList()) {
                        System.out.println("ID: " + movieData.getId());
                        System.out.println("Plot: " + movieData.getPlot());
                        System.out.println("Gêneros: " + movieData.getGenresList());
                        System.out.println("Duração: " + movieData.getRuntime());
                        System.out.println("Atores: " + movieData.getCastList());
                        System.out.println("Quantidade de comentários: " + movieData.getNumMflixComments());
                        System.out.println("Título: " + movieData.getTitle());
                        System.out.println("Enredo: " + movieData.getFullplot());
                        System.out.println("Países: " + movieData.getCountriesList());
                        System.out.println("Data de lançamento: " + movieData.getReleased());
                        System.out.println("Diretores: " + movieData.getDirectorsList());
                        System.out.println("Rated: " + movieData.getRated());
                        System.out.println("Ano: " + movieData.getYear());
                        System.out.println("Tipo: " + movieData.getType());
                        System.out.println("--------------------------------------------------");
                    }
                }
            } else if (command.equals("2")){
                Movie.GetMoviesRequest.Builder request = Movie.GetMoviesRequest.newBuilder();
                String actor = getUserInput("Digite o ator: ", reader);
                request.setOperation("get_movies_by_actor");
                request.setParameter(actor);
                Movie.MovieList response = stub.getMovies(request);

                if (movieList.getMoviesCount() == 0) {
                    System.out.println("Nenhum filme de " + actor + " encontrado");
                } else {
                    for (Movie.MoviesData movieData : response.getMoviesList()) {
                        System.out.println("ID: " + movieData.getId());
                        System.out.println("Plot: " + movieData.getPlot());
                        System.out.println("Gêneros: " + movieData.getGenresList());
                        System.out.println("Duração: " + movieData.getRuntime());
                        System.out.println("Atores: " + movieData.getCastList());
                        System.out.println("Quantidade de comentários: " + movieData.getNumMflixComments());
                        System.out.println("Título: " + movieData.getTitle());
                        System.out.println("Enredo: " + movieData.getFullplot());
                        System.out.println("Países: " + movieData.getCountriesList());
                        System.out.println("Data de lançamento: " + movieData.getReleased());
                        System.out.println("Diretores: " + movieData.getDirectorsList());
                        System.out.println("Rated: " + movieData.getRated());
                        System.out.println("Ano: " + movieData.getYear());
                        System.out.println("Tipo: " + movieData.getType());
                        System.out.println("--------------------------------------------------");
                    }
                }

            } else if (command.equals("3")){
                Movie.GetMoviesRequest.Builder request = Movie.GetMoviesRequest.newBuilder();
                String genre = getUserInput("Digite o gênero: ", reader);
                request.setOperation("get_movies_by_genre");
                request.setParameter(genre);
                Movie.MovieList movieList = response.getMoviesList();
                
                if (movieList.getMoviesCount() == 0) {
                    System.out.println("Nenhum filme de gênero " + genre + " encontrado	");
                } else {
                    for (Movie.MoviesData movieData : response.getMoviesList()) {
                        System.out.println("ID: " + movieData.getId());
                        System.out.println("Plot: " + movieData.getPlot());
                        System.out.println("Gêneros: " + movieData.getGenresList());
                        System.out.println("Duração: " + movieData.getRuntime());
                        System.out.println("Atores: " + movieData.getCastList());
                        System.out.println("Quantidade de comentários: " + movieData.getNumMflixComments());
                        System.out.println("Título: " + movieData.getTitle());
                        System.out.println("Enredo: " + movieData.getFullplot());
                        System.out.println("Países: " + movieData.getCountriesList());
                        System.out.println("Data de lançamento: " + movieData.getReleased());
                        System.out.println("Diretores: " + movieData.getDirectorsList());
                        System.out.println("Rated: " + movieData.getRated());
                        System.out.println("Ano: " + movieData.getYear());
                        System.out.println("Tipo: " + movieData.getType());
                        System.out.println("--------------------------------------------------");
                    }
                }
            } else if (command.equals("4")){
                Movie.MoviesData.Builder movieDataBuilder = createMovieData(reader);
                Movie.Response response = stub.addMovie(movieDataBuilder.build());
                System.out.println(response.getMessage());
            } else if (command.equals("5")){
                Movie.MoviesData.Builder request = Movie.createMovieData(reader);
                String movie_id = getUserInput("Digite o id do filme: ", reader);
                request.setId(movie_id);
                Movie.Response response = stub.updateMovie(request.build());
                System.out.println(response.getMessage());
            } else if (command.equals("6")){
                Movie.DeleteMovieRequest.Builder request = Movie.DeleteMovieRequest.newBuilder();
                String movie_id = getUserInput("Digite o id do filme: ", reader);
                request.setMovieId(movie_id);
                Movie.Response response = stub.deleteMovie(request);
                System.out.println(response.getMessage());
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
    public static Movie.MoviesData.Builder createMovieData(BufferedReader reader) throws IOException {

        Movie.MoviesData.Builder movieDataBuilder = Movie.MoviesData.newBuilder();
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