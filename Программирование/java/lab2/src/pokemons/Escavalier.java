package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Escavalier extends Pokemon {
    public Escavalier(String name, int level) {
        super(name,level);
        setType(Type.BUG, Type.STEEL);
        setStats(70, 135, 105, 60, 105, 20);
        setMove(new BugBuzz(), new Leer(), new Swagger(), new IronHead());

    }

}