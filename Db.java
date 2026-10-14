package com.adaptivequiz.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** JDBC connection helper + first-run database setup. */
public final class Db {
    /** Database file is created at ./data/adaptivequiz.mv.db. Override with -Dquiz.db=... */
    private static final String URL = System.getProperty("quiz.db", "jdbc:h2:file:./data/adaptivequiz");

    private Db() { }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    /** Creates tables (if missing) and loads sample data the first time. */
    public static void init() {
        try (Connection c = get(); Statement st = c.createStatement()) {
            runScript(st, "/db/schema.sql");
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM subject")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    runScript(st, "/db/seed.sql");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Database initialisation failed: " + e.getMessage(), e);
        }
    }

    private static void runScript(Statement st, String resource) throws Exception {
        try (InputStream in = Db.class.getResourceAsStream(resource)) {
            if (in == null) throw new IllegalStateException("Missing resource " + resource);
            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : raw.split("\n")) {
                if (!line.trim().startsWith("--")) sb.append(line).append('\n');
            }
            for (String stmt : sb.toString().split(";")) {
                if (!stmt.isBlank()) st.execute(stmt.trim());
            }
        }
    }
}
