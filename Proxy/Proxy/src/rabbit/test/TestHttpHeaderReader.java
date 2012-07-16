package rabbit.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import org.khelekore.rnio.BufferHandler;
import org.khelekore.rnio.NioHandler;
import org.khelekore.rnio.StatisticsHolder;
import org.khelekore.rnio.impl.BasicStatisticsHolder;
import org.khelekore.rnio.impl.CachingBufferHandler;
import org.khelekore.rnio.impl.MultiSelectorNioHandler;
import org.khelekore.rnio.impl.SimpleThreadFactory;
import rabbit.io.CacheBufferHandle;
import rabbit.http.HttpHeader;
import rabbit.httpio.HttpHeaderListener;
import rabbit.httpio.HttpHeaderReader;
import rabbit.io.BufferHandle;
import rabbit.io.SimpleBufferHandle;
import rabbit.util.SimpleTrafficLogger;
import rabbit.util.TrafficLogger;

/** A class to help test the HttpHeaderReader.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TestHttpHeaderReader {
    private final NioHandler nioHandler;
    private final TrafficLogger trafficLogger = new SimpleTrafficLogger ();
    private final BufferHandler bufferHandler = new CachingBufferHandler ();
    private final TestListener listener = new TestListener ();
    private SocketChannel readFrom, writeTo;
    private final int PORT = 9966;

    /** Start the tests
     * @param args the command line arguments
     */
    public static void main (String[] args) {
	try {
	    TestHttpHeaderReader tester = new TestHttpHeaderReader ();
	    tester.start ();
	    tester.runTests ();
	    tester.waitForFinish ();
	    tester.shutdown ();
	} catch (IOException e) {
	    e.printStackTrace ();
	}
    }

    private TestHttpHeaderReader () throws IOException {
	ExecutorService es = Executors.newCachedThreadPool ();
	StatisticsHolder sh = new BasicStatisticsHolder ();
	nioHandler = 
	    new MultiSelectorNioHandler (es, sh, 1, Long.valueOf (15000L));
    }

    private void start () throws IOException {
	nioHandler.start (new SimpleThreadFactory ());
	ServerSocketChannel ssc = ServerSocketChannel.open ();
	ssc.socket ().bind (new InetSocketAddress (PORT));
	readFrom = SocketChannel.open ();
	readFrom.connect (new InetSocketAddress (PORT));
	readFrom.configureBlocking (false);
	writeTo = ssc.accept ();
    }

    private void runTests () throws IOException {	
	testSimpleFullHeader ();
	testTwoFullHeaders ();
	testEmpty ();
	testPartialHeader ();
	testNonZeroStart ();
	testNonZeroStartPartial ();
	testLargeHeader ();
    }

    private void waitForFinish () {
	listener.waitForReady ();	
    }

    private void shutdown () {
	nioHandler.shutdown ();
    }

    private void testSimpleFullHeader () throws IOException {
	BufferHandle clientHandle = getSimpleHeaderBuffer ();
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	if (clientHandle.getBuffer () != null)
	    throw new RuntimeException ("Failed to use full buffer");
    }

    private void testTwoFullHeaders () throws IOException {
	HttpHeader header = getSimpleHttpHeader ();
	byte[] data = header.getBytes ();
	ByteBuffer buf = ByteBuffer.allocate (data.length * 2);
	buf.put (data);
	buf.put (data);
	buf.flip ();
	BufferHandle clientHandle = new SimpleBufferHandle (buf);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	if (clientHandle.getBuffer () == null)
	    throw new RuntimeException ("Should Still have a buffer");
	listener.waitForReady ();
	reader.readHeader ();
	if (clientHandle.getBuffer () != null)
	    throw new RuntimeException ("Failed to use full buffer: " + clientHandle.getBuffer ());
    }

    private void testEmpty () throws IOException {
	BufferHandle clientHandle = new CacheBufferHandle (bufferHandler);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	BufferHandle header = getSimpleHeaderBuffer ();
	writeTo.write (header.getBuffer ());
    }

    private void testPartialHeader () throws IOException {
	BufferHandle clientHandle = getSimpleHeaderBuffer ();
	ByteBuffer buf = clientHandle.getBuffer ();
	ByteBuffer bc = buf.duplicate ();
	buf.limit (10);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	bc.position (10);
	writeTo.write (bc);
    }

    private void testNonZeroStart () throws IOException {
	HttpHeader header = getSimpleHttpHeader ();
	byte[] buf = header.getBytes ();
	ByteBuffer bc = ByteBuffer.allocate (buf.length * 2 + 100);
	bc.position (100);
	bc.put (buf);
	bc.put (buf);
	bc.position (100);
	BufferHandle clientHandle = new SimpleBufferHandle (bc);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	listener.waitForReady ();
	reader.readHeader ();
	if (clientHandle.getBuffer () != null)
	    throw new RuntimeException ("Failed to use full buffer: " + clientHandle.getBuffer ());
    }

    private void testNonZeroStartPartial () throws IOException {
	HttpHeader header = getSimpleHttpHeader ();
	byte[] buf = header.getBytes ();
	ByteBuffer bc = ByteBuffer.allocate (buf.length + 100);
	bc.position (100);
	bc.put (buf, 0, 10);
	bc.flip ();
	bc.position (100);
	BufferHandle clientHandle = new SimpleBufferHandle (bc);
	ByteBuffer rest = ByteBuffer.wrap (buf);
	rest.position (10);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	writeTo.write (rest);
    }

    private void testLargeHeader () throws IOException {
	HttpHeader header = getLargeHttpHeader ();
	BufferHandle clientHandle = new CacheBufferHandle (bufferHandler);
	HttpHeaderReader reader = getReader (clientHandle);
	reader.readHeader ();
	ByteBuffer buf = ByteBuffer.wrap (header.getBytes ());
	writeTo.write (buf);
    }

    private HttpHeaderReader getReader (BufferHandle clientHandle) {
	listener.waitForReady ();
	return new HttpHeaderReader (readFrom, clientHandle, nioHandler,
				     trafficLogger, true, false, listener);
    }

    private BufferHandle getSimpleHeaderBuffer () {
	HttpHeader header = getSimpleHttpHeader ();
	ByteBuffer buf = ByteBuffer.wrap (header.getBytes ());
	return new SimpleBufferHandle (buf);
    }

    private HttpHeader getLargeHttpHeader () {
	HttpHeader header = getSimpleHttpHeader ();
	char[] chars = new char[5000];
	Arrays.fill (chars, 'A');
	String val = new String (chars);
	header.addHeader ("Large", val);
	header.addHeader ("Last", "Last");
	return header;
    }

    private HttpHeader getSimpleHttpHeader () {
	HttpHeader header = new HttpHeader ();
	header.setRequestLine ("GET http://localhost:" + PORT + "/ HTTP/1.1");
	header.addHeader ("Host", "localhost");
	return header;
    }

    private class TestListener implements HttpHeaderListener {
	private Semaphore latch = new Semaphore (1);

	private void waitForReady () {
	    latch.acquireUninterruptibly ();
	}

	public void httpHeaderRead (final HttpHeader header, BufferHandle bh, 
				    boolean keepalive, boolean isChunked, 
				    long dataSize) {
	    System.out.print ("read a header\n" + header);
	    latch.release ();
	}

	public void timeout () {
	    latch.release ();
	    throw new RuntimeException ("Connection timed out...");
	}
	    
	public void failed (Exception e) {
	    latch.release ();
	    throw new RuntimeException ("Connection failed...", e);
	}

	public void closed () {
	    latch.release ();
	    throw new RuntimeException ("Connection closed...");
	}	
    }
}
