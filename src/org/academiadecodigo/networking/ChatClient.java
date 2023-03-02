package org.academiadecodigo.networking;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ChatClient class
 */
public class ChatClient {

    private Socket socket;
    private BufferedReader inputBufferedReader;
    private BufferedWriter outputBufferedWriter;


    /**
     * @param serverAddress server IP address
     * @param serverPort    server port number
     */
    public ChatClient(String serverAddress, int serverPort) {

        System.out.println("Trying to establish the connection, please wait...");

        try {

            // connect to the specified host name and port
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to: " + socket);

            // create the streams
            setupSocketStreams();

            // start a new thread to read and print messages from the server
            new Thread(() -> {
                try {
                    String message = "";
                    while ((message = inputBufferedReader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (UnknownHostException ex) {

            System.out.println("Host unknown: " + ex.getMessage());
            System.exit(1);

        } catch (IOException ex) {

            System.out.println(ex.getMessage());
            System.exit(1);

        }

        String line = "";

        // while the client doesn't signal to quit
        while (!line.equals("/quit")) {

            try {

                // read the pretended message from the console
                line = inputBufferedReader.readLine();

                // write the pretended message to the output buffer
                outputBufferedWriter.write(line);
                outputBufferedWriter.newLine();
                outputBufferedWriter.flush();

            } catch (IOException ex) {

                System.out.println("Sending error: " + ex.getMessage() + ", closing client...");
                break;

            }

        }

        // close the client socket and buffers
        stop();

    }

    /**
     * ChatServer entry point, processes the arguments and instantiates the chat client object
     *
     * @param args the chat server host ip and port
     */
    public static void main(String args[]) {

        if (args.length != 2) {

            System.out.println("Usage: java ChatClient <host> <port>");
            System.exit(1);

        }

        try {

            new ChatClient(args[0], Integer.parseInt(args[1]));

        } catch (NumberFormatException ex) {

            System.out.println("Invalid port number " + args[0]);

        }

    }

    /**
     * Instantiate the buffered readers and writers from the standard input and the socket output stream
     *
     * @throws IOException
     */
    public void setupSocketStreams() throws IOException {

        inputBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    /**
     * Close the buffers and the socket
     */
    public void stop() {

        try {

            if (socket != null) {
                System.out.println("Closing the socket");
                socket.close();
            }

        } catch (IOException ex) {

            System.out.println("Error closing connection: " + ex.getMessage());

        }
    }
}

