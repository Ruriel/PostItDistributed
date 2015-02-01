import java.awt.EventQueue;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.bson.types.ObjectId;

import com.mongodb.*;
import com.mongodb.util.JSON;
public class Server  implements PostItInterface{

	private DBCollection users;
	private DBCollection postits;
	
	protected Server() throws UnknownHostException {
		super();
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		users = mongoClient.getDB("PPD").getCollection("users");
		postits = mongoClient.getDB("PPD").getCollection("postits");
	}

	protected Server(String host, int port) throws UnknownHostException
	{
		super();
		MongoClient mongoClient = new MongoClient(host, port);
		users = mongoClient.getDB("PPD").getCollection("users");
		postits = mongoClient.getDB("PPD").getCollection("postits");
	}
	
	@Override
	public boolean criarUsuario(String login, String senha) 
	{
		if(users.findOne(new BasicDBObject("user", login)) != null)
		{
			System.out.println("Usuário "+login+" já existe! Use outro nome!");
			return false;
		}
		else
		{
			users.insert(new BasicDBObject("user", login).append("password", senha));
			System.out.println("Inserindo usuário "+login+".");
			return true;
		}
	}

	@Override
	public boolean atualizarUsuario(ObjectId id, String login, String senha) 
	{
		if(users.findOne(id) == null)
		{
			return false;
		}
		else
		{
			BasicDBObject query = new BasicDBObject("_id", id);
			BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("user", login).append("password", senha));
			users.update(query, update);
			return true;
		}
		
	}

	@Override
	public boolean deletarUsuario(ObjectId id) 
	{
		if(users.findOne(id) == null)
		{
			return false;
		}
		else
		{
			users.remove(new BasicDBObject("_id", id));
			return true;
		}
		
	}

	@Override
	public PostIt iniciarPostIt(ObjectId id)
	{
		PostIt myPostIt;
		DBObject postitObject = postits.findOne(id);
		
		if(postitObject == null || id == null)
		{
			myPostIt = new PostIt();
		}
		else
		{
			myPostIt = new PostIt(postitObject);
		}
		return myPostIt;
	}

	@Override
	public void salvarPostIt(String user, PostIt myPostIt)
	{
		DBObject postitObject = postits.findOne(myPostIt.getId());
		if(postitObject == null)
		{
			postits.insert(myPostIt.generateEntry(user));
		}
		else
		{
			BasicDBObject query = new BasicDBObject("_id", myPostIt.getId());
			DBObject update = ((DBObject) myPostIt.generateEntry(user));
			users.update(query, update);
		}
	}
	
	@Override
	public boolean deletarPostIt(ObjectId id) {
		return false;
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public boolean login(String login, String senha) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public PostIt novoPostIt() {
		return iniciarPostIt(null);
	}

}
