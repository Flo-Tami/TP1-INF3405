import common.NetworkUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

import static common.Constants.AUTHENTIFICATION_SUCCED;

public class Client {

    public static void main(String[] args) {
        String serverAddress = null;
        String userName = null;
        String password = null;
        Socket socket = null;
        int port = 0;
        Scanner scanner = new Scanner(System.in);

        while (socket == null) {
            serverAddress = NetworkUtils.askValidIp(scanner);
            port = NetworkUtils.askValidPort(scanner);

            try {
                System.out.println("Connexion au serveur " + serverAddress + ":" + port + " en cours...");
                socket = new Socket(serverAddress, port);
                System.out.println("Connexion réussie !");
            } catch (IOException e) {
                System.out.println("Serveur non disponible à cette adresse ou port. Réessayez.");
            }
        }

        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            boolean isAuthenticated = false;
            while (!isAuthenticated) {
                System.out.print("Entrez le nom d'utilisateur: ");
                userName = scanner.nextLine();

                System.out.print("Entrez le mot de passe: ");
                password = scanner.nextLine();

                out.writeUTF(userName);
                out.writeUTF(password);
                out.flush();

                String response = in.readUTF();
                isAuthenticated = response.equals(AUTHENTIFICATION_SUCCED);

                if (!isAuthenticated) {
                    System.out.println("Erreur dans la saisie du mot de passe. Réessayez.");
                } else {
                    System.out.println("Authentification réussie !");
                }
            }

            sendAndReceiveImage(scanner, out, in);

        } catch (IOException e) {
            System.out.println("Erreur de communication avec le serveur : " + e.getMessage());
        }
    }

    private static void sendAndReceiveImage(Scanner scanner, DataOutputStream out, DataInputStream in) throws IOException {
        File imageFile;
        do {
            System.out.print("Entrez le nom de l'image à transformer (avec l'extension): ");
            String imageName = scanner.nextLine();
            imageFile = new File(imageName);

            if (!imageFile.exists()) {
                System.out.println("Image introuvable.");
            }
        } while (!imageFile.exists());

        System.out.print("Entrez un nom + extension pour l'image obtenue: ");
        String imageNameAfterSobel = scanner.nextLine();

        // Envoi de l'image
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        out.writeUTF(imageFile.getName());
        out.writeLong(imageBytes.length);
        out.write(imageBytes);
        out.flush();
        System.out.format("L'image %s a été envoyée pour traitement.\n", imageFile.getName());

        // Réception de l'image
        long processedSize = in.readLong();
        byte[] processedBytes = new byte[(int) processedSize];
        in.readFully(processedBytes);
        File outputFile = new File(imageNameAfterSobel);

        Files.write(outputFile.toPath(), processedBytes);
        System.out.format("Image traitée reçue et sauvegardée : %s%n", outputFile.getAbsolutePath());
    }
}
