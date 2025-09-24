package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Girafarig extends Pokemon {
	public Girafarig(String name, int level) {
		super(name,level);
		setType(Type.NORMAL, Type.PSYCHIC);
		setStats(70, 80, 65, 90, 65, 85);
		setMove(new ZenHeadbutt(), new CalmMind(), new Growl(), new NastyPlot());

	}

}