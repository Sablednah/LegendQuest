package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

/**
 * Inherited subclass for making a connection to a MySQL server.<br>
 * Date Created: 2011-08-26 19:08
 * 
 * @author PatPeter
 */
public class MySQL extends Database {

    public enum Statements implements StatementEnum {
        // Data manipulation statements
        SELECT("SELECT"),
        INSERT("INSERT"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        DO("DO"),
        REPLACE("REPLACE"),
        LOAD("LOAD"),
        HANDLER("HANDLER"),
        CALL("CALL"),

        // Data definition statements
        CREATE("CREATE"),
        ALTER("ALTER"),
        DROP("DROP"),
        TRUNCATE("TRUNCATE"),
        RENAME("RENAME"),

        // Transactional and Locking Statements
        START("START"),
        COMMIT("COMMIT"),
        SAVEPOINT("SAVEPOINT"), // http://dev.mysql.com/doc/refman/5.6/en/implicit-commit.html#savepoint
        ROLLBACK("ROLLBACK"), // ROLLBACK TO SAVEPOINT
        RELEASE("RELEASE"), // RELEASE SAVEPOINT
        LOCK("LOCK"), // http://dev.mysql.com/doc/refman/5.6/en/lock-tables.html
        UNLOCK("UNLOCK"),

        // Prepared Statements
        PREPARE("PREPARE"),
        EXECUTE("EXECUTE"),
        DEALLOCATE("DEALLOCATE"),

        // Database Administration
        SET("SET"),
        SHOW("SHOW"),

        // Utility Statements
        DESCRIBE("DESCRIBE"),
        EXPLAIN("EXPLAIN"),
        HELP("HELP"),
        USE("USE");

        private String string;

        private Statements(final String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    private final HostnameDatabase delegate = new HostnameDatabaseImpl();

    public MySQL(final Logger log,
            final String prefix,
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[MySQL] ");
        setHostname(hostname);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.MySQL;
    }

    public MySQL(final Logger log,
            final String prefix,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[MySQL] ");
        setHostname("localhost");
        setPort(3306);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.MySQL;
    }

    @Deprecated
    public boolean createTable(final String query) {
        Statement statement = null;
        if (query == null || query.equals("")) {
            writeError("Could not create table: query is empty or null.", true);
            return false;
        }

        try {
            statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (final SQLException e) {
            writeError("Could not create table, SQLException: " + e.getMessage(), true);
            return false;
        }
        return true;
    }

    public String getDatabase() {
        return delegate.getDatabase();
    }

    public String getHostname() {
        return delegate.getHostname();
    }

    private String getPassword() {
        return delegate.getPassword();
    }

    public int getPort() {
        return delegate.getPort();
    }

    @Override
    public Statements getStatement(final String query) throws SQLException {
        final String[] statement = query.trim().split(" ", 2);
        try {
            final Statements converted = Statements.valueOf(statement[0].toUpperCase());
            return converted;
        } catch (final IllegalArgumentException e) {
            throw new SQLException("Unknown statement: \"" + statement[0] + "\".");
        }
    }

    public String getUsername() {
        return delegate.getUsername();
    }

    @Override
    protected boolean initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch (final ClassNotFoundException e) {
            writeError("MySQL driver class missing: " + e.getMessage() + ".", true);
            return false;
        }
    }

    @Override
    public boolean isTable(final String table) {
        Statement statement;
        try {
            statement = connection.createStatement();
        } catch (final SQLException e) {
            writeError("Could not create a statement in checkTable(), SQLException: " + e.getMessage(), true);
            return false;
        }
        try {
            statement.executeQuery("SELECT * FROM " + table);
            return true; // Result can never be null, bad logic from earlier versions.
        } catch (final SQLException e) {
            return false; // Query failed, table does not exist.
        }
    }

    @Override
    public boolean open() {
        if (initialize()) {
            final String url = "jdbc:mysql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
            try {
                this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
                this.connected = true;
                return true;
            } catch (final SQLException e) {
                writeError("Could not establish a MySQL connection, SQLException: " + e.getMessage(), true);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void queryValidation(final StatementEnum statement) throws SQLException {
        switch ((Statements) statement) {
        case USE:
            writeError("Please create a new connection to use a different database.", false);
            throw new SQLException("Please create a new connection to use a different database.");

        case PREPARE:
        case EXECUTE:
        case DEALLOCATE:
            writeError("Please use the prepare() method to prepare a query.", false);
            throw new SQLException("Please use the prepare() method to prepare a query.");
        }
    }

    private void setDatabase(final String database) {
        delegate.setDatabase(database);
    }

    private void setHostname(final String hostname) {
        delegate.setHostname(hostname);
    }

    private void setPassword(final String password) {
        delegate.setPassword(password);
    }

    private void setPort(final int port) {
        delegate.setPort(port);
    }

    private void setUsername(final String username) {
        delegate.setUsername(username);
    }

    @Override
    public boolean truncate(final String table) {
        Statement statement = null;
        String query = null;
        try {
            if (!isTable(table)) {
                writeError("Table \"" + table + "\" does not exist.", true);
                return false;
            }
            statement = this.connection.createStatement();
            query = "DELETE FROM " + table + ";";
            statement.executeUpdate(query);
            statement.close();

            return true;
        } catch (final SQLException e) {
            writeError("Could not wipe table, SQLException: " + e.getMessage(), true);
            return false;
        }
    }
}