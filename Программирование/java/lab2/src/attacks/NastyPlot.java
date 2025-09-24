package attacks;

import ru.ifmo.se.pokemon.*;

public class NastyPlot extends StatusMove {
    public NastyPlot() {
        super(Type.DARK, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);

        Effect e = new Effect().stat(Stat.SPECIAL_ATTACK,2);

        p.addEffect(e);
    }

    @Override
    protected String describe() {
        return "Использовал Nasty Plot";
    }

}