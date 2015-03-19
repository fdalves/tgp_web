package managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Usuario;

import org.primefaces.event.FileUploadEvent;

import ejb.UsuarioFacade;

@ManagedBean(name="usuarioMB")
@ViewScoped
public class UsuarioMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	private Usuario usuario =  new  Usuario();
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	
	
	
	public UsuarioMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		usarioList = usuarioFacade.findAll();
	}
	
	
	public void salvar(){
		usuarioFacade.save(usuario);
		
		String info = "Usuário Cadastrado com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Usuário " + usuario.getLogin(), info));
		
		usuario = new Usuario();
		usarioList = usuarioFacade.findAll();
	}
	
	
	
	
	 public void handleFileUpload(FileUploadEvent event) {
	        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
	        usuario.setFoto(event.getFile().getContents());
	        FacesContext.getCurrentInstance().addMessage(null, message);
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


	public List<Usuario> getUsarioList() {
		return usarioList;
	}


	public void setUsarioList(List<Usuario> usarioList) {
		this.usarioList = usarioList;
	}
	
	
	
	
}