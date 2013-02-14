package lib.PatPeter.SQLibrary.Delegates;

/**
 * Interface for DBMS that use host-port-username-password constructors.<br>
 * Date Created: 2012-12-18 04:45.
 * 
 * @author PatPeter
 */
public interface HostnameDatabase {

    String getDatabase();

    String getHostname();

    String getPassword();

    int getPort();

    String getUsername();

    void setDatabase(String database);

    void setHostname(String hostname);

    void setPassword(String password);

    void setPort(int port);

    void setUsername(String username);
}
