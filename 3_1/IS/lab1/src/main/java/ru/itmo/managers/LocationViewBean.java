package ru.itmo.managers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.model.Location;
import ru.itmo.service.LocationService;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("locationViewBean")
@ViewScoped
public class LocationViewBean implements Serializable {
    @Inject
    private LocationService locationService;

    private List<Location> allLocations;
    private List<Location> filteredLocations;
    private Map<String, String> availableColumns;
    private String selectedFilterColumn;
    private String filterValue;
    private Location selectedLocation;
    private Long locationId;

    @PostConstruct
    public void init() {
        allLocations = locationService.findAll();
        filteredLocations = allLocations;
        availableColumns = new LinkedHashMap<>();
        availableColumns.put("Название", "name");
    }

    public void loadLocation() {
        selectedLocation = locationId == null ? new Location() : locationService.findById(locationId).orElse(new Location());
    }

    public void applyGlobalFilter() {
        if (filterValue == null || filterValue.trim().isEmpty() || selectedFilterColumn == null) {
            clearGlobalFilter();
            return;
        }
        filteredLocations = allLocations.stream()
                .filter(location -> filterValue.trim().equalsIgnoreCase(getFieldValue(location, selectedFilterColumn)))
                .collect(Collectors.toList());
    }

    public void clearGlobalFilter() {
        filteredLocations = allLocations;
        filterValue = null;
        selectedFilterColumn = null;
    }

    public String saveOrUpdateLocation() {
        if (selectedLocation.getId() == null) {
            locationService.save(selectedLocation);
        } else {
            locationService.update(selectedLocation);
        }
        return "locations.xhtml?faces-redirect=true";
    }

    public void deleteLocation(Location location) {
        try {
            locationService.deleteById(location.getId());
            init();
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Удаление невозможно", ex.getMessage()));
        }
    }

    private String getFieldValue(Location location, String fieldName) {
        if (location == null) return null;
        return "name".equals(fieldName) ? location.getName() : null;
    }

    public List<Location> getFilteredLocations() { return filteredLocations; }
    public Map<String, String> getAvailableColumns() { return availableColumns; }
    public String getSelectedFilterColumn() { return selectedFilterColumn; }
    public void setSelectedFilterColumn(String selectedFilterColumn) { this.selectedFilterColumn = selectedFilterColumn; }
    public String getFilterValue() { return filterValue; }
    public void setFilterValue(String filterValue) { this.filterValue = filterValue; }
    public Location getSelectedLocation() { return selectedLocation; }
    public void setSelectedLocation(Location selectedLocation) { this.selectedLocation = selectedLocation; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
}
