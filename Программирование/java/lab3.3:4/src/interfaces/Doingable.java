package interfaces;

import first.Bag;
import first.Essence;
import first.Ponchik;

public interface Doingable {
    void toknow(Ponchik ponchik);
    void nottoknow(Ponchik ponchik);

    void shakeout(Bag bag, Ponchik ponchik);
    void shakeoutvery(Bag bag, Ponchik ponchik);

    void bringfood(Essence person);


}
