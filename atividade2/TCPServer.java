package atividade2;

/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem e finaliza a conexao
 */
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.File;
import java.nio.file.Files;

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
                System.out.println("Cliente conectado ... Criando thread ...");
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
 * Descricao: Rebebe um socket, cria os objetos de leitura e escrita,
 * aguarda msgs clientes e responde com a msg + :OK
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

            while (true) {

                String buffer = "";

                byte[] request = new byte[258];
                ByteBuffer header = ByteBuffer.allocate(258);
                in.read(request);
                header = ByteBuffer.wrap(request); // cria um buffer com o array de bytes recebido
                header.order(ByteOrder.BIG_ENDIAN); // define a ordem dos bytes (BIG_ENDIAN)
                byte messageType = header.get(); // tipo de mensagem (1 byte)
                byte commandId = header.get(); // código do comando (1 byte)
                byte filenameSize = header.get(); // tamanho do nome do arquivo (1 byte)
                byte[] filenameBytes = new byte[filenameSize]; // array de bytes para o nome do arquivo
                header.get(filenameBytes); // Lê o nome do arquivo (tamanho variável) em bytes
                String filename = new String(filenameBytes); // Converte o nome do arquivo para String

                if(commandId == 0x01){
                    int fileSize = header.getInt();
                    byte[] fileBytes = new byte[fileSize];
                    header.get(fileBytes);
                    // System.out.println("Tamanho do arquivo: " + fileSize);
                }


                Logger logger = Logger.getLogger("server.log"); // pegar o logger

                if (messageType == 1) { // verifica se é uma requisição

                    logger.info("Mensagem: " + messageType + " | Comando: " + commandId + " | Tamanho: " + filenameSize
                            + " | Arquivo: " + filename);

                    if (commandId == 1) {
                        handleAddFile(out, filename);

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

    private void handleAddFile(DataOutputStream out, String filename) throws IOException {
        Logger logger = Logger.getLogger("server.log");

        File srcFile = new File(localPath + "/" + filename);
        File destFile = new File(serverPath + "/" + filename);

        if (!srcFile.exists()) {
            logger.info("Arquivo " + filename + " não encontrado no cliente\n");
            sendDeleteAndAddFileResponse(out, (byte) 1, (byte) 0); // Envie uma resposta de erro
            return;
        }

        try (FileInputStream fis = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(destFile)) {

            int c;
            while ((c = fis.read()) != -1) {
                fos.write(c);
            }

            logger.info("Arquivo " + filename + " copiado com sucesso\n");
            logger.info("Enviando resposta para o cliente");
            sendDeleteAndAddFileResponse(out, (byte) 1, (byte) 1);
        } catch (IOException e) {
            logger.info("Erro ao copiar arquivo " + filename);
            e.printStackTrace();
            logger.info("Enviando resposta para o cliente");
            sendDeleteAndAddFileResponse(out, (byte) 1, (byte) 0);
        }
    }

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

    private void handleGetFile(DataOutputStream out, String filename) throws IOException{
        Logger logger = Logger.getLogger("server.log");

        File downloadDir = new File(downloadPath);

        File destFile = new File(downloadDir + "/" + filename);

        if(!downloadDir.exists()){
            downloadDir.mkdir();
        }
        
        File srcFile = new File(serverPath + "/" + filename);

        if(!srcFile.exists()){
            logger.info("Arquivo " + filename + " não encontrado no servidor\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFileResponse(out, (byte) 4, (byte) 0, null); // Envie uma resposta de erro
            return;
        }


        try (FileInputStream fis = new FileInputStream(srcFile);
                FileOutputStream fos = new FileOutputStream(downloadDir + "/" + filename)) {
            int c;
            while ((c = fis.read()) != -1) {
                fos.write(c);
            }
            logger.info("Arquivo " + filename + " baixado com sucesso\n");
            logger.info("Enviando resposta para o cliente");
            sendGetFileResponse(out, (byte) 4, (byte) 1, destFile);
        } catch (IOException e) {
            logger.info("Erro ao baixar arquivo " + filename);
            logger.info("Enviando resposta para o cliente");
            sendGetFileResponse(out, (byte) 4, (byte) 0, destFile);
            e.printStackTrace();
        }
    }

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
     * 
     * @param out DataOutputStream do socket do cliente para enviar a resposta 
     * @param command byte com o código do comando, 1 para adicionar arquivo e 4 para pegar arquivo
     * @param status byte com o status da operação (0x00 para erro e 0x01 para sucesso)
     * @param filename caminho do arquivo no qual foi feita a operação
     * @throws IOException caso ocorra algum erro de I/O
     */
    private void sendGetFileResponse(DataOutputStream out, byte command, byte status, File filename) throws IOException {

        long fileSize ;

        byte[] fileContent = new byte[0];

        if (filename == null) {
            fileSize = 0;
        } else {
            fileSize = filename.length();
            fileContent = Files.readAllBytes(filename.toPath()); 
        }

        // long fileSize = filename.length();
        
        if (fileSize > Math.pow(2, 232)) {
            throw new IllegalArgumentException("O arquivo é muito grande para ser incluído na resposta.");
        }

        int headerSize = 1 + 1 + 1 + 4;
        int fileSizeInt = (int) fileSize;
        int totalSize = headerSize + fileSizeInt + 1;
        ByteBuffer header = ByteBuffer.allocate(totalSize);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 2);
        header.put(command);
        header.put(status);
        header.putInt(fileSizeInt); 
        header.put(fileContent);
        // header.position(headerSize);

        int size = header.limit(); // tamanho do array de bytes

        System.out.println("Tamanho do array de bytes: " + size);

        byte [] bytes = header.array();
        out.write(bytes, 0, totalSize);
        out.flush();
    }

    /**
     * Envia o cabeçalho de resposta para o cliente do comando de deletar arquivo do servidor, os cabeçalhos são compostos por 3 bytes, sendo o primeiro o tipo de mensagem, o segundo o comando e o terceiro o status
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
