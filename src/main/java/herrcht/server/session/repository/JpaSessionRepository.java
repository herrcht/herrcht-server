package herrcht.server.session.repository;

import herrcht.server.session.JpaSessionEntity;
import herrcht.server.session.SessionEntity;
import herrcht.server.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "herrcht.session.store-type",
        havingValue = "POSTGRESQL"
)
@RequiredArgsConstructor
public class JpaSessionRepository implements SessionRepository {

    private final SpringDataJpaSessionRepository jpaRepository;

    @Override
    public SessionEntity save(SessionEntity session) {
        JpaSessionEntity entity = toEntity(session);
        JpaSessionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SessionEntity> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SessionEntity> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        jpaRepository.deleteAllByUserId(userId);
    }

    @Override
    public long countByUserIdAndRevokedFalse(UUID userId) {
        return jpaRepository.countActiveSessions(userId, OffsetDateTime.now());
    }

    private JpaSessionEntity toEntity(SessionEntity domain) {
        return JpaSessionEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .refreshTokenHash(domain.getRefreshTokenHash())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .revoked(domain.isRevoked())
                .createdAt(domain.getCreatedAt())
                .lastActiveAt(domain.getLastActiveAt())
                .expiresAt(domain.getExpiresAt())
                .build();
    }

    private SessionEntity toDomain(JpaSessionEntity entity) {
        return SessionEntity.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .refreshTokenHash(entity.getRefreshTokenHash())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .lastActiveAt(entity.getLastActiveAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}