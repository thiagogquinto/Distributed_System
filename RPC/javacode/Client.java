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

            // Criação do objeto de requisição
            MovieOuterClass.Movie.Builder requestBuilder = MovieOuterClass.Movie.newBuilder();
            MovieOuterClass.Movie.MovieData.Builder movieDataBuilder = MovieOuterClass.Movie.MovieData.newBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                System.out.println("Escolha um comando: ");
                System.out.println("get_movies = 1 \nget_movies_by_actor = 2 \nget_movies_by_genre = 3 \nadd_movie = 4 \nupdate_movie = 5 \ndelete_movie = 6 \nexit = 7 \n");
                String command = reader.readLine();

                if(command == "1"){
                    GetMoviesRequest request = GetMoviesRequest.newBuilder().build();
                    request.setOperation(command);
                    request.setParameter("todos");
                    GetMoviesResponse response = stub.getMovies(request);
                } else if (command == "2"){
                    GetMoviesRequest request = GetMoviesRequest.newBuilder().build();
                    String actor = getUserInput("Digite o ator: ", reader);
                    request.setOperation(command);
                    request.setParameter(actor);
                    GetMoviesResponse response = stub.getMovies(request);
                } else if (command == "3"){
                    GetMoviesRequest request = GetMoviesRequest.newBuilder().build();
                    String genre = getUserInput("Digite o gênero: ", reader);
                    request.setOperation(command);
                    request.setParameter(genre);
                    GetMoviesResponse response = stub.getMovies(request);
                } else if (command == "7"){
                    break;
                } else {
                    System.out.println("Comando inválido");
                }
            }

           
}