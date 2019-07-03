package comunicacao;

/**
 *
 * @author cpdeivis
 */
public class App {
    public static void main (String[] args) throws InterruptedException{
        Runnable server = new Servidor(1234, 200, 5);
        Runnable client = new Cliente(1234, 200, 5, '@');
        
        Thread ts = new Thread(server);
        Thread tc = new Thread(client);
        
        ts.start();
        tc.start();
        
        tc.join();
        ts.join();
    }
}
