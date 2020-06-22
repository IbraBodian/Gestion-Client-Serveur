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
import java.util.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author galse
 */
public class Utilisateur implements Runnable{
    BufferedReader userIn;//lire ce que l'utilisateur a écrit dans le terminal
    BufferedReader networkIn;//lire ce que le serveur a écrit dans la socket
    PrintWriter out; 
    DataOutputStream dout;
    DataInputStream din;
    Socket theSocket;
    PrivateKey privateKey;
    String user;
    Lecture discussion;
    
    public Utilisateur(Socket connection){        
            try{ 
                theSocket=connection;
                userIn = new BufferedReader(new InputStreamReader(System.in));
                networkIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));            
                out = new PrintWriter(theSocket.getOutputStream());
                dout=new DataOutputStream(connection.getOutputStream());
                din=new DataInputStream(connection.getInputStream());
            }catch(IOException ex){            
                System.err.println(ex);        
            }    
        }
        
        void sendKey(Key p){
            try{ 
                dout.writeInt(p.getEncoded().length);
                dout.flush();
                dout.write(p.getEncoded());
                dout.flush();
            }catch(Exception e){System.out.println("probleme");}
        }
        
        Key receiveKey(){
            Key p = null;
            try{ 
                int len = din.readInt();
                byte[] publicKeyBytes = new byte[len];
                if(len>0){
                    din.readFully(publicKeyBytes);//System.out.println(DatatypeConverter.printHexBinary(servPubKeyBytes));
                    X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    p=kf.generatePublic(ks);
                }
            }catch( Exception e){System.out.println("probleme");}
            return p;
        }
        
        public void sinscrire(){
            String s;
            
            try{
                Scanner sc=new Scanner(System.in);
                System.out.println("Entrez un identifiant :");
                s=sc.nextLine();
                out.println("INS"+"/n"+s);                
                out.flush();
                
                PublicKey pb = (PublicKey) receiveKey();
                
                System.out.println("Entrez un mot de passe :");
                s=sc.nextLine();
                Cipher ch=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                ch.init(Cipher.ENCRYPT_MODE,pb);
                byte[] text=s.getBytes();
                byte[] textc=ch.doFinal(text);
                
                dout.writeInt(textc.length);
                dout.flush();
                dout.write(textc);
                dout.flush();
                System.out.println(networkIn.readLine());
            }catch(Exception e){System.out.println("probleme");}
        }
        
        public void identification(){
            String s2,s4 = "NULL";
            try{
                
                do{
                    System.out.println("BONJOUR, vous êtes sur le BON COIN, pour vous connecter, veuillez entrez votre identifiant\n");
                    Scanner sc=new Scanner(System.in);
                    System.out.println("Saissizez votre identifiant :"); 
                    String s1=sc.nextLine();user=s1;
                    out.println("LOG"+"/n"+s1);                
                    out.flush();
                    s2=networkIn.readLine();
                    System.out.println(s2);
                    
                    if(s2.equals("LOG")){
                        out.println("PWD");                
                        out.flush();
                        PublicKey pb = (PublicKey) receiveKey();
                        System.out.println("Saissizez votre mot de passe :");
                        String s3=sc.nextLine();
                        Cipher ch=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                        ch.init(Cipher.ENCRYPT_MODE,pb);
                        byte[] text=s3.getBytes();
                        byte[] textc=ch.doFinal(text);
                        dout.writeInt(textc.length);
                        dout.flush();
                        dout.write(textc);
                        dout.flush();
                        s4=networkIn.readLine();
                        System.out.println(s4);
                    }
		}while( (!(s2.equals("LOG"))) || (!(s4.equals("PWD"))) );
            }catch(IOException ex){            
                System.err.println(ex);        
            }catch( Exception e){System.out.println("probleme");}
            try{          
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);         
                KeyPair keypair = keyGen.genKeyPair();
                privateKey = keypair.getPrivate();
                sendKey(keypair.getPublic());
            }catch( Exception e){System.out.println("probleme");}
            discussion=new Lecture(theSocket.getInetAddress().getHostAddress(),privateKey);
            discussion.start();
	}
        
        public void menu(){
            String s;
            try{
                System.out.println("[1]: S'inscrire\n");
                System.out.println("[2]: Se connecter\n");
                s=userIn.readLine();
                switch(s){
                    case "1":
                        sinscrire();
                        userIn.close();                    
                        out.close();
                        networkIn.close();
                        break;
                    case "2":
                        identification();
                        break;
                    default:
                        System.out.println("Ceci ne correspond a aucune de ces fonctions ! Recommencez !");
                        userIn.close();                    
                        out.close();
                        networkIn.close();
                        break;
                }
             }catch (IOException ex) {System.err.println(ex);} 
        }
	
	
        
	public void poster(){
            try{
                String dom,prix,des;
                System.out.println("Entrez le domaine de votre annonce :");
		dom=userIn.readLine();
		System.out.println("Entrez le prix de l'article :");
		prix=userIn.readLine();
		System.out.println("Décrivez comment est l'article :");
		des=userIn.readLine();
		out.println("ADD"+"/n"+dom+"/n"+prix+"/n"+des+"/n");                
		out.flush();
                System.out.println(networkIn.readLine());
            }catch(IOException ex){ 
                ex.printStackTrace();
            }    
        }

	public void retirer(){
            try{ 
		System.out.println("Quel annonce souhaitez vous retirez, tapez le numero associez (A coté de l'annonce) :");
		String str=userIn.readLine();
		out.println("DEL"+"/n"+str);
		out.flush();
		System.out.println(networkIn.readLine());
            }catch(IOException ex){ 
                ex.printStackTrace();
            }    
	}
	
	public void communiquer(){
            try{ 
		System.out.println("Entrez le login de la personne avec qui vous voulez parlez :");
		String log=userIn.readLine();
		out.println("COM"+"/n"+log);
		out.flush();
                String str=networkIn.readLine();
                System.out.println(str);
                if(str.equals("COM")){
                    discussion.ecritureTCP(log,user);
                }else{
                    System.out.println(str);
                }
            }catch(IOException ex){ 
                ex.printStackTrace();
            }    
	}
	
        public void afficher(){
            try{
		Scanner sc;
                networkIn.readLine();
		String str=networkIn.readLine();
		int i=Integer.parseInt(str);
		for(int j=0;j<i;j++){
                    str=networkIn.readLine();
                    sc=new Scanner(str).useDelimiter("/n");
                    System.out.println("Annonce: "+sc.next());
                    System.out.println("Client: "+sc.next());
                    System.out.println("Domaine: "+sc.next());
                    System.out.println("Prix: "+sc.next());
                    System.out.println("Description: "+sc.next());
                    System.out.println("________________________");
		}
            }catch(IOException ex){ 
                ex.printStackTrace();
            }    
	}
 
	public void run(){
            try{
                while (true){
                    System.out.println("******MENU******\n\n");
                    System.out.println("[1]: Poster une annonce\n");
                    System.out.println("[2]: Retirer une annonce\n");
                    System.out.println("[3]: Afficher la liste d'annonce\n");
                    System.out.println("[4]: Envoyer un message\n");
                    System.out.println("[5]: Se deconnecter\n");
                    Scanner sc=new Scanner(System.in);
                    String s=sc.nextLine();
                    switch(s){
			case "1":
                            poster();
                            break;
			case "2":
                            retirer();
                            break;
			case "3":
                            out.println("LIST");
                            out.flush();
                            afficher();
                            break;
			case "4":
                            communiquer();
                            break;
			case "5":
                            out.println("OUT");
                            out.flush();
                            networkIn.readLine();
                            userIn.close();                    
                            out.close();
                            networkIn.close();
                            break;
			default:
                            out.println("ERR");
                            out.flush();
                            System.out.println("Ceci ne correspond a aucune de ces fonctions ! Recommencez !");
                            break;
                    }
                }                    
            }catch (IOException ex) {System.err.println(ex);}        
            finally{            
                try {                
                    if(networkIn != null) 
                        networkIn.close();                
                    if(out != null) 
                        out.close();            
                }catch(IOException ex){}        
            } 
        }

    public static void main(String[] args) {
         try{ 
                Socket connection =new Socket("172.28.175.107", 1027);
                Utilisateur clt=new Utilisateur(connection);
                Thread cl=new Thread(clt);
                clt.menu();
        	cl.start();
         }catch(UnknownHostException ex){            
            ex.printStackTrace();        
         }catch (IOException ex) {System.err.println(ex);}
            
    }
    
}
