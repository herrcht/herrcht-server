package herrcht.server.session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    SessionEntity save(SessionEntity session);
    Optional<SessionEntity> findById(UUID id);
    List<SessionEntity> findByUserId(UUID userId);
    void deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
    long countByUserIdAndRevokedFalse(UUID userId);
}