package tp1.film.Services.interfaces;

import tp1.film.Entity.User;

public interface AuthService {
    User register(String name, String email, String password);

    String login(String email, String password);

    boolean confirmAccount(String token);

    void logout(String token);
}