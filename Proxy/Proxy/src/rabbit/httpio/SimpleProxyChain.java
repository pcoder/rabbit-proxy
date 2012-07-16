package rabbit.httpio;

import org.khelekore.rnio.NioHandler;
import rabbit.dns.DNSHandler;
import rabbit.io.ProxyChain;
import rabbit.io.Resolver;

/** A default implementation of a ProxyChain that always return 
 *  the same SimpleResolver.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class SimpleProxyChain implements ProxyChain {
    private final Resolver resolver;

    /** Create a new Proxy chain that always uses direct connections.
     * @param nio the NioHandler to use for running background tasks
     * @param dnsHandler the DNSHandler to use for DNS lookups
     */
    public SimpleProxyChain (NioHandler nio, DNSHandler dnsHandler) {
	resolver = new SimpleResolver (nio, dnsHandler);
    }

    public Resolver getResolver (String url) {
	return resolver;
    }
}
