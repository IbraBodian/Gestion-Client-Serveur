import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.security.*;

public class Client{
	String id_Client;
        String mdp;
	InetAddress ip_Address;
	boolean connecte;
        PublicKey pb;
        
	public Client(){
		id_Client="NULL";
		mdp="NULL";	
		connecte=false;
	}

	public Client(String idC,String pwd,boolean c){
		id_Client=idC;
		mdp=pwd;
		connecte=c;
	}
	
	String getIdClient(){
		return id_Client;
	}
	
	InetAddress getInetAddress(){
		return ip_Address;
	}
        
	boolean getConnecte(){
		return connecte;
	}
        
	void setIdClient(String id){
		id_Client=id;
	}

	void setInetAddress(InetAddress ip){
		ip_Address=ip;
	}
        
	void setConnecte(boolean c){
		connecte=c;
        }
         
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Client))
			return false;
		Client other=(Client) o;
		return ( id_Client.equals(other.getIdClient()) && mdp.equals(other.mdp));
	}
}
