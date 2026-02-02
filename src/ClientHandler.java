import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends Thread {
    private Socket socket;
    private int clientNumber;
    private static final Map<String, String> users = new HashMap<>();
    static final int MIN_PORT = 5000;
    static final int MAX_PORT = 5050;
    static final String INPUT_ERROR = "Entrée invalide. Réessayez!";

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String userName = in.readUTF();
            String password = in.readUTF();

            if (users.containsKey(userName)) {
                if  (!users.get(userName).equals(password)) {
                    out.writeUTF("Mauvais mot de passe");
                }
            } else {
                users.put(userName, password);
            }

            out.writeUTF("Hello from server - you are client#" + clientNumber);
        } catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
        }
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