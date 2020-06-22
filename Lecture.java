/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilisateur;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author galse
 */
public class Lecture extends Thread {
    BufferedReader userIn;
    BufferedReader networkIn;
    PrintWriter out;
    Socket connection;
    BufferedInputStream bin;
    PrivateKey privateKey;
    
    public Lecture(String add,PrivateKey pv){
        this.out = null;
        this.bin = null;
        this.connection = null;
        this.networkIn = null;
        this.userIn = null;
        try{
            privateKey=pv;
            connection=new Socket(add,1027); 
            userIn = new BufferedReader(new InputStreamReader(System.in));
            networkIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));            
            out = new PrintWriter(connection.getOutputStream());
            bin=new BufferedInputStream((connection.getInputStream()));
        }catch(IOException ex){            
            System.err.println(ex);        
        }
    }
    
    public PublicKey handshake(String login){
        PublicKey p = null;
        try{
            out.println("KEY"+"/n"+login);
            out.flush();
            String s=networkIn.readLine();
            if(s.equals("KEY")){
                byte[] lenb = new byte[294];
                bin.read(lenb);
                X509EncodedKeySpec ks = new X509EncodedKeySpec(lenb);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                p=kf.generatePublic(ks);
            }
        }catch(IOException ex){System.err.println(ex);} catch (InvalidKeySpecException ex) {
            Logger.getLogger(Lecture.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Lecture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }
    
   
    public void run(){ 
         PublicKey pb=null;
        try{
            ServerSocket server = new ServerSocket(3042);
            Socket con = server.accept();
            BufferedReader lecture = new BufferedReader(new InputStreamReader(con.getInputStream())); 
            String log=lecture.readLine();
            System.out.println("Bonjour,je suis "+log+" , est ce que me vous me croyez ?");
            pb=handshake(log);
            if(pb==null){
                System.out.println("E05");
            }
            else{
                connection.close();
                Thread env=new SendMessage(log,con,pb,privateKey);
                Thread rec=new ReceiveMessage(log,con,pb,privateKey);
                env.start();
                rec.start();
            }
        }catch(IOException ex){ 
            System.err.println(ex);        
        }
    }
     
    public void ecritureTCP(String login,String user){
        PublicKey pb=null;
        Scanner sc;
        try{
            out.println("COM"+"/n"+login);
            out.flush();
            String s=networkIn.readLine();
            if(s.equals("COM")){
                System.out.println("avant");
                s=networkIn.readLine();
                System.out.println("après");
                sc=new Scanner(s).useDelimiter("/n");
                String add=sc.next();
                Socket con=new Socket(add,3042);
                PrintWriter ecriture=new PrintWriter(new OutputStreamWriter(con.getOutputStream()));
                ecriture.println(user);
                ecriture.flush();
                pb=handshake(login);
                if(pb==null){
                    System.out.println("E05");
                }
                else{
                    Thread env=new SendMessage(login,con,pb,privateKey);
                    Thread rec=new ReceiveMessage(login,con,pb,privateKey);
                    rec.start();
                    env.start();
                }
            }
        }catch(IOException ex){ 
            System.err.println(ex);        
        }
    }
}
