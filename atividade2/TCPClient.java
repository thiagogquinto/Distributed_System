package atividade2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

/**
 * Descrição: Cliente TCP simples que se conecta com o servidor e envia comandos para manipular arquivos no servidor
 * como adicionar, deletar, listar e baixar arquivos. 
 * A comunicação entre o cliente e o servidor é feita através de um protocolo de comunicação simples, onde o cliente
 * envia um cabeçalho com o tipo de mensagem e o comando a ser executado, e o servidor responde com um cabeçalho com o
 * tipo de mensagem e o status da operação, seguido dos dados da resposta.
 * 
 * Autor: Thiago Gariani Quinto, Marcos Vinicius de Quadros
 */


public class TCPClient {
    
    public static void main(String args[]) {
        Socket clientSocket = null;               // socket do cliente
        Scanner reader = new Scanner(System.in);  // ler mensagens via teclado

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


                byte[] headerBytes = new byte[258];
                in.read(headerBytes);
                ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
                headerBuffer.order(ByteOrder.BIG_ENDIAN);
                byte messageType = headerBuffer.get();
                byte commandId = headerBuffer.get();
               
                Logger logger = TCPServer.getLogger();

                /* verifica qual o tipo de comando realizado para tratar o cabeçalho corretamente */
                if(messageType == 0x02){
                    switch(commandId){
                        case 0x01:
                            handleAddFileResponse(headerBuffer, logger);
                            break;
                        case 0x02:
                            handleDeleteResponse(headerBuffer, logger);
                            break;
                        case 0x03:
                            handleGetFilesListResponse(headerBuffer, logger);
                            break;
                        case 0x04:
                            handleGetFileResponse(headerBuffer, logger);
                            break;
                    }
                }
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

    /**
     * Envia uma requisição para o servidor com o comando e o nome do arquivo a ser manipulado 
     * @param out DataOutputStream do socket do cliente para enviar a requisição
     * @param commandId código do comando
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro de I/O
     */
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

    private static void handleAddFileResponse(ByteBuffer response, Logger logger) throws IOException{
        
        byte statusCode = response.get();
        Integer fileSize = response.getInt();
        // int fileSizeInt = fileSize.intValue();
        byte[] fileBytes = new byte[fileSize];
        response.get(fileBytes);
        String fileContent = new String(fileBytes);

        if(statusCode == 1){
            // logger.info("Status: " + statusCode + " File size: " + fileSize + " File content: " + fileContent);
            logger.info("Arquivo de " + fileSize + " bytes inserido com sucesso");
        } else{
            logger.info("Erro ao inserir arquivo de " + fileSize + " bytes");
        }
    }
    
    private static void handleDeleteResponse(ByteBuffer response, Logger logger) throws IOException{
        byte statusCode = response.get();

        if (statusCode == 0x01) {
            logger.info("Status: " + statusCode + " - Arquivo deletado com sucesso");
        } else {
            logger.info("Status: " + statusCode + " - Não foi possível deletar o arquivo");
        }
    }    

    private static void handleGetFilesListResponse(ByteBuffer response, Logger logger){
        byte statusCode = response.get();
        Short filesCount = response.getShort();

        for (int i = 0; i < filesCount; i++) {
            byte filenameSize = response.get();
            byte[] filenameBytes = new byte[filenameSize];
            response.get(filenameBytes);
            String filename = new String(filenameBytes);
            logger.info("Status: " + statusCode + " - " + filename);
        }
    }

    private static void handleGetFileResponse(ByteBuffer response, Logger logger){
        byte statusCode = response.get();
        Integer fileSize = response.getInt();
        // int fileSizeInt = fileSize.intValue();
        byte[] fileBytes = new byte[fileSize];
        response.get(fileBytes);
        String fileContent = new String(fileBytes);

        if(statusCode == 1){
            logger.info("Arquivo de " + fileSize + " baixado com sucesso!");
        }
    }
} // class
