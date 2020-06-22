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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
public class ReceiveMessage extends Thread {
    DataInputStream din;
    Cipher ch;
    String log;
    Socket so;
    PublicKey pb;
    PrivateKey pv;
    
    public ReceiveMessage(String login,Socket s,PublicKey b,PrivateKey v){
        try {
            log=login;
            so=s;
            pb=b;
            pv=v;
            din=new DataInputStream(so.getInputStream());
            ch=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        }catch(IOException ex){
            Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchAlgorithmException ex){
            Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch(NoSuchPaddingException ex){
            Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void run(){
        try{
            String str="NULL";
            while(true){
                System.out.println("Attendez de recevoir un message de :"+log);
                int len = din.readInt();
                if(len>0){
                    byte[] textc = new byte[len];
                    din.readFully(textc);
                    ch.init(Cipher.DECRYPT_MODE,pv);
                    byte[] plainText = ch.doFinal(textc);
                    str=new String(plainText);
                    System.out.println(" Vous avez reçu : '"+str+"' de la part "+log+" d'adresse "+so.getInetAddress());
                }
                if(str.equals("bye")){
                    so.close();
                    break;
                }
            }
        }catch (IOException ex){
                Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (InvalidKeyException ex){
                Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IllegalBlockSizeException ex) {
                Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }catch (BadPaddingException ex){
                Logger.getLogger(ReceiveMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
