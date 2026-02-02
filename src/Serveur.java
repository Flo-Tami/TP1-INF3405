import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {
    private static ServerSocket Listener; // Application Serveur

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        String serverAddress;
        int serverPort = -1;
        boolean isValid;

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
                    serverPort = scanner.nextInt();
                    if (serverPort < ClientHandler.MIN_PORT || serverPort > ClientHandler.MAX_PORT) {
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
            } while (serverPort < ClientHandler.MIN_PORT || serverPort > ClientHandler.MAX_PORT);
            scanner.nextLine();
        }
        
        Listener = new ServerSocket();
        Listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);

        Listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
        try {
            while (true) {
                 new ClientHandler(Listener.accept(), clientNumber++).start();
            }
        } finally {
            Listener.close();
        }
    }
}