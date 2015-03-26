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

import model.DocProjeto;
import model.Projeto;
import model.Usuario;
import model.UsuarioProjeto;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;

import ejb.DocProjetoFacade;
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
	@EJB
	private DocProjetoFacade docProjetoFacade;
	
	private Projeto projeto = new Projeto();
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	private List<SelectItem> projetoSelectItems = new ArrayList<SelectItem>();
	private int projetoSelecionadoAba2 = 0;
	private int projetoSelecionadoAba3 = 0;
	private DualListModel<Usuario> usuListModel = new DualListModel<Usuario>();
	private List<Usuario> usuariosListDisponiveis = new ArrayList<Usuario>();
	private List<Usuario> usuariosListSelecionados = new ArrayList<Usuario>();
	private DocProjeto docProjeto = new DocProjeto();
	private List<DocProjeto> docProjetosList = new ArrayList<DocProjeto>();
	
	
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
		usuariosListSelecionados = new ArrayList<Usuario>();
		usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis, usuariosListSelecionados);
		docProjeto = new DocProjeto(); 
		docProjetosList = docProjetoFacade.findAll();
		
	}
	
	
	//--tab1----------------------------------
	
	public void salvar(){
	
		if(this.projeto.getProjetoId() == 0){
			
			if (!this.validaNomeProjeto(this.projeto.getNomeProjeto())){
				String info = "Nome Projeto ja Cadastrado..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Nome Projeto " + projeto.getNomeProjeto(), info));
				return;
			}
			
			
			if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
				String info = "Sigla ja Cadastrada..";
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
					String info = "Nome Projeto Ja Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Projeto " + projeto.getNomeProjeto(), info));
					return;
				}
			}
			
			if (projetoPersist.getSiglaProjeto().equalsIgnoreCase(this.projeto.getSiglaProjeto())){
				projetoPersist.setSiglaProjeto(this.projeto.getSiglaProjeto());
			} else {
				if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
					String info = "Sigla Projeto Ja Cadastrada..";
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
	
	
	
	
	


    // tab 2 ---------------------------------------------------------------------------
    
    
    public void salvarTab2(){
    	
    	System.out.println(projetoSelecionadoAba2);
    }
    
    
    public void selectProjeto(){
    	
    	if (projetoSelecionadoAba2 == 0){
    		this.usuariosListDisponiveis = usuarioFacade.findAll();
    		this.usuariosListSelecionados = new ArrayList<Usuario>();
    		this.usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis, usuariosListSelecionados);
    		return;
    	}
    	
    	Projeto projeto = projetoFacade.find(projetoSelecionadoAba2);
    	List<UsuarioProjeto> usuarioProjetos = projeto.getUsuarioProjetos();
    	
    	List<Usuario> usuariosSelecionados = new ArrayList<Usuario>();
    	
    	for (UsuarioProjeto usuarioProjeto : usuarioProjetos) {
			usuariosSelecionados.add(usuarioProjeto.getUsuario());
		}
    	
    	List<Usuario> usuariosDispoveis = new ArrayList<Usuario>();
    	
    	List<Usuario> usuariosAll= usuarioFacade.findAll();
    	
    	for (Usuario usuario : usuariosAll) {
			
    		boolean contem = false;
    		
    		for (Usuario usuarioProjeto : usuariosSelecionados) {
				
    			if(usuario.getUsuarioId() == usuarioProjeto.getUsuarioId()) contem = true;
			}
    		
    		if (!contem) usuariosDispoveis.add(usuario);
    		
		}
    	
    	this.usuariosListSelecionados = usuariosSelecionados;
    	this.usuariosListDisponiveis = usuariosDispoveis;
    	
    	this.usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis,usuariosListSelecionados);
    	
    }
    
    
    
    public void salvarUsuariosProjeto(){
    	
    	
    	this.usuariosListDisponiveis = this.usuListModel.getSource();
    	this.usuariosListSelecionados = this.usuListModel.getTarget();
    	
    	Projeto projeto = projetoFacade.find(projetoSelecionadoAba2);
    	
    	projeto.setUsuarioProjetos(new ArrayList<UsuarioProjeto>());
    	
    	projeto = projetoFacade.update(projeto);
    	
    	List<UsuarioProjeto> list = new ArrayList<UsuarioProjeto>();
    	
    	for (Usuario usuario : this.usuariosListSelecionados ) {
			UsuarioProjeto usuarioProjeto = new UsuarioProjeto();
			usuarioProjeto.setProjeto(projeto);
			usuarioProjeto.setUsuario(usuario);
			list.add(usuarioProjeto);
    	}
    	projeto.setUsuarioProjetos(list);
    	
    	
    	projetoFacade.update(projeto);
    	
    	String info = "Usuario(s) Associado(s) ao projeto";
        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
    	
    }
    
    
    
    //----------------tab3---
    
    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        this.docProjeto.setDoc(event.getFile().getContents());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    public void selectProjetoTab3(){
    	
    	
    	
    }
    
    public void salvarDoc(){
    	
    	if (this.docProjeto.getDoc() == null){
    		String info = "Selecione um Documento";
    	    FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"", info));
    	    return;
    	}
    	
    	Projeto projeto = this.projetoFacade.find(this.projetoSelecionadoAba3);
    	docProjeto.setProjeto(projeto);
    	this.docProjetoFacade.save(docProjeto);
    	
    	
        String info = "Documento Associado ao projeto";
        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
        
        this.ini();
    	
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


	public DocProjeto getDocProjeto() {
		return docProjeto;
	}


	public void setDocProjeto(DocProjeto docProjeto) {
		this.docProjeto = docProjeto;
	}


	public int getProjetoSelecionadoAba3() {
		return projetoSelecionadoAba3;
	}


	public void setProjetoSelecionadoAba3(int projetoSelecionadoAba3) {
		this.projetoSelecionadoAba3 = projetoSelecionadoAba3;
	}


	public DocProjetoFacade getDocProjetoFacade() {
		return docProjetoFacade;
	}


	public void setDocProjetoFacade(DocProjetoFacade docProjetoFacade) {
		this.docProjetoFacade = docProjetoFacade;
	}


	public List<DocProjeto> getDocProjetosList() {
		return docProjetosList;
	}


	public void setDocProjetosList(List<DocProjeto> docProjetosList) {
		this.docProjetosList = docProjetosList;
	}

	
	
	
	
	
}
