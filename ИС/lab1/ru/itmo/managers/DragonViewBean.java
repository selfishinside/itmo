package ru.itmo.managers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.model.Color;
import ru.itmo.model.Coordinates;
import ru.itmo.model.Country;
import ru.itmo.model.Dragon;
import ru.itmo.model.DragonCave;
import ru.itmo.model.DragonCharacter;
import ru.itmo.model.DragonHead;
import ru.itmo.model.DragonType;
import ru.itmo.model.Location;
import ru.itmo.model.Person;
import ru.itmo.service.DragonService;
import ru.itmo.service.LocationService;
import ru.itmo.service.PersonService;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("dragonViewBean")
@ViewScoped
public class DragonViewBean implements Serializable {
    @Inject
    private DragonService dragonService;

    @Inject
    private PersonService personService;

    @Inject
    private LocationService locationService;

    private List<Dragon> allDragons;
    private List<Dragon> filteredDragons;
    private Map<String, String> availableColumns;
    private String selectedFilterColumn;
    private String filterValue;

    private Dragon selectedDragon;
    private Integer dragonId;
    private boolean hasKiller;
    private String killerBirthday;
    private Long selectedKillerId;
    private Long selectedKillerLocationId;
    private boolean useExistingKillerLocation = true;
    private List<Person> allKillers;
    private List<Location> allLocations;

    private List<Color> colors;
    private List<DragonType> types;
    private List<DragonCharacter> characters;
    private List<Country> countries;

    @PostConstruct
    public void init() {
        allDragons = dragonService.findAll();
        filteredDragons = allDragons;
        availableColumns = new LinkedHashMap<>();
        availableColumns.put("Имя", "name");
        availableColumns.put("Тип", "type");
        availableColumns.put("Характер", "character");
        availableColumns.put("Цвет", "color");
        availableColumns.put("Убийца", "killer.name");
    }

    public void loadDragon() {
        if (dragonId == null) {
            selectedDragon = new Dragon();
            selectedDragon.setCoordinates(new Coordinates());
            selectedDragon.setCave(new DragonCave());
            selectedDragon.setHead(new DragonHead());
        } else {
            selectedDragon = dragonService.findById(dragonId).orElse(null);
        }
        if (selectedDragon != null) {
            ensureNestedObjects();
            hasKiller = selectedDragon.getKiller() != null;
            selectedKillerId = hasKiller && selectedDragon.getKiller().getId() != null ? selectedDragon.getKiller().getId() : null;
            selectedKillerLocationId = hasKiller && selectedDragon.getKiller().getLocation() != null
                    ? selectedDragon.getKiller().getLocation().getId()
                    : null;
            useExistingKillerLocation = selectedKillerLocationId != null;
            killerBirthday = hasKiller && selectedDragon.getKiller().getBirthday() != null
                    ? selectedDragon.getKiller().getBirthday().toString()
                    : null;
        }
        allKillers = personService.findAll();
        allLocations = locationService.findAll();
        colors = Arrays.asList(Color.values());
        types = Arrays.asList(DragonType.values());
        characters = Arrays.asList(DragonCharacter.values());
        countries = Arrays.asList(Country.values());
    }

    public void applyGlobalFilter() {
        if (filterValue == null || filterValue.trim().isEmpty() || selectedFilterColumn == null) {
            clearGlobalFilter();
            return;
        }
        filteredDragons = allDragons.stream()
                .filter(dragon -> filterValue.trim().equalsIgnoreCase(getFieldValue(dragon, selectedFilterColumn)))
                .collect(Collectors.toList());
    }

    public void clearGlobalFilter() {
        filteredDragons = allDragons;
        filterValue = null;
        selectedFilterColumn = null;
    }

    public String prepareNew() {
        return "dragon-form.xhtml?faces-redirect=true";
    }

