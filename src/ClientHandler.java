import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static common.Constants.AUTHENTIFICATION_FAILED;
import static common.Constants.AUTHENTIFICATION_SUCCED;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            boolean authenticated = false;
            String user = null;
            String password = null;

            while (!authenticated) {
                user = in.readUTF();
                password = in.readUTF();

                if (AuthService.authenticate(user, password)) {
                    authenticated = true;
                    out.writeUTF(AUTHENTIFICATION_SUCCED);
                    out.flush();
                    System.out.println("Client #" + clientNumber + " connecté");
                } else {
                    out.writeUTF(AUTHENTIFICATION_FAILED);
                    out.flush();
                }
            }

            // Réception image
            String fileName = in.readUTF();
            long size = in.readLong();
            byte[] imageBytes = new byte[(int) size];
            in.readFully(imageBytes);

            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss"));
            System.out.format("[%s - %s:%d - %s] : image %s reçue pour traitement\n",
                    user, socket.getInetAddress().getHostAddress(), socket.getPort(), date, fileName);

            // Traitement
            byte[] processed = ImageProcessor.process(imageBytes);

            // Envoi image
            out.writeLong(processed.length);
            out.write(processed);
            out.flush();

            System.out.println("Image traitée envoyée à " + user);
        } catch (IOException e) {
            System.out.println("Erreur client #" + clientNumber + " : " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException message) {
                System.out.println("Erreur lors de la fermeture du socket : " + message.getMessage());
            }
        }
    }
}
