package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Delegates.HostnameDatabaseImpl;

/**
 * Child class for the mSQL database.<br>
 * Date Created: 2012-12-29 01:00.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class mSQL extends Database {

    protected enum Statements implements StatementEnum {}

    private final HostnameDatabase delegate = new HostnameDatabaseImpl();

    public mSQL(final Logger log,
            final String prefix,
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[mSQL] ");
        setHostname(hostname);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.mSQL;
    }

    public mSQL(final Logger log,
            final String prefix,
            final String database,
            final String username,
            final String password) {
        super(log, prefix, "[mSQL] ");
        setHostname("localhost");
        setPort(1114);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        this.driver = DBMS.mSQL;
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
            Class.forName("com.imaginary.sql.msql.MsqlDriver");
            return true;
        } catch (final ClassNotFoundException e) {
            writeError("mSQL driver class missing: " + e.getMessage() + ".", true);
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
            url = "jdbc:msql://" + getHostname() + ":" + getPort() + "/" + getDatabase();
            try {
                this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
                this.connected = true;
                return true;
            } catch (final SQLException e) {
                writeError("Could not establish a mSQL connection, SQLException: " + e.getMessage(), true);
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