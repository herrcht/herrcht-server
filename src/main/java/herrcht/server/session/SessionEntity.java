package herrcht.server.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEntity {

    private UUID id;
    private UUID userId;
    private String refreshTokenHash;
    private String ipAddress;
    private String userAgent;

    @Builder.Default
    private boolean revoked = false;

    private OffsetDateTime createdAt;
    private OffsetDateTime lastActiveAt;
    private OffsetDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt != null && OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}