package atividade2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.file.Files;

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

                /* verifica qual o comando digitado pelo usuário para tratar adequadamente */
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
                        case 0x04:
                            handleGetFileResponse(headerBuffer);
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
     * Gera um cabeçalho de requisição com os campos de ID do comando, tamanho do nome do arquivo, nome do arquivo.
     * Caso o comando seja de adicionar arquivo, também é adicionado o tamanho do arquivo e os bytes do arquivo.
     * 
     * @param commandId identificador do comando
     * @param filenameSize tamanho do nome do arquivo
     * @param filename nome do arquivo
     * @param fileSize tamanho do arquivo
     * @param fileBytes bytes do arquivo
     * @return ByteBuffer com o cabeçalho da requisição
     */
    private static ByteBuffer generateReqHeader(byte commandId, byte filenameSize, byte[] filename, int fileSize, byte[] fileBytes) {
        ByteBuffer header = ByteBuffer.allocate(258 + fileSize);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 1);
        header.put(commandId);
        header.put(filenameSize);
        header.put(filename);
        // return header;

        System.out.println("fileSize: " + fileSize);
        if(commandId == 0x01){
            header.putInt(fileSize);
            header.put(fileBytes);
        }

        System.out.println("header: " + header);

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
    private static void sendRequest(DataOutputStream out, byte command, String filename) throws IOException {

        if(command == 0x01){
            File file = new File(System.getProperty("user.dir") + "/" + filename);

            if(!file.exists()){
                byte[] fileBytes = new byte[0];
                ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filename.getBytes(), 0, fileBytes);
                out.write(header.array());
                return;
            }

            long fileSize = file.length();
            int fileSizeInt = (int) fileSize;
            byte[] fileBytes = new byte[fileSizeInt];
            fileBytes = Files.readAllBytes(file.toPath());
            ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filename.getBytes(), fileSizeInt, fileBytes);
            out.write(header.array());
            return;
        }
        byte[] filenameBytes = filename.getBytes();
        ByteBuffer header = generateReqHeader(command, (byte) filename.length(), filenameBytes, 0, null);
        out.write(header.array());
    }

    /**
     * Função que trata a resposta do servidor para os comandos de adicionar e deletar arquivos.
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
                System.out.println("Status: " + statusCode + " - Não foi possível deletar o arquivo");
            }
        } else if(commandId == 0x01){
            if(statusCode == 0x01){
                System.out.println("Status: " + statusCode + " - Arquivo adicionado com sucesso no servidor");
            } else{
                System.out.println("Status: " + statusCode + " - Não foi possível adicionar o arquivo no servidor");
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

    /**
     * Função que trata a resposta do servidor para o comando de baixar arquivos.
     * @param response ByteBuffer com o restante da resposta do servidor
     */
    private static void handleGetFileResponse(ByteBuffer response){
        byte statusCode = response.get();
        Integer fileSize = response.getInt();
        // int fileSizeInt = fileSize.intValue();
        byte[] fileBytes = new byte[fileSize];
        response.get(fileBytes);
        String fileContent = new String(fileBytes);

        if(statusCode == 1){
            System.out.println("Arquivo de " + fileSize + " bytes baixado com sucesso" );
        } else{
           System.out.println("Erro ao baixar arquivo de " + fileSize + " bytes");
        }
    }
} // class
