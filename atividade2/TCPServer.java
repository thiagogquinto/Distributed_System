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


public class TCPServer {
    public static void main(String args[]) {

        try {
            int serverPort = 6666; // porta do servidor

            /* cria um socket e mapeia a porta para aguardar conexao */
            ServerSocket listenSocket = new ServerSocket(serverPort);

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
  
    public ClientThread(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
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

                byte messageType = in.readByte(); // Lê o tipo de mensagem (1 byte)
                byte commandId = in.readByte(); // Lê o código do comando (1 byte)
                byte filenameSize = in.readByte(); // Lê o tamanho do nome do arquivo (1 byte)
                byte[] filenameBytes = new byte[filenameSize];
                in.readFully(filenameBytes); // Lê os bytes do nome do a
                String filename = new String(filenameBytes, StandardCharsets.UTF_8);

                if (messageType == 1) { // verifica se é uma requisição
                    if (commandId == 1) {
                        handleAddFile();
                    } else if (commandId == 2) {
                        handleDelete();
                    } else if (commandId == 3) {
                        handleGetFilesList();
                    } else if (commandId == 4) {
                        handleFetFile();
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

    private void handleAddFile() {

    }

    private void handleDelete() {

    }

    private void handleGetFilesList() {

    }

    private void handleFetFile() {

    }

} // class
