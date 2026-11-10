package herrcht.server.identity.user.usecase;

import herrcht.server.identity.recovery.UserEmailEntity;
import herrcht.server.identity.user.UserEntity;
import herrcht.server.identity.user.UserService;
import herrcht.server.identity.user.dto.RegisterUserRequest;
import herrcht.server.identity.user.dto.RegisterUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserService userService;

    @Transactional
    public RegisterUserResponse execute(RegisterUserRequest request) {
        // 1. Converte DTO para Entity base
        UserEntity userEntity = UserEntity.builder()
                .username(request.username())
                .password(request.password())
                .emails(new ArrayList<>())
                .build();

        // 2. Se o e-mail estiver presente (preenchido), associa como e-mail primário
        if (request.email() != null && !request.email().isBlank()) {
            UserEmailEntity userEmail = UserEmailEntity.builder()
                    .user(userEntity)
                    .email(request.email().trim())
                    .primary(true)
                    .verified(false)
                    .build();

            userEntity.getEmails().add(userEmail);
        }

        // 3. Invoca a lógica de domínio para validar disponibilidade, codificar a senha e salvar
        UserEntity savedUser = userService.registerUser(userEntity);

        // 4. Retorna a resposta com os dados da conta criada
        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "Usuário cadastrado com sucesso!"
        );
    }
}