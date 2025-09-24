package enums;

public enum MOOD {
    normal("обычное"),

    sad("грустное"),
    confused("расстеряное"),
    happy("радостное"),
    verysad("очень грустное"),

    surprised("удивленное"),

    veryhappy();

    private String title;
    MOOD(String title){
        this.title = title;
    }
    public String getTitle(){ return title;}

    MOOD(){

    }

}



