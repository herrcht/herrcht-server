package herrcht.server.identity.user.usecase;

import herrcht.server.identity.user.UserEntity;
import herrcht.server.identity.user.UserService;
import herrcht.server.identity.user.dto.LoginRequest;
import herrcht.server.identity.user.dto.LoginResponse;
import herrcht.server.identity.user.exception.InvalidCredentialsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar LoginResponse")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("player1", "password123");
        UUID userId = UUID.randomUUID();

        UserEntity userMock = UserEntity.builder()
                .id(userId)
                .username("player1")
                .password("hashedPassword")
                .build();

        when(userService.authenticate("player1", "password123")).thenReturn(userMock);

        LoginResponse response = loginUserUseCase.execute(request);

        assertNotNull(response);
        assertEquals(userId, response.userId());
        assertEquals("player1", response.username());
        assertEquals("Autenticação realizada com sucesso! (Modo Dummy)", response.message());

        verify(userService).authenticate("player1", "password123");
    }

    @Test
    @DisplayName("Deve propagar InvalidCredentialsException quando a autenticação falhar")
    void shouldPropagateExceptionWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest("player1", "wrongPassword");

        when(userService.authenticate("player1", "wrongPassword"))
                .thenThrow(new InvalidCredentialsException());

        assertThrows(InvalidCredentialsException.class, () -> loginUserUseCase.execute(request));

        verify(userService).authenticate("player1", "wrongPassword");
    }
}