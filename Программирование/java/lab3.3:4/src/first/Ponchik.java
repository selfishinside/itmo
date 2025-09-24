package first;

import enums.MOOD;

import java.util.List;

public class Ponchik extends Essence{



    public Ponchik(String name, String mood, int state) {
        super(name, mood, state);
    }


    //внутренний класс
    public class Eyes {
        void seeCharacters(List<String> characters) {
            for (String character : characters) {
                System.out.println("Пончик видит: " + character);
            }
            if (!characters.isEmpty()) {
                setMood(MOOD.surprised);
            }
        }
    }

    public void observeCharacters(List<String> characters) {
        Eyes eyes = new Eyes();
        eyes.seeCharacters(characters);
    }

    public void cry(String mood) {
        if (mood.equalsIgnoreCase("грустное")) {
            System.out.println("Пончик хныкал");
        }
        if(mood.equalsIgnoreCase("очень грустное")){
            System.out.println("Пончик громко заплакал");
        }


    }

    public void encounterWithVintikAndShpuntik(boolean hasMetVintikAndShpuntik) {
        // Локальный класс
        class PostEncounterState {
            private void expressFeelings() {
                System.out.println("Ой, как меня потрясло от встречи с Винтиком и Шпунтиком!");
            }

            public void reactToEncounter() {
                if (hasMetVintikAndShpuntik) {
                    expressFeelings();
                } else {
                    System.out.println("Пончик еще не встречал Винтика и Шпунтика.");
                }
            }
        }
        PostEncounterState postEncounterState = new PostEncounterState();
        postEncounterState.reactToEncounter();
    }



    @Override
    public void talk(String prhase) {
        System.out.println(prhase);

    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
