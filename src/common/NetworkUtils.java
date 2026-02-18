package common;

import java.util.Scanner;

import static common.Constants.*;

public class NetworkUtils {

    public static boolean isValidIp(String ipAddress) {
        String[] blocs = ipAddress.split("\\.");

        if (blocs.length != 4) return false;

        for (String bloc : blocs) {
        	if (bloc.length() > 1 && bloc.startsWith("0")) return false;
            try {
                int value = Integer.parseInt(bloc);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public static String askValidIp(Scanner scanner) {
        String serverAddress;
        boolean isValid;

        do {
            System.out.print("Entrez l'adresse IP du serveur: ");
            serverAddress = scanner.nextLine();
            isValid = NetworkUtils.isValidIp(serverAddress);

            if (!isValid) {
                System.out.println(INPUT_ERROR);
            }
        } while (!isValid);

        return serverAddress;
    }

    public static int askValidPort(Scanner scanner) {
        int port = -1;

        do {
            System.out.print("Entrez le port d’écoute: ");

            if (scanner.hasNextInt()) {
                port = scanner.nextInt();
                if (port < MIN_PORT || port > MAX_PORT) {
                    System.out.format(
                            "La valeur du port doit être entre %d et %d%n",
                            MIN_PORT, MAX_PORT
                    );
                }
            } else {
                System.out.println(INPUT_ERROR);
                scanner.next();
            }
        } while (port < MIN_PORT || port > MAX_PORT);

        scanner.nextLine();
        return port;
    }
}
