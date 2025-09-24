package attacks;

import ru.ifmo.se.pokemon.*;

public class ZenHeadbutt extends PhysicalMove {
    public ZenHeadbutt() {
        super(Type.PSYCHIC, 80, 90);
    }

    private boolean Flinched = false;

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() < 0.2) {
            Flinched = true;
            Effect.flinch(p);
        }
    }

    @Override
    protected String describe() {
        return "Использовал Zen Headbutt" + ((Flinched) ? " и накладывает эффект flinch на цель" : "");
    }

}