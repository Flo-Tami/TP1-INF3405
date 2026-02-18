import common.NetworkUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        String userName;
        String password;
        String imageName;
        String imageNameAfterSobel;

        try (Scanner scanner = new Scanner(System.in)) {
            String serverAddress = NetworkUtils.askValidIp(scanner);
            int port = NetworkUtils.askValidPort(scanner);
            boolean isAuthenticated = false;

            Socket socket = null;
            DataOutputStream out = null;
            DataInputStream in = null;

            do {
                System.out.print("Entrez un nom d'utilisateur: ");
                userName = scanner.nextLine();
                System.out.print("Entrez le mot de passe: ");
                password = scanner.nextLine();

                if (socket != null) socket.close();
                socket = new Socket(serverAddress, port);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF(userName);
                out.writeUTF(password);
                out.flush();

                String authResponse = in.readUTF();

                isAuthenticated = authResponse.equals("OK");

                if (!isAuthenticated) {
                    System.out.println("Erreur dans la saisie du mot de passe");
                }
            } while (!isAuthenticated);

            File imageFile;

            do {
                System.out.print("Entrez le nom de l'image à transformer (avec l'extension): ");
                imageName = scanner.nextLine();

                imageFile = new File(imageName);

                if (!imageFile.exists()) {
                    System.out.println("Image introuvable.");
                }
            } while (!imageFile.exists());

            System.out.print("Entrez un nom pour l'image obtenue: ");
            imageNameAfterSobel = scanner.nextLine();


            // envoi de l'image
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            out.writeUTF(imageFile.getName());
            out.writeLong(imageBytes.length);
            out.write(imageBytes);
            out.flush();
            System.out.format("L'image %s a été envoyée pour traitement.\n", imageFile.getName());

            // reception de l'image
            long processedSize = in.readLong();
            byte[] processedBytes = new byte[(int) processedSize];
            in.readFully(processedBytes);
            File outputFile = new File(imageNameAfterSobel);

            // ecriture sur le disque
            Files.write(outputFile.toPath(), processedBytes);
            System.out.format("Image traitée reçue et sauvegardée : %s%n", outputFile.getAbsolutePath());
            socket.close();
        }
    }
}