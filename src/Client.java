import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;

    public static void main(String[] args) throws IOException {
        boolean isValid;
        int port = -1;
        String serverAddress;
        String userName;
        String password;

        try (Scanner scanner = new Scanner(System.in)) {
            do {
                System.out.print("Entrez l'adresse IP du serveur: ");
                serverAddress = scanner.nextLine();

                isValid = ClientHandler.ipIsValid(serverAddress);
                if (!isValid) {
                    System.out.println(ClientHandler.INPUT_ERROR);
                }

            } while (!isValid);

            do {
                System.out.print("Entrez le port d’écoute: ");
                if (scanner.hasNextInt()) {
                    port = scanner.nextInt();
                    if (port < ClientHandler.MIN_PORT || port > ClientHandler.MAX_PORT) {
                        System.out.format(
                                "La valeur du port doit être entre %d et %d%n",
                                ClientHandler.MIN_PORT,
                                ClientHandler.MAX_PORT
                        );
                    }
                } else {
                    System.out.println(ClientHandler.INPUT_ERROR);
                    scanner.next();
                }
            } while (port < ClientHandler.MIN_PORT || port > ClientHandler.MAX_PORT);
            scanner.nextLine();

            System.out.print("Entrez un nom d'utilisateur: ");
            userName = scanner.nextLine();

            System.out.print("Entrez le mot de passe: ");
            password = scanner.nextLine();

            socket = new Socket(serverAddress, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(userName);
            out.writeUTF(password);
            out.flush();
            System.out.println(in.readUTF());
            socket.close();
        }
    }
}
