package managedbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Atividade;
import model.ConfigAtividade;
import model.DocAtividade;
import model.Projeto;
import model.Usuario;
import model.UsuarioAtividade;
import ejb.AtividadeFacade;
import ejb.ConfigAtividadeFacade;
import ejb.DocAtividadeFacade;
import ejb.ProjetoFacade;
import ejb.UsuarioFacade;

@ManagedBean(name="initTgpMB")
@RequestScoped
public class InitTgpMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private AtividadeFacade atividadeFacade;
	@EJB
	private ProjetoFacade projetoFacade;
	@EJB
	private DocAtividadeFacade docAtividadeFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ConfigAtividadeFacade configAtividadeFacade;


	private Usuario usuario =  new  Usuario();
	private Usuario usuarioSelectChat = new Usuario(); 
	private List<Usuario> usarioList = new ArrayList<Usuario>();
	private List<Atividade> atividadesList = new ArrayList<Atividade>();
	private String[] nomesProjetos = null;  
	private List<Projeto> listProjetos = null; 
	private String[] prioridades = null;
	private Atividade atividadeAtucal = new Atividade();
	private List<UsuarioAtividade> usuarioAtividades = new ArrayList<UsuarioAtividade>();
	
	
	public InitTgpMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		this.usarioList = usuarioFacade.findAll();
		this.listaFotosUser();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		Usuario usuario = (Usuario) session.getAttribute("user");
		
		List<UsuarioAtividade> list = atividadeFacade.findAtividadeByUsuario(usuario.getUsuarioId());
		
		for (UsuarioAtividade atividade : list) {
			this.atividadesList.add(atividade.getAtividade());
		}
		
		
		
		List<Projeto> listProjetos = projetoFacade.findAll();
		
		
		nomesProjetos = new String[listProjetos.size()];
		int count = 0;
		for (Projeto projeto : listProjetos) {
			
			nomesProjetos[count] = projeto.getNomeProjeto();
			count++;
		}
		
		 this.prioridades = new String [3];
		 prioridades[0] = "baixa";
		 prioridades[1] = "normal";
		 prioridades[2] = "alta";
		 
		this.atividadeAtucal = new Atividade();
		 
		
	}
	
	
	
	

	public String goUser(){
		return "go_user";
	}
	 
	
	private void listaFotosUser() {

		try {
			ServletContext sContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			File folder = new File(sContext.getRealPath("/temp"));
			if (!folder.exists())
				folder.mkdirs();

			for (Usuario u : usarioList) {
				if (u.getFoto() != null) {
					String nomeArquivo = u.getUsuarioId() + ".jpg";
					String arquivo = sContext.getRealPath("/temp")
							+ File.separator + nomeArquivo;

					criaArquivo(u.getFoto(), arquivo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	 
	private void criaArquivo(byte[] bytes, String arquivo) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(arquivo);
			fos.write(bytes);

			fos.flush();
			fos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public void carregaGravacao(){
	
			DocAtividade docAtividade = new DocAtividade();
			
			  byte[] value =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiden").getBytes();
				
			  docAtividade.setDoc(value);
			  docAtividade.setAtividade(atividadeFacade.find(24));
			  docAtividade.setNomeDoc("teste");
			  docAtividade.setExtensao(".wav");
			  docAtividade.setTypeName(".wav");
			  
			  docAtividadeFacade.save(docAtividade);
				 
			
			System.out.println("entrou");
			
		
	}
	
	
	
	
	public void initUsuariosAtividade(Atividade atividade){
		this.atividadeAtucal = atividade;
		this.usuarioAtividades =  this.atividadeFacade.findUsuarioAtividade(this.atividadeAtucal.getAtividadeId());
		ConfigAtividade configAtividade = configAtividadeFacade.find(atividade.getConfigAtividade().getConfigAtividadeId());
		
		for (UsuarioAtividade usuarioAtividade : this.usuarioAtividades) {
			this.calculaData(atividade.getDtIni(), atividade.getDtFim(), configAtividade, usuarioAtividade, usuarioAtividades.size());
		}
		
		List<DocAtividade> docAtividades = atividadeFacade.findDocAtividade(this.atividadeAtucal.getAtividadeId());
		
		this.atividadeAtucal.setDocAtividades(docAtividades);
		
	}
	
	
	
	
	
	private void calculaData(Date dtIni, Date dtFim,  ConfigAtividade configAtividade, UsuarioAtividade usuarioAtividade,Integer quantUser){
		
		Date initialDate = dtIni; 
		Date finalDate = dtFim;
	    
		if(initialDate == null || finalDate == null){
			return;
		}
		if (initialDate.getTime() > finalDate.getTime()){
			return;
			
		}
	        int diasT = 0;  
	        int totD = somaDias( initialDate, finalDate );        
	        Calendar calendar = new GregorianCalendar();  
	        calendar.setTime(initialDate);  
	          
	        for( int i = 0; i <= totD; i++ ) {  
	            if( !( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) && !( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) ) {  
	                diasT++;  
	            }  
	            if (configAtividade.isTrabSab() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )) diasT++;
	            if (configAtividade.isTrabDom() && ( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )) diasT++;
	            calendar.add( Calendar.DATE, 1 );  
	        }         
	        
	       
        	if (configAtividade.getQuantDiasFolgaFeriado() >= diasT ){
        		String info = "Verifique a configuração de Dias Úteis";
    			FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,  info, ""));
        		return;
        	}
        	usuarioAtividade.setDiasTrabalhados( new Float( diasT - configAtividade.getQuantDiasFolgaFeriado()));
        	usuarioAtividade.setHorasTrabalho(usuarioAtividade.getDiasTrabalhados() * configAtividade.getQuantHorasDias());
	        
	        this.divideReplicaTempo(usuarioAtividade,quantUser);
	    }  
	

	private int somaDias( Date initialDate, Date finalDate ) {  
    
	    if( initialDate == null || finalDate == null ) {  
	        return 0;  
	    }  
	  
	    int days = ( int ) ( ( finalDate.getTime() - initialDate.getTime() )/( 24*60*60*1000 ) );  
	    return ( days > 0 ? days : 0 ) ;  
	}  
	
	public void divideReplicaTempo( UsuarioAtividade usuarioAtividade, Integer quantUser){
		
		if ( usuarioAtividade.getAtividade().getDivideReplicaTempo().equals("D")){
			float dias = usuarioAtividade.getDiasTrabalhados() / quantUser;
			BigDecimal bd = new BigDecimal(dias);
			bd= bd.setScale(1, RoundingMode.CEILING);
			usuarioAtividade.setDiasTrabalhados(bd.floatValue());
			float horas = usuarioAtividade.getHorasTrabalho() / quantUser;
			bd = new BigDecimal(horas);
			bd = bd.setScale(1, RoundingMode.CEILING);
			usuarioAtividade.setHorasTrabalho(bd.floatValue());
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
		
	
	public void teste(){
		
		System.out.println("entrou...");
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
	
	
	public void initDoc(Atividade ativ){
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.setAttribute("ativId", ativ.getAtividadeId());
	}
	
	public UsuarioFacade getUsuarioFacade() {
		return usuarioFacade;
	}


	public void setUsuarioFacade(UsuarioFacade usuarioFacade) {
		this.usuarioFacade = usuarioFacade;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public List<Usuario> getUsarioList() {
		return usarioList;
	}


	public void setUsarioList(List<Usuario> usarioList) {
		this.usarioList = usarioList;
	}


	
	
	public Usuario getUsuarioSelectChat() {
		return usuarioSelectChat;
	}


	public void setUsuarioSelectChat(Usuario usuarioSelectChat) {
		this.usuarioSelectChat = usuarioSelectChat;
	}


	public AtividadeFacade getAtividadeFacade() {
		return atividadeFacade;
	}


	public void setAtividadeFacade(AtividadeFacade atividadeFacade) {
		this.atividadeFacade = atividadeFacade;
	}


	public DocAtividadeFacade getDocAtividadeFacade() {
		return docAtividadeFacade;
	}


	public void setDocAtividadeFacade(DocAtividadeFacade docAtividadeFacade) {
		this.docAtividadeFacade = docAtividadeFacade;
	}


	public List<Atividade> getAtividadesList() {
		return atividadesList;
	}


	public void setAtividadesList(List<Atividade> atividadesList) {
		this.atividadesList = atividadesList;
	}


	public String[] getNomesProjetos() {
		return nomesProjetos;
	}


	public void setNomesProjetos(String[] nomesProjetos) {
		this.nomesProjetos = nomesProjetos;
	}


	public ProjetoFacade getProjetoFacade() {
		return projetoFacade;
	}


	public void setProjetoFacade(ProjetoFacade projetoFacade) {
		this.projetoFacade = projetoFacade;
	}


	public List<Projeto> getListProjetos() {
		return listProjetos;
	}


	public void setListProjetos(List<Projeto> listProjetos) {
		this.listProjetos = listProjetos;
	}


	public String[] getPrioridades() {
		return prioridades;
	}


	public void setPrioridades(String[] prioridades) {
		this.prioridades = prioridades;
	}


	public Atividade getAtividadeAtucal() {
		return atividadeAtucal;
	}


	public void setAtividadeAtucal(Atividade atividadeAtucal) {
		this.atividadeAtucal = atividadeAtucal;
	}


	public List<UsuarioAtividade> getUsuarioAtividades() {
		return usuarioAtividades;
	}


	public void setUsuarioAtividades(List<UsuarioAtividade> usuarioAtividades) {
		this.usuarioAtividades = usuarioAtividades;
	}

	

	
}
