package com.project.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UniqueIndexMigrationRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueIndexMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public UniqueIndexMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        migrateUniqueIndex(
                "cart_items",
                "cart_id,product_id",
                "cart_id,product_id,size",
                "uk_cart_items_cart_product_size");
        migrateUniqueIndex(
                "order_details",
                "order_id,product_id",
                "order_id,product_id,size",
                "uk_order_details_order_product_size");
    }

    private void migrateUniqueIndex(
            String tableName,
            String legacyColumns,
            String desiredColumns,
            String desiredIndexName) {
        if (!tableExists(tableName) || !columnExists(tableName, "size")) {
            return;
        }

        List<UniqueIndexMetadata> indexes = loadUniqueIndexes(tableName);

        for (UniqueIndexMetadata index : indexes) {
            boolean isLegacyIndex = legacyColumns.equals(index.columns());
            boolean isWrongDesiredIndexName =
                    desiredIndexName.equalsIgnoreCase(index.name()) && !desiredColumns.equals(index.columns());

            if (!"PRIMARY".equalsIgnoreCase(index.name()) && (isLegacyIndex || isWrongDesiredIndexName)) {
                LOGGER.info("Dropping legacy unique index {} on table {}", index.name(), tableName);
                jdbcTemplate.execute("DROP INDEX `" + index.name() + "` ON `" + tableName + "`");
            }
        }

        boolean desiredIndexExists = loadUniqueIndexes(tableName).stream()
                .anyMatch(index -> desiredColumns.equals(index.columns()));

        if (desiredIndexExists) {
            return;
        }

        LOGGER.info("Creating unique index {} on table {}", desiredIndexName, tableName);
        jdbcTemplate.execute(
                "CREATE UNIQUE INDEX `" + desiredIndexName + "` ON `" + tableName + "` ("
                        + desiredColumns.replace(",", ", ")
                        + ")");
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName);
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName);
        return count != null && count > 0;
    }

    private List<UniqueIndexMetadata> loadUniqueIndexes(String tableName) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                SELECT index_name,
                       GROUP_CONCAT(column_name ORDER BY seq_in_index SEPARATOR ',') AS columns_list
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND non_unique = 0
                GROUP BY index_name
                """,
                tableName);

        return rows.stream()
                .map(row -> new UniqueIndexMetadata(
                        String.valueOf(row.get("index_name")),
                        String.valueOf(row.get("columns_list"))))
                .toList();
    }

    private record UniqueIndexMetadata(String name, String columns) {
    }
}
