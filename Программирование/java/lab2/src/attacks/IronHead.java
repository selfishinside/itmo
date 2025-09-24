package attacks;

import ru.ifmo.se.pokemon.*;

public class IronHead extends PhysicalMove {
    public IronHead() {
        super(Type.STEEL, 80, 100);
    }

    private boolean Flinched = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() < 0.3) {
            Flinched = true;
            Effect.flinch(p);
        }
    }

    @Override
    protected String describe() {
        return "Использовал Iron Head" + ((Flinched) ? " и накладывает эффект flinch на цель " : "");
    }

}