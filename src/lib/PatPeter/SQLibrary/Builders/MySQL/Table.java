package lib.PatPeter.SQLibrary.Builders.MySQL;

import java.sql.SQLException;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.Builders.Builder;

/**
 * CREATE TABLE query builder.<br>
 * Date Created: 2012-08-18 13:08.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Table implements Builder {

    private final Database db;
    public boolean temporary = false;
    public boolean exists = true;
    public String name = "";

    public Table(final Database db, final String name) {
        this.db = db;
        this.name = name;
    }

    public boolean create() throws SQLException {
        db.query(toString());
        return true;
    }

    @Override
    public String toString() {
        return "CREATE " + (temporary ? "TEMPORARY " : "") + "TABLE " +
                (exists ? "IF NOT EXISTS " : "") + name;
    }

    public boolean truncate() throws SQLException {
        db.query("TRUNCATE " + name);
        return true;
    }

    @Deprecated
    public boolean wipe() throws SQLException {
        return truncate();
    }
}
