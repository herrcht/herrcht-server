package herrcht.server.session.repository;

import herrcht.server.session.SessionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySessionRepositoryTest {

    private InMemorySessionRepository repository;
    private UUID userId;

    @BeforeEach
    void setUp() {
        repository = new InMemorySessionRepository();
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve salvar e recuperar uma sessão por ID")
    void shouldSaveAndFindById() {
        SessionEntity session = SessionEntity.builder()
                .userId(userId)
                .ipAddress("127.0.0.1")
                .userAgent("Flutter Client")
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        SessionEntity saved = repository.save(session);

        assertNotNull(saved.getId());
        Optional<SessionEntity> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(userId, found.get().getUserId());
    }

    @Test
    @DisplayName("Deve listar todas as sessões de um usuário específico")
    void shouldFindByUserId() {
        SessionEntity s1 = SessionEntity.builder().userId(userId).build();
        SessionEntity s2 = SessionEntity.builder().userId(userId).build();
        SessionEntity s3 = SessionEntity.builder().userId(UUID.randomUUID()).build();

        repository.save(s1);
        repository.save(s2);
        repository.save(s3);

        List<SessionEntity> userSessions = repository.findByUserId(userId);
        assertEquals(2, userSessions.size());
    }

    @Test
    @DisplayName("Deve contar apenas sessões válidas (não revogadas e não expiradas)")
    void shouldCountActiveSessions() {
        SessionEntity active = SessionEntity.builder()
                .userId(userId)
                .revoked(false)
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        SessionEntity revoked = SessionEntity.builder()
                .userId(userId)
                .revoked(true)
                .expiresAt(OffsetDateTime.now().plusDays(1))
                .build();

        SessionEntity expired = SessionEntity.builder()
                .userId(userId)
                .revoked(false)
                .expiresAt(OffsetDateTime.now().minusDays(1))
                .build();

        repository.save(active);
        repository.save(revoked);
        repository.save(expired);

        long count = repository.countByUserIdAndRevokedFalse(userId);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Deve deletar todas as sessões de um usuário")
    void shouldDeleteAllByUserId() {
        repository.save(SessionEntity.builder().userId(userId).build());
        repository.save(SessionEntity.builder().userId(userId).build());

        repository.deleteAllByUserId(userId);

        assertTrue(repository.findByUserId(userId).isEmpty());
    }
}