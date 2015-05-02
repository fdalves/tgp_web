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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import model.Usuario;
import ejb.UsuarioFacade;

@ManagedBean(name="tgpMB")
@SessionScoped
public class TgpMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	private Usuario usuario =  new  Usuario();
	private Usuario usuarioSelectChat = new Usuario(); 
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	
	
	
	
	public TgpMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.usarioList = usuarioFacade.findAll();
		this.listaFotosUser();
		this.usuario =  new  Usuario();
		
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


	
	
	public Usuario getUsuarioSelectChat() {
		return usuarioSelectChat;
	}


	public void setUsuarioSelectChat(Usuario usuarioSelectChat) {
		this.usuarioSelectChat = usuarioSelectChat;
	}

	
	
	
	
	
}
