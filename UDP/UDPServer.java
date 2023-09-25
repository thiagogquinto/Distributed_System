package UDP;

/**
 * UDPServer: Servidor UDP
 * Descricao: Recebe um datagrama de um cliente contendo um arquivo e envia um 
 * datagrama ao cliente informando se o arquivo foi recebido com sucesso ou não
 * 
 * Autores: Thiago Gariani Quinto e Marcos Vinicius de Quadros
 * 
 * Data de criação: 20/09/2023
 * Datas de modificação: 21/09/2023, 22/09/2023, 24/09/2023
 */

import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;


public class UDPServer{

    private static int fileSize;
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

                // recebe as informações do arquivo que será enviado pelo cliente (nome e tamanho)
                if (dgramPacket.getLength() > 0) {
                    String fileInfo = new String(dgramPacket.getData(), 0, dgramPacket.getLength());
                    String[] fileInfoParts = fileInfo.split(":");
                    if (fileInfoParts.length == 2) {
                        fileName = fileInfoParts[0]; // nome do arquivo
                        fileSize = Integer.parseInt(fileInfoParts[1]); // tamanho do arquivo
                        System.out.println("Nome do arquivo: " + fileName);
                        System.out.println("Tamanho do arquivo: " + fileSize);
                    } else {
                        System.out.println("Erro ao receber informações do arquivo");
                    }
                }

                
                File serverDir = new File(serverPath); // cria um objeto do tipo arquivo para o diretorio do servidor
                
                /* verifica se o diretorio do servidor existe, caso não exista, cria o diretorio */
                if (!serverDir.exists()) {
                    serverDir.mkdir();
                }
                
                File destFile = new File(System.getProperty("user.dir") + "/server/" + fileName); // cria um objeto do tipo arquivo para o arquivo que será recebido
                
                int totalPackets = (int) Math.ceil((double) fileSize / 1024); // calcula o total de pacotes que serão recebidos
                
                byte[] fileBytes = new byte[fileSize];
                
                /* recebe os pacotes do arquivo e armazena no vetor de bytes */
                for (int packetNumber = 1; packetNumber <= totalPackets; packetNumber++) {
                    System.out.println("Recebendo pacote " + packetNumber + " de " + totalPackets);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    dgramSocket.receive(packet);
                    System.arraycopy(packet.getData(), 0, fileBytes, (packetNumber - 1) * 1024, packet.getLength()); // copia o conteudo do pacote para o vetor de bytes 
                }
                
                int checksumCheck = 0;
                try{
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    int checksumSize = 20;
                    byte [] receivedChecksumBytes = new byte[checksumSize];
                    DatagramPacket checksumPacket = new DatagramPacket(receivedChecksumBytes, receivedChecksumBytes.length); // cria um pacote para receber o checksum
                    dgramSocket.receive(checksumPacket); // recebe o checksum
                    byte[] calculatedChecksumBytes = sha1.digest(fileBytes); // calcula o checksum do arquivo recebido
                    
                    if(Arrays.equals(receivedChecksumBytes, calculatedChecksumBytes)){ // verifica se os checksums são iguais
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

                // envia uma resposta ao cliente informando se o arquivo foi recebido com sucesso ou não 
                if (checksumCheck == 1) {
                    FileOutputStream fos = new FileOutputStream(destFile, true);
                    fos.write(fileBytes);
                    fos.close();
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
