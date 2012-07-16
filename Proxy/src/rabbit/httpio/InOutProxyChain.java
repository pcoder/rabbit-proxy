package rabbit.httpio;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.khelekore.rnio.NioHandler;
import rabbit.dns.DNSHandler;
import rabbit.io.ProxyChain;
import rabbit.io.Resolver;

/** A proxy chain that connects directly to the local network and uses
 *  a chained proxy to connect to the outside.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class InOutProxyChain implements ProxyChain {
    private final Pattern insidePattern;
    private final Resolver directResolver;
    private final Resolver proxiedResolver;

    public InOutProxyChain (String insideMatch,
			    NioHandler nio, DNSHandler dnsHandler,
			    InetAddress proxy, int port, String proxyAuth) {
	insidePattern = Pattern.compile (insideMatch);
	directResolver = new SimpleResolver (nio, dnsHandler);
	proxiedResolver = new ProxyResolver (proxy, port, proxyAuth);
    }

    public Resolver getResolver (String url) {
	Matcher m = insidePattern.matcher (url);
	if (m.find ())
	    return directResolver;
	return proxiedResolver;
    }
}
