package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Porygonz extends Pokemon {
    public Porygonz(String name, int level) {
        super(name, level);
        setType(Type.NORMAL);
        setStats(85, 80, 70, 135, 75, 90);
        setMove(new Psybeam(), new Facade(), new DefenseCurl(), new SignalBeam());

    }

}