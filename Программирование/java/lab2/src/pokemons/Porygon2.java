package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Porygon2 extends Pokemon {
    public Porygon2(String name, int level) {
        super(name, level);
        setType(Type.NORMAL);
        setStats(85, 80, 90, 105, 95, 60);
        setMove(new Psybeam(),new Facade(), new DefenseCurl());

    }

}