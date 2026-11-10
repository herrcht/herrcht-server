package herrcht.server.identity.user.dto;

import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String username,
        String message
) {}