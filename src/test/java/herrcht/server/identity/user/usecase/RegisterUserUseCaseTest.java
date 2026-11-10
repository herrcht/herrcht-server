package herrcht.server.identity.user.usecase;

import herrcht.server.identity.user.UserEntity;
import herrcht.server.identity.user.UserService;
import herrcht.server.identity.user.dto.RegisterUserRequest;
import herrcht.server.identity.user.dto.RegisterUserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Branch 1: Deve converter o DTO para entidade sem e-mail quando o e-mail for nulo")
    void shouldExecuteRegistrationWhenEmailIsNull() {
        RegisterUserRequest request = new RegisterUserRequest("player1", "password123", null);
        UUID generatedId = UUID.randomUUID();

        UserEntity savedMock = UserEntity.builder()
                .id(generatedId)
                .username("player1")
                .password("hashedPassword")
                .build();

        when(userService.registerUser(any(UserEntity.class))).thenReturn(savedMock);

        RegisterUserResponse response = registerUserUseCase.execute(request);

        assertNotNull(response);
        assertEquals(generatedId, response.id());
        assertEquals("player1", response.username());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).registerUser(captor.capture());
        assertTrue(captor.getValue().getEmails().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Branch 3 e 4: Deve converter o DTO para entidade sem e-mail quando o e-mail estiver em branco ou vazio")
    void shouldExecuteRegistrationWhenEmailIsBlankOrEmpty(String blankEmail) {
        RegisterUserRequest request = new RegisterUserRequest("player1", "password123", blankEmail);
        UUID generatedId = UUID.randomUUID();

        UserEntity savedMock = UserEntity.builder()
                .id(generatedId)
                .username("player1")
                .password("hashedPassword")
                .build();

        when(userService.registerUser(any(UserEntity.class))).thenReturn(savedMock);

        RegisterUserResponse response = registerUserUseCase.execute(request);

        assertNotNull(response);
        assertEquals(generatedId, response.id());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).registerUser(captor.capture());
        assertTrue(captor.getValue().getEmails().isEmpty());
    }

    @Test
    @DisplayName("Branch 2: Deve converter o DTO para entidade com e-mail primário quando o e-mail for válido e preenchido")
    void shouldExecuteRegistrationWithEmail() {
        RegisterUserRequest request = new RegisterUserRequest("player2", "password123", "player2@herrcht.io");
        UUID generatedId = UUID.randomUUID();

        UserEntity savedMock = UserEntity.builder()
                .id(generatedId)
                .username("player2")
                .password("hashedPassword")
                .build();

        when(userService.registerUser(any(UserEntity.class))).thenReturn(savedMock);

        RegisterUserResponse response = registerUserUseCase.execute(request);

        assertNotNull(response);
        assertEquals(generatedId, response.id());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userService).registerUser(captor.capture());

        UserEntity capturedUser = captor.getValue();
        assertEquals(1, capturedUser.getEmails().size());
        assertEquals("player2@herrcht.io", capturedUser.getEmails().get(0).getEmail());
        assertTrue(capturedUser.getEmails().get(0).isPrimary());
    }
}