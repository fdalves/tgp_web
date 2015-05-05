package managedbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import model.Usuario;
import ejb.UsuarioFacade;

@ManagedBean(name="loginMB")
@SessionScoped
public class LoginMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	private Usuario usuario =  new  Usuario();
	private String login = new String();
	private String senha = new String();
	
	
	public LoginMB() {

	}

	
	@PostConstruct
	public void ini(){
		this.usuario =  new  Usuario();
		login = new String();
		senha = new String();
	}
	
	
	
	
	
	public String login(){
		
		List<Usuario>  list = usuarioFacade.listarPorLogin(this.login);
		
		if (list != null && !list.isEmpty()){
			this.usuario = list.get(0);
			if (usuario.getSenha().equals(this.senha)){
				FacesContext fc = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
				session.setAttribute("user", usuario);
				return "go_initTgp";
			} else {
				String info = "Senha Inválida !";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, ""));
			}
			
		} else {
			String info = "Login Inválido !";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, ""));
		}
		
		return "";
	}
	

	public UsuarioFacade getUsuarioFacade() {
		return usuarioFacade;
	}


	public void setUsuarioFacade(UsuarioFacade usuarioFacade) {
		this.usuarioFacade = usuarioFacade;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	public String getSenha() {
		return senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	
	
	
	
	
	
}
