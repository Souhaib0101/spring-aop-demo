package net.montaki.springaopdemo.security;

import java.util.Set;

/** Contexte pedagogique local au thread ; les identifiants sont ceux de la demonstration. */
public final class SecurityContext {
    private static final ThreadLocal<Identity> CURRENT_USER = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static void authenticate(String username, String password) {
        clear();
        if (!"1234".equals(password)) {
            throw new SecurityException("Authentification refusee");
        }
        Set<String> roles = switch (username == null ? "" : username) {
            case "root" -> Set.of("USER", "ADMIN");
            case "user" -> Set.of("USER");
            default -> throw new SecurityException("Authentification refusee");
        };
        CURRENT_USER.set(new Identity(username, roles));
    }

    public static boolean isAuthenticated() {
        return CURRENT_USER.get() != null;
    }

    public static boolean hasRole(String role) {
        Identity identity = CURRENT_USER.get();
        return identity != null && identity.roles().contains(role);
    }

    public static String getUsername() {
        Identity identity = CURRENT_USER.get();
        return identity == null ? "anonymous" : identity.username();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    private record Identity(String username, Set<String> roles) {
    }
}
