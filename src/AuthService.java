import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static final Map<String, String> users = new HashMap<>();
    private static final File file = new File("users.txt");
    static {
        loadUsers();
    }

    private static synchronized void loadUsers() {
        if(!file.exists()) {
            System.out.println("fichier n'existe pas");
            return;
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                users.put(line.split(":")[0], line.split(":")[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void saveUsers(String user, String password) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(user + ":" + password);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized boolean authenticate(String user, String password) {
        if (users.containsKey(user)) {
            return users.get(user).equals(password);
        }
        users.put(user, password);
        saveUsers(user, password);
        return true;
    }
}
