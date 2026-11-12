package herrcht.server.identity.user.usecase;

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
        return userService.authenticate(request.username(), request.password());
    }
}