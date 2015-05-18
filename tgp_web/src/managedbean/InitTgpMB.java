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
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import model.DocAtividade;
import model.Usuario;
import ejb.AtividadeFacade;
import ejb.DocAtividadeFacade;
import ejb.UsuarioFacade;

@ManagedBean(name="initTgpMB")
@RequestScoped
public class InitTgpMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private AtividadeFacade atividadeFacade;
	@EJB
	private DocAtividadeFacade docAtividadeFacade;
	@EJB
	private UsuarioFacade usuarioFacade;


	private Usuario usuario =  new  Usuario();
	private Usuario usuarioSelectChat = new Usuario(); 
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	 
	
	public InitTgpMB() {

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
	
	
	
	public String goUser(){
		return "go_user";
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


	public void carregaGravacao(){
	
			DocAtividade docAtividade = new DocAtividade();
			System.out.println("cuzinhoo..");	
			  byte[] value =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiden").getBytes();
				
			  docAtividade.setDoc(value);
			  docAtividade.setAtividade(atividadeFacade.find(24));
			  docAtividade.setNomeDoc("teste");
			  docAtividade.setExtensao(".wav");
			  docAtividade.setTypeName(".wav");
			  
			  docAtividadeFacade.save(docAtividade);
				 
			
			System.out.println("entrou");
			
		
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
