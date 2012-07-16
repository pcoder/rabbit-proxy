package rabbit.client.sample;

import rabbit.http.HttpHeader;

/** A handler of http HEAD responses.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HeadResponseListener {
    /** Handle a http response.
     * @param request the request that has was sent
     * @param response the response that was recieved
     */
    void response (HttpHeader request, HttpHeader response);
}
