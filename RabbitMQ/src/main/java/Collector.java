/**
 * Código responsável por coletar os tweets e enviar para a fila do RabbitMQ
 * Autores: Marcos Vinicius de Quadros e Thiago Gariani Quinto
 * Data de criação: 08/11/2023
 */

import java.io.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.opencsv.exceptions.CsvValidationException;


public class Collector {

    public void sendToRabbitMQ(JSONObject data) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        // Tenta criar uma conexão com o RabbitMQ e enviar a mensagem
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            channel.queueBind("tweets_queue", "tweets_exchange", "tweets");
            channel.basicPublish("tweets_exchange", "tweets", null, data.toString().getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void readCsvFile() {
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/tweets_data.csv")).build();
            String[] nextLine;

            while((nextLine = reader.readNext()) != null) {
                JSONObject tweet = new JSONObject();
                tweet.put("name", nextLine[14]);
                tweet.put("text", nextLine[19]);
                
                sendToRabbitMQ(tweet);
            }

            reader.close();
        
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }catch (IOException  e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Collector collector = new Collector();
        collector.readCsvFile();
    }
}
