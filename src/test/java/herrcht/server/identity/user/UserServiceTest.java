package herrcht.server.identity.user;

import herrcht.server.identity.recovery.UserEmailEntity;
import herrcht.server.identity.recovery.UserEmailRepository;
import herrcht.server.identity.user.exception.EmailAlreadyExistsException;
import herrcht.server.identity.user.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEmailRepository userEmailRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .username("player1")
                .password("plainTextPassword")
                .build();
    }

    @Test
    @DisplayName("Deve lançar exceção se o username já estiver em uso")
    void shouldThrowExceptionWhenUsernameExists() {
        when(userRepository.existsByUsername("player1")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(userEntity));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se o e-mail já estiver cadastrado")
    void shouldThrowExceptionWhenEmailExists() {
        UserEmailEntity emailEntity = UserEmailEntity.builder()
                .email("player1@herrcht.io")
                .user(userEntity)
                .build();
        userEntity.setEmails(List.of(emailEntity));

        when(userRepository.existsByUsername("player1")).thenReturn(false);
        when(userEmailRepository.existsByEmail("player1@herrcht.io")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(userEntity));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso e encriptar a senha")
    void shouldRegisterUserSuccessfully() {
        UserEmailEntity emailEntity = UserEmailEntity.builder()
                .email("player1@herrcht.io")
                .user(userEntity)
                .build();
        userEntity.setEmails(List.of(emailEntity));

        when(userRepository.existsByUsername("player1")).thenReturn(false);
        when(userEmailRepository.existsByEmail("player1@herrcht.io")).thenReturn(false);
        when(passwordEncoder.encode("plainTextPassword")).thenReturn("$2a$10$hashedPasswordSample");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity savedUser = userService.registerUser(userEntity);

        assertNotNull(savedUser);
        assertEquals("$2a$10$hashedPasswordSample", savedUser.getPassword());
        verify(passwordEncoder).encode("plainTextPassword");
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso quando a lista de e-mails for nula")
    void shouldRegisterUserSuccessfullyWhenEmailsIsNull() {
        userEntity.setEmails(null);

        when(userRepository.existsByUsername("player1")).thenReturn(false);
        when(passwordEncoder.encode("plainTextPassword")).thenReturn("$2a$10$hashedPasswordSample");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity savedUser = userService.registerUser(userEntity);

        assertNotNull(savedUser);
        verify(userEmailRepository, never()).existsByEmail(any());
        verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso quando a lista de e-mails estiver vazia")
    void shouldRegisterUserSuccessfullyWhenEmailsIsEmpty() {
        userEntity.setEmails(Collections.emptyList());

        when(userRepository.existsByUsername("player1")).thenReturn(false);
        when(passwordEncoder.encode("plainTextPassword")).thenReturn("$2a$10$hashedPasswordSample");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity savedUser = userService.registerUser(userEntity);

        assertNotNull(savedUser);
        verify(userEmailRepository, never()).existsByEmail(any());
        verify(userRepository).save(userEntity);
    }
}