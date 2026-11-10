package herrcht.server.identity.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("O nome de usuário '" + username + "' já está em uso.");
    }
}