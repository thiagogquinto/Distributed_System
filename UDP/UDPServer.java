package UDP;

/**
 * UDPServer: Servidor UDP
 * Descricao: Recebe um datagrama de um cliente, imprime o conteudo e retorna o mesmo
 * datagrama ao cliente
 */

import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.nio.file.Files;


public class UDPServer{

    private static int totalPackets;
    private static String fileName;
    private static String serverPath = System.getProperty("user.dir") + "/server/";

    public static void main(String args[]){ 
    	DatagramSocket dgramSocket = null;
        try{
            dgramSocket = new DatagramSocket(6666); // cria um socket datagrama em uma porta especifica
            System.out.println("Servidor iniciado");
            
            while(true){
                byte[] buffer = new byte[1024]; // cria um buffer para receber requisições
                /* cria um pacote vazio */
                DatagramPacket dgramPacket = new DatagramPacket(buffer, buffer.length);
                dgramSocket.receive(dgramPacket);  // aguarda a chegada de datagramas

                /* imprime e envia o datagrama de volta ao cliente */
                // System.out.println("Cliente: " + new String(dgramPacket.getData(), 0, dgramPacket.getLength())); 
                if (dgramPacket.getLength() > 0) {
                    String fileInfo = new String(dgramPacket.getData(), 0, dgramPacket.getLength());
                    String[] fileInfoParts = fileInfo.split(":");
                    if (fileInfoParts.length == 2) {
                        fileName = fileInfoParts[0];
                        totalPackets = Integer.parseInt(fileInfoParts[1]); // armazena o número total de pacotes que serão enviados para o servidor (tamanho do arquivo / 1024)
                        System.out.println("Nome do arquivo: " + fileName);
                        System.out.println("Número total de pacotes: " + totalPackets);
                    } else {
                        System.out.println("Erro ao receber informações do arquivo");
                    }
                }

                
                File serverDir = new File(serverPath);
                
                if (!serverDir.exists()) {
                    serverDir.mkdir();
                }
                
                File destFile = new File(System.getProperty("user.dir") + "/server/" + fileName); // cria um objeto do tipo arquivo
                
                if (!destFile.exists()) {
                    destFile.createNewFile();
                }
                
                FileOutputStream fos = new FileOutputStream(destFile, true);

                for (int packetNumber = 1; packetNumber <= totalPackets; packetNumber++) {
                    System.out.println("Recebendo pacote " + packetNumber + " de " + totalPackets);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    dgramSocket.receive(packet);
                    fos.write(packet.getData(), 0, packet.getLength());
                }
                
                fos.close();

                
                // byte[] checksumBytes = null;

                int checksumCheck = 0;
                try{
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    byte [] fileBytes = Files.readAllBytes(destFile.toPath());
                    int checksumSize = 20;
                    byte [] receivedChecksumBytes = new byte[checksumSize];
                    DatagramPacket checksumPacket = new DatagramPacket(receivedChecksumBytes, receivedChecksumBytes.length);
                    dgramSocket.receive(checksumPacket);
                    byte[] calculatedChecksumBytes = sha1.digest(fileBytes);
                    
                    if(Arrays.equals(receivedChecksumBytes, calculatedChecksumBytes)){
                        System.out.println("Arquivo recebido com sucesso");
                        System.out.println("Arquivo salvo em: " + destFile.getAbsolutePath());
                        System.out.println("Checksum OK");
                        checksumCheck = 1;
                    }else{
                        checksumCheck = 0;
                        System.out.println("Checksum NOK");
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao calcular o checksum");
                }
               
                if (checksumCheck == 1) {
                    String respStr = "Arquivo recebido com sucesso";
                    DatagramPacket respPacket = new DatagramPacket(respStr.getBytes(), respStr.getBytes().length, dgramPacket.getAddress(), dgramPacket.getPort());
                    dgramSocket.send(respPacket);
                } else {
                    String respStr = "Erro ao receber o arquivo";
                    DatagramPacket respPacket = new DatagramPacket(respStr.getBytes(), respStr.getBytes().length, dgramPacket.getAddress(), dgramPacket.getPort());
                    dgramSocket.send(respPacket);
                }

           
            } //while
        }catch (SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            dgramSocket.close();
        } //finally
    } //main
}//class
