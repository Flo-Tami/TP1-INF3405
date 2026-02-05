import common.NetworkUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Serveur {

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        Scanner scanner = new Scanner(System.in);

        String serverAddress = NetworkUtils.askValidIp(scanner);
        int serverPort = NetworkUtils.askValidPort(scanner);

        ServerSocket listener = new ServerSocket();
        listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);
        listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("Le serveur roule sur %s:%d%n", serverAddress, serverPort);

        try {
            while (true) {
                new ClientHandler(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }
}