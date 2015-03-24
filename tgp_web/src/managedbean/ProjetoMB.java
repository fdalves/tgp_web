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
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

import model.Projeto;
import model.Usuario;
import ejb.ProjetoFacade;
import ejb.UsuarioFacade;

@ManagedBean(name="projetoMB")
@ViewScoped
public class ProjetoMB  implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	@EJB
	private ProjetoFacade projetoFacade;
	@EJB
	private UsuarioFacade usuarioFacade; 
	private Projeto projeto = new Projeto();
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	private List<SelectItem> projetoSelectItems = new ArrayList<SelectItem>();
	private int projetoSelecionadoAba2 = 0;
	private DualListModel<Usuario> usuListModel = new DualListModel<Usuario>();
	private List<Usuario> usuariosListDisponiveis = new ArrayList<Usuario>();
	private List<Usuario> usuariosListSelecionados = new ArrayList<Usuario>();
	
	
	public ProjetoMB() {
		
	}

	
	public void onTabChange(){
		this.ini();
	}
	
	
	public void onTabClose(){
		
	}
	
	
	@PostConstruct
	public void ini(){
		this.projeto = new Projeto();
		projetosList = projetoFacade.findAll();
		
		projetoSelectItems = new ArrayList<SelectItem>();
		
		for (Projeto projeto : projetosList) {
			SelectItem item = new SelectItem(projeto.getProjetoId(), projeto.getDescProjeto() + " - " + projeto.getNomeProjeto());
			projetoSelectItems.add(item);
		}
		
		projetoSelecionadoAba2 = 0;
		
		usuariosListDisponiveis = usuarioFacade.findAll();
		usuariosListSelecionados = usuarioFacade.findAll();
		usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis, usuariosListSelecionados);
		
		
	}
	
	
	
	public void salvar(){
	
		if(this.projeto.getProjetoId() == 0){
			
			if (!this.validaNomeProjeto(this.projeto.getNomeProjeto())){
				String info = "Nome Projeto j� Cadastrado..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Nome Projeto " + projeto.getNomeProjeto(), info));
				return;
			}
			
			
			if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
				String info = "Sigla j� Cadastrada..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login " + projeto.getSiglaProjeto(), info));
				return;
			}
			
			this.projetoFacade.save(projeto);
			String info = "Projeto Cadastrado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Projeto " + projeto.getNomeProjeto(), info));
			
			
		} else{
			
			Projeto projetoPersist = this.projetoFacade.find(projeto.getProjetoId());
			
			if (projetoPersist.getNomeProjeto().equalsIgnoreCase(this.projeto.getNomeProjeto())){
				projetoPersist.setNomeProjeto(this.projeto.getNomeProjeto());
			} else {
				if (!this.validaNomeProjeto(this.projeto.getNomeProjeto())){
					String info = "Nome Projeto J� Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Projeto " + projeto.getNomeProjeto(), info));
					return;
				}
			}
			
			if (projetoPersist.getSiglaProjeto().equalsIgnoreCase(this.projeto.getSiglaProjeto())){
				projetoPersist.setSiglaProjeto(this.projeto.getSiglaProjeto());
			} else {
				if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
					String info = "Sigla Projeto J� Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Sigla Projeto " + projeto.getSiglaProjeto(), info));
					return;
				}
			}
			
			projetoPersist.setDescProjeto(this.projeto.getDescProjeto());
			projetoPersist.setEscopoFecahdo(this.projeto.getEscopoFecahdo());
			projetoPersist.setData_ini(this.projeto.getData_ini());
			projetoPersist.setData_fim(this.projeto.getData_fim());
			
			this.projetoFacade.update(projetoPersist);
			String info = "Projeto Alterado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"projeto " + projeto.getNomeProjeto(), info));
		}
		
		this.ini();
	}

	
	private boolean validaSiglaProjeto(String siglaProjeto) {
		List<Projeto> list = projetoFacade.listarPorSiglaProjeto(siglaProjeto);
 		return list.isEmpty();
	}

	private boolean validaNomeProjeto(String nomeProjeto) {
		List<Projeto> list = projetoFacade.listarPorNomeProjeto(nomeProjeto);
 		return list.isEmpty();
	}

	public void excluir(Projeto projeto){
		this.projetoFacade.delete(projeto);
		String info = "Projeto Excluido com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Projeto Excluido com Sucesso", info));
		this.ini();
	}
	
	
	
	
	public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for(Object item : event.getItems()) {
            builder.append(((Usuario) item).getNome()).append("<br />");
        }
         
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Items Transferred");
        msg.setDetail(builder.toString());
         
        FacesContext.getCurrentInstance().addMessage(null, msg);
    } 
 
    public void onSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", event.getObject().toString()));
    }
     
    public void onUnselect(UnselectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Unselected", event.getObject().toString()));
    }
     
    public void onReorder() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
    } 
	

	public ProjetoFacade getProjetoFacade() {
		return projetoFacade;
	}


	public void setProjetoFacade(ProjetoFacade projetoFacade) {
		this.projetoFacade = projetoFacade;
	}


	public Projeto getProjeto() {
		return projeto;
	}


	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public List<Projeto> getProjetosList() {
		return projetosList;
	}

	public void setProjetosList(List<Projeto> projetosList) {
		this.projetosList = projetosList;
	}


	public List<SelectItem> getProjetoSelectItems() {
		return projetoSelectItems;
	}


	public void setProjetoSelectItems(List<SelectItem> projetoSelectItems) {
		this.projetoSelectItems = projetoSelectItems;
	}


	public int getProjetoSelecionadoAba2() {
		return projetoSelecionadoAba2;
	}


	public void setProjetoSelecionadoAba2(int projetoSelecionadoAba2) {
		this.projetoSelecionadoAba2 = projetoSelecionadoAba2;
	}


	public UsuarioFacade getUsuarioFacade() {
		return usuarioFacade;
	}


	public void setUsuarioFacade(UsuarioFacade usuarioFacade) {
		this.usuarioFacade = usuarioFacade;
	}


	public DualListModel<Usuario> getUsuListModel() {
		return usuListModel;
	}


	public void setUsuListModel(DualListModel<Usuario> usuListModel) {
		this.usuListModel = usuListModel;
	}


	public List<Usuario> getUsuariosListDisponiveis() {
		return usuariosListDisponiveis;
	}


	public void setUsuariosListDisponiveis(List<Usuario> usuariosListDisponiveis) {
		this.usuariosListDisponiveis = usuariosListDisponiveis;
	}


	public List<Usuario> getUsuariosListSelecionados() {
		return usuariosListSelecionados;
	}


	public void setUsuariosListSelecionados(List<Usuario> usuariosListSelecionados) {
		this.usuariosListSelecionados = usuariosListSelecionados;
	}

	
	
	
	
	
}
