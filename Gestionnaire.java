import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;


public class Gestionnaire extends Thread{
	ConcurrentLinkedQueue <Annonce> alist;
        ConcurrentLinkedQueue <Client> clist;
	BufferedReader in;    
	PrintWriter out;
        DataOutputStream dout;BufferedOutputStream bos;
        DataInputStream din;
	Socket connection;
	Client current;

	public Gestionnaire(Socket connection,ConcurrentLinkedQueue <Annonce> al,ConcurrentLinkedQueue <Client> cl){        
            try{            
                this.connection=connection;                                    
         	in = new BufferedReader(new InputStreamReader(connection.getInputStream()));            
                out = new PrintWriter(connection.getOutputStream());
                dout=new DataOutputStream(connection.getOutputStream());bos=new BufferedOutputStream((connection.getOutputStream()));
                din=new DataInputStream(connection.getInputStream());
		alist=al;clist=cl;
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
                if(len>0){
                    byte[] publicKeyBytes = new byte[len];
                    din.readFully(publicKeyBytes);
                    X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    p=kf.generatePublic(ks);
                }
            }catch( Exception e){System.out.println("probleme");}
            return p;
        }
        
        public void souscription(String s){
          
                KeyPairGenerator keyGen = null;
            try {
                keyGen = KeyPairGenerator.getInstance("RSA");
            
                keyGen.initialize(2048);         
                KeyPair keypair = keyGen.genKeyPair();
                PrivateKey pv = keypair.getPrivate();
                sendKey(keypair.getPublic());
                
                String str;
                int len = din.readInt();
                if(len>0){
                    byte[] textc = new byte[len];
                    din.readFully(textc);
                    Cipher ch = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                    ch.init(Cipher.DECRYPT_MODE,pv);
                    byte[] plainText = ch.doFinal(textc);
                    str=new String(plainText);
                    clist.add(new Client(s,str,false));
                    out.println("INS");
                    out.flush();
                }
            } catch (IOException ex){
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NoSuchPaddingException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
	public void recognization(String str){
            int i,flag1=0,flag2=0;
            try{
                Client[] list = new Client[clist.size()];
                Object[] array = clist.toArray(list); 
                for(i=0;i<clist.size();i++)
                    if(str.equals(list[i].getIdClient())){
                        out.println("LOG");
                        out.flush();
                        flag1++;
                        break;
                    }
                if(i==clist.size()){
                    out.println("E01");
                    out.flush();
                }else{
                    in.readLine();
                    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                    keyGen.initialize(2048);         
                    KeyPair keypair = keyGen.genKeyPair();
                    PrivateKey pv = keypair.getPrivate();
                    sendKey(keypair.getPublic());
                    int len = din.readInt();
                    byte[] textc = new byte[len];
                    din.readFully(textc);
                    Cipher ch = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                    ch.init(Cipher.DECRYPT_MODE,pv);
                    byte[] plainText = ch.doFinal(textc);
                    str=new String(plainText);
                    if(str.equals(list[i].mdp)){
                        out.println("PWD");
                        out.flush();
                        flag2++;
                    }else{
                        out.println("E05");
                        out.flush();
                    }
			
                }
                if(flag2==1 && flag2==1){
                    clist.remove(list[i]);
                    list[i].pb = (PublicKey) receiveKey();
                    list[i].ip_Address=connection.getInetAddress();
                    list[i].connecte=true;
                    current=list[i];
                    clist.add(list[i]);
                }
            }catch (IOException ex){
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }catch (NoSuchPaddingException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

	public void mettre(String dom,String prix,String des){
            try{
                Integer.parseInt(prix);
            }catch(NumberFormatException e){
                out.println("E02");
                out.flush();
		return ;
            }
            out.println("ADD");
            out.flush();
            Annonce a=new Annonce(dom,prix,des,current.getIdClient());
            alist.add(a);
	}

	public void enlever(String a){
            try{
                Integer.parseInt(a);
            }catch(NumberFormatException e){
                out.println("E03");
                out.flush();
                return ;
            }
            int j=Integer.parseInt(a);
            if( j<0 || j>alist.size() ){
                out.println("E03");
		out.flush();
                return;
            }
            Annonce[] list = new Annonce[alist.size()];
            Object[] array = alist.toArray(list); 
            if(list[j].getID().equals(current.getIdClient()) ){
                alist.remove(list[j]);
		out.println("DEL");
		out.flush();
            }else{
		out.println("E03");	
		out.flush();
            }
		  
	}

	public void envoyer(){
            out.println("LIST");
            out.flush();
            out.println(alist.size());
            out.flush();
            Annonce[] list = new Annonce[alist.size()];
            Object[] array = alist.toArray(list); 
            for(int i=0;i<alist.size();i++){
		out.println(i+"/n"+list[i].getID()+"/n"+list[i].getDomaine()+"/n"+list[i].getDescriptif()
		+"/n"+list[i].getPrix()+"/n");
		out.flush();
            }
        }

	public void chercher(String log){
            String s;
            boolean c;
            int i;
            Client[] list = new Client[clist.size()];
            Object[] array = clist.toArray(list); 
            for(i=0;i<clist.size();i++){
                s=list[i].getIdClient();
		c=list[i].getConnecte();
		if(log.equals(s)){
                    out.println("COM");
                    out.flush();
                    if(c==true){
                        out.println(list[i].getInetAddress().getHostAddress()+"/n"+"true");
                        out.flush();
                    }else{
                        out.println(list[i].getInetAddress().getHostAddress()+"/n"+"false");
                        out.flush();
                    }
                    break;
                }
            }
            if(i==clist.size()){
                out.println("E04");
                out.flush();
            }
	}
        
        public void donner(String id){
            try{
                String s;
                int i;
                Client[] list = new Client[clist.size()];
                Object[] array = clist.toArray(list); 
                for(i=0;i<clist.size();i++){
                    s=list[i].getIdClient();
                    if(id.equals(s)){
                        out.println("KEY");
                        out.flush();
                        bos.write(list[i].pb.getEncoded());
                        bos.flush();
                        break;
                    }
                }
                if(i==clist.size()){
                    out.println("E05");
                    out.flush();
                }
            }catch (IOException ex){
                Logger.getLogger(Gestionnaire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
	public void run(){        
          try{  
            while (true){
                String s=in.readLine();
		Scanner sc=new Scanner(s).useDelimiter("/n");
		String com=sc.next();
          	switch(com){
                    case "INS":
                        String es=sc.next();
                        souscription(es);
                        break;
                    case "LOG":
                        String ds=sc.next();
                        recognization(ds);
                        break;
                    case "ADD":
			String dom=sc.next();
			String prix=sc.next();
                        String des=sc.next();
			mettre(dom,prix,des);
			break;
                    case "DEL":
			String ann=sc.next();
			enlever(ann);
			break;
                    case "LIST":
                        envoyer();
			break;
                    case "COM":
			String log=sc.next();
			chercher(log);
                        break;
                    case "KEY":
                        String id=sc.next();
                        donner(id);
                        break;
                    case "OUT":
                        out.println("OUT");
                        out.flush();int i;
                        Client[] list = new Client[clist.size()];
                        Object[] array = clist.toArray(list); 
                        for(i=0;i<clist.size();i++)
                            if(current.equals(list[i])){
                                clist.remove(list[i]);
                                list[i].connecte=false;
                                clist.add(list[i]);
                            }
			in.close();                    
                        out.close();                  
                     	break;
		    default:
			break;
            	}                                
    		Thread.sleep(1000);
            }
        }catch(InterruptedException ex){ 
            ex.printStackTrace();        
        }catch(IOException ex){ 
            System.err.println(ex);        
        }try{            
            in.close();            
            out.close();        
        }catch(IOException ex){ 
            ex.printStackTrace();
        }    
    }


    public static void main(String[] args){
        try{           
            ServerSocket server = new ServerSocket(1027);           
            System.out.println(" Serveur : "+server+" en écoute sur le port: " + server.getLocalPort()+" est lancé "); 
            ConcurrentLinkedQueue <Annonce> alist=new ConcurrentLinkedQueue<Annonce>() ;
            ConcurrentLinkedQueue <Client> clist=new ConcurrentLinkedQueue<Client>();
            clist.add(new Client("tupac","shakur",false));clist.add(new Client("amelie","poulin",false));
            while(true){                
                Socket connection = server.accept();//méthode bloquante              
                System.out.println("Serveur connexion avec: "+ connection); 
                //Gestionnaire res=new Gestionnaire(connection);              
                Thread ges=new Gestionnaire(connection,alist,clist);
		//res.recognization();                
                ges.start();
            }
        }catch(IOException ex){            
            ex.printStackTrace();        
        }    
    } 
}
