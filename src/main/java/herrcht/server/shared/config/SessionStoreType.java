package herrcht.server.shared.config;

public enum SessionStoreType {
    NONE,        // Sem sessão no servidor (Pure Stateless Access Token)
    IN_MEMORY,   // Guarda sessões na RAM
    DATABASE,    // Guarda sessões no banco relacional do jogo (PostgreSQL, MySQL, MariaDB)
    REDIS        // Guarda sessões no Redis
}