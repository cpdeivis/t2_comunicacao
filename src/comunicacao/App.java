package comunicacao;

/**
 *
 * @author cpdeivis
 */
public class App {
    public static void main (String[] args) throws InterruptedException{
        Runnable server = new Servidor(1234, 5, "saida.txt");
        Runnable client = new Cliente(1234, 200, 5, "airplane.txt");
        
//        Runnable server = new Servidor(1234, 50, "saida.jpg");
//        Runnable client = new Cliente(1234, 200, 50, "aloha.jpg");
        
        Thread ts = new Thread(server);
        Thread tc = new Thread(client);
        
        ts.start();
        tc.start();
        
        tc.join();
        ts.join();
    }
}
