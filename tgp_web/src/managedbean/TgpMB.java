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
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.Usuario;
import ejb.UsuarioFacade;

@ManagedBean(name="tgpMB")
@RequestScoped
public class TgpMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	private Usuario usuario =  new  Usuario();
	private Usuario usuarioSelectChat = new Usuario(); 
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	
	private String room = "teste";
	
	
	public TgpMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.usarioList = usuarioFacade.findAll();
		this.listaFotosUser();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		Usuario usuario = (Usuario) session.getAttribute("user");
		
		this.usuario =  usuario;
		
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


	public String getRoom() {
		return room;
	}


	public void setRoom(String room) {
		this.room = room;
	}

	
	
	
	
	
}
