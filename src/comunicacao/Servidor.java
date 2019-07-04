package comunicacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cpdeivis
 */
public class Servidor implements Runnable{
    private final int port;
    private final int tam;
    private final int janela;
    
    public Servidor(int port, int tam, int maxf){
        this.port = port;
        this.tam = tam;
        this.janela = maxf;
    }
    
    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(port);   
            Freime f;
            File saida = new File("saida.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(saida));
            
            while (true){
                Socket client = server.accept();
                System.out.println("Conectado ao IP: " + client.getInetAddress().getHostAddress());
                
                InputStream receptor = client.getInputStream();
                OutputStream emissor = client.getOutputStream();
                
                int i = 0;
                int espera = 0;
                
                do{ 
                    System.out.println("Servidor: Verificando frame.. ");

                    f = new Freime(receptor.readNBytes(tam + 15));
                    
                    if(f.decode()){
                        if(f.chk == f.Cheksum(f.msg.getBytes(Charset.forName("UTF-8")))){
                            if(espera == f.ack){
                                writer.write(f.msg);
                                emissor.write(f.ack);
                                espera++;
                                espera = espera > janela ? 0 : espera;
                            } else
                                espera = 0;
                        }
                        else
                            emissor.flush();
   
                        i = 0;
                    } else{
                        i++;
                        if(i > janela && receptor.read() == -1)
                            break;
                    }
                } while(client.isConnected() && !client.isClosed());
                
                server.close();
                writer.close();
                break;
            }
            
        } catch (Exception e){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
