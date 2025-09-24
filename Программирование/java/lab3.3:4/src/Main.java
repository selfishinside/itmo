import enums.MOOD;
import first.*;
import interfaces.Astronaut;

import javax.print.Doc;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        Ponchik ponchik = new Ponchik("пончик", "обычное", 100);
        Vintik vintik = new Vintik("винтик", "обычное", 100);
        Shpuntik shpuntik = new Shpuntik("шпунтик", "обычное", 100);
        Story story = new Story();
        Bag bag = new Bag();
        Random random = new Random();
        int randomValue = random.nextInt(2);
        DoctorPilulkin doctorPilulkin = new DoctorPilulkin("доктор пилюлькин", "обычное", 100);
        Food healthyFood = new HealthyFood("Яблоко");
        Food regularFood = new RegularFood("Хлеб");
        Food unhealthyFood = new UnhealthyFood("Газировка");
        List<String> characters = List.of("Знайка", "Фуксия", "Селедочка", "Тюбик", "доктор Пилюлькин");
        boolean hasMetVintikAndShpuntik = false;


        //анонимные
        Astronaut znayka = new Astronaut() {
            @Override
            public void onMeeting() {
                System.out.println("Знайка: помахал пончику");
            }
        };

        Astronaut fuxia = new Astronaut() {
            @Override
            public void onMeeting() {
                System.out.println("Фуксия: помахала пончику");
            }
        };

        Astronaut seledochka = new Astronaut() {
            @Override
            public void onMeeting() {
                System.out.println("Селедочка: помахала пончику");
            }
        };

        Astronaut tyubik = new Astronaut() {
            @Override
            public void onMeeting() {
                System.out.println("Тюбик: помахал пончику");
            }
        };





        story.changepartofstory("background", ponchik, vintik, shpuntik, doctorPilulkin);
        ponchik.encounterWithVintikAndShpuntik(hasMetVintikAndShpuntik);
        hasMetVintikAndShpuntik = true;
        ponchik.encounterWithVintikAndShpuntik(hasMetVintikAndShpuntik);

        System.out.println("Пончик, который еще не опомнился от встречи с Винтиком и Шпунтиком");



        ponchik.observeCharacters(characters);
        System.out.println("настроение у " + ponchik.getName() + "a " + ponchik.getMood());




        znayka.onMeeting();
        fuxia.onMeeting();
        seledochka.onMeeting();
        tyubik.onMeeting();

        story.changepartofstory(1, ponchik, vintik, shpuntik, bag);
        System.out.println();
        bag.addPonchik(ponchik);
        if (bag.hasItem()){
            vintik.nottoknow(ponchik);
            vintik.setMood(MOOD.normal);
            shpuntik.nottoknow(ponchik);
            shpuntik.setMood(MOOD.sad);
            ponchik.setMood(MOOD.confused);
        }
        else {
            vintik.toknow(ponchik);
            vintik.setMood(MOOD.happy);
            shpuntik.toknow(ponchik);
            shpuntik.setMood(MOOD.happy);
            ponchik.setMood(MOOD.happy);
        }
        System.out.println();
        System.out.println("настроение у " + ponchik.getName() + "a " + ponchik.getMood());
        System.out.println("настроение у " + vintik.getName() + "a " + vintik.getMood());
        System.out.println("настроение у " + shpuntik.getName() + "a " + shpuntik.getMood());
        System.out.println();

        story.changepartofstory(2, ponchik, vintik, shpuntik, bag);
        System.out.println();
        if (randomValue == 1) {
            vintik.shakeout(bag,ponchik);
            ponchik.setMood(MOOD.sad);
        }
        else {
            shpuntik.shakeoutvery(bag, ponchik);
            ponchik.setMood(MOOD.verysad);
        }

        ponchik.cry(ponchik.getMood());

        System.out.println();
        System.out.println("настроение у " + ponchik.getName() + "a " + ponchik.getMood());
        System.out.println("настроение у " + vintik.getName() + "a " + vintik.getMood());
        System.out.println("настроение у " + shpuntik.getName() + "a " + shpuntik.getMood());
        System.out.println();

        story.changepartofstory(3, ponchik, vintik, shpuntik, bag);
        vintik.talk("потом всю дорогу только и делал, что твердил:");
        ponchik.talk("Братцы, братцы!!!");
        shpuntik.talk("словно позабыл, какие еще на свете бывают слова.");
        System.out.println();

        story.changepartofstory(4, ponchik, vintik, shpuntik, doctorPilulkin, bag);
        doctorPilulkin.talk("Пончик скоро оправится от потрясения");
        doctorPilulkin.checkstate(ponchik);
        doctorPilulkin.talk("Надо дать ему поесть");

        try { ponchik.eatFood(healthyFood); } catch (StateValueException e)
        { System.out.println("Error " + e.getMessage()); }

        try { ponchik.eatFood(regularFood); } catch (StateValueException e)
        { System.out.println("Error " + e.getMessage()); }

        try { ponchik.eatFood(unhealthyFood); } catch (StateValueException e)
        { System.out.println("Error " + e.getMessage()); }

        System.out.println("Так и на самом деле случилось.");
        System.out.println("Пончик быстро уписал все это и тут же начал рассказывать о том, что произошло с ним");




    }
}