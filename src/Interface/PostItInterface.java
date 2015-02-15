package Interface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public interface PostItInterface extends Remote{

	public boolean criarUsuario(CharSequence login, CharSequence senha, boolean adm) throws RemoteException;
	public void atualizarUsuario(CharSequence loginThen, CharSequence loginNow, CharSequence senha, boolean adm) throws RemoteException;
	public void deletarUsuario(CharSequence login) throws RemoteException;
	public int buscarAdministradores() throws RemoteException;
	public ArrayList<DBObject> listarUsuarios() throws RemoteException;
	public int login(CharSequence login, CharSequence senha) throws RemoteException;
	public ArrayList<DBObject> iniciarPostIts(CharSequence user) throws RemoteException;
	public void salvarPostIt(DBObject obj) throws RemoteException;
	public boolean deletarPostIt(ObjectId id) throws RemoteException;
	public ArrayList<DBObject> buscarPostIt(CharSequence user) throws RemoteException;
}
