package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Karrablast extends Pokemon {
    public Karrablast(String name, int level) {
        super(name,level);
        setType(Type.BUG);
        setStats(50, 75, 45, 40, 45, 60);
        setMove(new BugBuzz(), new Leer(), new Swagger());

    }

}