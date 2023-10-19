import socket
import movie_pb2 
from bson import ObjectId
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

# Send a ping to confirm a successful connection
def ping_connection():
    try:
        client.admin.command('ping')
        print("Pinged your deployment. You successfully connected to MongoDB!")
    except Exception as e:
        print(e)

# Função que recebe a mensagem protobuf e retorna os filmes do MongoDB
def get_movies(args = None, movie_proto = None):
    try:
        
        if args is None:
            movies_cursor = movies_collection.find({}, {"imdb":0, "tomatoes":0, "awards":0})
        elif args == "cast":
            movies_cursor = movies_collection.find({"cast": movie_proto.cast[0]}, {"imdb":0, "tomatoes":0, "awards":0})
        elif args == "genres":
            movies_cursor = movies_collection.find({"genres": movie_proto.genres[0]}, {"imdb":0, "tomatoes":0, "awards":0})

        # Cria uma mensagem protobuf para armazenar os filmes
        response_proto = movie_pb2.Movie()

        # Preenche a mensagem protobuf com os dados dos filmes
        for movie in movies_cursor:
            movie_proto = response_proto.movies.add()
            movie_proto.id = str(movie.get("_id", ""))
            movie_proto.plot = movie.get("plot", "")
            movie_proto.genres.extend(movie.get("genres", []))
            movie_proto.runtime = movie.get("runtime", 0)
            movie_proto.cast.extend(movie.get("cast", []))
            movie_proto.num_mflix_comments = movie.get("num_mflix_comments", 0)
            movie_proto.title = movie.get("title", "")
            movie_proto.fullplot = movie.get("fullplot", "")
            movie_proto.countries.extend(movie.get("countries", []))
            movie_proto.released = str(movie.get("released", ""))
            movie_proto.directors.extend(movie.get("directors", []))
            movie_proto.rated = movie.get("rated", "")
            movie_proto.lastupdated = movie.get("lastupdated", "")
            movie_proto.year = str(movie.get("year", ""))
            movie_proto.type = movie.get("type", "")

        # Retorna a mensagem protobuf diretamente, sem chamar SerializeToBytes
        return response_proto

    except Exception as e:
        # Retorna a mensagem de erro como bytes
        return bytes(f"Erro ao obter filmes: {e}\n", 'utf-8')
 
# Função que recebe a mensagem protobuf e adiciona um filme no MongoDB
def add_movie(movie_proto):
    try:
        # Extrair os campos da mensagem protobuf
        movie_data = {
            "plot": movie_proto.plot,
            "genres": list(movie_proto.genres),
            "runtime": movie_proto.runtime,
            "cast": list(movie_proto.cast),
            "num_mflix_comments": movie_proto.num_mflix_comments,
            "title": movie_proto.title,
            "fullplot": movie_proto.fullplot,
            "countries": list(movie_proto.countries),
            "released": movie_proto.released,
            "directors": list(movie_proto.directors),
            "rated": movie_proto.rated,
            "lastupdated": movie_proto.lastupdated,
            "year": movie_proto.year,
            "type": movie_proto.type
        }

        # Inserir o filme no MongoDB
        result = movies_collection.insert_one(movie_data)

        # Cria uma mensagem protobuf para armazenar os filmes
        response_proto = movie_pb2.Movie()

        if result.inserted_id is None:
            response_proto.response = "Erro ao adicionar filme\n"
        else:
            response_proto.response = "Filme adicionado com sucesso. ID: " + str(result.inserted_id) + "\n"

        return response_proto

    except Exception as e:
        response_proto.response = "Erro ao adicionar filme: " + str(e) + "\n"
        return response_proto
    
