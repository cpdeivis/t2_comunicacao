package comunicacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author cpdeivis
 */
public class Cliente implements Runnable{
    private final Boolean teste;
    private final int port;
    private final int tam;
    private final int maxf;
    private final String filename;
    private Socket client;
    
    public Cliente(int port, int tam, int maxf, String filename, Boolean t){
        this.port = port;
        this.maxf = maxf;
        this.tam = tam;
        this.filename = filename;
        this.teste = t;
    }
    
    @Override
    public void run(){
        try{
            //CONFIGURAÇÃO DO SOCKET AQUI
            client = new Socket("127.0.0.1", port);
            client.setSoTimeout(1000);
            //TESTE DE CONEXÃO AQUI
            InputStream receptor = client.getInputStream();
            OutputStream emissor = client.getOutputStream();
            emissor.flush();
            
            //127.0.0.1 -> 2130706433
            ArrayList<Frame> frames = criaFrames(2130706433, 2130706433);
            Frame[] f;
            
            System.out.println("Client: Enviando " + frames.size() + " frames..");
            
            int i = 0, espera = 0, confirma = 0;

            while(i < frames.size()){
                System.out.println("Client: Montando bloco do " + i + " até " + (i+maxf));
                System.out.println("Client: Gerando id para o bloco");
                f = montaJanela(frames, i);
                
                if(teste){ // TESTE PARA DESLOCAMENTO DA JANELA
                    for(int j = f.length-1; j >= 0; j--){
                        emissor.write(f[j].encode());
                        emissor.flush();
                    }
                } else{
                    for(Frame aux : f){
                        emissor.write(aux.encode());
                        emissor.flush();
                    }
                }
                while(true){
                    try{
                        confirma = (int) receptor.read();
                        if(espera == confirma){
                            System.out.println("Client: recebido " + i);
                            espera++;
                            i++;
                            
                            espera = espera > maxf-1 ? 0 : espera;
                        }
                    } catch(SocketTimeoutException e){
                        espera = 0;
                        break;
                    }
                }
            }
            
            client.close();
        } catch (Exception e){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public Frame[] montaJanela (ArrayList<Frame> fr, int ind){
        Frame[] f;
        if(fr.size() < ind + maxf){
            f = new Frame[fr.size() - ind];
        }else{
            f = new Frame[maxf];
        }
        for (int i = 0; i < maxf; i++){
            if(ind < fr.size()){
                f[i] = fr.get(ind);
                f[i].updateAck(i);
                ind++;
            }
        }
        return f;
    }
    
    public ArrayList<Frame> criaFrames(int endP, int endC) throws FileNotFoundException, IOException{
        ArrayList<Frame> fr = new ArrayList();
        byte[] buffer = readBytes(new File(filename));
        byte[] pedaco;
        int i = 0, j = tam, ind = 0;
        if(buffer.length > tam){                
            while(j < buffer.length){
                pedaco = Arrays.copyOfRange(buffer, i, j);
                fr.add(new Frame(pedaco, endP, endC, ind));
                i += tam;
                j += tam;
                ind++;
                if(j > buffer.length){
                    j = buffer.length - i;
                    j += i;
                    pedaco = Arrays.copyOfRange(buffer, i, j);
                    
                    fr.add(new Frame(pedaco, endP, endC, ind));
                    ind++;
                }
                if(ind == maxf){
                    ind = 0;
                }
            }
        }else{
            fr.add(new Frame(buffer, endP, endC, ind));
        }
        return fr;
    }
    
    private byte[] readBytes(File file) throws IOException{
        byte[] buffer = new byte[(int) file.length()];
        InputStream ios = null;
        try {
            ios = new FileInputStream(file);
            if (ios.read(buffer) == -1) {
                throw new IOException(
                        "EOF reached while trying to read the whole file");
            }
        } finally {
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return buffer;
    }
}
