package attacks;

import ru.ifmo.se.pokemon.*;

public class CalmMind extends StatusMove {
    public CalmMind() {
        super(Type.PSYCHIC, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);

        Effect e = new Effect().stat(Stat.SPECIAL_ATTACK,1);

        p.addEffect(e);
    }

    @Override
    protected String describe() {
        return "Использовал Calm Mind";
    }

}