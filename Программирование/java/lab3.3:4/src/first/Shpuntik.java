package first;

import interfaces.Doingable;

public class Shpuntik extends Essence implements Doingable {

    public Shpuntik(String name, String mood, int state) {
        super(name, mood, state);
    }

    @Override
    public void talk(String phrase) {
        System.out.println(phrase);

    }

    @Override
    public void toknow(Ponchik ponchik) {
        System.out.println(getName() + " смог ничего добиться от что-то" + ponchik.getName() + "a");
    }

    @Override
    public void nottoknow(Ponchik ponchik) {
        System.out.println(getName() + " не смог ничего добиться от " + ponchik.getName() + "a он в мешке...");
    }

    @Override
    public void shakeout(Bag bag, Ponchik ponchik) {
        bag.removePonchik(ponchik);

    }

    @Override
    public void shakeoutvery(Bag bag, Ponchik ponchik) {
        bag.removePonchikv2(ponchik);

    }

    @Override
    public void bringfood(Essence person) {

    }


}
