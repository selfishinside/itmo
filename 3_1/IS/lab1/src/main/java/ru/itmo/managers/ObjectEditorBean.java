package ru.itmo.managers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.model.Dragon;
import ru.itmo.model.Location;
import ru.itmo.model.Person;
import ru.itmo.service.DragonService;
import ru.itmo.service.LocationService;
import ru.itmo.service.PersonService;
import java.io.Serializable;
import java.util.List;

@Named("objectEditorBean")
@ViewScoped
public class ObjectEditorBean implements Serializable {
    @Inject
    private DragonService dragonService;

    @Inject
    private PersonService personService;

    @Inject
    private LocationService locationService;

    private String objectType = "dragon";
    private String mode = "create";
    private Integer selectedDragonId;
    private Long selectedPersonId;
    private Long selectedLocationId;
    private List<Dragon> dragons;
    private List<Person> persons;
    private List<Location> locations;

    @PostConstruct
    public void init() {
        dragons = dragonService.findAll();
        persons = personService.findAll();
        locations = locationService.findAll();
    }

    public String go() {
        if ("dragon".equals(objectType)) {
            return "edit".equals(mode) && selectedDragonId != null
                    ? "dragon-form.xhtml?faces-redirect=true&id=" + selectedDragonId
                    : "dragon-form.xhtml?faces-redirect=true";
        }
        if ("person".equals(objectType)) {
            return "edit".equals(mode) && selectedPersonId != null
                    ? "person-form.xhtml?faces-redirect=true&id=" + selectedPersonId
                    : "person-form.xhtml?faces-redirect=true";
        }
        return "edit".equals(mode) && selectedLocationId != null
                ? "location-form.xhtml?faces-redirect=true&id=" + selectedLocationId
                : "location-form.xhtml?faces-redirect=true";
    }

    public String getObjectType() { return objectType; }
    public void setObjectType(String objectType) { this.objectType = objectType; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public Integer getSelectedDragonId() { return selectedDragonId; }
    public void setSelectedDragonId(Integer selectedDragonId) { this.selectedDragonId = selectedDragonId; }
    public Long getSelectedPersonId() { return selectedPersonId; }
    public void setSelectedPersonId(Long selectedPersonId) { this.selectedPersonId = selectedPersonId; }
    public Long getSelectedLocationId() { return selectedLocationId; }
    public void setSelectedLocationId(Long selectedLocationId) { this.selectedLocationId = selectedLocationId; }
    public List<Dragon> getDragons() { return dragons; }
    public List<Person> getPersons() { return persons; }
    public List<Location> getLocations() { return locations; }
}
