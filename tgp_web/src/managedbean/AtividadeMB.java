package managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import model.Atividade;
import model.ConfigAtividade;
import ejb.AtividadeFacade;

@ManagedBean(name="atividadeMB")
@ViewScoped
public class AtividadeMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final float QUANTIDADE_HORAS_DIA = 7.5f;
	
	
	@EJB
	private AtividadeFacade atividadeFacade;
	private Atividade atividade =  new  Atividade();
	private List<Atividade> atividadeList = new ArrayList<Atividade>();
	private int diasTrabalhados = 0;
	private ConfigAtividade configAtividade = new ConfigAtividade(); 
	private float teste = 0.0f;
	
	
	public AtividadeMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.atividadeList = atividadeFacade.findAll();
		this.atividade =  new  Atividade();
		this.diasTrabalhados = 0;
		
		this.configAtividade = new ConfigAtividade();
		
		this.configAtividade.setQuantDiasFolgaFeriado(0);
		this.configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
		this.configAtividade.setTrabDom(false);
		this.configAtividade.setTrabSab(false);
		
		this.atividade.setConfigAtividade(configAtividade);
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
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"Atividade " + atividade.getAtividadeNome()+ info, null));
		}
		
		this.ini();
	}
	
	
	
	public void calculaData(){
		
		Date initialDate = this.atividade.getDtIni(); 
		Date finalDate = this.atividade.getDtFim();
	    
		if(initialDate == null || finalDate == null){
			
			return;
		}
		
		if (initialDate.getTime() > finalDate.getTime()){
			String info = "Data Inicial deve ser menor que a data final";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, null));
			this.atividade.setDtIni(null);
			
		}
		
	        int diasT = 0;  
	        int totD = somaDias( initialDate, finalDate );        
	          
	        Calendar calendar = new GregorianCalendar();  
	          
	        
	        calendar.setTime(initialDate);  
	          
	        for( int i = 0; i <= totD; i++ ) {  
	              
	            if( !( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) && !( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) ) {  
	                diasT++;  
	            }  
	            calendar.add( Calendar.DATE, 1 );  
	              
	        }         
	        this.diasTrabalhados = diasT;  
	    }  
	

	private int somaDias( Date initialDate, Date finalDate ) {  
    
    if( initialDate == null || finalDate == null ) {  
        return 0;  
    }  
  
    int days = ( int ) ( ( finalDate.getTime() - initialDate.getTime() )/( 24*60*60*1000 ) );  
      
      
    return ( days > 0 ? days : 0 ) ;  
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


	public int getDiasTrabalhados() {
		return diasTrabalhados;
	}


	public void setDiasTrabalhados(int diasTrabalhados) {
		this.diasTrabalhados = diasTrabalhados;
	}


	public ConfigAtividade getConfigAtividade() {
		return configAtividade;
	}


	public void setConfigAtividade(ConfigAtividade configAtividade) {
		this.configAtividade = configAtividade;
	}


	public float getTeste() {
		return teste;
	}


	public void setTeste(float teste) {
		this.teste = teste;
	}
	
	
	
	 
	
	
	
	
	
	
}
