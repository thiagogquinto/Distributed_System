package UDP;

/**
 * UDPClient: Cliente UDP Descricao: Envia uma msg em um datagrama e recebe a
 * mesma msg do servidor
 */
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.util.Arrays;


/*
 Fazer um sistema de upload de arquivos via UDP. Um servidor UDP deverá receber as partes dos arquivos
(1024 bytes), verificar ao final a integridade via um checksum (SHA-1) e armazenar o arquivo em uma pasta padrão.
Sugestões: o servidor pode receber o nome e tamanho do arquivo como o primeiro pacote e o checksum como o último.
Testar o servidor com arquivos textos e binários (ex: imagens, pdf) de tamanhos arbitrários (ex: 100 bytes, 4KiB,
4MiB). O protocolo para a comunicação deve ser criado e especificado textualmente ou graficamente
 */

public class UDPClient {

    public static void main(String args[]) {
        DatagramSocket dgramSocket;
        int resp = 0;

        try {
            dgramSocket = new DatagramSocket(); //cria um socket datagrama
            
            String dstIP = "127.0.0.1"; // IP do servidor
            int dstPort = 6666; // porta do servidor
            
            /* armazena o IP do destino */
            InetAddress serverAddr = InetAddress.getByName(dstIP);
            int serverPort = dstPort; // porta do servidor

            do {
                // String msg = JOptionPane.showInputDialog("Mensagem?");

                System.out.println("Digite o nome do arquivo (ex: arquivo.txt): ");

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // cria um buffer para ler a entrada do teclado
                String msg = in.readLine(); // lê a entrada do teclado

                File file = new File(System.getProperty("user.dir") + "/" + msg); // cria um objeto do tipo arquivo

                long fileSize = file.length(); // armazena o tamanho do arquivo

                int totalPackets = (int) Math.ceil((double) fileSize / 1024); // calcula o número total de pacotes que serão enviados para o servidor (tamanho do arquivo / 1024)
                
                String essentialsInfos = file.getName() + ":" + totalPackets; // cria uma string com o nome e o tamanho do arquivo
                byte[] infoBytes = essentialsInfos.getBytes(); // transforma a string em bytes
                
                /* cria um pacote datagrama */
                DatagramPacket infos = new DatagramPacket(infoBytes, infoBytes.length, serverAddr, serverPort); // cria um pacote com os dados essenciais do arquivo (nome, tamanho)
                dgramSocket.send(infos); // envia o pacote
                
                byte [] fileBytes = Files.readAllBytes(file.toPath());
                int totalBytes = fileBytes.length;
                int bytesSent = 0;
                int packetNumber = 0;

                while (bytesSent < totalBytes) {
                    int remainingBytes = totalBytes - bytesSent;
                    int packetSize = Math.min(remainingBytes, 1024); // Tamanho do pacote, no máximo 1024 bytes

                    byte[] packetData = Arrays.copyOfRange(fileBytes, bytesSent, bytesSent + packetSize);

                    DatagramPacket packet = new DatagramPacket(packetData, packetSize, serverAddr, serverPort);
                    dgramSocket.send(packet);

                    bytesSent += packetSize;
                    packetNumber++;

                    System.out.println("Pacote " + packetNumber + " de " + (totalBytes / 1024 + 1) + " enviado");
                }

                /* cria um buffer vazio para receber datagramas */
                 byte[] bufferRes = new byte[1024];
                 DatagramPacket reply = new DatagramPacket(bufferRes, bufferRes.length);

                 /* aguarda datagramas */
                 dgramSocket.receive(reply);
                 System.out.println("Resposta: " + new String(reply.getData(),0,reply.getLength()));

                 System.out.println("Deseja enviar uma nova mensagem? (s/n))");
                 
                 String respStr = in.readLine();

                 if (respStr.equals("n")) {
                     resp = JOptionPane.NO_OPTION;
                 } else {
                     resp = JOptionPane.YES_OPTION;
                 }
                 
            } while (resp != JOptionPane.NO_OPTION);

            /* libera o socket */
            dgramSocket.close();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } //catch
    } //main		      	
} //class
