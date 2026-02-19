import common.NetworkUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {

    public static void main(String[] args) {
        int clientNumber = 0;

        try (Scanner scanner = new Scanner(System.in)) {

            ServerSocket listener = null;

            while (listener == null) {
                try {
                    String serverAddress = NetworkUtils.askValidIp(scanner);
                    int serverPort = NetworkUtils.askValidPort(scanner);

                    InetAddress serverIP = InetAddress.getByName(serverAddress);
                    listener = new ServerSocket();
                    listener.setReuseAddress(true);
                    listener.bind(new InetSocketAddress(serverIP, serverPort));

                    System.out.format("Le serveur roule sur %s:%d%n", serverAddress, serverPort);
                } catch (IOException e) {
                    System.out.println("Impossible de démarrer le serveur sur cette adresse/port.");
                    listener = null;
                }
            }

            while (true) {
                new ClientHandler(listener.accept(), clientNumber++).start();
            }

        } catch (IOException e) {
            System.out.println("Erreur serveur critique : " + e.getMessage());
        }
    }
}