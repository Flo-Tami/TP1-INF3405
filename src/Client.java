import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;

    public static void main(String[] args) throws IOException {
        final int MIN_PORT = 5000;
        final int MAX_PORT = 5050;
        final String INPUT_ERROR = "Entrée invalide. Réessayez!";
        boolean isValid;
        int port = -1;
        String serverAddress;
        String userName;
        String password;

        try (Scanner scanner = new Scanner(System.in)) {
            do {
                System.out.print("Entrez l'adresse IP du serveur: ");
                serverAddress = scanner.nextLine();

                isValid = ipIsValid(serverAddress);
                if (!isValid) {
                    System.out.println(INPUT_ERROR);
                }

            } while (!isValid);

            do {
                System.out.print("Entrez le port d’écoute: ");
                if (scanner.hasNextInt()) {
                    port = scanner.nextInt();
                    if (port < MIN_PORT || port > MAX_PORT) {
                        System.out.format(
                                "La valeur du port doit être entre %d et %d%n",
                                MIN_PORT,
                                MAX_PORT
                        );
                    }
                } else {
                    System.out.println(INPUT_ERROR);
                    scanner.next();
                }
            } while (port < MIN_PORT || port > MAX_PORT);
            scanner.nextLine();

            System.out.print("Entrez un nom d'utilisateur: ");
            userName = scanner.nextLine();

            System.out.print("Entrez le mot de passe: ");
            password = scanner.nextLine();
        }

        // Connexion
        // socket = new Socket(serverAddress, port);
        // DataInputStream in = new DataInputStream(socket.getInputStream());
        // System.out.println(in.readUTF());
        // socket.close();
    }

    public static boolean ipIsValid(String ipAddress) {
        String[] blocs = ipAddress.split("\\.");

        if (blocs.length != 4) return false;

        for (String bloc : blocs) {
            try {
                int value = Integer.parseInt(bloc);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
