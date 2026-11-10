package herrcht.server.identity.user.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Nome de usuário ou senha inválidos.");
    }
}