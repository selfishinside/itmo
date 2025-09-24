package attacks;

import ru.ifmo.se.pokemon.*;

public class Psybeam extends SpecialMove {
    public Psybeam() {
        super(Type.PSYCHIC, 65, 100);
    }

    private boolean Confused = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() < 0.1) {
            Confused = true;
            Effect.confuse(p);
        }
    }

    @Override
    protected String describe() {
        return "Использовал Psybeam" + ((Confused) ? " и накладывает эффект Confused на цель" : "");
    }

}