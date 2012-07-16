package rabbit.proxy;

import rabbit.cache.CacheEntry;
import rabbit.handler.HandlerFactory;
import rabbit.http.HttpHeader;
import rabbit.httpio.ResourceSource;
import rabbit.io.BufferHandle;
import rabbit.io.CacheBufferHandle;
import rabbit.io.WebConnection;

/** A container to send around less parameters.
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class RequestHandler {
    private BufferHandle webHandle; 
    private final ConditionalChecker cond;

    private ResourceSource content = null;
    private HttpHeader webHeader = null;
    private CacheEntry<HttpHeader, HttpHeader> entry = null;
    private HttpHeader dataHook = null; // the entrys datahook if any.
    private HandlerFactory handlerFactory = null;
    private long size = -1;
    private WebConnection wc = null;	
    private boolean conditional;
    
    public RequestHandler (Connection con) {
	webHandle = new CacheBufferHandle (con.getProxy ().getBufferHandler ());
	cond = new ConditionalChecker ();
    }

    public synchronized BufferHandle getWebHandle () {
	return webHandle;
    }

    public synchronized void setWebHandle (BufferHandle webHandle) {
	this.webHandle = webHandle;
    }

    public synchronized HttpHeader getWebHeader () {
	return webHeader;
    }

    public  synchronized void setWebHeader (HttpHeader webHeader) {
	this.webHeader = webHeader;
    }
    
    public synchronized HttpHeader getDataHook () {
	return dataHook;
    }

    public synchronized void setDataHook (HttpHeader dataHook) {
	this.dataHook = dataHook;
    }

    public ConditionalChecker getCond () {
	return cond;
    }

    public synchronized CacheEntry<HttpHeader, HttpHeader> getEntry () {
	return entry;
    }

    public synchronized void setEntry (CacheEntry<HttpHeader, HttpHeader> entry) {
	this.entry = entry;
    }

    public synchronized WebConnection getWebConnection () {
	return wc;
    }

    public synchronized void setWebConnection (WebConnection wc) {
	this.wc = wc;
    }

    public synchronized boolean isConditional () {
	return conditional;
    }

    public synchronized void setConditional (boolean conditional) {
	this.conditional = conditional;
    }

    public synchronized ResourceSource getContent () {
	return content;
    }

    public synchronized void setContent (ResourceSource content) {
	this.content = content;
    }

    public synchronized HandlerFactory getHandlerFactory () {
	return handlerFactory;
    }

    public synchronized void setHandlerFactory (HandlerFactory handlerFactory) {
	this.handlerFactory = handlerFactory;
    }

    public synchronized long getSize () {
	return size;
    }

    public synchronized void setSize (long size) {
	this.size = size;
    }
}
