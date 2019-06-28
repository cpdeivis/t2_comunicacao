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
public class App {
    public static void main (String[] args) throws InterruptedException{
        Servidor s = new Servidor();
        Cliente c = new Cliente();
        
        Thread ts = new Thread(s);
        Thread tc = new Thread(c);
        
        ts.start();
        tc.start();
        
        tc.join();
        ts.join();
        
    }
}
