package ru.itmo.managers;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ru.itmo.model.Dragon;
import ru.itmo.model.DragonType;
import ru.itmo.service.DragonService;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Named("dragonOperationsBean")
@ViewScoped
public class DragonOperationsBean implements Serializable {
    @Inject
    private DragonService dragonService;

    private Map<String, Long> headGroups;
    private DragonType typeToCount;
    private long typeCount;
    private String nameSubstring;
    private List<Dragon> nameSearchResult;
    private Dragon deepestCaveDragon;
    private Double caveDepth;
    private String killersResult;
    private List<DragonType> types;

    @PostConstruct
    public void init() {
        types = Arrays.asList(DragonType.values());
    }

    public void groupByHead() {
        headGroups = dragonService.groupByHead();
        addMessage("Группировка выполнена");
    }

    public void countByType() {
        typeCount = dragonService.countByType(typeToCount);
        addMessage("Подсчёт выполнен");
    }

    public void findByNameSubstring() {
        nameSearchResult = dragonService.findByNameSubstring(nameSubstring);
        addMessage("Поиск выполнен");
    }

    public void findDeepestCaveDragon() {
        deepestCaveDragon = dragonService.findDeepestCaveDragon().orElse(null);
        addMessage("Самая глубокая пещера найдена");
    }

    public void sendKillersToCave() {
        killersResult = caveDepth == null ? "Укажите глубину пещеры" : dragonService.sendKillersToCave(caveDepth);
        addMessage("Команда обработана");
    }

    private void addMessage(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Готово", detail));
    }

    public Map<String, Long> getHeadGroups() { return headGroups; }
    public DragonType getTypeToCount() { return typeToCount; }
    public void setTypeToCount(DragonType typeToCount) { this.typeToCount = typeToCount; }
    public long getTypeCount() { return typeCount; }
    public String getNameSubstring() { return nameSubstring; }
    public void setNameSubstring(String nameSubstring) { this.nameSubstring = nameSubstring; }
    public List<Dragon> getNameSearchResult() { return nameSearchResult; }
    public Dragon getDeepestCaveDragon() { return deepestCaveDragon; }
    public Double getCaveDepth() { return caveDepth; }
    public void setCaveDepth(Double caveDepth) { this.caveDepth = caveDepth; }
    public String getKillersResult() { return killersResult; }
    public List<DragonType> getTypes() { return types; }
}
