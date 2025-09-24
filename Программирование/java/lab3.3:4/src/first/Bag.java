package first;

import enums.MOOD;


public class Bag {
    private Ponchik item;
    public void addPonchik(Ponchik item) {
        this.item = item;
        System.out.println("Пончик попал в мешок((");
    }

    public boolean hasItem() {
        return item != null;
    }

    public void removePonchik(Ponchik item) {
        item.setState(75);
        item = null;;
        System.out.println("пончик упал из мешка.");

    }

    public void removePonchikv2(Ponchik item) {
        item.setState(50);
        item = null;
        System.out.println("пончик упал из мешка и ударился(((");

    }

    public void checkPonchik() {
        if (hasItem()) {
            System.out.println("В сумке есть пончик.");
        } else {
            System.out.println("Сумка пуста.");
        }
    }
}