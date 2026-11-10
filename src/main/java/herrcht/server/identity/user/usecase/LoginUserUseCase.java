package herrcht.server.identity.user.usecase;

import herrcht.server.identity.user.UserEntity;
import herrcht.server.identity.user.UserService;
import herrcht.server.identity.user.dto.LoginRequest;
import herrcht.server.identity.user.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserService userService;

    public LoginResponse execute(LoginRequest request) {
        UserEntity user = userService.authenticate(request.username(), request.password());

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                "Autenticação realizada com sucesso! (Modo Dummy)"
        );
    }
}