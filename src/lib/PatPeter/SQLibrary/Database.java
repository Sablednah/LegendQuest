package lib.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Builders.Builder;

/**
 * Abstract superclass for all subclass database files.<br>
 * Date Created: 2011-08-26 19:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public abstract class Database {

    /**
     * Logger to log errors to.
     */
    protected Logger log;
    /**
     * Plugin prefix to display during errors.
     */
    protected final String PREFIX;
    /**
     * Database prefix to display after the plugin prefix.
     */
    protected final String DATABASE_PREFIX;

    /**
     * The driver of the Database as an enum.
     */
    protected DBMS driver;
    /**
     * Whether the Database is connected or not.
     */
    protected boolean connected;
    /**
     * The Database Connection.
     */
    protected Connection connection;

    /**
     * Statement registration for PreparedStatement query validation.
     */
    protected Map<PreparedStatement, StatementEnum> preparedStatements = new HashMap<PreparedStatement, StatementEnum>();
    /**
     * Holder for the last update count by a query.
     */
    @Deprecated
    public int lastUpdate;

    /**
     * Constructor used in child class super().
     * 
     * @param log the Logger used by the plugin.
     * @param prefix the prefix of the plugin.
     * @param dp the prefix of the database.
     */
    public Database(final Logger log, final String prefix, final String dp) throws DatabaseException {
        if (log == null) {
            throw new DatabaseException("Logger cannot be null.");
        }
        if (prefix == null || prefix.length() == 0) {
            throw new DatabaseException("Plugin prefix cannot be null or empty.");
        }

        this.log = log;
        this.PREFIX = prefix;
        this.DATABASE_PREFIX = dp; // Set from child class, can never be null or empty
        this.connected = false;
    }

    /**
     * Checks the connection between Java and the database engine.
     * 
     * @return the status of the connection, true for up, false for down.
     */
    public final boolean checkConnection() {
        if (connection != null) {
            return true;
        }
        return false;
    }

    /**
     * Deprecated method retained as an alias to {@link Database#isTable(String)}.
     * 
     * @param table the table to check.
     * @return true if table exists, false if table does not exist.
     */
    @Deprecated
    public boolean checkTable(final String table) {
        return isTable(table);
    }

    /**
     * Closes a connection with the database.
     */
    public final boolean close() {
        this.connected = false;
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (final SQLException e) {
                writeError("Could not close connection, SQLException: " + e.getMessage(), true);
                return false;
            }
        } else {
            writeError("Could not close connection, it is null.", true);
            return false;
        }
    }

    /**
     * Deprecated method that can now be substituted with {@link Database#query(String)} or the CREATE TABLE {@link Database#query(Builder)}.
     * 
     * @return always false.
     */
    @Deprecated
    public boolean createTable() {
        return false;
    }

    /**
     * Gets the connection variable
     * 
     * @return the {@link java.sql.Connection} variable.
     */
    public final Connection getConnection() {
        return this.connection;
    }

    /**
     * Get the DBMS enum value of the Database.
     * 
     * @return the DBMS enum value.
     */
    public final DBMS getDBMS() {
        return this.driver;
    }

    /**
     * Gets the last update count from the last execution.
     * 
     * @return the last update count.
     */
    public final int getLastUpdateCount() {
        return this.lastUpdate;
    }

    /**
     * Determines the statement and converts it into an enum.
     */
    public abstract StatementEnum getStatement(String query) throws SQLException;

    /**
     * Used to check whether the class for the SQL engine is installed.
     */
    protected abstract boolean initialize();

    /**
     * Specifies whether the Database object is connected or not.
     * 
     * @return a boolean specifying connection.
     */
    public final boolean isConnected() {
        return this.connected;
    }

    /**
     * Checks a table in a database based on the table's name.
     * 
     * @param table name of the table to check.
     * @return success of the method.
     * @throws SQLException
     */
    public abstract boolean isTable(String table);

    /**
     * Opens a connection with the database.
     * 
     * @return the success of the method.
     */
    public abstract boolean open();

    /**
     * Writes information to the console.
     * 
     * @param message the {@link java.lang.String}.
     *            of content to write to the console.
     */
    protected final String prefix(final String message) {
        return this.PREFIX + this.DATABASE_PREFIX + message;
    }

    /**
     * Prepares to send a query to the database.
     * 
     * @param query the SQL query to prepare to send to the database.
     * @return the prepared statement.
     */
    public final PreparedStatement prepare(final String query) throws SQLException {
        final StatementEnum s = getStatement(query); // Throws an exception and stops creation of the PreparedStatement.
        final PreparedStatement ps = connection.prepareStatement(query);
        preparedStatements.put(ps, s);
        return ps;
    }

    /**
     * Method for executing builders.
     * 
     * @param builder the Builder.
     * @return the ResultSet from the query or null if none was sent.
     * @throws SQLException if any error occurs during the query.
     */
    public final ResultSet query(final Builder builder) throws SQLException {
        return query(builder.toString());
    }

    /**
     * Executes a query given a {@link java.sql.PreparedStatement}.
     * 
     * @param ps the PreparedStatement to execute.
     * @return a ResultSet, if any, from executing the PreparedStatement, otherwise a ResultSet of the update count.
     * @throws SQLException if any part of the statement execution fails.
     */
    public final ResultSet query(final PreparedStatement ps) throws SQLException {
        final ResultSet output = query(ps, preparedStatements.get(ps));
        preparedStatements.remove(ps);
        return output;
    }

    /**
     * Executes a query given a PreparedStatement and StatementEnum.
     * 
     * @param ps the PreparedStatement to execute.
     * @param statement the enum to use for validation.
     * @return the ResultSet generated by the query, otherwise a ResultSet containing the update count of the query.
     * @throws SQLException if any part of the statement execution fails.
     */
    protected final ResultSet query(final PreparedStatement ps, final StatementEnum statement) throws SQLException {
        queryValidation(statement);
        if (ps.execute()) {
            return ps.getResultSet();
        } else {
            final int uc = ps.getUpdateCount();
            this.lastUpdate = uc;
            return this.connection.createStatement().executeQuery("SELECT " + uc);
        }
    }

    /**
     * Sends a query to the SQL database.
     * 
     * @param query the SQL query to send to the database.
     * @return the table of results from the query.
     */
    public final ResultSet query(final String query) throws SQLException {
        queryValidation(getStatement(query));
        final Statement statement = getConnection().createStatement();
        if (statement.execute(query)) {
            return statement.getResultSet();
        } else {
            final int uc = statement.getUpdateCount();
            this.lastUpdate = uc;
            return getConnection().createStatement().executeQuery("SELECT " + uc);
        }
    }

    /**
     * Validates a query before execution.
     * 
     * @throws SQLException if the query is invalid.
     */
    protected abstract void queryValidation(StatementEnum statement) throws SQLException;

    /**
     * Truncates (empties) a table given its name.
     * 
     * @param table name of the table to wipe.
     * @return success of the method.
     */
    public abstract boolean truncate(String table);

    /**
     * Deprecated method retained as an alias to {@link Database#truncate(String)}.
     * 
     * @param table the table to wipe.
     * @return true if successful, false on failure.
     */
    @Deprecated
    public boolean wipeTable(final String table) {
        return truncate(table);
    }

    /**
     * Writes either errors or warnings to the console.
     * 
     * @param toWrite the {@link java.lang.String}.
     *            written to the console.
     * @param severe whether console output should appear as an error or warning.
     */
    public final void writeError(final String toWrite, final boolean severe) {
        if (toWrite != null) {
            if (severe) {
                this.log.severe(prefix(toWrite));
            } else {
                this.log.warning(prefix(toWrite));
            }
        }
    }

    /**
     * Writes information to the console.
     * 
     * @param toWrite the {@link java.lang.String}.
     *            of content to write to the console.
     */
    public final void writeInfo(final String toWrite) {
        if (toWrite != null) {
            this.log.info(prefix(toWrite));
        }
    }
}