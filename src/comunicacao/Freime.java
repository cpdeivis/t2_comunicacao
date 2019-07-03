package comunicacao;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *
 * @author cpdeivis
 */

/* | FLAG | EndP | EndC | Ack  | ChkSum | DADO | FLAG | */
/* | 2 bt | 4 bt | 4 bt | 1 bt | 2 bt   | X bt | 2 bt | */
public class Freime {
    private ByteBuffer freime;
    private int endP;
    private int endC;
    public byte ack;
    public short chk;
    public String msg;
    
    public Freime(String info, char flag, int endP, int endC, int ack){
        short chk = Cheksum(info.getBytes(Charset.forName("UTF-8")));
        info = addStuffing(info, flag);
        byte[] info_bt = info.getBytes(Charset.forName("UTF-8"));
        
        freime = ByteBuffer.allocate(info_bt.length + 15);
        freime.putChar(flag);
        freime.putInt(endP);
        freime.putInt(endC);
        freime.put((byte)ack);
        freime.putShort(chk);
        freime.put(info_bt);
        freime.putChar(flag);
    }
    
    public Freime(byte[] serialized){
        freime = ByteBuffer.wrap(serialized);
    }
    
    public byte[] encode(){
        return freime.array();
    }
    
    public Boolean decode(){
        if (freime.hasArray() && freime.array().length > 15){
            freime.position(2);
            endP = freime.getInt();
            endC = freime.getInt();
            ack = freime.get();
            chk = freime.getShort();
            
            byte[] msg = new byte[freime.array().length - 15];
            freime.get(msg);
            this.msg = new String(msg, Charset.forName("UTF-8"));
            this.msg = rmvStuffing(this.msg, freime.getChar());
            
            return true;
        }
        return false;
    }
    
    public void updateAck(int ack){
        this.ack = (byte)ack;
        freime.position(10);
        freime.put(this.ack);
    }
    
    public static short Cheksum (byte[] s){
        short soma = 0;
        for(byte a : s){
            soma += a;
        }
        while (soma > 9999){
            soma = (short)(soma - 9999);
        }
        return soma;
    }
    
    public static String addStuffing(String s, char flag){
        StringBuilder aux = new StringBuilder(s);        
        char c;
        for (int i = 0; i < s.length(); i++){
            c = aux.charAt(i);
            if (c == flag){
                aux.insert(i, flag);
                i++;
            }
        }
        return aux.substring(0);
    }
    
    public static String rmvStuffing(String s, char flag){
        StringBuilder aux = new StringBuilder(s);        
        char c;
        for (int i = 0; i < aux.length(); i++){
            c = aux.charAt(i);
            if (c == flag){
                aux.deleteCharAt(i);
                i++;
            }
        }
        return aux.substring(0);
    }
}
