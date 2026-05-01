package tp1.film.Services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tp1.film.Entity.Session;
import tp1.film.Entity.User;
import tp1.film.Repository.SessionRepository;
import tp1.film.Services.interfaces.SessionService;
import tp1.film.utils.TokenUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

//service pou me session
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final TokenUtils tokenUtils;

    // fonction pour le seesion
    @Override
    public Session createSession(User user, String token, String type) {
        Date expirationDate = tokenUtils.extractClaim(token, Claims::getExpiration);

        Session session = Session.builder()
                .token(token)
                .type(type) // e.g., "auth" or "confirmation"
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(expirationDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();

        return sessionRepository.save(session);
    }
// fonction pour le valide sseesion

    @Override
    public boolean validateSession(String token, String type) {
        return sessionRepository.findByToken(token)
                .map(s -> s.getType().equalsIgnoreCase(type) &&
                        s.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    // fonction pour le  invalied me token
    @Override
    @Transactional // <--- Add this
    public void invalidateSession(String token) {
        sessionRepository.deleteByToken(token);
    }
}