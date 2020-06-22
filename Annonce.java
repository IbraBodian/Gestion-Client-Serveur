import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;




public class Annonce{
	String domaine;
	String prix;
	String descriptif;
	String id_Client;

	public Annonce(String domain,String price,String descript,String id){
		domaine=domain;
		prix=price;
		descriptif=descript;
		id_Client=id;
	}
	
	String getDomaine(){
		return domaine;
	}
	
	String getPrix(){
		return prix;
	}
	
	String getDescriptif(){
		return descriptif;
	}

	String getID(){
		return id_Client;
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof Annonce))
			return false;
		Annonce other=(Annonce) o;
		return (domaine.equals(other.getDomaine()) && prix.equals(other.getPrix()) && descriptif.equals(other.getDescriptif()) && id_Client.equals(other.getID()));
	}
}
