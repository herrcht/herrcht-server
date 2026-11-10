package herrcht.server.shared.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "herrcht.security")
@Validated
@Getter
@Setter
public class ServerSecurityProperties {

    /**
     * Modo do recurso de recuperação por e-mail: DISABLED, OPTIONAL ou REQUIRED.
     */
    @NotNull(message = "O modo de recuperação por e-mail (email-recovery-mode) deve ser definido.")
    private EmailRecoveryMode emailRecoveryMode = EmailRecoveryMode.OPTIONAL;

    /**
     * Limite máximo de perfis por usuário. 0 representa ilimitado.
     */
    @Min(value = 0, message = "O limite de perfis por usuário não pode ser negativo.")
    private int maxProfilesPerUser = 5;

    /**
     * Limite máximo de e-mails correlacionados por usuário. 0 representa ilimitado.
     */
    @Min(value = 0, message = "O limite de e-mails por usuário não pode ser negativo.")
    private int maxEmailsPerUser = 3;

    /**
     * Retorna o limite efetivo de e-mails, forçando 0 caso a recuperação esteja desativada (DISABLED).
     */
    public int getEffectiveMaxEmailsPerUser() {
        if (emailRecoveryMode == EmailRecoveryMode.DISABLED) {
            return 0;
        }
        return maxEmailsPerUser;
    }
}