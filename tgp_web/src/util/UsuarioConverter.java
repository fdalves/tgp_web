package util;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import model.Usuario;
import ejb.UsuarioFacade;

@FacesConverter(value="usuarioConverter")
public class UsuarioConverter implements Converter {
	
	
	
	

	 @EJB
	 private UsuarioFacade facade;
		
	
	@Override
	 public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
	 List<Usuario> usu = facade.listarPorLogin(string);
	 
	 if (usu.isEmpty()) return false;
	 
	 return usu.get(0);
	 }

	 @Override
	 public String getAsString(FacesContext fc, UIComponent uic, Object o) {
	 Usuario usu = new Usuario();
	 usu = (Usuario) o;
	 return usu.getLogin();
	 }
	 
	 public UsuarioFacade getFacade() {
			return facade;
		}

		public void setFacade(UsuarioFacade facade) {
			this.facade = facade;
		}


	

}
