package servidor;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.*;
/**
 * 
 * Classe responsável pelas operações a serem feitas no servidor.
 * @author Ruriel
 *
 */
public class Servidor extends UnicastRemoteObject implements ServerInterface{

	private static final long serialVersionUID = 6984557984483665585L;
	/**
	 * Variável referente a coleção de usuário.
	 */
	private DBCollection users;
	/**
	 * Variável referente a coleção de notas.
	 */
	private DBCollection postits;
	
	/**
	 * Construtor básico da classe.
	 * @param host Endereço do servidor.
	 * @param port Porta a ser utilizada pelo banco de dados.
	 * @throws UnknownHostException
	 * @throws RemoteException
	 */
	public Servidor(String host, int port) throws UnknownHostException, RemoteException
	{
		super();
		MongoClient mongoClient = new MongoClient(host, port);
		users = mongoClient.getDB("PPD").getCollection("users");
		postits = mongoClient.getDB("PPD").getCollection("postits");
	}
	
	/**
	 * Construtor sem paramêtros. Conecta automaticamene ao endereço local e a porta padrão 
	 * 27017.
	 * @throws UnknownHostException
	 * @throws RemoteException
	 */
	public Servidor() throws UnknownHostException, RemoteException {
		this("localhost", 27017);
	}

	
	
	@Override
	/**
	 * Método que insere um usuário no banco.
	 * @param login Nome de usuário
	 * @param senha Senha de usuário
	 * @param adm Se o usuário é administrador ou não.
	 * @return false se já houver um usuário com o login no banco
	 * e true caso não exista.
	 */
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
	/**
	 * Atualiza dados do usuário bem como suas referências em quaisquer notas.
	 * @param loginThen Login do usuário antes da atualização.
	 * @param loginNow Novo login do usuário.
	 * @param senha Nova senha..
	 * @param adm Novo direito de admnistração.
	 */
	public void atualizarUsuario(CharSequence loginThen, CharSequence loginNow, CharSequence senha, boolean adm) 
	{
		BasicDBObject query = new BasicDBObject("user", loginThen);
		BasicDBObject obj = new BasicDBObject();
		obj.append("user", loginNow);
		obj.append("password", senha);
		obj.append("adm", adm);
		BasicDBObject update = new BasicDBObject("$set", obj);
		users.update(query, update);
		postits.update(query, new BasicDBObject("$set", new BasicDBObject("user", loginNow)), false, true);
	}

	@Override
	/**
	 * Deleta o usuário do banco.
	 * @param login Usuário a ser eliminado do banco.
	 */
	public void deletarUsuario(CharSequence login) 
	{
		BasicDBObject query = new BasicDBObject("user", login);
		users.remove(query);
		postits.remove(query);
	}

	@Override
	/**
	 * Busca todas as notas referentes ao usuário.
	 * @param login Nome do usuário.
	 * @return Uma lista de DBObjects contendo as configurações das notas do usuário.
	 */
	public ArrayList<DBObject> iniciarPostIts(CharSequence login)
	{
		DBCursor cursor = postits.find(new BasicDBObject("user", login));
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		for(DBObject obj : cursor)
			list.add(obj);
		return list;
	}
	
	@Override
	/**
	 * Salva as modificações da nota ou cria uma nova se não houver no banco.
	 * @param obj DBObject gerado com a configuração da nota.
	 */
	public void salvarPostIt(DBObject obj)
	{
		DBObject postitObject = postits.findOne(obj.get("_id"));
		if(postitObject == null)
			postits.insert(obj);
		else
		{
			BasicDBObject query = new BasicDBObject("_id", obj.get("_id"));
			DBObject update = ((DBObject) obj);
			postits.update(query, update);
		}
	}
	
	@Override
	/**
	 * Apaga a nota do banco.
	 * @param id Id da nota a ser apagada.
	 * @return true caso a nota exista no banco e false caso não exista.
	 */
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
	/**
	 * Realiza o login no sistema.
	 * @param login Nome de usuário.
	 * @param senha Senha de usuário.
	 * @return 0 caso o usuário seja admnistrador, 1 caso não seja,
	 * 2 caso a senha esteja incorreta e 3 se não existir o nome de usuário.
	 */
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
	/**
	 * Lista todos o usuários existentes no banco.
	 * @return A lista de DBObjects contendo os usuário ou null caso o banco esteja vazio.
	 */
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
	/**
	 * Conta a quantidade de admnistradores.
	 * @return Número de admistradores.
	 */
	public int contarAdministradores() throws RemoteException 
	{
		return users.find(new BasicDBObject("adm", true)).length();
	}

	@Override
	/**
	 * Busca o usuário no banco.
	 * @param login Nome de usuário.
	 * @return Entrada do usuário no banco ou null caso não exista.
	 */
	public DBObject buscarUsuario(CharSequence login) throws RemoteException {
		BasicDBObject user = new BasicDBObject("user", login);
		return users.findOne(user);
	}
}
