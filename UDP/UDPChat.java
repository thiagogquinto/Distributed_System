package UDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class UDPChat {
    public static void main(String[] args) throws IOException {

        Scanner reader = new Scanner(System.in); // ler mensagens via teclado
        InetAddress destinationIp = InetAddress.getByName("localhost");

        System.out.println("Porta de origem: ");
        String portString = reader.nextLine();
        int originPort = Integer.parseInt(portString);

        System.out.println("Porta de destino: ");
        portString = reader.nextLine();
        int destinationPort = Integer.parseInt(portString);

        DatagramSocket socket = new DatagramSocket();
        DatagramSocket receiverSocket = new DatagramSocket(originPort); // Use a porta do Cliente 1

        System.out.println("Digite seu apelido:");
        
        String nick = "";
        while (nick.isEmpty() || nick.contains(":")) {
            nick = reader.nextLine();
            if (nick.isEmpty() || nick.contains(":")) {
                System.err.println("Apelido inválido!");
            }
        }
        
        byte[] nickBytes = nick.getBytes();
        byte nickSize = (byte) nickBytes.length;
        
        // Thread para enviar mensagens
        Thread senderThread = new Thread(() -> {
            String messageText = "";
            try {
                while (true) {
                    // Leitura da mensagem do usuário
                    messageText = JOptionPane.showInputDialog("Mensagem (tipo:mensagem) :");

                    // Verifica se a mensagem não está vazia
                    if (messageText.isEmpty()) {
                        System.out.println("Mensagem inválida!");
                        continue;
                    }

                    String[] infos = messageText.split(":");
                    // Verifica se há mais de uma informação no array infos
                    if (infos.length < 2) {
                        System.out.println("Mensagem inválida! Falta de argumentos");
                        continue;
                    }

                    // Construção da mensagem
                    byte messageType = Byte.parseByte(infos[0]);

                    if (messageType < 0 || messageType > 4) {
                        System.err.println("Tipo de mensagem inválido! Deve estar no intervalo de 0 a 4");
                        continue;
                    }


                    if (messageType == 0x02) {
                        messageText = getEmoji(infos[1]);

                    } else {
                        messageText = infos[1];
                    }
                    byte[] messageBytes = messageText.getBytes();
                    byte messageSize = (byte) messageBytes.length;

                    // Montagem do pacote de dados
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(messageType);
                    byteArrayOutputStream.write(nickSize);
                    byteArrayOutputStream.write(nickBytes);
                    byteArrayOutputStream.write(messageSize);
                    byteArrayOutputStream.write(messageBytes);

                    byte[] sendData = byteArrayOutputStream.toByteArray();

                    // Envio do pacote de dados
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationIp,
                            destinationPort);

                    socket.send(sendPacket);

                    // Imprimir a mensagem enviada
                    System.out.println("Você: " + messageText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Thread para receber mensagens
        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    // Recebimento de mensagens
                    byte[] receiveData = new byte[322];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiverSocket.receive(receivePacket);

                    // Processamento da mensagem recebida
                    byte[] receivedData = receivePacket.getData();
                    byte receivedMessageType = receivedData[0];
                    byte receivedNickSize = receivedData[1];
                    String receivedNick = new String(receivedData, 2, receivedNickSize);
                    byte receivedMessageSize = receivedData[2 + receivedNickSize];
                    String receivedMessage = new String(receivedData, 3 + receivedNickSize, receivedMessageSize);

                    if (receivedMessageType == 0x04) {

                        // Montagem do pacote de dados
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byteArrayOutputStream.write(1);
                        byteArrayOutputStream.write(nickSize);
                        byteArrayOutputStream.write(nickBytes);
                        byteArrayOutputStream.write(receivedMessageSize);
                        byteArrayOutputStream.write(receivedMessage.getBytes());

                        byte[] responseMessage = byteArrayOutputStream.toByteArray();

                        // Exemplo de envio da resposta (precisa ser ajustado ao seu código):
                        DatagramPacket sendPacketResposta = new DatagramPacket(responseMessage,
                                responseMessage.length, destinationIp, destinationPort);

                        socket.send(sendPacketResposta);

                    } else {
                        System.out.println(receivedNick + ": " + receivedMessage);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        // Iniciar as threads
        senderThread.start();
        receiverThread.start();
    }

    public static String getEmoji(String messageContent) {
        String response = "";
        switch (messageContent) {
            case "1":
                response = ":-)";
                break;
            case "2":
                response = ":-(";
                break;
            case "3":
                response = ":-P";
                break;
            case "4":
                response = ":-/";
                break;
        }

        return response;
    }
}