package herrcht.server.identity.user;

import herrcht.server.identity.recovery.UserEmailRepository;
import herrcht.server.identity.user.dto.LoginResponse;
import herrcht.server.identity.user.exception.EmailAlreadyExistsException;
import herrcht.server.identity.user.exception.InvalidCredentialsException;
import herrcht.server.identity.user.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Valida a disponibilidade do username e email, codifica a senha do usuário
     * e realiza a persistência atômica no banco de dados.
     */
    @Transactional
    public UserEntity registerUser(UserEntity user) {
        validateUsernameAvailability(user.getUsername());
        validateEmailAvailability(user);

        // Gera o hash seguro da senha antes de persistir
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Autentica o usuário validando a existência do username e a correspondência do hash da senha.
     * Retorna o LoginResponse contendo o accessToken dummy e sessionToken nulo.
     */
    @Transactional(readOnly = true)
    public LoginResponse authenticate(String username, String rawPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Stub/Dummy de token de acesso temporário enquanto o módulo de sessão/JWT é finalizado
        String dummyAccessToken = "dummy-access-token-" + user.getId();

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                dummyAccessToken,
                null
        );
    }

    private void validateUsernameAvailability(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void validateEmailAvailability(UserEntity user) {
        if (user.getEmails() == null || user.getEmails().isEmpty()) {
            return;
        }

        for (var userEmail : user.getEmails()) {
            if (userEmailRepository.existsByEmail(userEmail.getEmail())) {
                throw new EmailAlreadyExistsException(userEmail.getEmail());
            }
        }
    }
}