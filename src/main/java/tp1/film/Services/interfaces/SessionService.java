package tp1.film.Services.interfaces;


import tp1.film.Entity.Session;
import tp1.film.Entity.User;

public interface SessionService {
    Session createSession(User user, String token, String type);

    boolean validateSession(String token, String type);

    void invalidateSession(String token);
}