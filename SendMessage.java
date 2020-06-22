/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilisateur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author galse
 */
public class SendMessage extends Thread {
    DataOutputStream dout;
    Cipher ch;
    String log;
    Socket so;
    PublicKey pb;
    PrivateKey pv;
    
    public SendMessage(String login,Socket s,PublicKey b,PrivateKey v){
         try {
            log=login;
            so=s;
            pb=b;
            pv=v;
            dout=new DataOutputStream(so.getOutputStream());
            ch=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        }catch(IOException ex){
            Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchPaddingException ex){
            Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     
    
    public void run(){
        try{
            Scanner sc=new Scanner(System.in);
            String str="NULL";
            while(true){
                System.out.println("Entreez un message à:"+log);
                str=sc.nextLine();
                if(!str.equals("m")){
                    ch.init(Cipher.ENCRYPT_MODE,pb);
                    byte[] text=str.getBytes();
                    byte[] textc=ch.doFinal(text);
                    dout.writeInt(textc.length);
                    dout.flush();
                    dout.write(textc);
                    dout.flush();
                    if(str.equals("bye")){
                        so.close();
                        break;
                    }
                }
            }
        }catch (IOException ex){
                Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (InvalidKeyException ex){
                Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IllegalBlockSizeException ex) {
                Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (BadPaddingException ex){
                Logger.getLogger(SendMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
