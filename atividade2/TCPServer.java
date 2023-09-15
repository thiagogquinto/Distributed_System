package atividade2;

/**
 * TCPServer: Servidor para conexao TCP com Threads Descricao: Recebe uma
 * conexao, cria uma thread, recebe uma mensagem e finaliza a conexao
 */
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
                        handleDelete(filename);

                    } else if (commandId == 3) {
                        handleGetFilesList(out);

                    } else if (commandId == 4) {
                        handleGetFile(filename);
                    }

                }

                
                out.writeUTF(buffer); // envia a mensagem para o servidor
                out.flush();

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

    private void handleAddFile(String filename) throws IOException {
        Logger logger = Logger.getLogger("server.log");

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

            logger.info("Arquivo " + filename + " copiado com sucesso\n");
            sendResponse(out, (byte) 1, (byte) 1);

        } catch (IOException e) {
            logger.info("Erro ao copiar arquivo " + filename);
            sendResponse(out, (byte) 1, (byte) 2);
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

    private void handleDelete(String filename) throws IOException {

        Logger logger = Logger.getLogger("server.log"); // pegar o logger
        File file = new File(serverPath + "/" + filename);
        if (file.delete()) {
            logger.info("Arquivo " + filename + " deletado com sucesso\n");
            sendResponse(out, (byte) 2, (byte) 1);
        } else {
            logger.info("Erro ao deletar arquivo " + filename + "\n");
            sendResponse(out, (byte) 2, (byte) 2);
        }

    }

    private void handleGetFilesList(DataOutputStream out) throws IOException {
        Logger logger = Logger.getLogger("server.log"); // pegar o logger
        ByteBuffer buffer = ByteBuffer.allocate(258);

        // Listar os arquivos no diretório de destino (no servidor)
        File file = new File(serverPath);
        File[] files = file.listFiles();
        Integer filesCount = 0;
        StringBuilder filesNames = new StringBuilder(); // Usaremos um StringBuilder para construir a lista de nomes

        for (File f : files) {
            if (f.isFile()) {
                filesCount++;
                filesNames.append(f.getName()).append("\n"); // Adiciona um caractere de quebra de linha após cada nome
            }
        }

        
        logger.info("Criando e adicionando dados no buffer");
        buffer = ByteBuffer.allocate(2);
        buffer.put((byte) ((filesCount >> 8) & 0xFF)); // byte mais significativo
        buffer.put((byte) (filesCount & 0xFF)); // byte menos significativo

        byte[] bytes = buffer.array();
        int size = buffer.limit();

        out.write(bytes, 0, size);
        out.flush();

        // Agora, envie os nomes dos arquivos um a um
        String[] fileNamesArray = filesNames.toString().split("\n"); // Divide os nomes por quebra de linha
        for (String fileName : fileNamesArray) {
            byte[] filenameBytes = fileName.getBytes(StandardCharsets.UTF_8); // Use a codificação correta

            byte filenameLength = (byte) filenameBytes.length;

            out.write(filenameLength);
            out.flush();

            logger.info("Enviando nome do arquivo byte a byte");
            out.write(filenameBytes);
            out.flush();

            // Adicione uma linha em branco após cada nome de arquivo
            out.write('\n');
            out.flush();
        }
    }

    private void handleGetFile(String filename) {
        System.out.println(filename);
    }

    private static ByteBuffer sendResponse(DataOutputStream out, byte command, byte status) throws IOException {

        byte[] bytes = new byte[259];
        ByteBuffer header = ByteBuffer.allocate(258);
        header.order(ByteOrder.BIG_ENDIAN);
        header.put((byte) 2);
        header.put(command);
        header.put(status);
        bytes = header.array();
        int size = header.limit();
        out.write(bytes, 0, size);
        out.flush();

        return header;
    }
} // class