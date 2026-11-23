package de.starwit.application.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoleMapperTest {

    private RoleMapper roleMapper;

    @BeforeEach
    void setUp() {
        roleMapper = new RoleMapper();
        roleMapper.adminRoleName = "ext_admin";
        roleMapper.userRoleName = "ext_user";
        roleMapper.readerRoleName = "ext_reader";
    }

    @Test
    void mapsConfiguredRoles() {
        assertEquals(List.of("admin", "user", "reader"),
                roleMapper.mapAllRoles(List.of("ext_admin", "ext_user", "ext_reader")));
    }

    @Test
    void passesThroughUnmappedRoles() {
        assertEquals(List.of("offline_access", "admin"),
                roleMapper.mapAllRoles(List.of("offline_access", "admin")));
    }

    @Test
    void handlesNull() {
        assertEquals(List.of(), roleMapper.mapAllRoles(null));
    }
}
