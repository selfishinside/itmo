package ru.itmo.managers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.model.Color;
import ru.itmo.model.Country;
import ru.itmo.model.Location;
import ru.itmo.model.Person;
import ru.itmo.service.LocationService;
import ru.itmo.service.PersonService;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("personViewBean")
@ViewScoped
public class PersonViewBean implements Serializable {
    @Inject
    private PersonService personService;

    @Inject
    private LocationService locationService;

    private List<Person> allPersons;
    private List<Person> filteredPersons;
    private Map<String, String> availableColumns;
    private String selectedFilterColumn;
    private String filterValue;

    private Person selectedPerson;
    private Long personId;
    private Long selectedLocationId;
    private boolean useExistingLocation = true;
    private String birthday;
    private List<Location> allLocations;
    private List<Color> colors;
    private List<Country> countries;

    @PostConstruct
    public void init() {
        allPersons = personService.findAll();
        filteredPersons = allPersons;
        availableColumns = new LinkedHashMap<>();
        availableColumns.put("Имя", "name");
        availableColumns.put("Цвет глаз", "eyeColor");
        availableColumns.put("Цвет волос", "hairColor");
        availableColumns.put("Локация", "location.name");
        availableColumns.put("Национальность", "nationality");
    }

    public void loadPerson() {
        selectedPerson = personId == null ? new Person() : personService.findById(personId).orElse(null);
        if (selectedPerson == null) {
            selectedPerson = new Person();
        }
        if (selectedPerson.getLocation() == null) {
            selectedPerson.setLocation(new Location());
            useExistingLocation = false;
        } else {
            selectedLocationId = selectedPerson.getLocation().getId();
            useExistingLocation = selectedLocationId != null;
        }
        birthday = selectedPerson.getBirthday() == null ? null : selectedPerson.getBirthday().toString();
        allLocations = locationService.findAll();
        colors = Arrays.asList(Color.values());
        countries = Arrays.asList(Country.values());
    }

    public void applyGlobalFilter() {
        if (filterValue == null || filterValue.trim().isEmpty() || selectedFilterColumn == null) {
            clearGlobalFilter();
            return;
        }
        filteredPersons = allPersons.stream()
                .filter(person -> filterValue.trim().equalsIgnoreCase(getFieldValue(person, selectedFilterColumn)))
                .collect(Collectors.toList());
    }

    public void clearGlobalFilter() {
        filteredPersons = allPersons;
        filterValue = null;
        selectedFilterColumn = null;
    }

    public String saveOrUpdatePerson() {
        try {
            applyLocationState();
            if (birthday != null && !birthday.isBlank()) {
                selectedPerson.setBirthday(LocalDate.parse(birthday));
            }
            if (selectedPerson.getId() == null) {
                personService.save(selectedPerson);
            } else {
                personService.update(selectedPerson);
            }
            return "persons.xhtml?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", ex.getMessage()));
            return null;
        }
    }

    public void deletePerson(Person person) {
        personService.deleteById(person.getId());
        init();
    }

    private void applyLocationState() {
        if (useExistingLocation && selectedLocationId != null) {
            Location location = locationService.findById(selectedLocationId)
                    .orElseThrow(() -> new IllegalArgumentException("Локация не найдена"));
            selectedPerson.setLocation(location);
        }
    }

    private String getFieldValue(Person person, String fieldName) {
        if (person == null) return null;
        switch (fieldName) {
            case "name": return person.getName();
            case "eyeColor": return person.getEyeColor() == null ? null : person.getEyeColor().name();
            case "hairColor": return person.getHairColor() == null ? null : person.getHairColor().name();
            case "location.name": return person.getLocation() == null ? null : person.getLocation().getName();
            case "nationality": return person.getNationality() == null ? null : person.getNationality().name();
            default: return null;
        }
    }

    public List<Person> getFilteredPersons() { return filteredPersons; }
    public Map<String, String> getAvailableColumns() { return availableColumns; }
    public String getSelectedFilterColumn() { return selectedFilterColumn; }
    public void setSelectedFilterColumn(String selectedFilterColumn) { this.selectedFilterColumn = selectedFilterColumn; }
    public String getFilterValue() { return filterValue; }
    public void setFilterValue(String filterValue) { this.filterValue = filterValue; }
    public Person getSelectedPerson() { return selectedPerson; }
    public void setSelectedPerson(Person selectedPerson) { this.selectedPerson = selectedPerson; }
    public Long getPersonId() { return personId; }
    public void setPersonId(Long personId) { this.personId = personId; }
    public Long getSelectedLocationId() { return selectedLocationId; }
    public void setSelectedLocationId(Long selectedLocationId) { this.selectedLocationId = selectedLocationId; }
    public boolean isUseExistingLocation() { return useExistingLocation; }
    public void setUseExistingLocation(boolean useExistingLocation) { this.useExistingLocation = useExistingLocation; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public List<Location> getAllLocations() { return allLocations; }
    public List<Color> getColors() { return colors; }
    public List<Country> getCountries() { return countries; }
}
