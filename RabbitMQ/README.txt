Como compilar:
    Primeiramente, certifique-se que está no diretório RabbitMQ e execute os seguinte comandos:
        make maven_build

Como executar:

    Primeiramente, execute o cliente com o seguinte comando:
        make run_client

    Em seguida, execute o servidor com o seguinte comando:
        make run_server

Bibliotecas usadas:
    Bibliotecas servidor:
        org.json.JSONObject;
        java.nio.charset.StandardCharsets;
        com.opencsv.CSVReader;
        com.opencsv.CSVReaderBuilder;
        com.rabbitmq.client.Channel;
        com.rabbitmq.client.Connection;
        com.rabbitmq.client.ConnectionFactory;
        com.opencsv.exceptions.CsvValidationException;
        
    Bibliotecas cliente:
        pika
        questionary

Exemplos de uso:

    Assim que o cliente se conectar com o servidor aparecerá a seguinte mensagem:

     Em quais tópicos você deseja se inscrever? Use as setas para navegar, <espaço> para selecionar, <a> para alternar, <i> para inverter
        » ○ basketball
          ○ football

    Dessa forma, basta seguir a instrução e selecionar os tópicos de interesse. Uma vez que isso for feito, o cliente receberá as mensagens
    que forem enviadas para os tópicos selecionados.