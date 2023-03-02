package org.academiadecodigo.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ChatServer
 */
public class ChatServer {

    private Socket clientSocket;
    private ServerSocket serverSocket;
    private BufferedReader inputBufferedReader;

    public ChatServer(int port) {

        try {

            // bind the socket to specified port
            System.out.println("Binding to port " + port);
            serverSocket = new ServerSocket(port);

            System.out.println("Server started: " + serverSocket);

            // block waiting for a client to connect
            System.out.println("Waiting for a client connection");
            clientSocket = serverSocket.accept();

            // handle client connection
            System.out.println("Client accepted: " + clientSocket);
            setupSocketStream();

            while (true) {

                try {

                    // read line from socket input reader
                    String line = inputBufferedReader.readLine();

                    // if received /quit close break out of the reading loop
                    if (line == null || line.equals("/quit")) {

                        System.out.println("Client closed, exiting");
                        break;

                    }

                    // show the received line to the console
                    System.out.println(line);

                } catch (IOException ex) {

                    System.out.println("Receiving error: " + ex.getMessage());

                }

            }


        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());

        } finally {

            close();

        }

    }

    /**
     * ChatServer entry point
     *
     * @param args ChatServer port number
     */
    public static void main(String args[]) {

        // exit application if no port number is specified
        if (args.length == 0) {
            System.out.println("Usage: java ChatServer [port]");
            System.exit(1);
        }

        try {
            // try to create an instance of the ChatServer at port specified at args[0]
            new ChatServer(Integer.parseInt(args[0]));

        } catch (NumberFormatException ex) {
            // write an error message if an invalid port was specified by the user
            System.out.println("Invalid port number " + args[0]);
        }

    }

    /**
     * Instantiate a buffered reader from the input stream of the client socket
     *
     * @throws IOException
     */
    public void setupSocketStream() throws IOException {
        inputBufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


    /**
     * Closes the client socket and the buffered input reader
     */
    public void close() {

        try {

            if (clientSocket != null) {
                System.out.println("Closing client connection");
                clientSocket.close();
            }

            if (serverSocket != null) {
                System.out.println("Closing server socket");
                serverSocket.close();
            }


        } catch (IOException ex) {

            System.out.println("Error closing connection: " + ex.getMessage());

        }

    }
}
