package rabbit.filter;

import rabbit.html.HtmlBlock;
import rabbit.html.Tag;
import rabbit.html.TagType;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;

/** This class removes background images from html pages.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class BackgroundFilter extends SimpleTagFilter {
    
    // for the factory part.
    public BackgroundFilter () {
    }

    /** Create a new BackgroundFilter for the given request, response pair.
     * @param con the Connection handling the request.
     * @param request the actual request made.
     * @param response the actual response being sent.
     */
    public BackgroundFilter (Connection con, HttpHeader request, HttpHeader response) {	
	super (con, request, response);
    }

    public HtmlFilter newFilter (Connection con, 
				 HttpHeader request, 
				 HttpHeader response) {
	return new BackgroundFilter (con, request, response);
    }

    /** Remove background images from the given block.
     * @param tag the current Tag
     */
    @Override public void handleTag (Tag tag, HtmlBlock block, int tokenIndex) {
	TagType type = tag.getTagType ();
	if (type == TagType.BODY || 
	    type == TagType.TABLE || 
	    type == TagType.TR || 
	    type == TagType.TD) {
	    tag.removeAttribute ("background");
	} 
    }
}
