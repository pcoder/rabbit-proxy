package rabbit.proxy;

import rabbit.http.HttpHeader;
import rabbit.httpio.AsyncListener;

/** A listener for resource transfers. 
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ClientResourceTransferredListener extends AsyncListener {

    /** The client resource have been successfully transferred.
     */
    void clientResourceTransferred ();
    
    /** The transfer of the client resource has been aborted.
     * @param error the failure code. 
     */
    void clientResourceAborted (HttpHeader error);
}
