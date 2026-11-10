package herrcht.server.identity.user.validation;

import herrcht.server.shared.config.EmailRecoveryMode;
import herrcht.server.shared.config.ServerSecurityProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailRecoveryValidator implements ConstraintValidator<ValidEmailRecovery, String> {

    private final ServerSecurityProperties securityProperties;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        EmailRecoveryMode mode = securityProperties.getEmailRecoveryMode();
        boolean isBlank = email == null || email.trim().isEmpty();

        return switch (mode) {
            case DISABLED -> {
                if (!isBlank) {
                    buildCustomViolation(context, "A recuperação por e-mail está desativada nesta instância. O campo deve estar vazio.");
                    yield false;
                }
                yield true;
            }
            case REQUIRED -> {
                if (isBlank) {
                    buildCustomViolation(context, "O e-mail de recuperação é obrigatório nesta instância.");
                    yield false;
                }
                yield validateEmailFormat(email, context);
            }
            case OPTIONAL -> {
                if (isBlank) {
                    yield true;
                }
                yield validateEmailFormat(email, context);
            }
        };
    }

    private boolean validateEmailFormat(String email, ConstraintValidatorContext context) {
        boolean valid = EmailValidator.getInstance().isValid(email);
        if (!valid) {
            buildCustomViolation(context, "O e-mail fornecido possui um formato inválido.");
        }
        return valid;
    }

    private void buildCustomViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}