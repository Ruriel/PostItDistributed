package servidor;
import java.awt.EventQueue;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import Interface.PostIt;
import Interface.PostItInterface;

import com.mongodb.*;
import com.mongodb.util.JSON;
public class Servidor extends UnicastRemoteObject implements PostItInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6984557984483665585L;
	private DBCollection users;
	private DBCollection postits;
	
	public Servidor() throws UnknownHostException, RemoteException {
		super();
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		users = mongoClient.getDB("PPD").getCollection("users");
		postits = mongoClient.getDB("PPD").getCollection("postits");
	}

	public Servidor(String host, int port) throws UnknownHostException, RemoteException
	{
		super();
		MongoClient mongoClient = new MongoClient(host, port);
		users = mongoClient.getDB("PPD").getCollection("users");
		postits = mongoClient.getDB("PPD").getCollection("postits");
	}
	
	@Override
	public boolean criarUsuario(CharSequence login, CharSequence senha, boolean adm) 
	{
		BasicDBObject newUser = new BasicDBObject("user", login);
		if(users.findOne(newUser) != null)
		{
			System.out.println("Usuário "+login+" já existe! Use outro nome!");
			return false;
		}
		else
		{
			users.insert(newUser.append("password", senha).append("adm", adm));
			System.out.println("Inserindo usuário "+login+".");
			return true;
		}
	}

	@Override
	public void atualizarUsuario(CharSequence loginThen, CharSequence loginNow, CharSequence senha, boolean adm) 
	{
		BasicDBObject query = new BasicDBObject("user", loginThen);
		BasicDBObject obj = new BasicDBObject();
		if(!loginNow.equals("") && !(loginNow == null))
			obj.append("user", loginNow);
		if(!senha.equals("") && !(senha == null))
			obj.append("password", senha);
		obj.append("adm", adm);
		BasicDBObject update = new BasicDBObject("$set", obj);
		users.update(query, update);
		postits.update(query, new BasicDBObject("$set", new BasicDBObject("user", loginNow)));
		
	}

	@Override
	public void deletarUsuario(CharSequence login) 
	{
		BasicDBObject query = new BasicDBObject("user", login);
		users.remove(query);
		postits.remove(query);
	}

	@Override
	public ArrayList<DBObject> iniciarPostIts(CharSequence user)
	{
		DBCursor objects = postits.find(new BasicDBObject("user", user));
		ArrayList<DBObject> dbobj = new ArrayList<DBObject>();
		while(objects.hasNext())
		{
			dbobj.add(objects.next());
		}
		return dbobj;
	}

	@Override
	public void salvarPostIt(DBObject obj)
	{
		DBObject postitObject = postits.findOne(obj.get("_id"));
		if(postitObject == null)
		{
			postits.insert(obj);
		}
		else
		{
			BasicDBObject query = new BasicDBObject("_id", obj.get("_id"));
			DBObject update = ((DBObject) obj);
			postits.update(query, update);
		}
	}
	
	@Override
	public boolean deletarPostIt(ObjectId id) {
		DBObject postitObject = postits.findOne(id);
		if(postitObject == null)
		{
			return false;
		}
		else
		{
			postits.remove(postitObject);
			return true;
		}
		
		
	}

	@Override
	public int login(CharSequence login, CharSequence senha) 
	{
		
		DBObject user = users.findOne(new BasicDBObject("user", login));
		if(user != null)
		{
			if(senha.equals(user.get("password")))
			{
				System.out.println("Seja bem-vindo, "+login+".");
				if((boolean)user.get("adm") == true)
					return 0;
				else
					return 1;
			}
			else
			{
				System.out.println("Senha incorreta.");
				return 2;
			}
		}
		else
			System.out.println("Usuário inexistente.");
		return 3;
	}

	@Override
	public ArrayList<DBObject> buscarPostIt(CharSequence user) throws RemoteException {
		DBCursor cursor = postits.find(new BasicDBObject("user", user));
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		for(DBObject obj : cursor)
			list.add(obj);
		return list;
	}

	@Override
	public ArrayList<DBObject> listarUsuarios() throws RemoteException {
		ArrayList<DBObject> userList = new ArrayList<DBObject>();
		DBCursor userCursor = users.find();
		if(userCursor != null)
		{
			for(DBObject user : userCursor)
				userList.add(user);
			return userList;
		}
		return null;
	}

	@Override
	public int buscarAdministradores() throws RemoteException 
	{
		return users.find(new BasicDBObject("adm", true)).length();
	}
}
