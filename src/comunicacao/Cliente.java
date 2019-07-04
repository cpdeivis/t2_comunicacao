package comunicacao;

import java.io.BufferedReader;
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
    private final int port;
    private final int tam;
    private final char flag;
    private final int maxf;
    private Socket client;
    
    public Cliente(int port, int tam, int maxf, char flag){
        this.port = port;
        this.maxf = maxf;
        this.tam = tam;
        this.flag = flag;
    }
    
    @Override
    public void run(){
        try{
            //CONFIGURAÇÃO DO SOCKET AQUI
            client = new Socket("127.0.0.1", port);
            client.setSoTimeout(2500);
            //TESTE DE CONEXÃO AQUI
            InputStream receptor = client.getInputStream();
            OutputStream emissor = client.getOutputStream();
            emissor.flush();
            
            //127.0.0.1 -> 2130706433
            ArrayList<Freime> frames = criaFrames(2130706433, 2130706433);
            Freime[] f;
            
            System.out.println("Client: Enviando " + frames.size() + " frames..");
            
            int i = 0, espera = 0, confirma = 0;

            while(i < frames.size()){
                System.out.println("Client: Montando bloco do " + i + " até " + (i+maxf));
                System.out.println("Client: Gerando id para o bloco");
                f = montaJanela(frames, i);
                
//                TESTE PARA DESLOCAMENTO DA JANELA
                for(int j = f.length-1; j >= 0; j--){
                    emissor.write(f[j].encode());
                    emissor.flush();
                }
                
//                for(Freime aux : f){
//                    emissor.write(aux.encode());
//                    emissor.flush();
//                }
                
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
    
    public Freime[] montaJanela (ArrayList<Freime> fr, int ind){
        Freime[] f;
        if(fr.size() < ind + maxf){
            f = new Freime[fr.size() - ind];
        }else{
            f = new Freime[maxf];
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
    
    public ArrayList<Freime> criaFrames(int endP, int endC) throws FileNotFoundException, IOException{
        ArrayList<Freime> fr = new ArrayList();
        String pedaco;
        String s = lerTxt();
        int i = 0, j = tam, ind = 0;
        if(s.length() > tam){                
            while(j < s.length()){
                pedaco = s.substring(i, j);
                fr.add(new Freime(pedaco, flag, endP, endC, ind));
                i += tam;
                j += tam;
                ind++;
                if(j > s.length()){
                    j = s.length() - i;
                    j += i;
                    pedaco = s.substring(i, j);
                    
                    char[] repeat = new char[tam - pedaco.length()];
                    Arrays.fill(repeat, ' ');
                    pedaco += new String(repeat);
                    //System.out.println(pedaco.length());
                    fr.add(new Freime(pedaco, flag, endP, endC, ind));
                    ind++;
                }
                if(ind == maxf){
                    ind = 0;
                }
            }
        }else{
            fr.add(new Freime(s, flag, endP, endC, ind));
        }
        return fr;
    }
    
    private String lerTxt() throws FileNotFoundException, IOException{
        BufferedReader buffer = new BufferedReader(new FileReader("airplane.txt"));
        String texto;
        //Leitura do arquivo
        try{
            StringBuilder s = new StringBuilder();
            String linhas = buffer.readLine();
            while (linhas != null) {
                s.append(linhas);
                linhas = buffer.readLine();
            }
            texto = s.toString();
        } finally {
            buffer.close();
        }
        return texto;
    }
}
