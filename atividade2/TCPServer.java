package atividade2;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;
import java.nio.file.Files;
/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem do cliente e envia uma resposta
 * para o cliente de acordo com o comando recebido e o resultado da operação.
 * 
 * Autores: Thiago Gariani Quinto, Marcos Vinicius de Quadros
 * 
 * Data de criação: 11/09
 * 
 * Data de atualização: 12/09, 13/09, 14/09, 15/09, 16/09, 17/09, 18/09
 */

public class TCPServer {

    public static void main(String args[]) {

        try {
            int serverPort = 6666; // porta do servidor

            /* cria um socket e mapeia a porta para aguardar conexao */
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /* cria um arquivo de log para o servidor */

            FileHandler fh = new FileHandler("server.log"); // cria um arquivo de log
            Logger logger = Logger.getLogger("server.log"); // cria um logger
            logger.addHandler(fh); // adiciona o arquivo de log ao logger
            SimpleFormatter formatter = new SimpleFormatter(); // cria um formatador
            fh.setFormatter(formatter); // adiciona o formatador ao arquivo de log

            System.out.println("Servidor aguardando conexao ...");
            while (true) {

                /* aguarda conexoes */
                Socket clientSocket = listenSocket.accept();

                /* cliente conectado */
                logger.info("Cliente conectado ... Criando thread ...");
                /* cria um thread para atender a conexao */
                ClientThread c = new ClientThread(clientSocket);

                /* inicializa a thread */
                c.start();

            } // while

        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());

        } // catch
    } // main
} // class

/**
 * Classe ClientThread: Thread responsavel pela comunicacao
 * Descricao: Recebe um socket, cria os objetos de leitura e escrita,
 * 
 */

