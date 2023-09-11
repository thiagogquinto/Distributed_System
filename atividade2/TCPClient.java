package atividade2;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.*;
import java.util.Scanner;
public class TCPClient {
    public static void main(String args[]) {
        Socket clientSocket = null; // socket do cliente
        Scanner reader = new Scanner(System.in); // ler mensagens via teclado

        try {
            /* Endereço e porta do servidor */
            int serverPort = 6666;
            InetAddress serverAddr = InetAddress.getByName("127.0.0.1");

            /* conecta com o servidor */
            clientSocket = new Socket(serverAddr, serverPort);

            /* cria objetos de leitura e escrita */
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            /* protocolo de comunicação */
            
            String buffer = "";
            while (true) {
                System.out.print("$ ");
                buffer = reader.nextLine(); // lê mensagem via teclad
                String[] infos = buffer.split(" ");
                
                if(infos[0].equals("ADDFILE") && infos.length == 2){
                    File file = new File(infos[1]);
                    
                    if(file.exists()){
                        byte[] fileBytes = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(fileBytes);
                        fis.close();
                        ByteBuffer header = generateReqHeader((byte) 1, (byte) infos[1].length(), infos[1]);
                        buffer = in.readUTF(); // aguarda resposta do servidor
                        System.out.println(buffer); // imprime resposta do servidor
                    }
                } else if(infos[0].equals("GETFILE") && infos.length == 2){
                   
                } else if(infos[0].equals("DELFILE") && infos.length == 2){
                    
                    System.out.println(buffer); // imprime resposta do servidor
                } else if(infos[0].equals("LISTFILES") && infos.length == 1){

                }else {
                    System.out.println("Comando inválido");
                }
            
                out.writeUTF(buffer); // envia a mensagem para o servidor
                buffer = in.readUTF(); // aguarda resposta do servidor
                System.out.println(buffer); // imprime resposta do servidor
            }
        } catch (
        UnknownHostException ue) {
            System.out.println("Socket:" + ue.getMessage());
        } catch (EOFException eofe) {
            System.out.println("EOF:" + eofe.getMessage());
        } catch (IOException ioe) {
            System.out.println("IO:" + ioe.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ioe) {
                System.out.println("IO: " + ioe);
                ;
            }
        }

    } // main

    private static ByteBuffer generateReqHeader(byte commandId, byte filenameSize, String filename){
        ByteBuffer header = ByteBuffer.allocate(258);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 1);
        header.put(commandId);
        header.put(filenameSize);
        header.put(filename.getBytes(StandardCharsets.UTF_8));
        return header;
    }

} // class
