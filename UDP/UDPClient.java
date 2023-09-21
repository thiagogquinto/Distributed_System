package UDP;

/**
 * UDPClient: Cliente UDP Descricao: Envia uma msg em um datagrama e recebe a
 * mesma msg do servidor
 */
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;



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
                
                /* divide o arquivo e envia os bytes */
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
                
                // byte[] checksumBytes = null;

                try {
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    byte[] checksumBytes = sha1.digest(fileBytes);
                    DatagramPacket checksum = new DatagramPacket(checksumBytes, checksumBytes.length, serverAddr, serverPort);
                    dgramSocket.send(checksum);
                    System.out.println("Checksum enviado");
                } catch (Exception e) {
                    System.out.println("Erro ao calcular o checksum");
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
