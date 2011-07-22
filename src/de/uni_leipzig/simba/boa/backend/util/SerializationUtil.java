package de.uni_leipzig.simba.boa.backend.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class SerializationUtil {
	
	private NLPediaLogger logger = new NLPediaLogger(SerializationUtil.class); 
	
	 public void serializeObject(Serializable object, String filename) {
		 
	 FileOutputStream fos = null;
	 ObjectOutputStream out = null;

	 try  {
		 
		 fos = new FileOutputStream(filename);
		 out = new ObjectOutputStream(fos);
		 out.writeObject(object);
		 out.close();
	 }
	 catch(IOException ex) {
		 
		 this.logger.error("Error serializing object " + object.toString() + " in " + filename);
	 }
	 }
	 
	public <T> T deserializeObject(T clazz, String filename) {

		Object object = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {

			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			object = (T) in.readObject();
			in.close();
		}
		catch (IOException ex) {

			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return (T) object;
	}
	
	public static void main(String[] args) {

		SerializationUtil s = new SerializationUtil();
		
		String sss = "asdasdas";
		
		s.serializeObject(sss, "/Users/gerb/asd.ser");
		
		String p = s.deserializeObject(new String(), "/Users/gerb/asd.ser");
		
		
		System.out.println(p);
	}

	public boolean isDeserializeable(String pathToSerializedFile) {

		// TODO Auto-generated method stub
		return false;
	}
	
}
