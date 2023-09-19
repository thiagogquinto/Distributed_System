package atividade2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
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
            String filenameDown = "";
            while (true) {

                System.out.print("$ ");
                buffer = reader.nextLine(); // lê mensagem via teclado

                String[] infos = buffer.split(" ");

                /* verifica qual o comando digitado pelo usuário para tratar adequadamente */
                if (infos[0].equals("ADDFILE") && infos.length == 2) {
                    sendAddFileRequest(out, (byte) 1, infos[1]); // envia requisição para adicionar arquivo no servidor

                } else if (infos[0].equals("DELETE") && infos.length == 2) {
                    sendCommonRequests(out, (byte) 2, infos[1]);

                } else if (infos[0].equals("GETFILESLIST") && infos.length == 1) {
                    sendCommonRequests(out, (byte) 3, "");

                } else if (infos[0].equals("GETFILE") && infos.length == 2) {
                    filenameDown = infos[1];
                    sendCommonRequests(out, (byte) 4, infos[1]);
                    int size = 258;

                    File src = new File(System.getProperty("user.dir") + "/files/" + filenameDown);
                    if(src.exists()){
                        size += src.length();
                    }
                    

                    byte [] headerBytes = new byte[size];
                    in.read(headerBytes);
                    ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
                    headerBuffer.order(ByteOrder.BIG_ENDIAN);
                    byte messageType = headerBuffer.get();
                    byte commandId = headerBuffer.get();
                    byte statusCode = headerBuffer.get();
                    Integer fileSize = headerBuffer.getInt();
                  
                    if(fileSize > 0 && statusCode == 1){
                         byte [] bytes = new byte[1];
                        byte[] contentByte = new byte[fileSize];
                        for (int i = 0; i < fileSize; i++) {
                            in.read(bytes);
                            byte b = bytes[0];
                            contentByte[i] = b;
                        }

                        String content = new String(contentByte);
                        
                        File downloadDir = new File(System.getProperty("user.dir") + "/Downloads");

                        if(!downloadDir.exists()){
                            downloadDir.mkdir();
                        }

                        File file = new File(System.getProperty("user.dir") + "/Downloads/" + filenameDown);
                        if (file.createNewFile()) {
                            FileWriter writer = new FileWriter(file, true);
                            BufferedWriter buf = new BufferedWriter(writer);
                            buf.write(content);
                            buf.flush();
                            buf.close();
                        }
                        System.out.println("Arquivo solictado baixado com sucesso no diretório Downloads");
                    } else{
                        System.out.println("Erro ao baixar arquivo de solicitado ");
                    }
                   
                    continue;
                    
                } else {
                    System.out.println("Comando inválido");
                    continue;
                }


                byte[] headerBytes = new byte[258];
                in.read(headerBytes);
                ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
                headerBuffer.order(ByteOrder.BIG_ENDIAN);
                byte messageType = headerBuffer.get();
                byte commandId = headerBuffer.get();

                /* verifica qual o tipo de comando realizado pelo servidor para tratar o cabeçalho corretamente */
                if(messageType == 0x02){
                    switch(commandId){
                        case 0x01:
                            handleDeleteAndAddFileResponse(headerBuffer, commandId);
                            break;
                        case 0x02:
                            handleDeleteAndAddFileResponse(headerBuffer, commandId);
                            break;
                        case 0x03:
                            handleGetFilesListResponse(headerBuffer);
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
     * Gera um cabeçalho de requisição com os campos de , tipo do cabeçalho, ID do comando, tamanho do nome do arquivo, nome do arquivo.
     * 
     * @param commandId identificador do comando
     * @param filenameSize tamanho do nome do arquivo
     * @param filename nome do arquivo
     * @param fileSize tamanho do arquivo
     * @param fileBytes bytes do arquivo
     * @return ByteBuffer com o cabeçalho da requisição
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

    /**
     * Função que chama a função generateReqHeader para gerar o cabeçalho da requisição e envia a requisição para o servidor.
     * 
     * @param out DataOutputStream do socket do cliente para enviar a requisição
     * @param command código do comando
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro ao enviar a requisição
     */
    private static void sendCommonRequests(DataOutputStream out, byte command, String filename) throws IOException {
        ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filename.getBytes());
        int size = header.limit(); // tamanho do array de bytes
        int headerSize = header.position(); // tamanho do cabeçalho
        byte[] headerBytes = header.array();
        out.write(headerBytes, 0, headerSize);
        out.flush();
    }

    /**
     * Função que lida com o envio de requisição para adicionar arquivos no servidor. Este cabeçalho possui os * seguintes campos: tipo do cabeçalho, commandId, filenameSize, filename, fileSize e fileBytes.
     * 
     * @param out DataOutputStream do socket do cliente para enviar a requisição
     * @param command código do comando
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro ao enviar a requisição
     */
    private static void sendAddFileRequest(DataOutputStream out,  byte command, String filename) throws IOException{
        byte filenameSize = (byte) filename.length();
        byte [] filenameBytes = filename.getBytes();
        File srcFile = new File(System.getProperty("user.dir") + "/" + filename);

        long fileSize;
        
        if (!srcFile.exists()) {
            fileSize = 0;
        }
        else{
            fileSize = srcFile.length();
        }
        int fileSizeInt = (int) fileSize;

        ByteBuffer header = ByteBuffer.allocate(262);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 1);
        header.put(command);
        header.put(filenameSize);
        header.put(filenameBytes);
        header.putInt(fileSizeInt);
        int size = header.limit(); // tamanho do array de bytes
        int headerSize = header.position(); // tamanho do cabeçalho
        byte[] headerBytes = header.array();
        
        if(!srcFile.exists()){
            System.out.println("Arquivo não encontrado: " + srcFile);
            out.write(headerBytes, 0, headerSize);
        } else{
           try (FileInputStream fis = new FileInputStream(srcFile)) {
            out.write(headerBytes, 0, headerSize); // Envie o cabeçalho

            if (fileSize > 0) {
                int c;
                while ((c = fis.read()) != -1) {
                    out.write(c); // Envie os bytes do arquivo um por um
                }
            }
        } 
        }

        out.flush();
    }

    /**
     * Função que trata a resposta do servidor para os comandos de adicionar e deletar arquivos. Possui os seguintes campos: tipo do cabeçalho, commandId e statusCode.
     * 
     * @param response ByteBuffer com o restante da resposta do servidor
     * @param commandId código do comando
     * @throws IOException caso ocorra algum erro ao ler a resposta do servidor
     */
    private static void handleDeleteAndAddFileResponse(ByteBuffer response, byte commandId) throws IOException{
        byte statusCode = response.get();
        
        if(commandId == 0x02){
            if (statusCode == 0x01) {
                System.out.println("Status: " + statusCode + " - Arquivo deletado com sucesso");
            } else {
                System.out.println("Status: " + statusCode + " - Erro ao deletar o arquivo");
            }
        } else if(commandId == 0x01){
            if(statusCode == 0x01){
                System.out.println("Status: " + statusCode + " - Arquivo adicionado com sucesso no servidor");
            } else{
                System.out.println("Status: " + statusCode + " - Erro ao adicionar o arquivo no servidor");
            }
        }
    }    

    /**
     * Função que trata a resposta do servidor para o comando de listar arquivos e trata a resposta do servidor para o comando de listar arquivos.
     * @param response ByteBuffer com o restante da resposta do servidor
     */
    private static void handleGetFilesListResponse(ByteBuffer response){
        byte statusCode = response.get();
        Short filesCount = response.getShort();

        System.out.println(filesCount + " arquivos encontrados: ");
        
        if(statusCode == 0x01){
            /* itera lendo cada nome de arquivo retornado no cabeçalho */
            for (int i = 0; i < filesCount; i++) {
                byte filenameSize = response.get();
                byte[] filenameBytes = new byte[filenameSize];
                response.get(filenameBytes);
                String filename = new String(filenameBytes);
                System.out.println(filename);
            }
        } else{
            System.out.println("Erro ao listar arquivos");
        }
    }

} // class
