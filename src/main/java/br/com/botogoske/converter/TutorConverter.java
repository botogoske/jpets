package br.com.botogoske.converter;

import br.com.botogoske.model.Tutor;
import br.com.botogoske.service.TutorService;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@Named("tutorConverter")
@ApplicationScoped
@FacesConverter(value = "tutorConverter", managed = true)
public class TutorConverter implements Converter<Tutor> {

    @Inject
    private TutorService tutorService;

    @Override
    public Tutor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return tutorService.buscarPorId(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Tutor tutor) {
        if (tutor == null || tutor.getId() == null) {
            return "";
        }
        return String.valueOf(tutor.getId());
    }
}
