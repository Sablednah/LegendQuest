package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

/**
 * Child class for the PostgreSQL database.<br>
 * Date Created: 2011-09-03 17:18.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class PostgreSQL extends Database {

    /**
     * http://www.postgresql.org/docs/7.3/static/sql-commands.html
     */
    protected enum Statements implements StatementEnum {
        ABORT("ABORT"),
        ALERT("ALERT"),
        ANALYZE("ANALYZE"),
        BEGIN("BEGIN"),
        CHECKPOINT("CHECKPOINT"),
        CLOSE("CLOSE"),
        CLUSTER("CLUSTER"),
        COMMENT("COMMENT"),
        COMMIT("COMMIT"),
        COPY("COPY"),
        CREATE("CREATE"),
        DEALLOCATE("DEALLOCATE"),
        DECLARE("DECLARE"),
        DELETE("DELETE"),
        DROP("DROP"),
        END("END"),
        EXECUTE("EXECUTE"),
        EXPLAIN("EXPLAIN"),
        FETCH("FETCH"),
        GRANT("GRANT"),
        INSERT("INSERT"),
        LISTEN("LISTEN"),
        LOAD("LOAD"),
        LOCK("LOCK"),
        MOVE("MOVE"),
        NOTIFY("NOTIFY"),
        PREPARE("PREPARE"),
        REINDEX("REINDEX"),
        RESET("RESET"),
        REVOKE("REVOKE"),
        ROLLBACK("ROLLBACK"),
        SELECT("SELECT"),
        SET("SET"),
        SHOW("SHOW"),
        START("START"),
        TRUNCATE("TRUNCATE"),
        UNLISTEN("UNLISTEN"),
        UPDATE("UPDATE"),
        VACUUM("VACUUM");

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

    public PostgreSQL(final Logger log,
            final String prefix,
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[PostgreSQL] ");
        setHostname(hostname);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.PostgreSQL;
    }

    public PostgreSQL(final Logger log,
            final String prefix,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[PostgreSQL] ");
        setHostname("localhost");
        setPort(1433);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.PostgreSQL;
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
    public boolean initialize() {
        try {
            Class.forName("org.postgresql.Driver");
            return true;
        } catch (final ClassNotFoundException e) {
            writeError("PostgreSQL driver class missing: " + e.getMessage() + ".", true);
            return false;
        }
    }

    @Override
    public boolean isTable(final String table) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean open() {
        if (initialize()) {
            String url = "";
            url = "jdbc:postgresql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
            try {
                this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
                this.connected = true;
                return true;
            } catch (final SQLException e) {
                writeError("Could not establish a PostgreSQL connection, SQLException: " + e.getMessage(), true);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void queryValidation(final StatementEnum statement) throws SQLException {
        switch ((Statements) statement) {
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
        throw new UnsupportedOperationException();
    }
}
