package rabbit.client;

import rabbit.http.HttpHeader;
import rabbit.httpio.WebConnectionResourceSource;

/** A client that handles the event generated by the ClientBase class.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface ClientListener {
    /** Handle a redirected request
     * @param request the request that was redirected
     * @param location the location the redirect gives
     * @param base the actual client
     */
    void redirected (HttpHeader request, String location, ClientBase base);

    /** Handle a response for a request.
     * @param request the request made
     * @param response the response header 
     * @param wcrs the actual resource
     */
    void handleResponse (HttpHeader request, HttpHeader response, 
			 WebConnectionResourceSource wcrs);
    
    /** Check if this client request wants to automatically follow redirects
     * @return true if the client should follow redirects automatically
     */
    boolean followRedirects ();

    /** A request has been fully handled.
     * @param request the request that has been fully handled
     */
    void requestDone (HttpHeader request);

    /** Handle a timeout of a given request.
     * @param request the request that had a timeout
     */
    void handleTimeout (HttpHeader request);

    /** Handle a failure of a given request.
     * @param request the request that failed
     * @param e the actual exception that occurred
     */
    void handleFailure (HttpHeader request, Exception e);
}
