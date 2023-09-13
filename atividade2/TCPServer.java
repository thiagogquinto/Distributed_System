package atividade2;

/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem e finaliza a conexao
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.io.FileReader;
import java.io.FileWriter;


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

                /* cria um thread para atender a conexao */
                ClientThread c = new ClientThread(clientSocket);

                c.start();

                /* inicializa a thread */
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
            String buffer = "";
            while (true) {

                byte messageType = in.readByte(); // tipo de mensagem (1 byte)
                byte commandId = in.readByte(); // código do comando (1 byte)
                byte filenameSize = in.readByte(); // tamanho do nome do arquivo (1 byte)
                byte[] filenameBytes = new byte[filenameSize]; // array de bytes para o nome do arquivo
                in.readFully(filenameBytes); // Lê o nome do arquivo (tamanho variável) em bytes
                String filename = new String(filenameBytes); // Converte o nome do arquivo para String

                Logger logger = Logger.getLogger("server.log"); // pegar o logger

                if (messageType == 1) { // verifica se é uma requisição
                    logger.info("Mensagem: " + messageType + " | Comando: " + commandId + " | Tamanho: " + filenameSize
                            + " | Arquivo: " + filename);

                    if (commandId == 1) {
                        handleAddFile(filename);
                    } else if (commandId == 2) {
                    } else if (commandId == 3) {
                    } else if (commandId == 4) {
                    }

                }

                out.writeUTF(buffer);
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

   private void handleAddFile(String filename) {
        Logger logger = Logger.getLogger("server.log"); // pegar o logger
        File srcFile = new File(localPath + "/" + filename);
        File destFile = new File(serverPath + "/" + filename);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);

             int c;
 
            while ((c = fis.read()) != -1) {
                fos.write(c);
            }

            logger.info("Arquivo " + filename + " copiado com sucesso");
 
        } catch (IOException e) {
            logger.info("Erro ao copiar arquivo " + filename);
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
 
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleDelete() {
    }

    private void handleGetFilesList() {
    }

    private void handleFetFile() {
    }

} // class
