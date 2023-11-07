import java.io.*;
import java.util.*;
import org.json.*;

import java.nio.charset.StandardCharsets;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;

public class Classifier {

    // Função para receber mensagens do RabbitMQ e retornar uma String com a mensagem
    public static String receiveMessage(String exchangeName, String routingKey) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // replace with your host if not localhost
        final String[] message = new String[1];
    
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);
            // channel.queueDeclare(queueName, true, false, false, null);
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    message[0] = new String(body, StandardCharsets.UTF_8);
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return message[0];
    }

    // Função para enviar mensagens para o RabbitMQ
    public static void sendMessage(String queueName, String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // substitua por seu host se não for localhost
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("\n [x] Sent '" + message + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void teste() {
        String content = receiveMessage("tweets_exchange", "tweets");

        if (content == null) {
            System.out.println("\n\nNenhuma mensagem recebida\n\n");
            return;
        }

        JSONArray jsons = new JSONArray(content);

        for (int i = 0; i < jsons.length(); i++) {
            JSONObject json = jsons.getJSONObject(i);
            if (json.getString("topic").equals("basketball")) {
                // System.out.println("\n\n" + json.toString() + "\n\n");
                sendMessage("basketball", json.toString());
            } else if (json.getString("topic").equals("football")){
                // System.out.println("\n\n"+ json.toString() + "\n\n");
                sendMessage("football", json.toString());
            }
        }
    }

    public static void main(String[] args) {
        Classifier classifier = new Classifier();
        String msg = "    [{\r\n" + //
                "        \"name\": \"aye_brenden\",\r\n" + //
                "        \"description\": \"Averett Football #1 john 3:16 God,Family,Football\",\r\n" + //
                "        \"text\": \"@kass_xo was it really that funny lol and how are you stranger lol\",\r\n" + //
                "        \"topic\": \"football\"\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "        \"name\": \"user2\",\r\n" + //
                "        \"description\": \"Another description\",\r\n" + //
                "        \"text\": \"Another tweet text\",\r\n" + //
                "        \"topic\": \"basketball\"\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "        \"name\": \"user3\",\r\n" + //
                "        \"description\": \"Yet another description\",\r\n" + //
                "        \"text\": \"Yet another tweet text\",\r\n" + //
                "        \"topic\": \"basketball\"\r\n" + //
                "    }]";
        
        sendMessage("topic", msg);
        
        // classifier.teste();
    }
}

