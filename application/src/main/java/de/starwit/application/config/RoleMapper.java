package de.starwit.application.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Maps role names coming from the identity provider to the internal role
 * structure shared by all applications: admin, user, reader.
 * Roles that do not match any configured name are passed through unchanged.
 */
@Component
public class RoleMapper {

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String READER = "reader";

    @Value("${security.rolemapping.admin:observatoryconfig_admin}")
    String adminRoleName;

    @Value("${security.rolemapping.user:observatoryconfig_user}")
    String userRoleName;

    @Value("${security.rolemapping.reader:observatoryconfig_reader}")
    String readerRoleName;

    public List<String> mapAllRoles(List<String> externalRoles) {
        if (externalRoles == null) {
            return List.of();
        }
        return externalRoles.stream().map(this::mapRole).toList();
    }

    private String mapRole(String externalRole) {
        if (externalRole.equals(adminRoleName)) {
            return ADMIN;
        } else if (externalRole.equals(userRoleName)) {
            return USER;
        } else if (externalRole.equals(readerRoleName)) {
            return READER;
        } else {
            return externalRole;
        }
    }
}
