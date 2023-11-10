/**
 * Código responsável por coletar os tweets e enviar para a fila do RabbitMQ
 * Autores: Marcos Vinicius de Quadros e Thiago Gariani Quinto
 * Data de criação: 08/11/2023
 * Data de modificações: 10//11/2023
 *
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

    private ConnectionFactory factory;

    /**
     * Construtor da classe Collector que inicializa a conexão com o RabbitMQ 
     * 
     */
    public Collector() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    /**
     * Método responsável por enviar os tweets coletados para a fila do RabbitMQ 
     * 
     * @param data tweet coletado
     */
    public void sendToRabbitMQ(JSONObject data) {
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            
            channel.exchangeDeclare("tweets_exchange", "direct", false); 
            channel.queueDeclare("tweets_queue", false, false, false, null);
            channel.queueBind("tweets_queue", "tweets_exchange", "tweets");
            channel.basicPublish("tweets_exchange", "tweets", null, data.toString().getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Método responsável por ler o arquivo csv e chamar o método sendToRabbitMQ para enviar os tweets para a fila do RabbitMQ
     */
    public void readCsvFile() {
        try {
            CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/tweets_data.csv")).build();
            String[] nextLine;

            int totalLines = countLines("src/main/resources/tweets_data.csv");

            while((nextLine = reader.readNext()) != null) {

                System.out.println("Lendo linha " + reader.getLinesRead() + " de " + totalLines);
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

    /**
     * Método responsável por contar o número de linhas do arquivo csv
     * 
     * @return número de linhas do arquivo csv
     */
    public int countLines(String filename) {
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while (reader.readLine() != null) lines++;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    public static void main(String[] args) {
        Collector collector = new Collector();
        collector.readCsvFile();
    }

}
