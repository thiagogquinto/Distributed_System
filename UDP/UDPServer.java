package UDP;

/**
 * UDPServer: Servidor UDP
 * Descricao: Recebe um datagrama de um cliente, imprime o conteudo e retorna o mesmo
 * datagrama ao cliente
 */

import java.net.*;
import java.io.*;
import java.security.MessageDigest;
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

                // buffer = new byte[1024];
                // FileWriter writer = new FileWriter(destFile, true);
                // BufferedWriter buf = new BufferedWriter(writer);
                // for (int packetNumber = 1; packetNumber <= totalPackets; packetNumber++) {
                //     DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                //     dgramSocket.receive(packet);
                    
                //     String data = new String(packet.getData(), 0, packet.getLength());
                //     buf.write(data);
                // }
                // buf.flush();    
                // buf.close();

                for (int packetNumber = 1; packetNumber <= totalPackets; packetNumber++) {
                    System.out.println("Recebendo pacote " + packetNumber + " de " + totalPackets);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    dgramSocket.receive(packet);
                    fos.write(packet.getData(), 0, packet.getLength());
                }

                fos.close();

                DatagramPacket checksum = new DatagramPacket(buffer, buffer.length);
                dgramSocket.receive(checksum);
                byte [] checksumBytesRec = checksum.getData();

                byte[] checksumBytes = null;
                byte [] fileBytes = Files.readAllBytes(destFile.toPath());

                try {
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    checksumBytes = sha1.digest(fileBytes);
                } catch (Exception e) {
                    System.out.println("Erro ao calcular o checksum");
                }

                if(checksumBytesRec.equals(checksumBytes)){
                    System.out.println("Checksum OK");
                }else{
                    System.out.println("Checksum NOK");
                }


                System.out.println("Arquivo recebido com sucesso");
                System.out.println("Arquivo salvo em: " + destFile.getAbsolutePath());


           
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
