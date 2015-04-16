package managedbean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import model.Atividade;
import model.Cargo;
import model.ConfigAtividade;
import model.Projeto;
import model.Usuario;
import model.UsuarioAtividade;

import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.TabChangeEvent;

import ejb.AtividadeFacade;
import ejb.ConfigAtividadeFacade;
import ejb.ProjetoFacade;
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
	@EJB
	private ProjetoFacade projetoFacade;
	
	
	private Atividade atividade =  new  Atividade();
	private List<Atividade> atividadeList = new ArrayList<Atividade>();
	private int diasTrabalhados = 0;
	private float horasTrabalho = 0;
	private ConfigAtividade configAtividade = new ConfigAtividade(); 
	private boolean mostraPopUpConfig = false;
	private boolean popUpSalve = false;
	private int maxFeriados = 0;
	private List<Usuario> usariosList = new ArrayList<Usuario>();
	private List<UsuarioAtividade> usuarioDispo = new ArrayList<UsuarioAtividade>();
	private List<UsuarioAtividade> usuariosSelect = new ArrayList<UsuarioAtividade>();
	private boolean disableTab = false;
	private String selectType = "";
	private float horaAtvUser = 0;
	private float diaAtvUser = 0;
	private UsuarioAtividade usuarioAtividadeSelect =new UsuarioAtividade();
	private List<SelectItem> projetoSelectItems = new ArrayList<SelectItem>();
	private int idProjetoSelect =0 ;
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	private String prioridade = new String();
	private int idUsuSelect =0 ;
	private List<Usuario> usuariosList =  new  ArrayList<Usuario>();
	private List<SelectItem> usuariosSelectItems = new ArrayList<SelectItem>();
	
	
	public AtividadeMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.atividadeList = atividadeFacade.findAll();
		this.atividade =  new  Atividade();
		this.diasTrabalhados = 0;
		this.horasTrabalho = 0;
		this.projetoSelectItems = new ArrayList<SelectItem>();
		this.projetosList = this.projetoFacade.findAll();
		for (Projeto projeto : projetosList) {
			SelectItem item = new SelectItem(projeto.getProjetoId(), projeto.getSiglaProjeto() + " - " + projeto.getNomeProjeto());
			projetoSelectItems.add(item);
		}
		
		this.idUsuSelect =0 ;
		this.usuariosList =  this.usuarioFacade.findAll();
		for (Usuario usu : usuariosList) {
			SelectItem item = new SelectItem(usu.getUsuarioId(), usu.getNome());
			usuariosSelectItems.add(item);
		}
		
		
		this.prioridade = "normal";
		this.iniPopUpConfig();
		this.iniUsu();
		
	}
	
	
	public void iniUsu(){
		
		this.usariosList = new ArrayList<Usuario>();
		this.usariosList = usuarioFacade.findAll();
		this.usuarioDispo = new ArrayList<UsuarioAtividade>();
		this.usuariosSelect = new ArrayList<UsuarioAtividade>();
		int tempId = 0;
		for (Usuario u : usariosList ) {
			UsuarioAtividade usuarioAtividade = new UsuarioAtividade();
			usuarioAtividade.setTempId(tempId);
			tempId++;
			Cargo cargo = new Cargo();
			usuarioAtividade.setCargo(cargo);
			cargo.setUsuarioAtividade(usuarioAtividade);
			usuarioAtividade.setUsuario(u);
			this.usuarioDispo.add(usuarioAtividade);
		}
		this.disableTab = true;
		this.selectType = "R";
		this.horaAtvUser = 0;
		this.diaAtvUser = 0;
		this.usuarioAtividadeSelect =new UsuarioAtividade();
		this.usuarioAtividadeSelect.setCargo(new Cargo());
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
			if (this.usuariosSelect != null && !this.usuariosSelect.isEmpty()){
				this.atividade.setConfigAtividade(this.configAtividade);
				this.atividade.setUsuarioAtividades(this.usuariosSelect);
				this.atividade.setProjeto(projetoFacade.find(this.idProjetoSelect));
				this.atividade.setGerente(usuarioFacade.find(this.idUsuSelect));
				String msg = this.atividadeFacade.savarAtividade(this.atividade);
				if (msg != null){
					FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_FATAL,msg,""));
					return;
				}
			} else {
				String info = "Selecione pelo menos um usuário respossável pela atividade !";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_FATAL,info, ""));
				return;
			}
			String info = "Atividade cadastrada com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,info, ""));
			this.ini();
		}
			/***
			
			
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
		
		*/
		
		
		
	}
	
	
	public void carregaCargo(UsuarioAtividade usuarioAtividade){
			this.usuarioAtividadeSelect = usuarioAtividade;
	}
	
	
	public void calculaData(){
		
		Date initialDate = this.atividade.getDtIni(); 
		Date finalDate = this.atividade.getDtFim();
	    
		if(initialDate == null || finalDate == null){
			
			return;
		}
		
		if (initialDate.getTime() > finalDate.getTime()){
			String info = "Data Inicial deve ser menor que a data final";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, ""));
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
	        	
	        	if (this.getConfigAtividade().getQuantDiasFolgaFeriado() >= diasT ){
	        		String info = "Verifique a configuração de Dias Úteis";
	    			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,  info, ""));
	        		return;
	        	}
	        	
	        	this.diasTrabalhados = diasT - this.getConfigAtividade().getQuantDiasFolgaFeriado();
	        	this.horasTrabalho = this.diasTrabalhados * this.getConfigAtividade().getQuantHorasDias();
	        } else {
	        	this.diasTrabalhados = diasT;
	        	this.horasTrabalho = new Float(this.diasTrabalhados).floatValue() * QUANTIDADE_HORAS_DIA;
	        }
	        
	        this.divideReplicaTempo();
	        
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
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_WARN,info,"" ));
			return ;
		}
		if (!this.popUpSalve){
			this.calculaData();
			this.configAtividade.setQuantDiasFolgaFeriado(0);
			this.configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
			this.configAtividade.setTrabDom(false);
			this.configAtividade.setTrabSab(false);
			this.maxFeriados = this.diasTrabalhados - 1;
			
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
		FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,info,"" ));
		this.ini();
	}

	
	public void addUser(DragDropEvent ddEvent){
		UsuarioAtividade user = ((UsuarioAtividade) ddEvent.getData());
		this.usuariosSelect.add(user);
        this.usuarioDispo.remove(user);
        this.divideReplicaTempo();
	}
	
	
	public void deleteUsu(UsuarioAtividade user){
		this.usuariosSelect.remove(user);
        this.usuarioDispo.add(user);
        this.divideReplicaTempo();
	}
	
	
	public void onTabChange(TabChangeEvent event){
		
		String id = event.getTab().getId();
		
		if (id.equals("t1")){
				if (this.getAtividade().getDtIni() != null &&  this.getAtividade().getDtFim() != null ){
				this.disableTab = false;
			} else {
				UIComponent components = event.getTab().getParent();
				AccordionPanel accordionPanel = (AccordionPanel) components;
				accordionPanel.setActiveIndex("-1");
				System.out.println(components.getId());
				String info = "Selecione Data Ini e Data final da atividade";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_WARN,info, ""));
			}
		}
	}
	
	
	public void divideReplicaTempo(){
		
		this.diaAtvUser = this.diasTrabalhados;
		this.horaAtvUser = this.horasTrabalho;
		
		if (this.selectType.equals("D") && this.usuariosSelect.size() >= 1){
			float dias = this.diaAtvUser / this.usuariosSelect.size();
			BigDecimal bd = new BigDecimal(dias);
			bd= bd.setScale(1, RoundingMode.CEILING);
			this.diaAtvUser = bd.floatValue();
			float horas = this.horaAtvUser / this.usuariosSelect.size();
			bd = new BigDecimal(horas);
			bd = bd.setScale(1, RoundingMode.CEILING);
			this.horaAtvUser = bd.floatValue();
		} 
	}

	
	public void salvaCargo(){
		UsuarioAtividade usuarioAtividade = this.usuarioAtividadeSelect;
		
		for (UsuarioAtividade u : this.usuariosSelect) {
			if (usuarioAtividade.getTempId() == u.getTempId()){
				u.setCargo(usuarioAtividade.getCargo());
				break;
			}
		}
		
		this.usuarioAtividadeSelect = new UsuarioAtividade();
		Cargo cargo = new Cargo();
		this.usuarioAtividadeSelect.setCargo(cargo);
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

	public List<UsuarioAtividade> getUsuarioDispo() {
		return usuarioDispo;
	}


	public void setUsuarioDispo(List<UsuarioAtividade> usuarioDispo) {
		this.usuarioDispo = usuarioDispo;
	}


	public List<UsuarioAtividade> getUsuariosSelect() {
		return usuariosSelect;
	}


	public void setUsuariosSelect(List<UsuarioAtividade> usuariosSelect) {
		this.usuariosSelect = usuariosSelect;
	}


	public boolean isDisableTab() {
		return disableTab;
	}


	public void setDisableTab(boolean disableTab) {
		this.disableTab = disableTab;
	}


	public String getSelectType() {
		return selectType;
	}


	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}


	public float getHoraAtvUser() {
		return horaAtvUser;
	}


	public void setHoraAtvUser(float horaAtvUser) {
		this.horaAtvUser = horaAtvUser;
	}


	public float getDiaAtvUser() {
		return diaAtvUser;
	}


	public void setDiaAtvUser(float diaAtvUser) {
		this.diaAtvUser = diaAtvUser;
	}


	public UsuarioAtividade getUsuarioAtividadeSelect() {
		return usuarioAtividadeSelect;
	}


	public void setUsuarioAtividadeSelect(UsuarioAtividade usuarioAtividadeSelect) {
		this.usuarioAtividadeSelect = usuarioAtividadeSelect;
	}


	public List<SelectItem> getProjetoSelectItems() {
		return projetoSelectItems;
	}


	public void setProjetoSelectItems(List<SelectItem> projetoSelectItems) {
		this.projetoSelectItems = projetoSelectItems;
	}


	public int getIdProjetoSelect() {
		return idProjetoSelect;
	}


	public void setIdProjetoSelect(int idProjetoSelect) {
		this.idProjetoSelect = idProjetoSelect;
	}


	public List<Projeto> getProjetosList() {
		return projetosList;
	}


	public void setProjetosList(List<Projeto> projetosList) {
		this.projetosList = projetosList;
	}


	public String getPrioridade() {
		return prioridade;
	}


	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}


	public ProjetoFacade getProjetoFacade() {
		return projetoFacade;
	}


	public void setProjetoFacade(ProjetoFacade projetoFacade) {
		this.projetoFacade = projetoFacade;
	}


	public int getIdUsuSelect() {
		return idUsuSelect;
	}


	public void setIdUsuSelect(int idUsuSelect) {
		this.idUsuSelect = idUsuSelect;
	}


	public List<Usuario> getUsuariosList() {
		return usuariosList;
	}


	public void setUsuariosList(List<Usuario> usuariosList) {
		this.usuariosList = usuariosList;
	}


	public List<SelectItem> getUsuariosSelectItems() {
		return usuariosSelectItems;
	}


	public void setUsuariosSelectItems(List<SelectItem> usuariosSelectItems) {
		this.usuariosSelectItems = usuariosSelectItems;
	}


	


	


	
	
	
	
	 
	
	
	
	
	
	
}
