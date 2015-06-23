package managedbean;




import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import model.Atividade;
import model.ConfigAtividade;
import model.Usuario;
import model.UsuarioAtividade;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import ejb.AtividadeFacade;
import ejb.ConfigAtividadeFacade;
 
@ManagedBean(name="scheduleView")
@ViewScoped
public class ScheduleView implements Serializable {
 
    private ScheduleModel eventModel;
     
    private ScheduleModel lazyEventModel;
 
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    @EJB
    private AtividadeFacade atividadeFacade;
    
    @EJB
    private ConfigAtividadeFacade configAtividadeFacade;
    
    private Usuario usuario =  new  Usuario();
    private List<Atividade> atividadesList = new ArrayList<Atividade>();
 
  
    
    /*
    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
        eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
        eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));
        eventModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", theDayAfter3Pm(), fourDaysLater3pm()));
         
        lazyEventModel = new LazyScheduleModel() {
             
            @Override
            public void loadEvents(Date start, Date end) {
                Date random = getRandomDate(start);
                addEvent(new DefaultScheduleEvent("Lazy Event 1", random, random));
                 
                random = getRandomDate(start);
                addEvent(new DefaultScheduleEvent("Lazy Event 2", random, random));
            }   
        };
    }
    
    **/
    
    @PostConstruct
    public void init() {
    	
    	this.initCalendar();
    }
    
    
    public void initCalendar(){
    	
    	FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		Usuario usuario = (Usuario) session.getAttribute("user");
		
		List<UsuarioAtividade> list = atividadeFacade.findAtividadeByUsuario(usuario.getUsuarioId());
		
		this.usuario = usuario;
		
		for (UsuarioAtividade atividade : list) {
			Atividade a = atividade.getAtividade();
			this.atividadesList.add(this.initSituacao(a));
		}
		
		this.carregaCalendar();
    }
    
    
    public void carregaCalendar(){
    	
    	
    	
    	List<Atividade> atividades = this.atividadesList;
    	eventModel = new DefaultScheduleModel();
    	
    	for (Atividade atividade : atividades) {
			
    		ConfigAtividade configAtividade = configAtividadeFacade.find(atividade.getConfigAtividade().getConfigAtividadeId());
    		
    		Calendar start = Calendar.getInstance();
    		start.setTime(atividade.getDtIni());
    		start.set(Calendar.HOUR, new Float(configAtividade.getQuantHorasDias()).intValue());
    		
    		
    		Calendar end = Calendar.getInstance();
    		end.setTime(atividade.getDtFim());
    		end.set(Calendar.HOUR, new Float(configAtividade.getQuantHorasDias()).intValue());
    		eventModel.addEvent(new DefaultScheduleEvent(atividade.getAtividadeNome(), start.getTime(), end.getTime()));
 			
		}
    	
    }
    
    public Atividade initSituacao(Atividade a){
		
		a = atividadeFacade.find(a.getAtividadeId());
		
		List<UsuarioAtividade> uaList = atividadeFacade.findUsuarioAtividade(a.getAtividadeId());
		
		Date dateFinal = a.getDtFim();
		
		if((new Date()).after(dateFinal)){
			if(isConcluida(uaList)){
				a.setSituacao("Conclu�da");
			} else {
				a.setSituacao("Atrasada");
			}
		} else{
			
			if (isConcluida(uaList)){
				a.setSituacao("Conclu�da");
			} else {
				a.setSituacao("Em Andamento");
			}
			
			if (isNova(uaList)) a.setSituacao("Nova");
		}
		
		return a;
	}
    
    private boolean isConcluida(List<UsuarioAtividade> uaList) {
		for (UsuarioAtividade usuarioAtividade : uaList) {
			if (this.usuario.getUsuarioId() == usuarioAtividade.getUsuario().getUsuarioId()){
				if (usuarioAtividade.getPercentConclusao() == null){
					return false;
				} else if (usuarioAtividade.getPercentConclusao().intValue() == 100) 
					return true;
			}
		}
		return false;
	}
	
	private boolean isNova(List<UsuarioAtividade> uaList) {
		for (UsuarioAtividade usuarioAtividade : uaList) {
			if (this.usuario.getUsuarioId() == usuarioAtividade.getUsuario().getUsuarioId()){
				if (usuarioAtividade.getPercentConclusao() == null ){
					return true;
				} else if (usuarioAtividade.getPercentConclusao().intValue() == 0 ) return true;
			}
		}
		return false;
	}
    
     
    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);    //set random day of month
         
        return date.getTime();
    }
     
    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
         
        return calendar.getTime();
    }
     
    public ScheduleModel getEventModel() {
        return eventModel;
    }
     
    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }
 
    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
 
        return calendar;
    }
     
    private Date previousDay8Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 8);
         
        return t.getTime();
    }
     
    private Date previousDay11Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 11);
         
        return t.getTime();
    }
     
    private Date today1Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 1);
         
        return t.getTime();
    }
     
    private Date theDayAfter3Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);     
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 3);
         
        return t.getTime();
    }
 
    private Date today6Pm() {
        Calendar t = (Calendar) today().clone(); 
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 6);
         
        return t.getTime();
    }
     
    private Date nextDay9Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 9);
         
        return t.getTime();
    }
     
    private Date nextDay11Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 11);
         
        return t.getTime();
    }
     
    private Date fourDaysLater3pm() {
        Calendar t = (Calendar) today().clone(); 
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
        t.set(Calendar.HOUR, 3);
         
        return t.getTime();
    }
     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null)
            eventModel.addEvent(event);
        else
            eventModel.updateEvent(event);
         
        event = new DefaultScheduleEvent();
    }
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
     
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }


	public AtividadeFacade getAtividadeFacade() {
		return atividadeFacade;
	}


	public void setAtividadeFacade(AtividadeFacade atividadeFacade) {
		this.atividadeFacade = atividadeFacade;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public List<Atividade> getAtividadesList() {
		return atividadesList;
	}


	public void setAtividadesList(List<Atividade> atividadesList) {
		this.atividadesList = atividadesList;
	}
    
    
    
}