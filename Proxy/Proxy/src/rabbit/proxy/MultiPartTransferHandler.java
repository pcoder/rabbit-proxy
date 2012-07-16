package rabbit.proxy;

import java.nio.ByteBuffer;
import rabbit.http.HttpHeader;
import rabbit.httpio.BlockSender;
import rabbit.httpio.BlockSentListener;
import rabbit.io.BufferHandle;
import rabbit.io.SimpleBufferHandle;

/** A handler that transfers request resources with multipart data.
 *  Will send the multipart upstream. Note that we can only do this 
 *  if we know that the upstream server is HTTP/1.1 compatible.
 *  
 *  How do we determine if upstream is HTTP/1.1 compatible? 
 *  If we can not then we have to add a Content-Length header, 
 *  That means we have to buffer the full resource.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class MultiPartTransferHandler extends ResourceHandlerBase 
    implements BlockSentListener {
    private MultiPartPipe mpp = null;

    public MultiPartTransferHandler (Connection con, 
				     BufferHandle bufHandle,
				     TrafficLoggerHandler tlh, 
				     String ctHeader) {
	super (con, bufHandle, tlh);
	mpp = new MultiPartPipe (ctHeader);
    }
    
    public void modifyRequest (HttpHeader header) {
	// nothing.
    }
    
    @Override void sendBuffer () {
	ByteBuffer buffer = bufHandle.getBuffer ();
	ByteBuffer sendBuffer = buffer.slice ();
	BufferHandle sbh = new SimpleBufferHandle (sendBuffer);
	mpp.parseBuffer (sendBuffer);
	fireResouceDataRead (sbh);
	BlockSender bs = 
	    new BlockSender (wc.getChannel (), con.getNioHandler (), 
			     tlh.getNetwork (), sbh, false, this);
	bs.write ();
    }
    public void blockSent () {
	if (!mpp.isFinished ())
	    doTransfer ();
	else 
	    listener.clientResourceTransferred ();
    }
}
