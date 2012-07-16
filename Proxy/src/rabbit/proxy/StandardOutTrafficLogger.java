package rabbit.proxy;

import rabbit.http.HttpHeader;
import rabbit.proxy.HttpProxy;
import rabbit.util.SProperties;
import rabbit.util.TrafficLogger;

/** A simple ClientTrafficLogger that just writes simple network usage to
 *  standard out.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class StandardOutTrafficLogger implements ClientTrafficLogger {

    public void logTraffic (String user, HttpHeader request,
			    TrafficLogger client, TrafficLogger network,
			    TrafficLogger cache, TrafficLogger proxy) {
	System.out.println ("user: " + user +
			    ", url: " + request.getRequestURI () +
			    ", client read: " + client.read () +
			    ", client write: " + client.write () +
			    ", network read: " + network.read () +
			    ", network write: " + network.write ());
    }

    public void setup (SProperties properties, HttpProxy proxy) {
	// empty
    }
}