import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.logging.Logger;
public class TunnelServer {
    private static final Logger logger = Logger.getLogger(TunnelServer.class.getName());

    public static void main(String[] args) {
        String path = "tunnellogs.txt";
        LogManager logManager = new LogManager(path);
        try {
            // Create a server socket to listen for incoming connections
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Tunnel server started on port 8080...");
            
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
               logger.info("Client connected: " + clientSocket);
                
                // Connect to the destination server
                Socket destinationSocket = new Socket("localhost", 8000); // Example destination server
                
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
                            String dataSent = new String(buffer, 0, bytesRead);
                            logger.info("Data sent from client : " + dataSent);
                        }
                    } catch (IOException e) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                
                Thread destinationToClient = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = destinationInput.read(buffer)) != -1) {
                            clientOutput.write(buffer, 0, bytesRead);
                            String dataReceived = new String(buffer, 0, bytesRead);
                            logger.info("Data received from destination server: " + dataReceived);
                        
                        }
                    } catch (IOException e) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
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
            logger.severe("Error accepting client connection: " + e.getMessage());

            e.printStackTrace();
        }
    }




}
