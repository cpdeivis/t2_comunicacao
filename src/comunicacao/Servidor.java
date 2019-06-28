/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.EOFException;


/**
 *
 * @author cpdeivis
 */
public class Servidor extends Thread{
    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(1234);         
            Frame f;
            String msg, info;
            int chk, ack, tamChk = 4;
            
            while (true){
                Socket client = server.accept();
                System.out.println("Conectado ao IP: " + client.getInetAddress().getHostAddress());
                ObjectOutputStream sai = new ObjectOutputStream (client.getOutputStream());
                ObjectInputStream ent = new ObjectInputStream (client.getInputStream());
                
                do{
                    f = (Frame) ent.readObject(); 
                    
                    System.out.println("Servidor: Verificando frame.. ");
                    ack = Integer.parseInt(f.extraiInfo(19, 20)); // extrai ack
                    msg = f.extraiInfo(21, f.toString().length() - 1); // extrai dado e checksum
                    info = msg.substring(msg.length() - tamChk, msg.length()); // pega checksum
                    msg = f.rmvStuffing(msg, f.extraiInfo(0, 1).charAt(0)); // procura e remove byte stuffing
                    msg = msg.substring(0, msg.length() - info.length()); // pega dado
                    chk = Integer.parseInt(info.trim()); // transforma para inteiro

                    //System.out.println(f.toString()+"|"+chk+"|"+f.calcula(msg));
                    
                    if(chk == f.calcula(msg)){ // verifica se o dado está correto
                        sai.writeObject(ack);
                    }else{
                        sai.flush();
                    }
                    
                    if(!client.isConnected()){
                        server.close();
                        break;
                    }

                }while(true);
            }            
        }catch (Exception e){
            //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }    
    
}
