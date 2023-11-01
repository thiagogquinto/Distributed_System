"""
Código do servidor em gRPC que realiza as operações CRUD no banco de dados MongoDB. De acordo com a operação solicitada
pelo cliente, o servidor realiza a operação no banco de dados e retorna uma resposta ao cliente.

Autores: Thiago Gariani Quinto e Marcos Vinicius de Quadros

Data de criação: 28/10/2023
Datas de modificação: 29/10/2023, 30/10/2023, 31/10/2023
"""

import socket
import movie_pb2 
import movie_pb2_grpc
import grpc
from bson import ObjectId
from concurrent import futures
from datetime import datetime
from pymongo import MongoClient

# Configurações do servidor
HOST = '127.0.0.1'  # Endereço IP do servidor
PORT = 12345         # Porta que o servidor vai escutar

# Conecta-se ao MongoDB
uri = "mongodb+srv://root:root@cluster0.ivcdldw.mongodb.net/?retryWrites=true&w=majority"
client = MongoClient(uri)
db = client.get_database('sample_mflix')
movies_collection = db.movies

class MovieService(movie_pb2_grpc.MovieServiceServicer):

    def handleRequest(self, request):
        if request.operation == "get_movies":
            return self.GetMovies(request)
        elif request.operation == "get_movies_by_actor":
            return self.GetMovieByActor(request)
        elif request.operation == "get_movies_by_genre":
            return self.GetMovieByGenre(request)
        elif request.operation == "delete_movie":
            return self.DeleteMovie(request)
        elif request.operation == "update_movie":
            return self.UpdateMovie(request)
        elif request.operation == "add_movie":
            return self.AddMovie(request)
        else:
            return movie_pb2.Empty()
        
    """
    Função que retorna todos os filmes do banco de dados e retorna uma lista de filmes ao cliente.
    """

    def GetMovies(self, request, context):

        print("Buscando filmes...")
        movies = movies_collection.find({}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie.get('plot', ""),
                genres=movie.get('genres', []),
                runtime=movie.get('runtime', 0),
                cast=movie.get('cast', []),
                num_mflix_comments=movie.get('num_mflix_comments', 0),
                title=movie.get('title', ""),
                fullplot=movie.get('fullplot', ""),
                countries=movie.get('countries', []),
                released=str(movie.get('released', "")),
                directors=movie.get('directors', []),
                rated=movie.get('rated', ""),
                lastupdated=movie.get('lastupdated', ""),
                year=str(movie.get('year', "")),
                type=movie.get('type', "")
            ))

        print("Enviando filmes...")

        return response
    
    """
    Função que retorna todos os filmes de um determinado ator e retorna uma lista de filmes ao cliente.
    """
    def GetMovieByActor(self, request, context):

        print("Buscando filmes de " + request.parameter + "...")
        movies = movies_collection.find({"cast": request.parameter}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie.get('plot', ""),
                genres=movie.get('genres', []),
                runtime=movie.get('runtime', 0),
                cast=movie.get('cast', []),
                num_mflix_comments=movie.get('num_mflix_comments', 0),
                title=movie.get('title', ""),
                fullplot=movie.get('fullplot', ""),
                countries=movie.get('countries', []),
                released=str(movie.get('released', "")),
                directors=movie.get('directors', []),
                rated=movie.get('rated', ""),
                lastupdated=movie.get('lastupdated', ""),
                year=str(movie.get('year', "")),
                type=movie.get('type', "")
            ))

        print("Enviando filmes...")

        return response

    """
    Função que retorna todos os filmes de um determinado gênero e retorna uma lista de filmes ao cliente.
    """
    def GetMovieByGenre(self, request, context):

        print("Buscando filmes de gênero " + request.parameter + "...")
        movies = movies_collection.find({"genres": request.parameter}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie.get('plot', ""),
                genres=movie.get('genres', []),
                runtime=movie.get('runtime', 0),
                cast=movie.get('cast', []),
                num_mflix_comments=movie.get('num_mflix_comments', 0),
                title=movie.get('title', ""),
                fullplot=movie.get('fullplot', ""),
                countries=movie.get('countries', []),
                released=str(movie.get('released', "")),
                directors=movie.get('directors', []),
                rated=movie.get('rated', ""),
                lastupdated=movie.get('lastupdated', ""),
                year=str(movie.get('year', "")),
                type=movie.get('type', "")
            ))

        print("Enviando filmes...")
        return response
    
    """
    Função que deleta um filme do banco de dados e retorna uma resposta ao cliente informando se o filme foi deletado ou não.
    """
    def DeleteMovie(self, request, context):

        if ObjectId.is_valid(request.id) == False: 
            return movie_pb2.Response(response="ID inválido!")
        else:
            result = movies_collection.delete_one({"_id": ObjectId(request.id)})

            if result.deleted_count == 1:
                return movie_pb2.Response(response="Filme deletado com sucesso!")
            else:
                return movie_pb2.Response(response="Filme não encontrado!") 
            

    """
    Função que atualiza um filme do banco de dados e retorna uma resposta ao cliente informando se o filme foi atualizado ou não.
    """
    def UpdateMovie(self, request, context):

        lstupdt = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        if ObjectId.is_valid(request.id) == False:
            return movie_pb2.Response(response="ID inválido!")
        else:
            updated_movie = {
                "plot": request.plot, 
                "genres": list(request.genres),  
                "runtime": request.runtime, 
                "cast": list(request.cast), 
                "num_mflix_comments": request.num_mflix_comments,  
                "title": request.title, 
                "fullplot": request.fullplot, 
                "countries": list(request.countries), 
                "released": request.released,
                "directors": list(request.directors), 
                "rated": request.rated, 
                "lastupdated": lstupdt,
                "year": request.year, 
                "type": request.type, 
            }

            result = movies_collection.update_one({"_id": ObjectId(request.id)}, {"$set": updated_movie})

            if result.modified_count == 1:
                return movie_pb2.Response(response="Filme atualizado com sucesso!")
            else:
                return movie_pb2.Response(response="Filme não encontrado!")

    """
    Função que adiciona um filme no banco de dados e retorna uma resposta ao cliente informando se o filme foi adicionado ou não.
    """
    def AddMovie(self, request, context):

        movie_data = {
            "plot": request.plot,
            "genres": list(request.genres),
            "runtime": request.runtime,
            "cast": list(request.cast),
            "num_mflix_comments": request.num_mflix_comments,
            "title": request.title,
            "fullplot": request.fullplot,
            "countries": list(request.countries),
            "released": request.released,
            "directors": list(request.directors),
            "rated": request.rated,
            "lastupdated": request.lastupdated,
            "year": request.year,
            "type": request.type
        }

        result = movies_collection.insert_one(movie_data)

        if result.inserted_id:
            return movie_pb2.Response(response="Filme adicionado com sucesso! - ID: " + str(result.inserted_id))
        else:
            return movie_pb2.Response(response="Erro ao adicionar filme!")
        
def main():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10), options=[('grpc.max_send_message_length', 1024 * 1024 * 1024), ('grpc.max_receive_message_length', 1024 * 1024 * 1024)])
    movie_pb2_grpc.add_MovieServiceServicer_to_server(MovieService(), server)
    server.add_insecure_port('[::]:12345')
    server.start()
    print("Servidor escutando em 12345...")
    server.wait_for_termination() 

if __name__ == "__main__":
    main()
