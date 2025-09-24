package attacks;

import ru.ifmo.se.pokemon.*;

public class SignalBeam extends SpecialMove {
    public SignalBeam() {
        super(Type.BUG, 75, 100);
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
        return "Использовал Signal Beam" + ((Confused) ? " и накладывает эффект Confused на цель" : "");
    }

}
