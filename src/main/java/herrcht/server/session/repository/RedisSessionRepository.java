package herrcht.server.session.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import herrcht.server.session.SessionEntity;
import herrcht.server.session.SessionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "herrcht.session.store-type",
        havingValue = "REDIS"
)
public class RedisSessionRepository implements SessionRepository {

    private static final String KEY_PREFIX = "herrcht:session:";
    private static final String USER_INDEX_PREFIX = "herrcht:user-sessions:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSessionRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public SessionEntity save(SessionEntity session) {
        if (session.getId() == null) {
            session.setId(UUID.randomUUID());
        }

        String sessionKey = KEY_PREFIX + session.getId();
        String userIndexKey = USER_INDEX_PREFIX + session.getUserId();

        try {
            String json = objectMapper.writeValueAsString(session);

            // Calcula o tempo restante para o Redis expirar a chave automaticamente (TTL)
            Duration ttl = Duration.between(OffsetDateTime.now(), session.getExpiresAt());
            if (ttl.isNegative() || ttl.isZero()) {
                ttl = Duration.ofSeconds(1);
            }

            // Salva a sessão com TTL
            redisTemplate.opsForValue().set(sessionKey, json, ttl);

            // Indexa o ID da sessão no Set do Usuário
            redisTemplate.opsForSet().add(userIndexKey, session.getId().toString());
            redisTemplate.expire(userIndexKey, ttl);

            return session;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar a sessão para o Redis", e);
        }
    }

    @Override
    public Optional<SessionEntity> findById(UUID id) {
        String sessionKey = KEY_PREFIX + id;
        String json = redisTemplate.opsForValue().get(sessionKey);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, SessionEntity.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<SessionEntity> findByUserId(UUID userId) {
        String userIndexKey = USER_INDEX_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userIndexKey);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return List.of();
        }

        List<SessionEntity> list = new ArrayList<>();
        for (String idStr : sessionIds) {
            findById(UUID.fromString(idStr)).ifPresent(list::add);
        }
        return list;
    }

    @Override
    public void deleteById(UUID id) {
        Optional<SessionEntity> sessionOpt = findById(id);
        if (sessionOpt.isPresent()) {
            SessionEntity session = sessionOpt.get();
            String sessionKey = KEY_PREFIX + id;
            String userIndexKey = USER_INDEX_PREFIX + session.getUserId();

            redisTemplate.delete(sessionKey);
            redisTemplate.opsForSet().remove(userIndexKey, id.toString());
        }
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        List<SessionEntity> userSessions = findByUserId(userId);
        for (SessionEntity session : userSessions) {
            deleteById(session.getId());
        }
        redisTemplate.delete(USER_INDEX_PREFIX + userId);
    }

    @Override
    public long countByUserIdAndRevokedFalse(UUID userId) {
        return findByUserId(userId).stream()
                .filter(session -> !session.isRevoked() && !session.isExpired())
                .count();
    }
}