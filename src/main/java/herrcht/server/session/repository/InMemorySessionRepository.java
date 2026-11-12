package herrcht.server.session.repository;

import herrcht.server.session.SessionEntity;
import herrcht.server.session.SessionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
        name = "herrcht.session.store-type",
        havingValue = "IN_MEMORY",
        matchIfMissing = true
)
public class InMemorySessionRepository implements SessionRepository {

    private final Map<UUID, SessionEntity> sessions = new ConcurrentHashMap<>();

    @Override
    public SessionEntity save(SessionEntity session) {
        if (session.getId() == null) {
            session.setId(UUID.randomUUID());
        }
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<SessionEntity> findById(UUID id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<SessionEntity> findByUserId(UUID userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        sessions.remove(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        sessions.values().removeIf(session -> session.getUserId().equals(userId));
    }

    @Override
    public long countByUserIdAndRevokedFalse(UUID userId) {
        return sessions.values().stream()
                .filter(session -> session.getUserId().equals(userId) && !session.isRevoked() && !session.isExpired())
                .count();
    }
}