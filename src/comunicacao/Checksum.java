/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacao;

/**
 *
 * @author cpdeivis
 */
public class Checksum {
    
    public int calcula (String s){        
        int soma = 0;
        for(char a : s.toCharArray()){
            soma += a;
        }
        
        while (soma > 9999){
            soma = soma - 9999;
        }
        return soma;
    }
}
