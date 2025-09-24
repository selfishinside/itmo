package attacks;

import ru.ifmo.se.pokemon.*;

public class BugBuzz extends SpecialMove {
    public BugBuzz() {
        super(Type.BUG, 90, 100);
    }

    private boolean ArmorReduction = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() < 0.1) {
            ArmorReduction = true;
            Effect e = new Effect().stat(Stat.SPECIAL_DEFENSE,-1);
            p.addEffect(e);
        }
    }

    @Override
    protected String describe() {
        return "Использовал Bug Buzz" + ((ArmorReduction) ? " и снижает SPECIAL DEFENSE противника " : "");
    }

}
