package herrcht.server.identity.user.validation;

import herrcht.server.shared.config.EmailRecoveryMode;
import herrcht.server.shared.config.ServerSecurityProperties;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailRecoveryValidatorTest {

    @Mock
    private ServerSecurityProperties securityProperties;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @InjectMocks
    private EmailRecoveryValidator validator;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    }

    @Nested
    @DisplayName("Cenários quando email-recovery-mode é DISABLED")
    class ModeDisabledTests {

        @BeforeEach
        void init() {
            when(securityProperties.getEmailRecoveryMode()).thenReturn(EmailRecoveryMode.DISABLED);
        }

        @Test
        @DisplayName("Deve ser válido quando o e-mail for nulo")
        void shouldBeValidWhenNull() {
            assertTrue(validator.isValid(null, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t"})
        @DisplayName("Deve ser válido quando o e-mail estiver em branco")
        void shouldBeValidWhenBlank(String email) {
            assertTrue(validator.isValid(email, context));
        }

        @Test
        @DisplayName("Deve ser inválido se algum e-mail for fornecido")
        void shouldBeInvalidWhenEmailIsProvided() {
            assertFalse(validator.isValid("user@example.com", context));
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("A recuperação por e-mail está desativada nesta instância. O campo deve estar vazio.");
        }
    }

    @Nested
    @DisplayName("Cenários quando email-recovery-mode é REQUIRED")
    class ModeRequiredTests {

        @BeforeEach
        void init() {
            when(securityProperties.getEmailRecoveryMode()).thenReturn(EmailRecoveryMode.REQUIRED);
        }

        @Test
        @DisplayName("Deve ser inválido quando o e-mail for nulo")
        void shouldBeInvalidWhenNull() {
            assertFalse(validator.isValid(null, context));
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("O e-mail de recuperação é obrigatório nesta instância.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t"})
        @DisplayName("Deve ser inválido quando o e-mail estiver em branco")
        void shouldBeInvalidWhenBlank(String email) {
            assertFalse(validator.isValid(email, context));
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("O e-mail de recuperação é obrigatório nesta instância.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"user@example.com", "valid.email+alias@domain.co.uk", "player123@herrcht.io"})
        @DisplayName("Deve ser válido quando for fornecido um e-mail com formato correto")
        void shouldBeValidWhenFormatIsCorrect(String email) {
            assertTrue(validator.isValid(email, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-email", "user@.com", "user@domain", "@domain.com", "user space@domain.com"})
        @DisplayName("Deve ser inválido quando for fornecido um e-mail sintaticamente incorreto")
        void shouldBeInvalidWhenFormatIsIncorrect(String email) {
            assertFalse(validator.isValid(email, context));
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("O e-mail fornecido possui um formato inválido.");
        }
    }

    @Nested
    @DisplayName("Cenários quando email-recovery-mode é OPTIONAL")
    class ModeOptionalTests {

        @BeforeEach
        void init() {
            when(securityProperties.getEmailRecoveryMode()).thenReturn(EmailRecoveryMode.OPTIONAL);
        }

        @Test
        @DisplayName("Deve ser válido quando o e-mail for nulo")
        void shouldBeValidWhenNull() {
            assertTrue(validator.isValid(null, context));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t"})
        @DisplayName("Deve ser válido quando o e-mail estiver em branco")
        void shouldBeValidWhenBlank(String email) {
            assertTrue(validator.isValid(email, context));
        }

        @Test
        @DisplayName("Deve ser válido quando for fornecido um e-mail válido")
        void shouldBeValidWhenValidEmailIsProvided() {
            assertTrue(validator.isValid("optional@domain.com", context));
        }

        @Test
        @DisplayName("Deve ser inválido quando for fornecido um e-mail malformatado")
        void shouldBeInvalidWhenInvalidEmailIsProvided() {
            assertFalse(validator.isValid("bad-email", context));
            verify(context).disableDefaultConstraintViolation();
            verify(context).buildConstraintViolationWithTemplate("O e-mail fornecido possui um formato inválido.");
        }
    }
}