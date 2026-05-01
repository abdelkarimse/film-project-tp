package tp1.film.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp1.film.Entity.Session;
import tp1.film.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByToken(String token);

    List<Session> findByUser(User user);

    void deleteByToken(String token);

    void deleteByUser(User user);
}