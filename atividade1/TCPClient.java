package atividade1;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.util.Scanner;

/**
 * Descrição: Cliente TCP simples que se conecta com o servidor e envia comandos
 * para manipular dados no servidor como
 * diretorios e arquivos.
 * 
 * Autor: Thiago Gariani Quinto, Marcos Vinicius de Quadros
 * 
 * Data de criação: 06/09/2023
 * Data de atualização: 07/09/2023, 09/09/2023, 11/09/2023, 12/09/2023,
 * 13/09/2023
 */

public class TCPClient {
    public static void main(String args[]) {
        Socket clientSocket = null; // socket do cliente
        Scanner reader = new Scanner(System.in); // ler mensagens via teclado
        boolean isAuthenticated = false;

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
            String username = "";
            String buffer = "";
            while (true) {
                System.out.print(username + "$ ");
                buffer = reader.nextLine(); // lê mensagem via teclado

                /*
                 * verifica se o usuário está autenticado para executar a ação
                 * se não estiver, verifica se a ação é de autenticação
                 */
                if (!isAuthenticated) {
                    if (buffer.startsWith("CONNECT")) {
                        String[] bufferArray = buffer.split(" ");
                        String hash = generateHashString(bufferArray[2]);
                        buffer = bufferArray[0] + " " + bufferArray[1] + " " + hash;
                        out.writeUTF(buffer); // envia a mensagem para o servidor
                        buffer = in.readUTF(); // aguarda resposta do servidor
                        if (buffer.equals("SUCCESS")) {
                            isAuthenticated = true;
                            username = bufferArray[1].replace(",", "");
                        } else {
                            System.out.println(buffer);
                        }
                    } else {
                        System.out.println("Você precisa se autenticar para executar essa ação");
                    }
                } else {
                    out.writeUTF(buffer); // envia a mensagem para o servidor

                    if (buffer.equals("EXIT")) {
                        break;
                    }
                    buffer = in.readUTF(); // aguarda resposta do servidor
                    System.out.println(buffer); // imprime resposta do servidor
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
                ;
            }
        }

    } // main

    /**
     * Gera um hash SHA-512 para a senha informada
     * 
     * @param password Senha a ser criptografada
     * @return Hash SHA-512 da senha
     */
    private static String generateHashString(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512"); // algoritmo de criptografia
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8)); // gera o hash da senha

            // Converte o hash em formato hexadecimal
            StringBuilder sb = new StringBuilder();
            /*
             * Converte cada byte do hash em um valor hexadecimal e concatena em uma string
             * para formar o hash completo
             */
            for (byte hashByte : hashBytes) {
                sb.append(String.format("%02x", hashByte));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular o hash da senha: " + e.getMessage());
        }
    }

} // class
