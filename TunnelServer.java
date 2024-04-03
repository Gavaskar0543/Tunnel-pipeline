import java.io.*;
import java.net.*;

public class TunnelServer {
    public static void main(String[] args) {
        try {
            // Create a server socket to listen for incoming connections
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Tunnel server started on port 8080...");
            
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                
                // Connect to the destination server
                Socket destinationSocket = new Socket("localhost", 3000); // Example destination server
                
                // Create input and output streams for client and server connections
                InputStream clientInput = clientSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream();
                InputStream destinationInput = destinationSocket.getInputStream();
                OutputStream destinationOutput = destinationSocket.getOutputStream();
                
                // Start separate threads for bidirectional data transfer
                Thread clientToDestination = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = clientInput.read(buffer)) != -1) {
                            destinationOutput.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
                Thread destinationToClient = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = destinationInput.read(buffer)) != -1) {
                            clientOutput.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
                // Start the data transfer threads
                clientToDestination.start();
                destinationToClient.start();
                
                // Wait for the threads to finish
                clientToDestination.join();
                destinationToClient.join();
                
                // Close client and destination sockets
                clientSocket.close();
                destinationSocket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
