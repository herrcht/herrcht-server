package herrcht.server.identity.user.dto;

import java.util.UUID;

public record LoginResponse(
        UUID userId,
        String username,
        String message
) {}