    public String saveOrUpdateDragon() {
        try {
            applyKillerState();
            if (selectedDragon.getId() == 0) {
                dragonService.save(selectedDragon);
            } else {
                dragonService.update(selectedDragon);
            }
            return "dragons.xhtml?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка сохранения", ex.getMessage()));
            return null;
        }
    }

    public void deleteDragon(Dragon dragon) {
        dragonService.deleteById(dragon.getId());
        init();
    }

    private void ensureNestedObjects() {
        if (selectedDragon.getCoordinates() == null) {
            selectedDragon.setCoordinates(new Coordinates());
        }
        if (selectedDragon.getCave() == null) {
            selectedDragon.setCave(new DragonCave());
        }
        if (selectedDragon.getHead() == null) {
            selectedDragon.setHead(new DragonHead());
        }
        if (selectedDragon.getKiller() == null) {
            selectedDragon.setKiller(new Person());
        }
        if (selectedDragon.getKiller().getLocation() == null) {
            selectedDragon.getKiller().setLocation(new Location());
        }
    }

    private void applyKillerState() {
        if (!hasKiller) {
            selectedDragon.setKiller(null);
            return;
        }
        if (selectedKillerId != null) {
            allKillers.stream()
                    .filter(killer -> selectedKillerId.equals(killer.getId()))
                    .findFirst()
                    .ifPresent(selectedDragon::setKiller);
            return;
        }
        if (useExistingKillerLocation && selectedKillerLocationId != null) {
            allLocations.stream()
                    .filter(location -> selectedKillerLocationId.equals(location.getId()))
                    .findFirst()
                    .ifPresent(location -> selectedDragon.getKiller().setLocation(location));
        }
        if (killerBirthday != null && !killerBirthday.isBlank()) {
            selectedDragon.getKiller().setBirthday(LocalDate.parse(killerBirthday));
        }
    }

    private String getFieldValue(Dragon dragon, String fieldName) {
        if (dragon == null) return null;
        switch (fieldName) {
            case "name": return dragon.getName();
            case "type": return dragon.getType() == null ? null : dragon.getType().name();
            case "character": return dragon.getCharacter() == null ? null : dragon.getCharacter().name();
            case "color": return dragon.getColor() == null ? null : dragon.getColor().name();
            case "killer.name": return dragon.getKiller() == null ? null : dragon.getKiller().getName();
            default: return null;
        }
    }

    public List<Dragon> getFilteredDragons() { return filteredDragons; }
    public Map<String, String> getAvailableColumns() { return availableColumns; }
    public String getSelectedFilterColumn() { return selectedFilterColumn; }
    public void setSelectedFilterColumn(String selectedFilterColumn) { this.selectedFilterColumn = selectedFilterColumn; }
    public String getFilterValue() { return filterValue; }
    public void setFilterValue(String filterValue) { this.filterValue = filterValue; }
    public Dragon getSelectedDragon() { return selectedDragon; }
    public void setSelectedDragon(Dragon selectedDragon) { this.selectedDragon = selectedDragon; }
    public Integer getDragonId() { return dragonId; }
    public void setDragonId(Integer dragonId) { this.dragonId = dragonId; }
    public boolean isHasKiller() { return hasKiller; }
    public void setHasKiller(boolean hasKiller) { this.hasKiller = hasKiller; }
    public String getKillerBirthday() { return killerBirthday; }
    public void setKillerBirthday(String killerBirthday) { this.killerBirthday = killerBirthday; }
    public Long getSelectedKillerId() { return selectedKillerId; }
    public void setSelectedKillerId(Long selectedKillerId) { this.selectedKillerId = selectedKillerId; }
    public Long getSelectedKillerLocationId() { return selectedKillerLocationId; }
    public void setSelectedKillerLocationId(Long selectedKillerLocationId) { this.selectedKillerLocationId = selectedKillerLocationId; }
    public boolean isUseExistingKillerLocation() { return useExistingKillerLocation; }
    public void setUseExistingKillerLocation(boolean useExistingKillerLocation) { this.useExistingKillerLocation = useExistingKillerLocation; }
    public List<Person> getAllKillers() { return allKillers; }
    public List<Location> getAllLocations() { return allLocations; }
    public List<Color> getColors() { return colors; }
    public List<DragonType> getTypes() { return types; }
    public List<DragonCharacter> getCharacters() { return characters; }
    public List<Country> getCountries() { return countries; }
}
