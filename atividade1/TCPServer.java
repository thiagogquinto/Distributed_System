package atividade1;

/**
 * Descrição: TCP Server simples que recebe comandos do cliente para manipular dados no servidor e 
 * envia respostas para o cliente com o resultado da operação.
 * 
 * Autor: Thiago Gariani Quinto, Marcos Vinicius de Quadros
 * 
 * Data de criação: 06/09/2023
 * Data de atualização: 07/09/2023, 09/09/2023, 11/09/2023, 12/09/2023, 13/09/2023
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
 * aguarda msgs clientes para realizar a ação desejada e devolve uma resposta
 * para o cliente.
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

    /**
     * metodo executado ao iniciar a thread - start() 
     * Descricao: aguarda o envio de dados pelo cliente e realiza a ação desejada
     */
    @Override
    public void run() {
        try {
            String buffer = "";
            while (true) {
                buffer = in.readUTF(); /* aguarda o envio de dados */

                /* de acordo com a requisição do cliente uma ação será realizado */
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
                        buffer = "ERROR";
                    } else if (!bufferArray[0].equals("CHDIR")) {
                        System.out.println(bufferArray[0]);
                        buffer = "ERROR";
                    } else {
                        if (bufferArray[1].split("/").length == 1) {
                            String path = bufferArray[1];
                            File file = new File(path);
                            
                            bufferArray[1] = bufferArray[1].replace("/", "");

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
                                buffer = "ERROR";
                            }
                        } else {
                            String path = bufferArray[1];
                            File currentDirFile = new File(currentDirectory);
                            String[] pathSegments = path.split("/");

                            for (String segment : pathSegments) {
                                if (segment.equals("..") || segment.equals("../")) {
                                    currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
                                    currentDirFile = new File(currentDirectory);
                                } else {
                                    currentDirFile = new File(currentDirFile, segment);
                                }
                            }
                            if (currentDirFile.exists() && currentDirFile.isDirectory()) {
                                currentDirectory = currentDirFile.getAbsolutePath();
                                buffer = "SUCCESS";
                            } else {
                                buffer = "ERROR";
                            }
                        }
                    }
                } else if (buffer.equals("GETFILES")) {
                    File file = new File(currentDirectory); // diretório atual 
                    File[] files = file.listFiles(); // lista de arquivos e diretórios do diretório atual
                    Integer filesCount = 0;
                    String filesNames = "";
                    for (File f : files) {
                        if (f.isFile()) { // se for arquivo
                            filesCount++;
                            filesNames += f.getName() + "\n";
                        }
                    }

                    buffer = filesCount + "\n" + filesNames;

                } else if (buffer.equals("GETDIRS")) {
                    File file = new File(currentDirectory);
                    File[] files = file.listFiles();
                    Integer dirsCount = 0;
                    String dirsNames = "";
                    for (File f : files) {
                        if (f.isDirectory()) { // se for diretório
                            dirsCount++;
                            dirsNames += f.getName() + "\n";
                        }
                    }

                    buffer = dirsCount + "\n" + dirsNames;
                }
                out.writeUTF(buffer); /* envia resposta para o cliente */
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
     * Método para autenticar o usuário - verifica se o usuário e senha estão no arquivo users.txt
     * que contém os usuários e senhas "registrados" no servidor.
     * @param user nome do usuário que está tentando se autenticar
     * @param password senha do usuário em HASH SHA-512 que está tentando se autenticar
     * @return true se o usuário e senha estiverem corretos, false caso contrário
     */
    private boolean authenticate(String user, String password) {
        File file = new File("./atividade1/users.txt"); // arquivo de usuarios e senhas "registrados" no servidor
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineArray = line.split(":");

                if (lineArray[0].equals(user) && lineArray[1].equals(password)){
                    return true; // sucesso
                }
            }
            return false; // usuário ou senha inválidos 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return false;
    }
} // class
