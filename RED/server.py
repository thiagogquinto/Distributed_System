import socket
import json
from pymongo import MongoClient
from pprint import pprint

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

def add_movie(movie):
    try:
        if not isinstance(movie, dict):
            movie = dict(movie)
                             
        result = movies_collection.insert_one(movie)
        if result.inserted_id is None:
            return "Erro ao adicionar filme\n"
        else:
            return "Filme adicionado com sucesso. ID: " + str(result.inserted_id) + "\n"
    except Exception as e:
        return "Erro ao adicionar filme: " + str(e) + "\n"

def get_movies():
    try:
        # movies = movies_collection.find({}, {"title": 1})
        movies = movies_collection.find({}, {"imdb":0, "tomatoes":0, "awards":0})

        movies_list = []
        for movie in movies:
            movies_list.append(movie)
        
        # Converte a lista de filmes para uma string JSON
        movie_json_string = json.dumps(movies_list, default=str)

        # Retorna a string JSON
        return movie_json_string+"\n"    
    
    except Exception as e:
        return f"Erro ao obter filmes: {e}\n"

def get_movies_by_criteria(criteria, value):
    try:
        
        # Pega os filmes com base no critério e exclui os campos especificados
        movies = movies_collection.find({criteria: value}, {"imdb": 0, "tomatoes": 0, "awards": 0})

        movies_list = list(movies)
        if len(movies_list) == 0:
            return "Nenhum filme encontrado\n"
        else:
            # Converte a lista de filmes para uma string JSON
            movie_json_string = json.dumps(movies_list, default=str)

            # Retorna a string JSON
            return movie_json_string+"\n"
    except Exception as e:
        return f"Erro ao obter filmes: {e}\n"    

def update_movie(title, movie):
            try:
                result = movies_collection.update_one({"title": title}, {"$set": movie})
                if result.matched_count == 0:
                    return "Filme não encontrado\n"
                else:
                    return f"Filme atualizado com sucesso. ID: {result.modified_count}\n"
            except Exception as e:
                return f"Erro ao atualizar filme: {e}\n"

def delete_movie(title):
    try:
        result = movies_collection.delete_one({"title": title})
        if result.deleted_count == 0:
            return "Filme não encontrado\n"
        else:
            return f"Filme removido com sucesso. ID: {result.deleted_id}\n"
    
    except Exception as e:
        return f"Erro ao remover filme: {e}\n"

while True:
    # Aguarda por uma conexão
    client_socket, addr = server_socket.accept()
    print(f"Conexão recebida de {addr}")

    while True:
        print("Aqui")
        # Recebe os dados do cliente
        data = client_socket.recv(1024)

        # Decodifica os dados (assumindo que são enviados como JSON)
        received_data = json.loads(data.decode('utf-8'))

        if received_data.get("operation") == "exit":
            # Se o comando for "exit," fecha a conexão e sai do loop interno
            client_socket.close()
            break

        # Aqui você pode chamar as funções que manipulam os dados, por exemplo:
        
        if received_data.get("operation") == "add_movie":
            response = add_movie(received_data.get("movie"))

        elif received_data.get("operation") == "get_movies":
            response = get_movies() 

        elif received_data.get("operation") == "get_movies_by_actor":
            response = get_movies_by_criteria("cast", received_data.get("actor"))

        elif received_data.get("operation") == "get_movies_by_genre":
            response = get_movies_by_criteria("genres", received_data.get("genre"))

        elif received_data.get("operation") == "update_movie":
            response = update_movie(received_data.get("title"), received_data.get("movie"))
        
        elif received_data.get("operation") == "delete_movie":
            response = delete_movie(received_data.get("title"))

        # Envia a resposta para o cliente
        # client_socket.send(json.dumps(response).encode('utf-8'))
        client_socket.send(response.encode('utf-8'))


    # Fecha a conexão com o cliente
    client_socket.close()