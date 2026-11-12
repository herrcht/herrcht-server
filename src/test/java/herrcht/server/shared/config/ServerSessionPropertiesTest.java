package herrcht.server.shared.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerSessionPropertiesTest {

    private ServerSessionProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ServerSessionProperties();
    }

    @Test
    @DisplayName("Deve converter sessionTtlDays para Duration corretamente")
    void shouldConvertSessionTtlToDuration() {
        properties.setSessionTtlDays(45);
        assertEquals(Duration.ofDays(45), properties.getSessionTtlDuration());
    }

    @ParameterizedTest
    @EnumSource(value = SessionStoreType.class, names = {"IN_MEMORY", "POSTGRESQL", "REDIS"})
    @DisplayName("Deve manter o accessTtlMinutes original quando storeType for diferente de NONE")
    void shouldKeepAccessTtlWhenStoreTypeIsNotNone(SessionStoreType storeType) {
        properties.setStoreType(storeType);
        properties.setAccessTtlMinutes(120);

        assertEquals(Duration.ofMinutes(120), properties.getEffectiveAccessTtlDuration());
    }

    @Test
    @DisplayName("Deve reduzir accessTtlMinutes para no máximo 30 minutos quando storeType for NONE e a configuração for maior que 30")
    void shouldCapAccessTtlTo30MinutesWhenStoreTypeIsNoneAndConfigIsHigher() {
        properties.setStoreType(SessionStoreType.NONE);
        properties.setAccessTtlMinutes(120);

        assertEquals(Duration.ofMinutes(30), properties.getEffectiveAccessTtlDuration());
    }

    @Test
    @DisplayName("Deve manter accessTtlMinutes se for menor que 30 minutos mesmo quando storeType for NONE")
    void shouldKeepAccessTtlWhenStoreTypeIsNoneAndConfigIsLowerThan30() {
        properties.setStoreType(SessionStoreType.NONE);
        properties.setAccessTtlMinutes(15);

        assertEquals(Duration.ofMinutes(15), properties.getEffectiveAccessTtlDuration());
    }
}