# Função que recebe a mensagem protobuf e atualiza um filme no MongoDB
def update_movie(movie_proto):
    try:
        response_proto = movie_pb2.Movie()  # Define response_proto como uma mensagem protobuf
        
        # atualiza o campo lastupdated
        movie_proto.lastupdated = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
         
        # extrai os campos da mensagem protobuf
        movie_data = {
            "plot": movie_proto.plot,
            "genres": list(movie_proto.genres),
            "runtime": movie_proto.runtime,
            "cast": list(movie_proto.cast),
            "num_mflix_comments": movie_proto.num_mflix_comments,
            "title": movie_proto.title,
            "fullplot": movie_proto.fullplot,
            "countries": list(movie_proto.countries),
            "released": movie_proto.released,
            "lastupdated": movie_proto.lastupdated,
            "rated": movie_proto.rated,
            "directors": list(movie_proto.directors),
            "year": movie_proto.year,
            "type": movie_proto.type
        }

        object_id = ObjectId(movie_proto.id)
        
        # atualiza os dados no MongoDB
        result = movies_collection.update_one({"_id": object_id}, {"$set": movie_data})

        if result.modified_count == 0:
            response_proto.response = "Filme não encontrado\n"
        else:
            response_proto.response = f"Filme atualizado com sucesso. Contagem: {result.modified_count}\n"
        
        return response_proto

    except Exception as e:
        response_proto.response = f"Erro ao atualizar filme: {e}\n"
        return response_proto


# Função que recebe a mensagem protobuf e deleta um filme no MongoDB   
def delete_movie(title, id):
    try:
        object_id = ObjectId(id)
        result = movies_collection.delete_one({"_id": object_id})
        print("Aqui")
        response_proto = movie_pb2.Movie()
        
        if result.deleted_count == 0:
            response_proto.response = "Filme não encontrado\n"
        else:
            response_proto.response = f"Filme removido com sucesso. Contagem: {result.deleted_count}\n"

        return response_proto
    
    except Exception as e:
        response_proto.response = f"Erro ao remover filme: {e}\n"
        return response_proto

# Função que recebe a mensagem protobuf e envia uma resposta
def send_response(response):
    serialized_response = response.SerializeToString()
    size = len(serialized_response)

    # Envia o tamanho do buffer como um inteiro seguido pelos dados serializados
    client_socket.send((str(size) + "\n").encode())
    client_socket.send(serialized_response)
    print("Resposta enviada")

while True:
    client_socket, addr = server_socket.accept()
    print(f"\n\nConexão recebida de {addr}")

    while True:

        try:
            # Receber o tamanho do buffer como um inteiro
            size_buffer = int.from_bytes(client_socket.recv(4), byteorder='big')

            # Receber os dados do buffer
            buffer = client_socket.recv(size_buffer)

            # Parse da mensagem recebida
            request = movie_pb2.Movie()
            request.ParseFromString(buffer)
            
            # Processar a mensagem recebida
            if request.operation == "get_movies":
                print("Recebido pedido de filmes")
                response = get_movies()            
                send_response(response)

            elif request.operation == "get_movies_by_actor":
                print("Recebido pedido de filmes por ator")
                response = get_movies("cast", request.movies[0])
                send_response(response)

            elif request.operation == "get_movies_by_genre":
                print("Recebido pedido de filmes por gênero")
                response = get_movies("genres", request.movies[0])
                send_response(response)

            elif request.operation == "add_movie":
                print("Recebido pedido para adicionar filme")
                response = add_movie(request.movies[0])
                send_response(response)

            elif request.operation == "delete_movie":
                print("Recebido pedido para deletar filme")
                response = delete_movie(request.movies[0].title, request.movies[0].id)
                send_response(response)

            elif request.operation == "update_movie":
                print("Recebido pedido para atualizar filme")
                response = update_movie(request.movies[0])
                print("AQUI")
                send_response(response)

            elif request.operation == "exit":
                print("Conexão encerrada\n")
                client_socket.close()
                break
        
        except ConnectionResetError:
            print("Cliente desconectado abruptamente.")
            client_socket.close()

        except Exception as e:
            print(f"Erro ao processar a mensagem: {e}")

