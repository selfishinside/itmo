package first;

import enums.MOOD;

public abstract class Essence {
    private String name;

    private int state;
    protected String mood;
    public Essence(String name, String mood, int state) {
        this.name = name;
        if(name.isEmpty())
            throw new NameException("Добавьте имя");

        this.mood = mood;
        if(mood.isEmpty())
            throw new NameException("Добавьте настроение");
        this.state = state;
        if(state <= 0)
            throw new NameException("Добавьте здоровье");
    }

    public String getName(){
        return name;
    }
    public String getMood() {
        return mood;
    }

    public int getState() { return state; }

    public void setState (int state) { this.state = state; }



    public void setMood(MOOD mood) {
        this.mood = String.valueOf(mood.getTitle());
    }

    public void eatFood(Food food) throws StateValueException{
        int newState = this.state + food.getStateValue();

        if (newState > 100) {
            throw new StateValueException("Невозможно увеличить здоровье выше 100.");
        }
        if (newState < 0) {
            throw new StateValueException("Невозможно уменьшить здоровье ниже 0.");
        }
        if (food.getStateValue() > 0) {
        System.out.println("Персонаж ест " + food.getName() + " и восстанавливает здоровье."); }
        if (food.getStateValue() < 0) {
            System.out.println("Персонаж ест " + food.getName() + " и теряет здоровье."); }
        this.state += food.getStateValue();
        System.out.println("Здоровье персонажа: " + this.state);
    }

    abstract void talk(String phrase);

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Essence essence = (Essence) obj;
        return essence.name.equals(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }


}



