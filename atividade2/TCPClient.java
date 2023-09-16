package atividade2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

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
            ByteBuffer response = null;
            String buffer = "";

            while (true) {
                System.out.print("$ ");
                buffer = reader.nextLine(); // lê mensagem via teclado

                String[] infos = buffer.split(" ");

                if (infos[0].equals("ADDFILE") && infos.length == 2) {
                    sendRequestHeader(out, (byte) 1, infos[1]);

                } else if (infos[0].equals("DELETE") && infos.length == 2) {
                    sendRequestHeader(out, (byte) 2, infos[1]);

                } else if (infos[0].equals("GETFILESLIST") && infos.length == 1) {
                    sendRequestHeader(out, (byte) 3, "");

                } else if (infos[0].equals("GETFILE") && infos.length == 2) {
                    sendRequestHeader(out, (byte) 4, infos[1]);

                } else {
                    System.out.println("Comando inválido");
                }

                // byte[] bytes = new byte[258]; // Inicializa o array de bytes

                // int bytesRead = in.read(bytes); // aguarda resposta do servidor

                // response = ByteBuffer.wrap(bytes, 0, bytesRead); // Use apenas os bytes lidos
                // response.order(ByteOrder.BIG_ENDIAN);
                // byte responseMessageType = response.get(0);
                // byte responseCommandId = response.get(1);
                // byte responseStatusCode = response.get(2);

                // printResponse(responseCommandId, responseStatusCode);

                buffer = in.readUTF(); // aguarda resposta do servidor
                System.out.println(buffer);

                // Não é necessário imprimir a mensagem original do cliente
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
    } // main // main

    private static void printResponse(byte responseCommandId, byte responseStatusCode) {

        switch (responseCommandId) {
            case 1:
                if (responseStatusCode == 1) {
                    System.out.println("Arquivo adicionado com sucesso");
                } else {
                    System.out.println("Erro ao adicionar arquivo");
                }
                break;
            case 2:
                if (responseStatusCode == 1) {
                    System.out.println("Arquivo removido com sucesso");
                } else {
                    System.out.println("Erro ao remover arquivo");
                }
                break;
            case 3:
                System.out.println("Lista de arquivos");
                break;
            case 4:
                System.out.println("Arquivo recebido");
                break;
            default:
                System.out.println("Comando inválido " + responseCommandId);
                break;
        }
    }

    /**
     * Envia uma requisição para o servidor com o comando e o nome do arquivo a ser manipulado 
     * @param out DataOutputStream do socket do cliente para enviar a requisição
     * @param commandId código do comando
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro de I/O
     */
    private static void sendRequestHeader(DataOutputStream out, byte commandId, String filename) throws IOException {
        byte[] filenameBytes = filename.getBytes();
        byte filenameSize = (byte) filenameBytes.length;
        out.write((byte) 1);
        out.write(commandId);
        out.write(filenameSize);
        out.write(filenameBytes);
        out.flush();
    }

    // private static void sendRequest(DataOutputStream out, byte command, String filename) throws IOException {
    //     byte[] filenameBytes = filename.getBytes();
    //     ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filenameBytes);
    //     out.write(header.array());
    // }

} // class
