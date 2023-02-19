package pt.fcul.lti.sinf;

/***************************************************************************
 *   Seguranca Informatica
 *
 *
 ***************************************************************************/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class myServer {

    public static void main(String[] args) {
        System.out.println("servidor: main");
        myServer server = new myServer();
        server.startServer();
    }

    public void startServer() {
        ServerSocket sSoc = null;

        try {
            sSoc = new ServerSocket(23456);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        while (true) {
            try {
                Socket inSoc = sSoc.accept();
                ServerThread newServerThread = new ServerThread(inSoc);
                newServerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //sSoc.close();
    }


    //Threads utilizadas para comunicacao com os clientes
    class ServerThread extends Thread {

        private Socket socket = null;

        ServerThread(Socket inSoc) {
            socket = inSoc;
            System.out.println("thread do server para cada cliente");
        }

        public void run() {
            try {
                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

                String user = null;
                String passwd = null;

                try {
                    user = (String) inStream.readObject();
                    passwd = (String) inStream.readObject();
                    System.out.println("thread: depois de receber a password e o user");
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                //TODO: refazer
                //este codigo apenas exemplifica a comunicacao entre o cliente e o servidor
                //nao faz qualquer tipo de autenticacao
                if (user.length() != 0) {
                    outStream.writeObject((Boolean) true);
                } else {
                    outStream.writeObject((Boolean) false);
                }

                // ******************************************************************
                // Trabalho da PL01: Receber um ficheiro do cliente
                // ******************************************************************
                // Create a file to write to on resources folder
                File file = new File("src/main/resources/server.txt");
                file.createNewFile();

                // Create file output stream
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                // Read file size
                long fileSize = (long) inStream.readObject();

                int size = 1024;
                byte[] buffer = new byte[size];

                // Read fileSize bytes from the client
                // TODO: fix this
                do {
                    int bytesRead = inStream.read(buffer, 0, size);

                    fileOutputStream.write(buffer, 0, bytesRead);

                    // EOF
                    if (bytesRead != size) {
                        break;
                    }
                } while (fileSize < file.length());

                /* do {
                    int bytesToRead = (int) inStream.readObject();
                    if (bytesToRead == 0) {
                        break;
                    }

                    byte[] buffer = (byte[]) inStream.readObject();
                    fileOutputStream.write(buffer);
                } while (true);*/
                // ******************************************************************

                outStream.close();
                inStream.close();

                socket.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
