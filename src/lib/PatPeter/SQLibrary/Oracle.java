package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

/**
 * Child class for the Oracle database.<br>
 * Date Created: 2011-08-27 17:03.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Oracle extends Database {

    /**
     * http://docs.oracle.com/html/A95915_01/sqcmd.htm
     */
    protected enum Statements implements StatementEnum {
        ALTER("ALTER"),
        CREATE("CREATE"),
        DROP("DROP"),
        GRANT("GRANT"),
        REVOKE("REVOKE"),
        TRUNCATE("TRUNCATE"),
        DELETE("DELETE"),
        EXPLAIN("EXPLAIN"),
        INSERT("INSERT"),
        SELECT("SELECT"),
        UPDATE("UPDATE"),
        COMMIT("COMMIT"),
        ROLLBACK("ROLLBACK"),
        SET("SET"),
        CONSTRAINT("CONSTRAINT"),
        CURRVAL("CURRVAL"),
        NEXTVAL("NEXTVAL"),
        ROWNUM("ROWNUM"),
        LEVEL("LEVEL"),
        OL_ROW_STATUS("OL_ROW_STATUS"),
        ROWID("ROWID");

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

    public Oracle(final Logger log,
            final String prefix,
            final int port,
            final String database,
            final String username,
            final String password) throws SQLException {
        super(log, prefix, "[Oracle] ");
        setHostname("localhost");
        setPort(1521);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.Oracle;
    }

    public Oracle(final Logger log,
            final String prefix,
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password) throws SQLException {
        super(log, prefix, "[Oracle] ");
        setHostname(hostname);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.Oracle;
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
            Class.forName("oracle.jdbc.driver.OracleDriver"); // com.jdbc.OracleDriver ?
            return true;
        } catch (final ClassNotFoundException e) {
            writeError("Oracle driver class missing: " + e.getMessage() + ".", true);
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
            url = "jdbc:oracle:thin:@" + getHostname() + ":" + getPort() + ":" + getDatabase();
            try {
                this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
                this.connected = true;
                return true;
            } catch (final SQLException e) {
                writeError("Could not establish an Oracle connection, SQLException: " + e.getMessage(), true);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void queryValidation(final StatementEnum statement) throws SQLException {}

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
