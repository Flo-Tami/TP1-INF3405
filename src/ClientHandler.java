import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("Client #" + clientNumber + " connecté");
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String user = in.readUTF();
            String password = in.readUTF();

            if (!AuthService.authenticate(user, password)) {
                out.writeUTF("Mauvais mot de passe");
                return;
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
            } catch (IOException ignored) {}
        }
    }
}
