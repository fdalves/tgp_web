package managedbean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import model.Atividade;
import model.Cargo;
import model.ConfigAtividade;
import model.Projeto;
import model.Usuario;
import model.UsuarioAtividade;

import org.primefaces.model.DualListModel;

import util.SendMail;
import ejb.AtividadeFacade;
import ejb.ConfigAtividadeFacade;
import ejb.ProjetoFacade;
import ejb.UsuarioFacade;

@ManagedBean(name="agendMeetMB")
@ViewScoped
public class AgendMeetMB  implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private static final int PROJETO_ID_MEETING = 100; 
	
	@EJB
	private ProjetoFacade projetoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AtividadeFacade atividadeFacade;
	@EJB
	private ConfigAtividadeFacade configAtividadeFacade;
	
	
	private DualListModel<Usuario> usuListModel = new DualListModel<Usuario>();
	private List<Usuario> usuariosListDisponiveis = new ArrayList<Usuario>();
	private List<Usuario> usuariosListSelecionados = new ArrayList<Usuario>();
	private Usuario usuario = new Usuario();
	private Date date = null;
	private String assunto = new String();
	
	
	public AgendMeetMB() {
		
	}

	
		
	@PostConstruct
	public void ini(){
		
		usuariosListDisponiveis = usuarioFacade.findAll();
		usuariosListSelecionados = new ArrayList<Usuario>();
		usuListModel = new DualListModel<Usuario>(usuariosListDisponiveis, usuariosListSelecionados);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		Usuario usuario = (Usuario) session.getAttribute("user");
		
		if (usuario != null){
			this.usuario = usuario;
		}
		
		
		Usuario usuarioRemov = null;
		for (Usuario usuario2 : usuariosListDisponiveis) {
			if (this.usuario.getUsuarioId() == usuario2.getUsuarioId()) usuarioRemov = (usuario2);
		}
		
		if (usuarioRemov != null) this.usuariosListDisponiveis.remove(usuarioRemov);
		this.usuariosListSelecionados.add(this.usuario);
		
		this.date = null;
		this.assunto = new String();
		
	}
	
	
	public void salvar(){
	
		if (this.date.after(new Date())){
			
			this.usuariosListDisponiveis = this.usuListModel.getSource();
	    	this.usuariosListSelecionados = this.usuListModel.getTarget();
			
			if (this.usuariosListSelecionados.size() >= 2){
				DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
				
				SimpleDateFormat formatM = new SimpleDateFormat("dd/MM/yyyy HH:mm");  
				
				System.out.println(format.format(date));
				System.out.println(df.format(this.getDate()));
				System.out.println(assunto);
				
				
				Projeto projeto = projetoFacade.find(PROJETO_ID_MEETING);
				
				Atividade atividade = new Atividade();
				atividade.setProjeto(projeto);
				Usuario usuario = usuarioFacade.find(this.usuario.getUsuarioId());
				atividade.setGerente(usuario);
				atividade.setDtIni(date);
				atividade.setDtFim(date);
				atividade.setAtividadeNome("Meeting - "+ formatM.format(this.getDate()));
				atividade.setDescAtividade(assunto);
				atividade.setDtInsert(new Date());
				atividade.setUsuarioAtividades(null);
				atividade.setDivideReplicaTempo("R");
				atividade.setPrioridade("normal");
				
				ConfigAtividade configAtividade = new ConfigAtividade();
				configAtividade.setAtividade(atividadeFacade.find(atividade.getAtividadeId()));
				configAtividade.setQuantDiasFolgaFeriado(0);
				configAtividade.setQuantHorasDias(7.5f);
				configAtividade.setTrabDom(false);
				configAtividade.setTrabSab(false);
				
				atividade.setConfigAtividade(configAtividade);
				
				
				List<UsuarioAtividade> usuarioAtividades = new ArrayList<UsuarioAtividade>();
				for(Usuario usuario2 : this.usuariosListSelecionados){
					UsuarioAtividade usuarioAtividade = new UsuarioAtividade();
					usuarioAtividade.setAtividade(atividadeFacade.find(atividade.getAtividadeId()));
					usuarioAtividade.setUsuario(usuarioFacade.find(usuario2.getUsuarioId()));
					usuarioAtividade.setCargo(new Cargo());
					
					usuarioAtividades.add(usuarioAtividade);
				}
				
				atividade.setUsuarioAtividades(usuarioAtividades);
				atividadeFacade.savarAtividade(atividade);
				
				String info = "Meeting Agendado com sucesso";
		        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,""+   info, info));
		        
		        SendMail mail = new SendMail();
		        
		        String from = "tgp@tgp.com";
		        String to [] = new String [usuarioAtividades.size()];
		        int i =0;
		        StringBuffer buffer = new StringBuffer();
		        for (UsuarioAtividade usuarioAtividade : usuarioAtividades) {
					to[i] = usuarioAtividade.getUsuario().getEmail();
					i++;
					buffer.append(usuarioAtividade.getUsuario().getNome());
					buffer.append(",");
				}
		       String subject = this.assunto;
		       
		       String message = " Meeting Agendado por " + this.usuario.getNome() + "\n " +
		        				"Inicio: " + df.format(this.getDate()) + ", " + format.format(date)+ "\n " +
		       					"Participantes: " + buffer.toString().substring(0,buffer.toString().length() -1 );
		       
		       
		       mail.sendMail(from, to, subject, message);
				
				
			} else {
				String info = "Selecione no mínimo 2 participantes";
		        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,""+  info, info));
				return;
			}
			
		} else {
			String info = "Data Inválida, selecione uma data superior a data atual";
	        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_ERROR,""+  info, info));
			return;
		}
		this.ini();
	}


	public ProjetoFacade getProjetoFacade() {
		return projetoFacade;
	}


	public void setProjetoFacade(ProjetoFacade projetoFacade) {
		this.projetoFacade = projetoFacade;
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


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public String getAssunto() {
		return assunto;
	}



	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	
	
	
   
    
   
    
    /*
    public void salvarUsuariosProjeto(){
    	
    	
    	this.usuariosListDisponiveis = this.usuListModel.getSource();
    	this.usuariosListSelecionados = this.usuListModel.getTarget();
    	
    //	Projeto projeto = projetoFacade.find(projetoSelecionadoAba2);
    	
    //	projeto.setUsuarioProjetos(new ArrayList<UsuarioProjeto>());
    	
 //   	projeto = projetoFacade.update(projeto);
    	
    	List<UsuarioProjeto> list = new ArrayList<UsuarioProjeto>();
    	
    	for (Usuario usuario : this.usuariosListSelecionados ) {
			UsuarioProjeto usuarioProjeto = new UsuarioProjeto();
//			usuarioProjeto.setProjeto(projeto);
			usuarioProjeto.setUsuario(usuario);
			list.add(usuarioProjeto);
    	}
   // 	projeto.setUsuarioProjetos(list);
    	
    	
  //  	projetoFacade.update(projeto);
    	
    	String info = "Usuario(s) Associado(s) ao projeto";
        FacesContext.getCurrentInstance().addMessage(null,	new FacesMessage(FacesMessage.SEVERITY_INFO,""+ " - " + info, info));
    	
    }
    
    
	**/
	
	
	
	
	
}
