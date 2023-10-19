Como compilar:
    
    javac -classpath .:protobuf-java-3.24.4.jar Client

Como executar:

Para executar o cliente é necessário estar no diretório javacode, execute o seguinte comando:
    java -classpath .:protobuf-java-3.24.4.jar Client

Para executar o servidor é necessário estar no diretório pythoncode, execute o seguinte comando:
    python3 server.python3

Bibliotecas usadas:
    Bibliotecas servidor:
        import socket
        import movie_pb2 
        from bson import ObjectId
        from pymongo import MongoClient
        
    Bibliotecas cliente:
        import java.io.*;
        import java.net.*;
        import java.util.Arrays;

Exemplos de uso:

    Assim que o cliente se conectar com o servidor aparecerá as seguinte opções:

        Escolha um comando: 
        get_movies = 1 
        get_movies_by_actor = 2 
        get_movies_by_genre = 3 
        add_movie = 4 
        update_movie = 5 
        delete_movie = 6 
        exit = 7 

    get_movies:
        Digite 1

    get_movies_by_actor:
        Digite 2 e informe o nome do ator que deseja realizar a listagem

    get_movies_by_genre:
        Digite 3 e informe o gênero do filme que deseja realizar a listagem

    add_movie:
        Digite 4 e informe os dados conforme forem sendo pedidos

    update_movie
        Digite 5, informe o id do filme e informe os dados conforme forem sendo pedidos

    delete_movie
        Digite 6 e informe o id do filme que deseja deletar

    exit
        Digite 7