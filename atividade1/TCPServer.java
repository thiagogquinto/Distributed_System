package atividade1;

/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem e finaliza a conexao
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;

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
    private String currentDirectory = System.getProperty("user.dir");

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

                    if (authenticate(user, password)) {
                        buffer = "SUCCESS";
                    } else {
                        buffer = "ERROR";
                    }
                } else if (buffer.equals("PWD")) {
                    buffer = currentDirectory;
                } else if (buffer.startsWith("CHDIR")) {
                    String[] bufferArray = buffer.split(" ");
                    if (bufferArray.length != 2) {
                        buffer = "ERROR - Comando inválido";
                    } else if (!bufferArray[0].equals("CHDIR")) {
                        System.out.println(bufferArray[0]);
                        buffer = "ERROR - Comando inválido";
                    } else {
                        if (bufferArray[1].split("/").length == 1 || !bufferArray[1].contains("/")) {
                            String path = bufferArray[1];
                            File file = new File(path);

                            if (bufferArray[1].equals("..")) {
                                path = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
                                file = new File(path);
                            } else {
                                file = new File(currentDirectory, path);
                                path = currentDirectory + "/" + path;
                            }

                            if (file.exists() && file.isDirectory()) {
                                currentDirectory = path;
                                buffer = "SUCCESS";
                            } else {
                                buffer = "ERROR - Diretório não encontrado";
                            }
                        } else {
                            String path = bufferArray[1];
                            File currentDirFile = new File(currentDirectory);
                            String[] pathSegments = path.split("/");

                            for (String segment : pathSegments) {
                                
                                if (segment.equals("..")) {
                                    currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
                                    currentDirFile = new File(currentDirectory);
                                } else {
                                    currentDirFile = new File(currentDirFile, segment);
                                }
                            }
                        }
                    }
                } else if (buffer.equals("GETFILES")) {
                    File file = new File(currentDirectory);
                    File[] files = file.listFiles();
                    buffer = "";
                    for (File f : files) {
                        if (f.isFile())
                            buffer += f.getName() + "\n";
                    }
                } else if (buffer.equals("GETDIRS")) {
                    File file = new File(currentDirectory);
                    File[] files = file.listFiles();
                    buffer = "";
                    for (File f : files) {
                        if (f.isDirectory())
                            buffer += f.getName() + "\n";
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

    private boolean authenticate(String user, String password) {
        File file = new File("./atividade1/users.txt");
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineArray = line.split(":");

                if (lineArray[0].equals(user) && lineArray[1].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return false;
    }
} // class
