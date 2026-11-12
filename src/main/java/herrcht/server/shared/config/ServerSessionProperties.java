package herrcht.server.shared.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "herrcht.session")
@Validated
@Getter
@Setter
public class ServerSessionProperties {

    /**
     * Tipo de armazenamento de sessão: NONE, IN_MEMORY, POSTGRESQL ou REDIS.
     */
    @NotNull(message = "O tipo de armazenamento de sessão (store-type) deve ser definido.")
    private SessionStoreType storeType = SessionStoreType.IN_MEMORY;

    /**
     * Tempo de vida da sessão (Session TTL) em dias.
     * Mínimo: 1 dia | Máximo: 180 dias (~6 meses).
     */
    @Min(value = 1, message = "O session-ttl mínimo é de 1 dia.")
    @Max(value = 180, message = "O session-ttl máximo é de 180 dias (~6 meses).")
    private int sessionTtlDays = 30;

    /**
     * Tempo de vida do token de acesso (Access TTL) em minutos.
     * Mínimo: 5 minutos | Máximo: 240 minutos (4 horas).
     */
    @Min(value = 5, message = "O access-ttl mínimo é de 5 minutos.")
    @Max(value = 240, message = "O access-ttl máximo é de 240 minutos (4 horas).")
    private int accessTtlMinutes = 30;

    /**
     * Retorna o Session TTL como um Duration.
     */
    public Duration getSessionTtlDuration() {
        return Duration.ofDays(sessionTtlDays);
    }

    /**
     * Retorna o Access TTL efetivo.
     * Se storeType for NONE, o tempo máximo de acesso é reduzido para no máximo 30 minutos.
     */
    public Duration getEffectiveAccessTtlDuration() {
        int effectiveMinutes = accessTtlMinutes;

        if (storeType == SessionStoreType.NONE && effectiveMinutes > 30) {
            effectiveMinutes = 30;
        }

        return Duration.ofMinutes(effectiveMinutes);
    }
}