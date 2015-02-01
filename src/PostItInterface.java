import java.rmi.Remote;

import org.bson.types.ObjectId;

public interface PostItInterface extends Remote{

	public boolean criarUsuario(String login, String senha);
	public boolean atualizarUsuario(ObjectId id, String login, String senha);
	public boolean deletarUsuario(ObjectId id);
	public boolean login(String login, String senha);
	public PostIt iniciarPostIt(ObjectId id);
	public PostIt novoPostIt();
	public void salvarPostIt(String user, PostIt myPostIt);
	public boolean deletarPostIt(ObjectId id);
}
