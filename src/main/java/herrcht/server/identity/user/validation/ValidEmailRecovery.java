package herrcht.server.identity.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailRecoveryValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmailRecovery {

    String message() default "Formato ou presença do e-mail inválido para a política configurada no servidor.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}