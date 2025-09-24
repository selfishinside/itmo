package first;

public class DoctorPilulkin extends Essence {

    public DoctorPilulkin(String name, String mood, int state) {
        super(name, mood, state);
    }
    @Override
    public void talk(String phrase) {
        System.out.println(phrase);
    }

    public void checkstate(Essence person) {
        int state_person = person.getState();
        if (state_person == 100) { System.out.println(person.getName() + " идеально здорвов!!"); }
        if (state_person >= 75 && state_person < 100) { System.out.println(person.getName() + " здоров!!"); }
        if (state_person >= 50 && state_person < 75) { System.out.println(person.getName() + " приболел!!"); }
        if (state_person >= 25 && state_person < 50) { System.out.println(person.getName() + " болен!!"); }
        if (state_person < 25) { System.out.println(person.getName() + "тяжело болен!!"); }

    }



}
