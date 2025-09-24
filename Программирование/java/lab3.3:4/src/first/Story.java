package first;

import enums.MOOD;
import interfaces.Storyable;

public class Story implements Storyable {


    public void changepartofstory(String part, Essence ponchik, Essence vintik, Essence shpuntik, Essence doctorpilulkin) {

        System.out.println(part + " часть истории");

        ponchik.setMood(MOOD.normal);
        vintik.setMood(MOOD.normal);
        shpuntik.setMood(MOOD.normal);
        doctorpilulkin.setMood(MOOD.normal);

    }



    @Override
    public void changepartofstory(int part, Essence ponchik, Essence vintik, Essence shpuntik, Bag bag) {

        System.out.println(part + " часть истории");

        if (part == 1) {
            ponchik.setMood(MOOD.normal);
            vintik.setMood(MOOD.normal);
            shpuntik.setMood(MOOD.normal);
            System.out.println("Настроение у " + ponchik + "a" + " стало " + ponchik.getMood());
            System.out.println("Настроение у " + vintik + "а" + " стало " + vintik.getMood());
            System.out.println("Настроение у " + shpuntik + "a" + " стало " + shpuntik.getMood());
        }

        if (part == 2) {
            ponchik.setMood(MOOD.normal);
            vintik.setMood(MOOD.normal);
            shpuntik.setMood(MOOD.normal);
            System.out.println("Настроение у " + ponchik + "a" + " стало " + ponchik.getMood());
            System.out.println("Настроение у " + vintik + "а" + " стало " + vintik.getMood());
            System.out.println("Настроение у " + shpuntik + "a" + " стало " + shpuntik.getMood());
            System.out.println("винтик и шпунтик рассказали, когда");
        }

        if (part == 3) {
            ponchik.setMood(MOOD.normal);
            vintik.setMood(MOOD.normal);
            shpuntik.setMood(MOOD.normal);
            System.out.println("Настроение у " + ponchik + "a" + " стало " + ponchik.getMood());
            System.out.println("Настроение у " + vintik + "а" + " стало " + vintik.getMood());
            System.out.println("Настроение у " + shpuntik + "a" + " стало " + shpuntik.getMood());
        }


    }
    public void changepartofstory(int part, Essence ponchik, Essence vintik, Essence shpuntik, Essence doctorPilulkin, Bag bag) {
        ponchik.setMood(MOOD.normal);
        vintik.setMood(MOOD.normal);
        shpuntik.setMood(MOOD.normal);
        doctorPilulkin.setMood(MOOD.normal);
        System.out.println("Настроение у " + ponchik + "a" + " стало " + ponchik.getMood());
        System.out.println("Настроение у " + vintik + "а" + " стало " + vintik.getMood());
        System.out.println("Настроение у " + shpuntik + "a" + " стало " + shpuntik.getMood());
        System.out.println("Настроение у " + doctorPilulkin + "a" + " стало " + shpuntik.getMood());



    }


}
