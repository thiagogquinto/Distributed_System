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

# Cria um socket TCP
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Liga o socket ao endereço e porta especificados
server_socket.bind((HOST, PORT))

# Habilita o socket para aceitar conexões
server_socket.listen()

print(f"Servidor escutando em {HOST}:{PORT}...")

class MovieService(movie_pb2_grpc.MovieServiceServicer):

    def handleRequest(self, request):
        if request.operation == "get_movies":
            return self.GetMovies(request)
        elif request.operation == "get_movie_by_actor":
            return self.GetMovieByActor(request)
        elif request.operation == "get_movie_by_genre":
            return self.GetMovieByGenre(request)
        elif request.operation == "delete_movie":
            return self.DeleteMovie(request)
        elif request.operation == "update_movie":
            return self.UpdateMovie(request)
        elif request.operation == "add_movie":
            return self.AddMovie(request)
        else:
            return movie_pb2.Empty()

    def GetMovies(self, request, context):

        movies = movies_collection.find({}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie['plot'],
                genres=movie['genres'],
                runtime=movie['runtime'],
                cast=movie['cast'],
                num_mflix_comments=movie['num_mflix_comments'],
                title=movie['title'],
                fullplot=movie['fullplot'],
                countries=movie['countries'],
                released=movie['released'].strftime('%Y-%m-%d'),
                directors=movie['directors'],
                rated=movie['rated'],
                lastupdated=movie['lastupdated'].strftime('%Y-%m-%d %H:%M:%S'),
                year=movie['year'],
                type=movie['type']
            ))

        return response
    
    def GetMovieByActor(self, request, context):
        movies = movies_collection.find({"cast": request.actor}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie['plot'],
                genres=movie['genres'],
                runtime=movie['runtime'],
                cast=movie['cast'],
                num_mflix_comments=movie['num_mflix_comments'],
                title=movie['title'],
                fullplot=movie['fullplot'],
                countries=movie['countries'],
                released=movie['released'].strftime('%Y-%m-%d'),
                directors=movie['directors'],
                rated=movie['rated'],
                lastupdated=movie['lastupdated'].strftime('%Y-%m-%d %H:%M:%S'),
                year=movie['year'],
                type=movie['type']
            ))

        return response

    def GetMovieByGenre(self, request, context):
        movies = movies_collection.find({"genres": request.genre}, {"imdb":0, "tomatoes":0, "awards":0})
        response = movie_pb2.MovieList()

        for movie in movies:
            response.movies.append(movie_pb2.MoviesData(
                id=str(movie['_id']),
                plot=movie['plot'],
                genres=movie['genres'],
                runtime=movie['runtime'],
                cast=movie['cast'],
                num_mflix_comments=movie['num_mflix_comments'],
                title=movie['title'],
                fullplot=movie['fullplot'],
                countries=movie['countries'],
                released=movie['released'].strftime('%Y-%m-%d'),
                directors=movie['directors'],
                rated=movie['rated'],
                lastupdated=movie['lastupdated'].strftime('%Y-%m-%d %H:%M:%S'),
                year=movie['year'],
                type=movie['type']
            ))

        return response
    
    def DeleteMovie(self, request, context):
        result = movies_collection.delete_one({"_id": ObjectId(request.id)})

        if result.deleted_count == 1:
            return movie_pb2.Response(message="Filme deletado com sucesso!")
        else:
            return movie_pb2.Response(message="Filme não encontrado!") 

    def UpdateMovie(self, request, context):

        updated_movie = {
            "plot": request.plot, 
            "genres": list(request.genres),  
            "runtime": request.runtime, 
            "cast": list(request.cast), 
            "num_mflix_comments": request.num_mflix_comments,  
            "title": request.title, 
            "fullplot": request.fullplot, 
            "countries": list(request.countries), 
            "released": datetime.strptime(request.released, '%Y-%m-%d'), 
            "directors": list(request.directors), 
            "rated": request.rated, 
            "lastupdated": datetime.strptime(request.lastupdated, '%Y-%m-%d %H:%M:%S'), 
            "year": request.year, 
            "type": request.type, 
        }

        result = movies_collection.update_one({"_id": ObjectId(request.id)}, {"$set": updated_movie})

        if result.modified_count == 1:
            return movie_pb2.Response(message="Filme atualizado com sucesso!")
        else:
            return movie_pb2.Response(message="Filme não encontrado!")

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
            return movie_pb2.Response(message="Filme adicionado com sucesso! - ID: " + str(result.inserted_id))
        else:
            return movie_pb2.Response(message="Erro ao adicionar filme!")
        
def main():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    movie_pb2_grpc.add_MovieServiceServicer_to_server(MovieService(), server)
    server.add_insecure_port('[::]:12345')
    server.start()
    print("Servidor escutando em 12345...")
    server.wait_for_termination() 

if __name__ == "__main__":
    main()
