/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

    ACK NO FRAME (ANTES DE CALCULAR O CHECKSUM), ESC BYTE STUFFING

 */
package comunicacao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cpdeivis
 */
public class Cliente extends Thread {
    Socket client;
    @Override
    public void run(){
        try {
            int i = 0, tam = 200, confirma, maxf = 5, espera = 0, posAck = 19;
            String s;
            client = new Socket("127.0.0.1", 1234);
            client.setSoTimeout(2500);
            ObjectInputStream receb = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream envia = new ObjectOutputStream(client.getOutputStream());
            envia.flush();
            
            s = lerTxt();
            ArrayList<Frame> fr = criaFrames(s, tam, client.getInetAddress().getHostAddress(), "181.0.0.3", "@");
            Frame[] f;
            boolean enviou = false;
            System.out.println("Client: Enviando " + fr.size() + " frames..");
            
            while (i < fr.size()){
                System.out.println("Client: Montando bloco do " + i + " até " + (i+maxf));
                System.out.println("Client: Gerando id para o bloco");
                f = montaJanela(fr, maxf, i);
                f = geraIdent(f, f.length, posAck);
                
                try {
                    if(enviou == false){
                        for(Frame aux : f){
                            //System.out.println("hola:" + aux.toString());
                            envia.writeObject(aux);
                            envia.flush();
                        }
                    }else if((i + maxf) <= fr.size()){
                        if(f.length > 0){
                            //System.out.println(f[0].toString());
                            envia.writeObject(f[f.length-1]);
                        }
                        envia.flush();
                    }
                       
                    try{
                        confirma = (Integer) receb.readObject();
                        if (confirma == espera){   
                            System.out.println("Client: recebido " + i);
                            espera++;
                            i++;
                            
                            enviou = true;
                            if (espera > 4){
                                espera = 4;
                                if(f.length == 1){
                                    espera = 0;
                                }
                            }
                        }else{
                            enviou = false;
                            espera = 0;                
                        }
                    }catch(SocketTimeoutException e){
                        enviou = false;
                    }
                                        
                } catch (ClassNotFoundException | NotSerializableException ex){
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Frame[] montaJanela (ArrayList<Frame> fr, int tam, int ind){
        Frame[] f;
        if(fr.size() < ind + tam){
            f = new Frame[fr.size() - ind];
        }else{
            f = new Frame[tam];
        }
        for (int i = 0; i < tam; i++){
            if(ind < fr.size()){
                f[i] = fr.get(ind);
                ind++;
            }
        }
        return f;
    }
    
    public Frame[] geraIdent(Frame[] f, int max, int posAck){
        StringBuilder s = new StringBuilder();
        for(int j = 0; j < max; j++){
            s.append(f[j].toString());
            s.insert(posAck, Integer.toString(j));
            f[j].setMsg(s.substring(0));
            s.replace(0, s.length(), "");
        }
        return f;
    }
    
    
    
    public String lerTxt() throws FileNotFoundException, IOException{
        BufferedReader buffer = new BufferedReader(new FileReader("airplane.txt"));
        String texto;
        //Leitura do arquivo
        try{
            StringBuilder s = new StringBuilder();
            String linhas = buffer.readLine();
            while (linhas != null) {
                s.append(linhas);
                //s.append(System.lineSeparator());
                linhas = buffer.readLine();
            }
            texto = s.toString();
        } finally {
            buffer.close();
        }
        return texto;
    }
    
    public ArrayList<Frame> criaFrames(String s, int tam, String endP, String endC, String flag){
        ArrayList<Frame> fr = new ArrayList();
        String pedaco;
        int i = 0, j = tam, ind = 0;
        if(s.length() > tam){                
            while(j < s.length()){
                pedaco = s.substring(i, j);
                fr.add(new Frame(pedaco, flag, endP, endC, ind));
                i += tam;
                j += tam;
                ind++;
                if(j > s.length()){
                    j = s.length() - i;
                    j += i;
                    pedaco = s.substring(i, j);
                    fr.add(new Frame(pedaco, flag, endP, endC, ind));
                    ind++;
                }
                if(ind == 5){
                    ind = 0;
                }
            }
        }else{
            fr.add(new Frame(s, flag , endP, endC, ind));
        }
        return fr;
    }
    
    
    /*public int GoBackN (Frame[] f, Servidor s){
        int cont;
        
        
        
        return cont;
    }*/
}
