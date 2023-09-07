package atividade1;

/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem e finaliza a conexao
 */
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

public class TCPServer {

    public static void main(String args[]) {
        try {
            int serverPort = 6666; // porta do servidor

            /* cria um socket e mapeia a porta para aguardar conexao */
            ServerSocket listenSocket = new ServerSocket(serverPort);

            ExecutorService executorService = Executors.newFixedThreadPool(100); // cria um pool de threads para lidar
                                                                                 // com múltiplos clientes

            while (true) {
                System.out.println("Servidor aguardando conexao ...");

                /* aguarda conexoes */
                Socket clientSocket = listenSocket.accept();

                System.out.println("Cliente conectado ... Criando thread ...");

                /* cria um thread para atender a conexao */
                ClientThread c = new ClientThread(clientSocket);

                /* inicializa a thread */
                executorService.submit(c);
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
                buffer = in.readUTF(); /* aguarda o envio de dados */

                if (buffer.startsWith("CONNECT")) {
                    String[] bufferArray = buffer.split(" ");

                    String user = bufferArray[1].replace(",", "");
                    String password = bufferArray[2];

                    System.out.println("Diretório de Trabalho Atual: " + System.getProperty("user.dir"));


                    File file = new File("./atividade1/users.txt");
                    Scanner scanner = new Scanner(file);
                    Boolean userFound = false;

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] lineArray = line.split(":");

                        if (lineArray[0].equals(user) && lineArray[1].equals(password)) {
                            System.out.println("Usuário e senha encontrados");
                            userFound = true;
                            buffer = "OK";
                            break;
                        }
                    }

                    if (!userFound) {
                        System.out.println("Usuário e senha não encontrados");
                        buffer = "ERROR";
                    }
                }

                // System.out.println("Cliente disse: " + buffer);

                // if (buffer.equals("PARAR")) break;

                // buffer = "";
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

} // class
