import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static final Map<String, String> users = new HashMap<>();

    public static synchronized boolean authenticate(String user, String password) {
        if (users.containsKey(user)) {
            return users.get(user).equals(password);
        }
        users.put(user, password);
        return true;
    }
}
