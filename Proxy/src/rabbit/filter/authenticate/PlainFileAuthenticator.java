package rabbit.filter.authenticate;

import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.util.SProperties;
import rabbit.util.SimpleUserHandler;

/** An authenticator that reads username and passwords from a plain 
 *  text file.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class PlainFileAuthenticator implements Authenticator {
    private final SimpleUserHandler userHandler;

    /** Create a new PlainFileAuthenticator that will be configured using
     *  the given properties.
     * @param props the configuration for this authenticator
     */
    public PlainFileAuthenticator (SProperties props) {
	String userFile = props.getProperty ("userfile", "conf/allowed");
	userHandler = new SimpleUserHandler ();
	userHandler.setFile (userFile);
    }

    public String getToken (HttpHeader header, Connection con) {
	return con.getPassword ();
    }

    public boolean authenticate (String user, String pwd) {
	return userHandler.isValidUser (user, pwd);
    }
}
