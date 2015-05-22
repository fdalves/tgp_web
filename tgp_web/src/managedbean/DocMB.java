package managedbean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Atividade;
import model.DocAtividade;
import model.Usuario;

import org.primefaces.event.FileUploadEvent;

import ejb.AtividadeFacade;
import ejb.DocAtividadeFacade;

@ManagedBean(name="docMB")
@ViewScoped
public class DocMB  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	
	@EJB
	private AtividadeFacade atividadeFacade;
	@EJB
	private DocAtividadeFacade docAtividadeFacade;
	
	
	
	private Atividade atividade =  new  Atividade();
	private DocAtividade docAtividade = new DocAtividade();
	
	
	public DocMB() {

	}

	
	@PostConstruct
	public void ini(){
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		Integer ativId  = (Integer) session.getAttribute("ativId");
		
		System.out.println(ativId);
		
		if (ativId != null){
			this.atividade =  atividadeFacade.find(ativId);
			this.atividade.setDocAtividades(atividadeFacade.findDocAtividade(this.atividade.getAtividadeId()));
			this.docAtividade = new DocAtividade();
		}
		
		
		
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
			
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
			Usuario usuario = (Usuario) session.getAttribute("user");
			
			if (usuario != null){
				
				docAtividade.setUsuarioAtualizador(usuario.getNome());
			}
			
			this.docAtividade.setDataInsert(new Date());
			
			
			this.atividade.getDocAtividades().add(docAtividade);
			this.docAtividade.setAtividade(this.atividade);
			
			docAtividadeFacade.save(this.docAtividade);
			
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


	public DocAtividade getDocAtividade() {
		return docAtividade;
	}


	public void setDocAtividade(DocAtividade docAtividade) {
		this.docAtividade = docAtividade;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	 
	 
	
	
	
	
}