class ClientThread extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    String serverPath = System.getProperty("user.dir") + "/files/";
    String localPath = System.getProperty("user.dir");
    String downloadPath = System.getProperty("user.dir") + "/Downloads/";

    public ClientThread(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;

            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            File directory = new File(serverPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

        } catch (IOException ioe) {
            System.out.println("Connection:" + ioe.getMessage());
        } // catch
    } // construtor

    /* metodo executado ao iniciar a thread - start() */
    @Override
    public void run() {
        try {

            int size = 258;
            while (true) {

                String buffer = "";

                byte[] request = new byte[258];
                // ByteBuffer header = ByteBuffer.allocate(258);
                in.read(request);
                ByteBuffer header = ByteBuffer.wrap(request); // cria um buffer com o array de bytes recebido
                header.order(ByteOrder.BIG_ENDIAN); // define a ordem dos bytes (BIG_ENDIAN)
                byte messageType = header.get(); // tipo de mensagem (1 byte)
                byte commandId = header.get(); // código do comando (1 byte)
                byte filenameSize = header.get(); // tamanho do nome do arquivo (1 byte)
               
                byte[] filenameBytes = new byte[filenameSize]; // array de bytes para o nome do arquivo
                header.get(filenameBytes); // Lê o nome do arquivo (tamanho variável) em bytes
                String filename = new String(filenameBytes); // Converte o nome do arquivo para String
               
                Logger logger = Logger.getLogger("server.log"); // pegar o logger

                if (messageType == 1) { // verifica se é uma requisição

                    logger.info("Mensagem: " + messageType + " | Comando: " + commandId + " | Tamanho: " + filenameSize
                            + " | Arquivo: " + filename);


                    /* como o ADDFILE tem campos a mais ele é tratado aqui  */
                    if (commandId == 1) {
                        Integer fileSize = header.getInt();
                        System.out.println("Tamanho do arquivo: " + fileSize);
                        
                        if(fileSize.intValue() > 0){
                            byte [] bytes = new byte[1];
                            byte[] contentByte = new byte[fileSize];
                            
                            for (int i = 0; i < fileSize; i++) {
                                in.read(bytes);
                                byte b = bytes[0];
                                contentByte[i] = b;
                            }

                            int worked = 0;
                            String content = new String(contentByte);
                            File file = new File(System.getProperty("user.dir") + "/files/" + filename);
                            if (file.createNewFile()) {
                                FileWriter writer = new FileWriter(file, true);
                                BufferedWriter buf = new BufferedWriter(writer);
                                buf.write(content);
                                buf.flush();
                                buf.close();
                                worked = 1;
                            }

                            if(worked == 1){
                                logger.info("Arquivo " + filename + " copiado com sucesso\n");
                                logger.info("Enviando resposta para o cliente");
                                sendDeleteAndAddFileResponse(out, (byte) 1, (byte) 1);
                            } else {
                                logger.info("Erro ao copiar arquivo " + filename);
                                logger.info("Enviando resposta para o cliente");
                                sendDeleteAndAddFileResponse(out, (byte) 1, (byte) 0);
                            }
                        } else {
                            sendDeleteAndAddFileResponse(out, commandId, (byte) 0);
                        }

                    } else if (commandId == 2) {
                        handleDelete(out, filename);

                    } else if (commandId == 3) {
                        handleGetFilesList();

                    } else if (commandId == 4) {
                        handleGetFile(out, filename);
                    }

                }

            }
        } catch (EOFException eofe) {
            System.out.println("EOF: " + eofe.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOE: " + ioe.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException ioe) {
                System.err.println("IOE: " + ioe);
            }
        }
        System.out.println("Thread comunicação cliente finalizada.");
    } // run

 
    /**
     * Método para deletar um arquivo do servidor (/files)
     * @param out DataOutputStream do socket do cliente para enviar a resposta
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro de I/O
     */
    private void handleDelete(DataOutputStream out, String filename) throws IOException {

        Logger logger = Logger.getLogger("server.log"); // pegar o logger
        File file = new File(serverPath + "/" + filename);
        if (file.delete()) {
            logger.info("Arquivo " + filename + " deletado com sucesso\n");
            logger.info("Enviando resposta para o cliente");
            sendDeleteAndAddFileResponse(out, (byte) 2, (byte) 1);
        } else {
            logger.info("Erro ao deletar arquivo " + filename + "\n");
            logger.info("Enviando resposta para o cliente");
            sendDeleteAndAddFileResponse(out, (byte) 2, (byte) 0);
        }

    }

    /**
     * Método para listar os arquivos no servidor (/files)
     */
    private void handleGetFilesList() {
        // Listar os arquivos no diretório de destino (no servidor)
        File file = new File(serverPath);
        File[] files = file.listFiles();
        Logger logger = Logger.getLogger("server.log"); // pegar o logger

    
        List <String> filesInDir = new ArrayList<String>();
        if(file.exists()){
            for (File f : files) {
                if (f.isFile()) {
                    filesInDir.add(f.getName());
                }
            }
            logger.info("Arquivos listados com sucesso\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFilesListResponse(out, (byte) 3, (byte) 1, filesInDir);
        } else {
            logger.info("Erro ao listar arquivos\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFilesListResponse(out, (byte) 3, (byte) 0, filesInDir);
        }
    }

    /**
     * Método para baixar um arquivo do servidor (/files) e salvar no diretório de download do cliente
     * 
     * @param out DataOutputStream do socket do cliente para enviar a resposta
     * @param filename nome do arquivo
     * @throws IOException caso ocorra algum erro de I/O
     */
    private void handleGetFile(DataOutputStream out, String filename) throws IOException{
        Logger logger = Logger.getLogger("server.log");

        File srcFile = new File(serverPath + "/" + filename);

        if(!srcFile.exists()){
            logger.info("Arquivo " + filename + " não encontrado no servidor\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFileResponse(out, (byte) 4, (byte) 0, null); // Envie uma resposta de erro
            return;
        } else {
            logger.info("Arquivo " + filename + " encontrado no servidor\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFileResponse(out, (byte) 4, (byte) 1, srcFile); // Envie uma resposta de erro
        }

    }

    /**
     * Envia o cabeçalho de resposta para o cliente do comando de listar os arquivos do servidor, os cabeçalhos são 
     * compostos pelos campos de tipo de mensagem, o comando, o status, o número de arquivos, sendo que para cada 
     * arquivo é enviado o tamanho do nome do arquivo e o nome do arquivo em si.
     * 
     * @param out DataOutputStream do socket do cliente para enviar a resposta
     * @param command byte com o código do comando
     * @param status byte com o status da operação (0x00 para erro e 0x01 para sucesso)
     * @param files lista de arquivos no servidor
     */
    private void sendGetFilesListResponse(DataOutputStream out, byte command, byte status, List <String> files){

        short qtdeFiles = (short) files.size(); 

        int headerSize = 5;
        for (String f : files) {
            headerSize += 1 + f.length(); 
        }

        ByteBuffer header = ByteBuffer.allocate(headerSize);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 2);
        header.put(command);
        header.put(status);
        header.putShort(qtdeFiles);

        for (String f : files) {
            header.put((byte) f.length());
            byte[] filenameBytes = f.getBytes();
            header.put(filenameBytes);
        }

        int size = header.limit(); // tamanho do array de bytes

        byte [] bytes = header.array();
        try {
            out.write(bytes, 0, size);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Envia o cabeçalho de resposta para o cliente do comando de baixar arquivo do servidor, os cabeçalhos são 
     * compostos pelos seguintes campos: tipo de mensagem, comando, status, tamanho do arquivo e o conteúdo do arquivo.
     * 
     * @param out DataOutputStream do socket do cliente para enviar a resposta 
     * @param command byte com o código do comando, 1 para adicionar arquivo e 4 para pegar arquivo
     * @param status byte com o status da operação (0x00 para erro e 0x01 para sucesso)
     * @param filename caminho do arquivo no qual foi feita a operação
     * @throws IOException caso ocorra algum erro de I/O
     */
    private void sendGetFileResponse(DataOutputStream out, byte command, byte status, File filename) throws IOException {

        long fileSize = 0;


        if (filename != null) {
            fileSize = filename.length();
        } 
        // long fileSize = filename.length();
        
        if (fileSize > Math.pow(2, 232)) {
            throw new IllegalArgumentException("O arquivo é muito grande para ser incluído na resposta.");
        }

        ByteBuffer header = ByteBuffer.allocate(7);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 2);
        header.put(command);
        header.put(status);
        header.putInt((int) fileSize);
        int size = header.limit(); // tamanho do array de bytes
        int headerSize = header.position(); // tamanho do cabeçalho
        int totalSize = headerSize + 1; // +1 para o byte do arquivo
        byte[] headerBytes = header.array();


        if (fileSize > 0) {
            Logger logger = Logger.getLogger("server.log");
            logger.info("Mandando byte a byte para cliente");
             try (FileInputStream fis = new FileInputStream(filename)) {
            out.write(headerBytes, 0, headerSize); // Envie o cabeçalho

            if (fileSize > 0) {
                int c;
                while ((c = fis.read()) != -1) {
                    out.write(c); // Envie os bytes do arquivo um por um
                }
        }
       
        out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        } else {
            out.write(headerBytes, 0, headerSize); // Envie o cabeçalho
            out.flush();
        }


    }

    /**
     * Envia o cabeçalho de resposta para o cliente do comando de deletar arquivo do servidor, os cabeçalhos são 
     * compostos por 3 bytes, sendo o primeiro o tipo de mensagem, o segundo o comando e o terceiro o status.
     * 
     * @param out DataOutputStream do socket do cliente para enviar a resposta
     * @param command byte com o código do comando
     * @param status byte com o status da operação (0x00 para erro e 0x01 para sucesso)
     * @throws IOException caso ocorra algum erro de I/O
     */
    private void sendDeleteAndAddFileResponse(DataOutputStream out, byte command, byte status) throws IOException {
        byte[] bytes = new byte[3];
        ByteBuffer header = ByteBuffer.allocate(3);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 2);
        header.put(command);
        header.put(status);
        out.write(header.array());
        out.flush();
    }
} // class
