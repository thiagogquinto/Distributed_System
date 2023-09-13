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
                buffer = reader.nextLine(); // lê mensagem via teclado
                String[] infos = buffer.split(" ");

                if (infos[0].equals("ADDFILE") && infos.length == 2) {
                    sendRequest(out, (byte) 1, infos[1]);
                } else if (infos[0].equals("DELETE") && infos.length == 2) {
                    sendRequest(out, (byte) 2, infos[1]);
                } else if (infos[0].equals("GETFILESLIST") && infos.length == 1) {
                    sendRequest(out, (byte) 3, "");
                } else if (infos[0].equals("GETFILE") && infos.length == 2) {
                    sendRequest(out, (byte) 4, infos[1]);
                } else {
                    System.out.println("Comando inválido");
                }

                out.writeUTF(buffer); // envia a mensagem para o servidor
                buffer = in.readUTF(); // aguarda resposta do servidor
                System.out.println(buffer); // imprime resposta do servidor
            }
        } catch (UnknownHostException ue) {
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
                
            }
        }

    } // main

    private static ByteBuffer generateReqHeader(byte commandId, byte filenameSize, byte[] filename) {
        ByteBuffer header = ByteBuffer.allocate(258);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 1);
        header.put(commandId);
        header.put(filenameSize);
        header.put(filename);
        return header;
    }

    private static void sendRequest(DataOutputStream out, byte command, String filename) throws IOException {

        byte[] filenameBytes = filename.getBytes();
        ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filenameBytes);
        out.write(header.array());
    }

} // class
