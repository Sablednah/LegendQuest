package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

/**
 * Child class for the Firebird database.<br>
 * Date Created: 2012-12-18 06:15.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Firebird extends Database {

    public enum Statements implements StatementEnum {}

    private final HostnameDatabase delegate = new HostnameDatabaseImpl();

    public Firebird(final Logger log,
            final String prefix,
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[Firebird] ");
        setHostname(hostname);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.Firebird;
    }

    public Firebird(final Logger log,
            final String prefix,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[Firebird] ");
        setHostname("localhost");
        setPort(3050);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.Firebird;
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
    public StatementEnum getStatement(final String query) throws SQLException {
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
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            return true;
        } catch (final ClassNotFoundException e) {
            writeError("Firebird driver class missing: " + e.getMessage() + ".", true);
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
            final String url = "jdbc:firebirdsql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
            try {
                this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
                this.connected = true;
                return true;
            } catch (final SQLException e) {
                writeError("Could not establish a Firebird connection, SQLException: " + e.getMessage(), true);
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
