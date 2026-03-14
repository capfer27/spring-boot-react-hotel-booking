package com.capfer.hotel.booking;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MigrationTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:18.3-alpine"));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Order(1)
    @Test
    @DisplayName(value = "The PostgreSQL container is created and it's up and running...")
    void test_container_is_running() {
        Assertions.assertTrue(postgres.isCreated(), "PostgreSQL container was not created");
        Assertions.assertTrue(postgres.isRunning(), "PostgreSQL container is not running");
    }

    @Order(2)
    @Test
    void extensionAndIndexShouldExist() {
        // Verify extension
        Boolean extensionExists = jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM pg_extension WHERE extname = 'pg_trgm'", Boolean.class);

        // Verify index
        Boolean indexExists = jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM pg_indexes WHERE indexname = 'idx_room_global_search'", Boolean.class);

        assertThat(extensionExists).isTrue();
        assertThat(indexExists).isTrue();
    }

    @Order(3)
    @Test
    void gistIndexExtensionAndIndexShouldExist() {
        // Verify extension
        Boolean extensionExists = jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM pg_extension WHERE extname = 'btree_gist'", Boolean.class);

        // Verify index
        Boolean indexExists = jdbcTemplate.queryForObject(
                "SELECT count(*) > 0 FROM pg_indexes WHERE indexname = 'idx_room_booking_overlap'", Boolean.class);

        assertThat(extensionExists).isTrue();
        assertThat(indexExists).isTrue();
    }
}

