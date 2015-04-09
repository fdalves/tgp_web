package managedbean;

import java.io.Serializable;
import java.nio.channels.SeekableByteChannel;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.event.DragDropEvent;

import model.Atividade;
import model.ConfigAtividade;
import model.Usuario;
import ejb.AtividadeFacade;
import ejb.ConfigAtividadeFacade;
import ejb.UsuarioFacade;

@ManagedBean(name="atividadeMB")
@ViewScoped
public class AtividadeMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final float QUANTIDADE_HORAS_DIA = 7.5f;
	
	
	@EJB
	private AtividadeFacade atividadeFacade;
	@EJB
	private ConfigAtividadeFacade configAtividadeFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	
	private Atividade atividade =  new  Atividade();
	private List<Atividade> atividadeList = new ArrayList<Atividade>();
	private int diasTrabalhados = 0;
	private float horasTrabalho = 0;
	private ConfigAtividade configAtividade = new ConfigAtividade(); 
	private float teste = 0.0f;
	private boolean mostraPopUpConfig = false;
	private boolean popUpSalve = false;
	private int maxFeriados = 0;
	private List<Usuario> usariosList = new ArrayList<Usuario>();
	private List<Usuario> usuariosSelect = new ArrayList<Usuario>();
	
	public AtividadeMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.atividadeList = atividadeFacade.findAll();
		this.atividade =  new  Atividade();
		this.diasTrabalhados = 0;
		this.horasTrabalho = 0;
		this.usariosList = new ArrayList<Usuario>();
		this.usariosList = usuarioFacade.findAll();
		usuariosSelect = new ArrayList<Usuario>();
		this.iniPopUpConfig();
	}
	
	
	public void iniPopUpConfig(){
		
		this.configAtividade = new ConfigAtividade();
		
		this.configAtividade.setQuantDiasFolgaFeriado(0);
		this.configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
		this.configAtividade.setTrabDom(false);
		this.configAtividade.setTrabSab(false);
		this.mostraPopUpConfig = false;
		this.popUpSalve = false;
		this.maxFeriados = 0;
		
		this.atividade.setConfigAtividade(configAtividade);
	}
	
	public void salvar(){
		
		if (atividade.getAitividadeId() == 0){
			
			this.configAtividadeFacade.save(this.configAtividade);
			ConfigAtividade c = configAtividadeFacade.find(this.configAtividade.getConfigAtividadeId());
			this.atividade.setConfigAtividade(c);
			this.atividadeFacade.save(this.atividade);
			c.setAtividade(this.atividadeFacade.find(this.atividade.getAitividadeId()));
			this.configAtividadeFacade.update(c);
			
			String info = "Atividade cadastrada com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
			this.ini();
			
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
	            
	            if (this.popUpSalve && this.getConfigAtividade().isTrabSab() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )) diasT++;
	            if (this.popUpSalve && this.getConfigAtividade().isTrabDom() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )) diasT++;
	            
	            calendar.add( Calendar.DATE, 1 );  
	              
	        }         
	        
	        if (this.popUpSalve){
	        	this.diasTrabalhados = diasT - this.getConfigAtividade().getQuantDiasFolgaFeriado();
	        	this.horasTrabalho = this.diasTrabalhados * this.getConfigAtividade().getQuantHorasDias();
	        } else {
	        	this.diasTrabalhados = diasT;
	        	this.horasTrabalho = new Float(this.diasTrabalhados).floatValue() * QUANTIDADE_HORAS_DIA;
	        }
	        
	    }  
	

	private int somaDias( Date initialDate, Date finalDate ) {  
    
	    if( initialDate == null || finalDate == null ) {  
	        return 0;  
	    }  
	  
	    int days = ( int ) ( ( finalDate.getTime() - initialDate.getTime() )/( 24*60*60*1000 ) );  
	    return ( days > 0 ? days : 0 ) ;  
	}  

	
	public void openPopupConfig(){
		
		if (this.getAtividade().getDtIni() != null && this.getAtividade().getDtFim() != null){
			this.mostraPopUpConfig = true;
		} else {
			String info = "Data Inicio e Fim devem ser informadas";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_WARN,"", info));
			return ;
		}
		if (!this.popUpSalve){
			this.calculaData();
			this.configAtividade.setQuantDiasFolgaFeriado(0);
			this.configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
			this.configAtividade.setTrabDom(false);
			this.configAtividade.setTrabSab(false);
			this.maxFeriados = this.diasTrabalhados;
			
		}
		
		return ;
	}
	
	
	public void salvaConfigPopUp(){
		this.popUpSalve = true;
		this.calculaData();
		this.mostraPopUpConfig = false;
		
	}
	
	public void limparConfigPopUp(){
		this.iniPopUpConfig();
		this.popUpSalve = false;
		this.calculaData();
		this.mostraPopUpConfig = false;
		
	}
	
	public void excluir(Atividade atividade){
		this.atividadeFacade.delete(atividade);
		String info = "Atividade Excluido com Sucesso";
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,"", info));
		this.ini();
	}

	
	
	public void addUser(DragDropEvent ddEvent){
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		
		Usuario user = ((Usuario) ddEvent.getData());
		
		
		List<Usuario> list =  (List<Usuario>) session.getAttribute("usuList");
		
		if (list != null){
			for (Usuario usuario : list) {
				this.usuariosSelect.add(usuario);
			}
			this.usuariosSelect.add(user);
			 session.setAttribute("usuList", this.usuariosSelect);
			 
			 return;
			
		}
		
		System.out.println("entrou....");
		
		
		  
        this.usuariosSelect.add(user);
        
        this.usariosList.remove(user);
        
        session.setAttribute("usuList", this.usuariosSelect);
        
       System.out.println("saiu");
	}

	public AtividadeFacade getAtividadeFacade() {
		return atividadeFacade;
	}


	public void teste(){
		System.out.println("entrou...");
		
		
		Usuario u = new Usuario();
		
		u.setNome("SDAD");
		
		this.usuariosSelect.add(u);
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


	


	public float getHorasTrabalho() {
		return horasTrabalho;
	}


	public void setHorasTrabalho(float horasTrabalho) {
		this.horasTrabalho = horasTrabalho;
	}


	public boolean isMostraPopUpConfig() {
		return mostraPopUpConfig;
	}


	public void setMostraPopUpConfig(boolean mostraPopUpConfig) {
		this.mostraPopUpConfig = mostraPopUpConfig;
	}


	public boolean isPopUpSalve() {
		return popUpSalve;
	}


	public void setPopUpSalve(boolean popUpSalve) {
		this.popUpSalve = popUpSalve;
	}


	public int getMaxFeriados() {
		return maxFeriados;
	}


	public void setMaxFeriados(int maxFeriados) {
		this.maxFeriados = maxFeriados;
	}


	public ConfigAtividadeFacade getConfigAtividadeFacade() {
		return configAtividadeFacade;
	}


	public void setConfigAtividadeFacade(ConfigAtividadeFacade configAtividadeFacade) {
		this.configAtividadeFacade = configAtividadeFacade;
	}


	public UsuarioFacade getUsuarioFacade() {
		return usuarioFacade;
	}


	public void setUsuarioFacade(UsuarioFacade usuarioFacade) {
		this.usuarioFacade = usuarioFacade;
	}


	public List<Usuario> getUsariosList() {
		return usariosList;
	}


	public void setUsariosList(List<Usuario> usariosList) {
		this.usariosList = usariosList;
	}


	public List<Usuario> getUsuariosSelect() {
		return usuariosSelect;
	}


	public void setUsuariosSelect(List<Usuario> usuariosSelect) {
		this.usuariosSelect = usuariosSelect;
	}


	


	
	
	
	
	 
	
	
	
	
	
	
}
