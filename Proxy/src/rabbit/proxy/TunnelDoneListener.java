package rabbit.proxy;

/** an interface for listening on tunnel closedowns.
 */
interface TunnelDoneListener {
    void tunnelClosed ();
}
