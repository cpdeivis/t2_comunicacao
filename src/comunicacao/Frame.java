/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

import java.io.Serializable;


/**
 *
 * @author cpdeivis
 */
public class Frame extends Checksum implements Serializable{
    private String msg;
    
    public Frame (String info, String flag, String endP, String endC, int i){
        String chk = Integer.toString(calcula(info));
        this.msg = new String();
        this.msg = this.msg.concat(flag); // Insere flag inicial
        this.msg = this.msg.concat(endP); // Insere endereço de partida
        this.msg = this.msg.concat(endC); // Insere endereço de chegada
        //ADICIONAR ESC E ACK NO FRAME    
        this.msg = this.msg.concat(Integer.toString(i));
        this.msg = this.msg.concat(addStuffing(info, flag.charAt(0))); // Insere o dado
        this.msg = this.msg.concat(chk);  // Insere checksum
        this.msg = this.msg.concat(flag); //Insere flag final
    }
    
    public String extraiInfo (int ini, int fim){
        return this.msg.substring(ini, fim);
    }
    
    
    public String addStuffing(String s, char flag){
        StringBuilder aux = new StringBuilder(s);        
        char c;
        for (int i = 0; i < s.length(); i++){
            c = aux.charAt(i);
            if (c == flag){
                aux.insert(i, flag);
                i++;
               // System.out.println("Byte stuffing gerado...");
            }
        }
        return aux.substring(0);
    }
    
    public String rmvStuffing(String s, char flag){
        StringBuilder aux = new StringBuilder(s);        
        char c;
        for (int i = 0; i < aux.length(); i++){
            c = aux.charAt(i);
            if (c == flag){
                aux.deleteCharAt(i);
                i++;
               // System.out.println("Byte stuffing gerado...");
            }
        }
        return aux.substring(0);
    }
    
    @Override
    public String toString(){
        return this.msg;
    }
    
    public void setMsg(String s) {
        this.msg = s;
    }
    
}