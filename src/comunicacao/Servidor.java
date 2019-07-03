package comunicacao;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.ArrayList;

/**
 *
 * @author cpdeivis
 */
public class Servidor implements Runnable{
    private final int port;
    private final int tam;
    private final int frames;
    
    public Servidor(int port, int tam, int maxf){
        this.port = port;
        this.tam = tam;
        this.frames = maxf;
    }
    
    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(port);
            
            Freime f;
            
            while (true){
                Socket client = server.accept();
                System.out.println("Conectado ao IP: " + client.getInetAddress().getHostAddress());
                
                InputStream receptor = client.getInputStream();
                OutputStream emissor = client.getOutputStream();
                
                int i = 0;

                do{ 
                    System.out.println("Servidor: Verificando frame.. ");

                    f = new Freime(receptor.readNBytes(tam + 15));
                    
                    if(f.decode()){
                        if(f.chk == f.Cheksum(f.msg.getBytes(Charset.forName("UTF-8"))))
                            emissor.write(f.ack);
                        else
                            emissor.flush();
   
                        i = 0;
                    } else{
                        i++;
                        if(i > frames && receptor.read() == -1)
                            break;
                    }
                } while(client.isConnected() && !client.isClosed());
                
                server.close();
                break;
            }
            
        } catch (Exception e){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
