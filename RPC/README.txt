Geração de arquivos:
    Para gerar os arquivos necessários tanto para o servidor quanto para o cliente, execute os seguintes comandos:
        make generate_python
        make maven_build

Como compilar:
    Primeiramente, certifique-se que está no diretório RPC e execute os seguinte comandos:
    
Como executar:

    Primeiramente, certifique-se que está no diretório RPC e execute os seguinte comandos:
        make server_run
        make client_run

Bibliotecas usadas:
    Bibliotecas servidor:
        socket
        grpc
        bson  
        concurrent  
        datetime  
        pymongo  
        
    Bibliotecas cliente:
        java.io.*;
        java.net.*;
        java.util.Arrays;
        io.grpc.ManagedChannel;
        io.grpc.ManagedChannelBuilder;

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