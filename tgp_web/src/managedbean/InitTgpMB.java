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
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.extensions.component.layout.LayoutPane;
import org.primefaces.extensions.event.OpenEvent;
import org.primefaces.extensions.model.layout.LayoutOptions;

import model.Usuario;
import ejb.UsuarioFacade;

@ManagedBean(name="initTgpMB")
@RequestScoped
public class InitTgpMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	private Usuario usuario =  new  Usuario();
	private Usuario usuarioSelectChat = new Usuario(); 
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	private LayoutOptions layoutOptions;  
	
	private String room = "teste";
	
	
	public InitTgpMB() {

	}

	
	@PostConstruct
	public void ini(){
		this.carregaLayout();
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

	public void carregaLayout(){
	
	layoutOptions = new LayoutOptions();  
	  
    // options for all panes  
    LayoutOptions panes = new LayoutOptions();  
    panes.addOption("slidable", false);  
    panes.addOption("resizeWhileDragging", false);  
    layoutOptions.setPanesOptions(panes);  

    // options for center pane  
    LayoutOptions center = new LayoutOptions();  
    layoutOptions.setCenterOptions(center);  

    // options for nested center layout  
    LayoutOptions childCenterOptions = new LayoutOptions();  
    center.setChildOptions(childCenterOptions);  

    // options for center-north pane  
    LayoutOptions centerNorth = new LayoutOptions();  
    centerNorth.addOption("size", "50%");  
    childCenterOptions.setNorthOptions(centerNorth);  

    // options for center-center pane  
    LayoutOptions centerCenter = new LayoutOptions();  
    centerCenter.addOption("minHeight", 60);  
    childCenterOptions.setCenterOptions(centerCenter);  

    // options for west pane  
    LayoutOptions west = new LayoutOptions();  
    west.addOption("size", 200);  
    layoutOptions.setWestOptions(west);  

    // options for nested west layout  
    LayoutOptions childWestOptions = new LayoutOptions();  
    west.setChildOptions(childWestOptions);  

    // options for west-north pane  
    LayoutOptions westNorth = new LayoutOptions();  
    westNorth.addOption("size", "33%");  
    childWestOptions.setNorthOptions(westNorth);  

    // options for west-center pane  
    LayoutOptions westCenter = new LayoutOptions();  
    westCenter.addOption("minHeight", "60");  
    childWestOptions.setCenterOptions(westCenter);  

    // options for west-south pane  
    LayoutOptions westSouth = new LayoutOptions();  
    westSouth.addOption("size", "33%");  
    childWestOptions.setSouthOptions(westSouth);  

    // options for east pane  
    LayoutOptions east = new LayoutOptions();  
    east.addOption("size", 200);  
    layoutOptions.setEastOptions(east);  

    // options for south pane  
    LayoutOptions south = new LayoutOptions();  
    south.addOption("size", 80);  
    layoutOptions.setSouthOptions(south);
	}
	
	
	public LayoutOptions getLayoutOptions() {  
        return layoutOptions;  
    }  
  
    public void handleClose(CloseEvent event) {  
        FacesMessage msg =  
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Layout Pane closed",  
                             "Position:" + ((LayoutPane) event.getComponent()).getPosition());  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
  
    public void handleOpen(OpenEvent event) {  
        FacesMessage msg =  
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Layout Pane opened",  
                             "Position:" + ((LayoutPane) event.getComponent()).getPosition());  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
  
    public void handleResize(ResizeEvent event) {  
        FacesMessage msg =  
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Layout Pane resized",  
                             "Position:" + ((LayoutPane) event.getComponent()).getPosition() + ", new width = "  
                             + event.getWidth() + "px, new height = " + event.getHeight() + "px");  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
}
