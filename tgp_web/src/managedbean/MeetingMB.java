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

import org.primefaces.event.FileUploadEvent;

import model.Atividade;
import model.DocAtividade;
import model.Projeto;
import ejb.AtividadeFacade;
import ejb.ProjetoFacade;

@ManagedBean(name="meetingMB")
@ViewScoped
public class MeetingMB  implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private ProjetoFacade projetoFacade;
	@EJB
	private AtividadeFacade atividadeFacade;
	
	
	private int idProjetoSelect =0 ;
	private int idAtiviSelect =0 ;
	private List<SelectItem> projetoSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> atividadesSelectItems = new ArrayList<SelectItem>();
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	private boolean carregaAtividade = false;
	private DocAtividade docAtividade = new DocAtividade();
	
	
	public MeetingMB() {
		
	}

	
	public void onTabChange(){
		this.ini();
	}
	
	
		
	@PostConstruct
	public void ini(){
		
		idProjetoSelect =0 ;
		idAtiviSelect= 0;
		this.projetoSelectItems = new ArrayList<SelectItem>();
		this.projetosList = new ArrayList<Projeto>();
		this.projetosList = this.projetoFacade.findAll();
		for (Projeto projeto : projetosList) {
			SelectItem item = new SelectItem(projeto.getProjetoId(), projeto.getSiglaProjeto() + " - " + projeto.getNomeProjeto());
			projetoSelectItems.add(item);
		}
		
		carregaAtividade = false;
		docAtividade = new DocAtividade();
		
	}

	
	public void selectProjeto(){
		Projeto p = projetoFacade.find(this.idProjetoSelect);
		if (p != null){
			List<Atividade> atividades = atividadeFacade.findAtividadeByProjeto(p.getProjetoId());
			this.atividadesSelectItems = new ArrayList<SelectItem>();
			for (Atividade atividade : atividades) {
				SelectItem item = new SelectItem(atividade.getAtividadeId(), atividade.getAtividadeNome());
				atividadesSelectItems.add(item);
			}
			
			if (atividadesSelectItems.size() > 0){
				this.carregaAtividade = true;
			}
		}
	}
	
	
	public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        this.docAtividade.setDoc(event.getFile().getContents());
        this.docAtividade.setTypeName(event.getFile().getContentType());
        String extensao = event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.') + 1); 
        this.docAtividade.setExtensao("."+extensao);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	

	public ProjetoFacade getProjetoFacade() {
		return projetoFacade;
	}


	public void setProjetoFacade(ProjetoFacade projetoFacade) {
		this.projetoFacade = projetoFacade;
	}


	public AtividadeFacade getAtividadeFacade() {
		return atividadeFacade;
	}


	public void setAtividadeFacade(AtividadeFacade atividadeFacade) {
		this.atividadeFacade = atividadeFacade;
	}


	public int getIdProjetoSelect() {
		return idProjetoSelect;
	}


	public void setIdProjetoSelect(int idProjetoSelect) {
		this.idProjetoSelect = idProjetoSelect;
	}


	public List<SelectItem> getProjetoSelectItems() {
		return projetoSelectItems;
	}


	public void setProjetoSelectItems(List<SelectItem> projetoSelectItems) {
		this.projetoSelectItems = projetoSelectItems;
	}


	public List<Projeto> getProjetosList() {
		return projetosList;
	}


	public void setProjetosList(List<Projeto> projetosList) {
		this.projetosList = projetosList;
	}


	public List<SelectItem> getAtividadesSelectItems() {
		return atividadesSelectItems;
	}


	public void setAtividadesSelectItems(List<SelectItem> atividadesSelectItems) {
		this.atividadesSelectItems = atividadesSelectItems;
	}


	public boolean isCarregaAtividade() {
		return carregaAtividade;
	}


	public void setCarregaAtividade(boolean carregaAtividade) {
		this.carregaAtividade = carregaAtividade;
	}


	public int getIdAtiviSelect() {
		return idAtiviSelect;
	}


	public void setIdAtiviSelect(int idAtiviSelect) {
		this.idAtiviSelect = idAtiviSelect;
	}


	public DocAtividade getDocAtividade() {
		return docAtividade;
	}


	public void setDocAtividade(DocAtividade docAtividade) {
		this.docAtividade = docAtividade;
	}
	
	
	


	


	
	
	
	
	
	
}
