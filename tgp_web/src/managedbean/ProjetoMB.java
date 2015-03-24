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
	
	
	public ProjetoMB() {
		
	}

	
	public void onTabChange(){
		
	}
	
	
	public void onTabClose(){
		
	}
	
	
	@PostConstruct
	public void ini(){
		this.projeto = new Projeto();
		projetosList = projetoFacade.findAll();
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
	
	
	
}
