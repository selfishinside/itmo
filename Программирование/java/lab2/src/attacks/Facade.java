package attacks;
import ru.ifmo.se.pokemon.*;

public class Facade extends StatusMove {
    public Facade() {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        super.applySelfEffects(p);

        Effect e = new Effect().stat(Stat.DEFENSE, 1);

        p.addEffect(e);
    }

    @Override
    protected String describe() {
        return "Использовал Facade";
    }

}