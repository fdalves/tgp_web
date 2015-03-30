package managedbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

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
	private String[] nomesProjetos = null; 
	
	
	public ProjetoMB() {
		
	}

	
	public void onTabChange(){
		this.ini();
	}
	
	
		
	@PostConstruct
	public void ini(){
		
		this.projeto = new Projeto();
		this.projeto.setEscopoFecahdo(false);
		projetosList = projetoFacade.findAll();
		
		projetoSelectItems = new ArrayList<SelectItem>();
		
		for (Projeto projeto : projetosList) {
			SelectItem item = new SelectItem(projeto.getProjetoId(), projeto.getDescProjeto() + " - " + projeto.getNomeProjeto());
			projetoSelectItems.add(item);
		}
		
		projetoSelecionadoAba2 = 0;
		projetoSelecionadoAba3 = 0;
		
		usuariosListDisponiveis = usuarioFacade.findAll();
		usuariosListSelecionados = new ArrayList<Usuario>();
		usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis, usuariosListSelecionados);
		docProjeto = new DocProjeto(); 
		docProjetosList = docProjetoFacade.findAll();
		
		nomesProjetos = new String[projetosList.size()];
		int count = 0;
		for (Projeto projeto : projetosList) {
			
			nomesProjetos[count] = projeto.getNomeProjeto();
			count++;
		}
		
		
	}
	
	
	//--tab1----------------------------------
	
	public void salvar(){
	
		if(this.projeto.getProjetoId() == 0){
			
			if (!this.validaNomeProjeto(this.projeto.getNomeProjeto())){
				String info = "Nome Projeto ja Cadastrado..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Nome Projeto " + projeto.getNomeProjeto() + " - " + info, info));
				return;
			}
			
			
			if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
				String info = "Sigla ja Cadastrada..";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login " + projeto.getSiglaProjeto()+ " - " + info, info));
				return;
			}
			
			this.projetoFacade.save(projeto);
			String info = "Projeto Cadastrado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Projeto " + projeto.getNomeProjeto()+ " - " + info, info));
			
			
		} else{
			
			Projeto projetoPersist = this.projetoFacade.find(projeto.getProjetoId());
			
			if (projetoPersist.getNomeProjeto().equalsIgnoreCase(this.projeto.getNomeProjeto())){
				projetoPersist.setNomeProjeto(this.projeto.getNomeProjeto());
			} else {
				if (!this.validaNomeProjeto(this.projeto.getNomeProjeto())){
					String info = "Nome Projeto Ja Cadastrado..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Projeto " + projeto.getNomeProjeto()+ " - " + info, info));
					return;
				}
			}
			
			if (projetoPersist.getSiglaProjeto().equalsIgnoreCase(this.projeto.getSiglaProjeto())){
				projetoPersist.setSiglaProjeto(this.projeto.getSiglaProjeto());
			} else {
				if (!this.validaSiglaProjeto(this.projeto.getSiglaProjeto())){
					String info = "Sigla Projeto Ja Cadastrada..";
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,"Sigla Projeto " + projeto.getSiglaProjeto()+ " - " + info, info));
					return;
				}
			}
			
			projetoPersist.setDescProjeto(this.projeto.getDescProjeto());
			projetoPersist.setEscopoFecahdo(this.projeto.getEscopoFecahdo());
			projetoPersist.setData_ini(this.projeto.getData_ini());
			projetoPersist.setData_fim(this.projeto.getData_fim());
			
			this.projetoFacade.update(projetoPersist);
			String info = "Projeto Alterado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"projeto " + projeto.getNomeProjeto()+ " - " + info, info));
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
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Projeto Excluido com Sucesso"+ " - " + info, info));
		this.ini();
	}
	
	
	
	
	


    // tab 2 ---------------------------------------------------------------------------
    
    
   
    
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
        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,""+ " - " + info, info));
    	
    }
    
    
    
    //----------------tab3---
    
    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        this.docProjeto.setDoc(event.getFile().getContents());
        this.docProjeto.setType(event.getFile().getContentType());
        String extensao = event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.') + 1); 
        this.docProjeto.setExtensao("."+extensao);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    public void setProjetoDoc(DocProjeto docProjeto){
    	this.projetoSelecionadoAba3 = docProjeto.getProjeto().getProjetoId();
    }
    
        
    public void salvarDoc(){
    	
    	Projeto projeto = this.projetoFacade.find(this.projetoSelecionadoAba3);
    	docProjeto.setProjeto(projeto);
    	
    	
    	if (this.docProjeto.getDocProjetoId() == 0){
	    	
	    	if (this.docProjeto.getDoc() == null){
	    		String info = "Selecione um Documento";
	    	    FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR, " - " + info, info));
	    	    return;
	    	}
	    	this.docProjetoFacade.save(docProjeto);
	        String info = "Documento Associado ao projeto";
	        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO, " - " + info, info));
	        
    	} else {
    		
    		DocProjeto docProjetoPersist = this.docProjetoFacade.find(this.docProjeto.getDocProjetoId());
    		
    		docProjetoPersist.setDescDoc(this.docProjeto.getDescDoc());
    		docProjetoPersist.setNomeDoc(this.docProjeto.getNomeDoc());
    		docProjetoPersist.setProjeto(projeto);
    		if (docProjeto.getDoc() != null){
    			
    			docProjetoPersist.setDoc(docProjeto.getDoc());
    			docProjetoPersist.setType(docProjeto.getType());
    			docProjetoPersist.setExtensao(docProjeto.getExtensao());
    		}
    		
    		this.docProjetoFacade.update(docProjetoPersist);
    		String info = "Documento alterado com sucesso";
 	        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO, " - " + info, info));
    		
    	}
        
        this.ini();
    	
    }
    
    
    public void excluirDoc(DocProjeto docProjeto){
    	this.docProjetoFacade.delete(docProjeto);
		String info = "Doc Projeto Excluido com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Documento Excluido com Sucesso", info));
		this.ini();
    	
    }
    
    
	public void downloadDoc(DocProjeto docProjeto) {

		ServletContext sContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String arquivo = sContext.getRealPath("/temp") + File.separator	+ docProjeto.getNomeDoc() + docProjeto.getExtensao();

		File file = new File(arquivo);

		try {
			FileOutputStream fileOuputStream = new FileOutputStream(file);
			fileOuputStream.write(docProjeto.getDoc());
			fileOuputStream.close();

			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String fileName = (docProjeto.getNomeDoc() + docProjeto.getExtensao());
		
		this.downloadFile(fileName, file, docProjeto.getType(), FacesContext.getCurrentInstance());
		
	}

	public  void downloadFile(String filename, File file,String mimeType, FacesContext facesContext) {
		FileInputStream in = null;
		try {
			ExternalContext context = facesContext.getExternalContext();
			System.out.println("Aqui para baixar o " + filename);
			HttpServletResponse response = (HttpServletResponse) context
					.getResponse();
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ filename + "\"");
			response.setContentLength((int) file.length());
			response.setContentType(mimeType);
			in = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			byte[] buf = new byte[(int) file.length()];
			int count;
			while ((count = in.read(buf)) >= 0) {
				out.write(buf, 0, count);
			}
			facesContext.responseComplete();
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		
 
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


	public String[] getNomesProjetos() {
		return nomesProjetos;
	}


	public void setNomesProjetos(String[] nomesProjetos) {
		this.nomesProjetos = nomesProjetos;
	}


	


	


	
	
	
	
	
	
}
