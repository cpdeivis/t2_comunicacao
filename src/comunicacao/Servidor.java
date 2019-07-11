package comunicacao;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cpdeivis
 */
public class Servidor implements Runnable{
    private final int port;
    private final int janela;
    private final String filename;
    
    public Servidor(int port, int maxf, String filename){
        this.port = port;
        this.janela = maxf;
        this.filename = filename;
    }
    
    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(port);   
            Frame f;
            FileOutputStream saida = new FileOutputStream(filename);
            BufferedOutputStream writer = new BufferedOutputStream(saida);
            
            while (true){
                Socket client = server.accept();
                System.out.println("Conectado ao IP: " + client.getInetAddress().getHostAddress());
                
                InputStream receptor = client.getInputStream();
                OutputStream emissor = client.getOutputStream();
                
                int i = 0;
                int espera = 0;
                
                do{ 
                    System.out.println("Servidor: Verificando frame.. ");

                    f = new Frame(receptor.readNBytes(15));//LÊ O CABEÇALHO PRIMEIRO
                    
                    if(f.decode() && f.getInfo(receptor.readNBytes(f.len))){
                        
                        if(f.chk == f.Cheksum(f.msg)){
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
                writer.flush();
                writer.close();
                break;
            }
            
        } catch (Exception e){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
