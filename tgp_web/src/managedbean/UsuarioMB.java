package managedbean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

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
		
		this.usarioList = usuarioFacade.findAll();
		this.listaFotosUser();
		this.usuario =  new  Usuario();
	}
	
	
	
	public void salvar(){
		
		if (usuario.getUsuarioId() == 0){
			

			if (!this.validaEmail(this.usuario.getEmail())){
				String info = "E-mail j� Cadastrado..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"E-mail " + usuario.getEmail(), info));
				return;
			}
			
			
			if (!this.validaLogin(this.usuario.getLogin())){
				String info = "Login j� Cadastrado..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login " + usuario.getLogin(), info));
				return;
			}
			
			this.usuarioFacade.save(usuario);
			String info = "Usuario Cadastrado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Usu�rio " + usuario.getLogin(), info));
		} else {
			Usuario usuarioPersist = this.usuarioFacade.find(usuario.getUsuarioId());
			
			if (usuarioPersist.getEmail().equalsIgnoreCase(this.usuario.getEmail())){
				usuarioPersist.setEmail(this.usuario.getEmail());
			} else {
				if (!this.validaEmail(this.usuario.getEmail())){
					String info = "E-mail J� Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"E-mail " + usuario.getEmail(), info));
					return;
				}
			}
			
			if (usuarioPersist.getLogin().equalsIgnoreCase(this.usuario.getLogin())){
				usuarioPersist.setLogin(this.usuario.getLogin());
			} else {
				if (!this.validaLogin(this.usuario.getLogin())){
					String info = "Login J� Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login " + usuario.getLogin(), info));
					return;
				}
			}
			
			usuarioPersist.setNome(this.usuario.getNome());
			usuarioPersist.setSenha(this.usuario.getSenha());
			usuarioPersist.setSuperUser(this.usuario.getSuperUser());
			usuarioPersist.setFoto(this.getUsuario().getFoto());
			
			this.usuarioFacade.update(usuarioPersist);
			String info = "Usuario Alterado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Usu�rio " + usuario.getLogin(), info));
		}
		
		this.ini();
	}
	
	
	public void excluir(Usuario usuario){
		this.usuarioFacade.delete(usuario);
		String info = "Usu�rio Excluido com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
		this.ini();
	}
	
	
	
	 public void handleFileUpload(FileUploadEvent event) {
	        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
	        usuario.setFoto(event.getFile().getContents());
	        FacesContext.getCurrentInstance().addMessage(null, message);
	    }
	 
	 
	
	private void listaFotosUser() {

		try {
			ServletContext sContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			File folder = new File(sContext.getRealPath("/temp"));
			if (!folder.exists())
				folder.mkdirs();

			for (Usuario u : usarioList) {
				if (u.getFoto() != null) {
					String nomeArquivo = u.getUsuarioId() + ".jpg";
					String arquivo = sContext.getRealPath("/temp")
							+ File.separator + nomeArquivo;

					criaArquivo(u.getFoto(), arquivo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	 
	private void criaArquivo(byte[] bytes, String arquivo) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(arquivo);
			fos.write(bytes);

			fos.flush();
			fos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	private boolean validaEmail(String email){
		List<Usuario> list = usuarioFacade.listarPorEmail(email);
		return list.isEmpty();
		
	}
	
	private boolean validaLogin(String login){
		List<Usuario> list = usuarioFacade.listarPorLogin(login);
		return list.isEmpty();
		
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
