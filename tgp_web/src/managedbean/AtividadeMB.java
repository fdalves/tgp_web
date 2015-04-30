package managedbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import model.Atividade;
import model.Cargo;
import model.ConfigAtividade;
import model.DocAtividade;
import model.Projeto;
import model.Usuario;
import model.UsuarioAtividade;

import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FileUploadEvent;
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
	private int diasTrabalhados = 0;
	private float horasTrabalho = 0;
	private boolean mostraPopUpConfig = false;
	private boolean popUpSalve = false;
	private int maxFeriados = 0;
	private List<Usuario> usariosList = new ArrayList<Usuario>();
	private List<UsuarioAtividade> usuarioDispo = new ArrayList<UsuarioAtividade>();
	private List<UsuarioAtividade> usuariosSelect = new ArrayList<UsuarioAtividade>();
	private boolean disableTab = false;
	private float horaAtvUser = 0;
	private float diaAtvUser = 0;
	private UsuarioAtividade usuarioAtividadeSelect =new UsuarioAtividade();
	private List<SelectItem> projetoSelectItems = new ArrayList<SelectItem>();
	private int idProjetoSelect =0 ;
	private List<Projeto> projetosList =  new  ArrayList<Projeto>();
	private int idUsuSelect =0 ;
	private List<Usuario> usuariosList =  new  ArrayList<Usuario>();
	private List<SelectItem> usuariosSelectItems = new ArrayList<SelectItem>();
	private DocAtividade docAtividade = new DocAtividade();
	private List<Atividade> atividadesList = new ArrayList<Atividade>();
	
	
	public AtividadeMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.atividade =  new  Atividade();
		this.diasTrabalhados = 0;
		this.horasTrabalho = 0;
		this.projetoSelectItems = new ArrayList<SelectItem>();
		this.projetosList = new ArrayList<Projeto>();
		this.projetosList = this.projetoFacade.findAll();
		for (Projeto projeto : projetosList) {
			SelectItem item = new SelectItem(projeto.getProjetoId(), projeto.getSiglaProjeto() + " - " + projeto.getNomeProjeto());
			projetoSelectItems.add(item);
		}
		
		this.idUsuSelect =0 ;
		this.usuariosList = new ArrayList<Usuario>();
		this.usuariosList =  this.usuarioFacade.findAll();
		this.usuariosSelectItems = new ArrayList<SelectItem>();
		for (Usuario usu : usuariosList) {
			SelectItem item = new SelectItem(usu.getUsuarioId(), usu.getNome());
			usuariosSelectItems.add(item);
		}
		
		this.docAtividade = new DocAtividade();
		this.atividade.setPrioridade("normal");
		List<DocAtividade> docAtividades = new ArrayList<DocAtividade>();
		this.atividade.setDocAtividades(docAtividades);
		this.atividadesList = new ArrayList<Atividade>();
		this.atividadesList = this.atividadeFacade.findAll();
		
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
		this.atividade.setDivideReplicaTempo("R");
		this.horaAtvUser = 0;
		this.diaAtvUser = 0;
		this.usuarioAtividadeSelect =new UsuarioAtividade();
		this.usuarioAtividadeSelect.setCargo(new Cargo());
	}
	
	public void iniPopUpConfig(){
		
		ConfigAtividade configAtividade  = new ConfigAtividade();
		
		configAtividade.setQuantDiasFolgaFeriado(0);
		configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
		configAtividade.setTrabDom(false);
		configAtividade.setTrabSab(false);
		mostraPopUpConfig = false;
		popUpSalve = false;
		maxFeriados = 0;
		
		this.atividade.setConfigAtividade(configAtividade);
	}
	
	
	public void salvar(){
		
		if (atividade.getAtividadeId() == 0){
			if (this.usuariosSelect != null && !this.usuariosSelect.isEmpty()){
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
		} else {
			Atividade atividadeNew = this.atividade;
			
			List<DocAtividade> docAtividadesOld = atividadeFacade.findDocAtividade(atividadeNew.getAtividadeId());
			List<DocAtividade> docAtividadesNew = this.atividade.getDocAtividades();
			
			List<UsuarioAtividade> usuarioAtividadesOld = atividadeFacade.findUsuarioAtividade(atividadeNew.getAtividadeId());
			List<UsuarioAtividade> usuarioAtividadesNew = this.getUsuariosSelect();
			atividadeNew.setGerente(usuarioFacade.find(this.idUsuSelect));
			atividadeNew.setProjeto(projetoFacade.find(this.idProjetoSelect));
			Atividade oldAtv = this.carregaAtividade(this.atividade.getAtividadeId());
			String msg = this.atividadeFacade.atualizaAtividade(oldAtv, atividadeNew,
																usuarioAtividadesOld,usuarioAtividadesNew,
																docAtividadesOld,docAtividadesNew);
			
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
	            if (this.popUpSalve && this.atividade.getConfigAtividade().isTrabSab() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )) diasT++;
	            if (this.popUpSalve && this.atividade.getConfigAtividade().isTrabDom() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )) diasT++;
	            calendar.add( Calendar.DATE, 1 );  
	        }         
	        
	        if (this.popUpSalve){
	        	if (this.atividade.getConfigAtividade().getQuantDiasFolgaFeriado() >= diasT ){
	        		String info = "Verifique a configuração de Dias Úteis";
	    			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,  info, ""));
	        		return;
	        	}
	        	this.diasTrabalhados = diasT - this.atividade.getConfigAtividade().getQuantDiasFolgaFeriado();
	        	this.horasTrabalho = this.diasTrabalhados * this.atividade.getConfigAtividade().getQuantHorasDias();
	        } else {
	        	this.diasTrabalhados = diasT;
	        	this.horasTrabalho = new Float(this.diasTrabalhados).floatValue() * QUANTIDADE_HORAS_DIA;
	        }
	        
	        if (this.diasTrabalhados > 0){
	        	this.maxFeriados = this.diasTrabalhados - 1;
	        } else {
	        	this.maxFeriados = 0;
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
			ConfigAtividade configAtividade = new ConfigAtividade();
			configAtividade.setQuantDiasFolgaFeriado(0);
			configAtividade.setQuantHorasDias(QUANTIDADE_HORAS_DIA);
			configAtividade.setTrabDom(false);
			configAtividade.setTrabSab(false);
			this.maxFeriados = this.diasTrabalhados - 1;
			this.atividade.setConfigAtividade(configAtividade);
			
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
		
		if (this.atividade.getDivideReplicaTempo().equals("D") && this.usuariosSelect.size() >= 1){
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
	
	
	public void handleFileUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        this.docAtividade.setDoc(event.getFile().getContents());
        this.docAtividade.setTypeName(event.getFile().getContentType());
        String extensao = event.getFile().getFileName().substring(event.getFile().getFileName().lastIndexOf('.') + 1); 
        this.docAtividade.setExtensao("."+extensao);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	
	public void salvarDoc(){
		
		if (docAtividade.getDoc() != null){
			DocAtividade docAtividade = this.docAtividade;
			
			if (docAtividade.getNomeDoc() == null || docAtividade.getNomeDoc().equals("")){
				String info = "Informe nome do documento.";
				FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, ""));
			}
			
			this.atividade.getDocAtividades().add(docAtividade);
			this.docAtividade = new DocAtividade();
			String info = "Doc. carregado com sucesso.";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,info, ""));
		} else {
			String info = "Carregue pelo menos um documento.";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,info, ""));
		}
		
	}
	
	
	public void downloadDoc(DocAtividade docAtividade) {

		ServletContext sContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String arquivo = sContext.getRealPath("/temp") + File.separator	+ docAtividade.getNomeDoc() + docAtividade.getExtensao();

		File file = new File(arquivo);

		try {
			FileOutputStream fileOuputStream = new FileOutputStream(file);
			fileOuputStream.write(docAtividade.getDoc());
			fileOuputStream.close();

			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String fileName = (docAtividade.getNomeDoc() + docAtividade.getExtensao());
		
		this.downloadFile(fileName, file, docAtividade.getTypeName(), FacesContext.getCurrentInstance());
		
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
	
	
	 public void excluirDoc(DocAtividade docAtividade){
	    	this.atividade.getDocAtividades().remove(docAtividade);
			String info = "Doc Atividade Excluido com Sucesso";
			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,info,null));
	}
	 
	 
	public void edtarAtiv(Atividade atividade){
		
		this.ini();
		
		this.atividade = atividade;
		this.idProjetoSelect = atividade.getProjeto().getProjetoId();
		this.idUsuSelect = atividade.getGerente().getUsuarioId();
		this.atividade.setDocAtividades(this.atividadeFacade.findDocAtividade(this.atividade.getAtividadeId()));
		List<UsuarioAtividade> usuarioAtividades = this.atividadeFacade.findUsuarioAtividade(this.atividade.getAtividadeId());
		List<UsuarioAtividade> usuDelete = new ArrayList<UsuarioAtividade>();
		for (UsuarioAtividade usuarioAtividade : usuarioAtividades) {
			this.usuariosSelect.add(usuarioAtividade);
			
			for (UsuarioAtividade usuarioDispo : this.usuarioDispo) {
				
				if(usuarioDispo.getUsuario().getUsuarioId() == usuarioAtividade.getUsuario().getUsuarioId()){
					usuDelete.add(usuarioDispo);
				}
			}
		}
		for (UsuarioAtividade usuarioAtividade : usuDelete) {
			this.usuarioDispo.remove(usuarioAtividade);
		}
		
		if (this.atividade.getConfigAtividade() != null){
			this.atividade.setConfigAtividade(this.configAtividadeFacade.find(atividade.getConfigAtividade().getConfigAtividadeId()));
			this.popUpSalve = true;
		}
		this.calculaData();
		
		
	} 
	
	
	private Atividade carregaAtividade(int atividadeId) {
		
		Atividade atividade = atividadeFacade.find(atividadeId);
		atividade.setDocAtividades(this.atividadeFacade.findDocAtividade(this.atividade.getAtividadeId()));
		atividade.setUsuarioAtividades(this.atividadeFacade.findUsuarioAtividade(this.atividade.getAtividadeId()));
				
		return atividade;
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


	public int getDiasTrabalhados() {
		return diasTrabalhados;
	}


	public void setDiasTrabalhados(int diasTrabalhados) {
		this.diasTrabalhados = diasTrabalhados;
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


	public DocAtividade getDocAtividade() {
		return docAtividade;
	}


	public void setDocAtividade(DocAtividade docAtividade) {
		this.docAtividade = docAtividade;
	}


	

	public List<Atividade> getAtividadesList() {
		return atividadesList;
	}


	public void setAtividadesList(List<Atividade> atividadesList) {
		this.atividadesList = atividadesList;
	}


	


	


	
	
	
	
	 
	
	
	
	
	
	
}
