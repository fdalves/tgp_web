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

import model.Projeto;
import ejb.ProjetoFacade;

@ManagedBean(name="projetoMB")
@ViewScoped
public class ProjetoMB  implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	@EJB
	private ProjetoFacade projetoFacade;
	private Projeto projeto = new Projeto();
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	
	
	
	public void change(){
		
		System.out.println(this.projeto.getEscopoFecahdo());
	}
	
	public ProjetoMB() {

		
	}

	
	@PostConstruct
	public void ini(){
		this.projeto = new Projeto();
		projetosList = projetoFacade.findAll();
	}
	
	
	
	public void salvar(){
	
		this.projetoFacade.save(projeto);
		String info = "Projeto Cadastrado com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Projeto " + projeto.getNomeProjeto(), info));
		this.ini();
	}

	
	public void excluir(Projeto projeto){

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
	
	
	
}
