package lib.PatPeter.SQLibrary.Delegates;

import lib.PatPeter.SQLibrary.DatabaseException;

/**
 * Implementation of databases using hostnames, usernames, etc.<br>
 * Date Created: 2012-12-18 04:45.
 * 
 * @author PatPeter
 */
public class HostnameDatabaseImpl implements HostnameDatabase {
    
    private String hostname = "localhost";
    private int port = 0;
    private String username = "minecraft";
    private String password = "";
    private String database = "minecraft";
    
    public String getDatabase() {
        return this.database;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setDatabase(final String database) {
        if (database == null || database.length() == 0) {
            throw new DatabaseException("Database cannot be null or empty.");
        }
        this.database = database;
    }
    
    public void setHostname(final String hostname) {
        if (hostname == null || hostname.length() == 0) {
            throw new DatabaseException("Hostname cannot be null or empty.");
        }
        this.hostname = hostname;
    }
    
    public void setPassword(final String password) {
        if (password == null || password.length() == 0) {
            throw new DatabaseException("Password cannot be null or empty.");
        }
        this.password = password;
    }
    
    public void setPort(final int port) {
        if (port < 0 || 65535 < port) {
            throw new DatabaseException("Port number cannot be below 0 or greater than 65535.");
        }
        this.port = port;
    }
    
    public void setUsername(final String username) {
        if (username == null || username.length() == 0) {
            throw new DatabaseException("Username cannot be null or empty.");
        }
        this.username = username;
    }
    
}
