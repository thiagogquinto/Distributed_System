Como compilar:

Primeiramente, certifique - se de que o diretório atual é o diretório que contém a pasta atividade2. Uma vez uqe esteja no diretório correto, execute os seguintes comandos:

    javac atividade2/TCPClient.java
    javac atividade2/TCPServer.java

Como executar:

Para executar o servidor, execute o seguinte comando:

    java atividade2.TCPServer

Para executar o cliente, abra um novo terminal e execute o seguinte comando:

    java atividade2.TCPClient

Bibliotecas usadas:

java.io.*;
java.net.*;
java.util.Scanner;
java.nio.ByteOrder;
java.nio.ByteBuffer;
java.nio.file.Files;
java.util.List;
java.util.ArrayList;
java.util.logging.*;
java.io.File;


Exemplos de uso:

- ADDFILE: ADDFILE <nome_arquivo>
- GETFILE: GETFILE <nome_arquivo>
- DELETE: DELETE <nome_arquivo>
- GETFILESLIST: GETFILESLIST
