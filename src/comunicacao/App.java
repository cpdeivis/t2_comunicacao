package comunicacao;

/**
 * TRABALHO 2 DE COMUNICAÇÃO DE DADOS
 * ENLACE -> GOBACKN E CHECKSUM
 * FEITO POR: CLAITON NEISSE, DEIVIS PEREIRA E WILLIAM FELIPE
 */
public class App {
    public static void main (String[] args) throws InterruptedException{
        Runnable server = new Servidor(1234, 5, "saida.txt");
        Runnable client = new Cliente(1234, 200, 5, "airplane.txt", false);

//        CASO DE TESTE PARA ENVIO DA JANELA INVERTIDA        
//        Runnable server = new Servidor(1234, 5, "saida2.txt");
//        Runnable client = new Cliente(1234, 200, 5, "invec.txt", true);
        
//        Runnable server = new Servidor(1234, 50, "saida.jpg");
//        Runnable client = new Cliente(1234, 200, 50, "aloha.jpg", false);
        
        Thread ts = new Thread(server);
        Thread tc = new Thread(client);
        
        ts.start();
        tc.start();
        
        tc.join();
        ts.join();
    }
}
