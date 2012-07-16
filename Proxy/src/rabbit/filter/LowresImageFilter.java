package rabbit.filter;

import rabbit.html.HtmlBlock;
import rabbit.html.Tag;
import rabbit.html.TagType;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;

/** This filter removes the &quot;<tt>lowsrc=some_image.gif</tt>&quot; attributes
 *  from the &lt;img&gt; tags.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class LowresImageFilter extends SimpleTagFilter {

    // For the factory.
    public LowresImageFilter () {
    }

    /** Create a new LowresImageFilter for the given request, response pair.
     * @param con the Connection handling the request.
     * @param request the actual request made.
     * @param response the actual response being sent.
     */
    public LowresImageFilter (Connection con, HttpHeader request, HttpHeader response) {	
	super (con, request, response);
    }

    public HtmlFilter newFilter (Connection con, 
				 HttpHeader request, 
				 HttpHeader response) {
	return new LowresImageFilter (con, request, response);
    }

    /** remove the lowres tags.
     * @param block the part of the html page we are filtering.
     */
    public void handleTag (Tag tag, HtmlBlock block, int tokenIndex) {
	if (tag.getTagType () == TagType.IMG)
	    tag.removeAttribute ("lowsrc");
    }
}
