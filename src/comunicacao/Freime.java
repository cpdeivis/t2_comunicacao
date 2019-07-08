package comunicacao;

import java.nio.ByteBuffer;

/**
 *
 * @author cpdeivis
 */

/* | EndP | EndC | Ack  | ChkSum | LEN  |  DADO |*/
/* | 4 bt | 4 bt | 1 bt | 2 bt   | 4 bt |  X bt |*/
public class Freime {
    private ByteBuffer freime;
    private int endP;
    private int endC;
    public byte ack;
    public short chk;
    public int len;
    public byte[] msg;
    
    public Freime(byte[] info, int endP, int endC, int ack){
        short chk = Cheksum(info);
        freime = ByteBuffer.allocate(info.length + 15);
        freime.putInt(endP);
        freime.putInt(endC);
        freime.put((byte)ack);
        freime.putShort(chk);
        freime.putInt(info.length);
        freime.put(info);
    }
    
    public Freime(byte[] serialized){
        freime = ByteBuffer.wrap(serialized);
    }
    
    public byte[] encode(){
        return freime.array();
    }
    
    public Boolean decode(){
        if (freime.array().length >= 15){
            freime.position(0);
            endP = freime.getInt();
            endC = freime.getInt();
            ack = freime.get();
            chk = freime.getShort();
            len = freime.getInt();
            
//            System.err.println(len);
        
            return true;
        }
        return false;
    }
    
    public Boolean getInfo(byte[] info){
        if(info.length == len){
            //freime.position(15);
            //freime.put(info);
            this.msg = info;
            return true;
        }
        return false;
    }
    
    public void updateAck(int ack){
        this.ack = (byte)ack;
        freime.position(8);
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
}
