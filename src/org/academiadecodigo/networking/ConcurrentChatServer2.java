package org.academiadecodigo.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentChatServer2 {
    private static Set<PrintWriter> writers = new HashSet<>();
    private static Map<String, PrintWriter> nicknameMap = new HashMap<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(7777)) {
            System.out.println("Chat server is running on port 7777.");
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket);
                threadPool.execute(new ClientHandler(clientSocket));
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private String nickname;
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                out.println("Enter your nickname: ");
                nickname = in.readLine();
                writers.add(out);
                nicknameMap.put(nickname, out);

                broadcast("SERVER: " + nickname + " has joined the chat.");
                System.out.println("It's " + nickname);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equalsIgnoreCase("/quit")) {
                        break;
                    } else if (inputLine.startsWith("/w ")) {
                        String[] tokens = inputLine.split(" ");
                        String recipientNickname = tokens[1];
                        String message = nickname + " (whisper): " + inputLine.substring(3 + recipientNickname.length());
                        sendPrivateMessage(nickname, recipientNickname, message);
                    } else {
                        broadcast(nickname + ": " + inputLine);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client " + nickname + ": " + e);
            } finally {
                writers.remove(out);
                nicknameMap.remove(nickname);
                broadcast("SERVER: " + nickname + " has left the chat.");
                System.out.println(nickname + " disconnected: " + clientSocket);

                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e);
                }
            }
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter writer : writers) {
            writer.println(message);
        }
    }

    private static void sendPrivateMessage(String senderNickname, String recipientNickname, String message) {
        PrintWriter recipientWriter = nicknameMap.get(recipientNickname);
        if (recipientWriter != null) {
            recipientWriter.println(message);
        } else {
            PrintWriter senderWriter = nicknameMap.get(senderNickname);
            senderWriter.println("SERVER: Could not deliver message to " + recipientNickname + ".");
        }
    }
}