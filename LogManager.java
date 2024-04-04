import java.io.File;
import java.io.IOException;

public class LogManager {
    String filePath;
    public  LogManager(String path) {
        // Specify the file path
        this.filePath = path;

        // Create a File object
        File file = new File(filePath);

        // Check if file already exists
        if (file.exists()) {
            System.out.println("File already exists.");
        } else {
            try {
                // Create the file
                if (file.createNewFile()) {
                    System.out.println("File created successfully!");
                } else {
                    System.out.println("File creation failed.");
                }
            } catch (IOException e) {
                System.err.println("Error creating the file: " + e.getMessage());
            }
        }
    }
}

