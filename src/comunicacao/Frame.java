package comunicacao;

//import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * @author cpdeivis
 */
public class Frame{
    private final int endP;
    private final int endC;
    private final byte ack;
    private final short chk;
    private String msg;
    ArrayList<Byte> frame = new ArrayList();
//    private ByteBuffer frames;
    
    public Frame (String info, char flag, int endP, int endC, int i){
        this.chk = Cheksum(info.getBytes(Charset.forName("UTF-8")));
        this.msg = new String();
        this.msg = addStuffing(info, flag);
        this.endP = endP;
        this.endC = endC;
        this.ack = (byte)i;
        
        frame.add((Byte)((byte)flag));
        insertInto(toByteArray(this.endP));
        insertInto(toByteArray(this.endC));
        frame.add((Byte)ack);
        insertInto(toByteArray(this.chk));
        insertInto(this.msg.getBytes(Charset.forName("UTF-8")));
        frame.add((Byte)((byte)flag));
    }
    
    private void insertInto(byte[] array){
        for(byte insert : array)
            frame.add((Byte)insert);
    }
    
    public byte[] encode(){
        byte[] result = new byte[frame.size()];
        for(int i = 0; i < frame.size(); i++) {
            result[i] = frame.get(i);
        }
        
        return result;
    }
    
    private static byte[] toByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value};
    }
    
    private static byte[] toByteArray(short value) {
        return new byte[] {
                (byte)(value >> 8),
                (byte)value};
    }
    
    private static short Cheksum (byte[] s){
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
