package herrcht.server.identity.user;

import herrcht.server.identity.recovery.UserEmailRepository;
import herrcht.server.identity.user.exception.EmailAlreadyExistsException;
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