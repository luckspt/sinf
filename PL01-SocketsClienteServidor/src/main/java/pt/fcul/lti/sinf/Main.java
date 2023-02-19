package pt.fcul.lti.sinf;

import java.io.*;
import java.net.Socket;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Socket clientSocket = new Socket("127.0.0.1", 23456);

            ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());

            outStream.writeObject("lucas"); // user
            outStream.writeObject("1234"); // password

            Boolean userValid = (Boolean) inStream.readObject();
            System.out.println("User valid: " + userValid);

            // ******************************************************************
            // 2. enviar um ficheiro para o servidor
            // ******************************************************************
            File file = new File("src/main/resources/client.txt");
            long fileSize = file.length();
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Send the file size bytes at a time
            int size = 1024;
            byte[] buffer = new byte[size];

            outStream.writeObject(fileSize);

            do {
                int bytesRead = bufferedInputStream.read(buffer, 0, size);
                // EOF
                if (bytesRead == -1) {
                    break;
                }

                outStream.write(buffer, 0, bytesRead);
            } while (true);

            /* int size = 1024;
            int chunk = 0;
            while (file.length() > (long) chunk * size) {
                int bytesToRead = (int) Math.min(size, file.length() - (long) chunk * size);
                byte[] bufferOut = new byte[bytesToRead];
                fileInputStream.read(bufferOut, chunk * size, bytesToRead);

                outStream.writeObject(bytesToRead);
                outStream.writeObject(bufferOut);

                chunk++;
            } */
            // ******************************************************************

            outStream.close();
            inStream.close();

            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}