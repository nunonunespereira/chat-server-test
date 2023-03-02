package org.academiadecodigo.networking;

import java.io.*;
import java.net.*;
import java.util.*;

public class ConcurrentChatServer implements Runnable {

    private Socket clientSocket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private static ArrayList<PrintWriter> clientWriters = new ArrayList<>();

    public ConcurrentChatServer(Socket socket) {
        this.clientSocket = socket;
    }

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            System.out.println("Chat server started on port 7777.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                Thread thread = new Thread(new ConcurrentChatServer(clientSocket));
                thread.start();
            }
        } catch (IOException ex) {
            System.out.println("Error starting server: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            clientWriters.add(outputWriter);

            while (true) {
                //Read from the client
                String input = inputReader.readLine();
                if (input == null) {
                    System.out.println("Client disconnected: " + clientSocket);
                    break;
                }

                //Broadcast
                for (PrintWriter writer : clientWriters) {
                    writer.println(Thread.currentThread().getName() + " :: " + input);
                }
            }
        } catch (IOException ex) {
            System.out.println("Error handling client: " + ex.getMessage());
        } finally {
            try {
                inputReader.close();
                outputWriter.close();
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing client socket: " + ex.getMessage());
            }

            clientWriters.remove(outputWriter);
        }
    }
}