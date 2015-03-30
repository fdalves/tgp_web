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

import model.Atividade;
import ejb.AtividadeFacade;

@ManagedBean(name="atividadeMB")
@ViewScoped
public class AtividadeMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private AtividadeFacade atividadeFacade;
	private Atividade atividade =  new  Atividade();
	private List<Atividade> atividadeList = new ArrayList<Atividade>();
	
	
	
	public AtividadeMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.atividadeList = atividadeFacade.findAll();
		this.atividade =  new  Atividade();
	}
	
	
	
	public void salvar(){
		
		if (atividade.getAitividadeId() == 0){
			this.atividadeFacade.save(atividade);
			String info = "Atividade Cadastrado com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Atividade " + atividade.getAtividadeNome(), info));

		} else {
			Atividade atividadePersist = this.atividadeFacade.find(atividade.getAitividadeId());
			
			
			
			atividadePersist.setAtividadeNome(this.atividade.getAtividadeNome());
			atividadePersist.setDescAtividade(atividade.getDescAtividade());
			atividadePersist.setDtIni(atividade.getDtIni());
			atividadePersist.setDtFim(atividade.getDtFim());
			atividadePersist.setProjeto(this.atividade.getProjeto());
			
			
			
			this.atividadeFacade.update(atividadePersist);
			String info = "Atividae Alterada com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Atividade " + atividade.getAtividadeNome(), info));
		}
		
		this.ini();
	}
	
	

	public void calculadata(){
		System.out.println("calcula....");
		
	}

	public void excluir(Atividade atividade){
		this.atividadeFacade.delete(atividade);
		String info = "Atividade Excluido com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
		this.ini();
	}


	public AtividadeFacade getAtividadeFacade() {
		return atividadeFacade;
	}


	public void setAtividadeFacade(AtividadeFacade atividadeFacade) {
		this.atividadeFacade = atividadeFacade;
	}


	public Atividade getAtividade() {
		return atividade;
	}


	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}


	public List<Atividade> getAtividadeList() {
		return atividadeList;
	}


	public void setAtividadeList(List<Atividade> atividadeList) {
		this.atividadeList = atividadeList;
	}
	
	
	
	 
	
	
	
	
	
	
}
