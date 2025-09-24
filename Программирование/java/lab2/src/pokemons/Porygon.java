package pokemons;

import ru.ifmo.se.pokemon.Type;
import ru.ifmo.se.pokemon.Pokemon;
import java.lang.*;
import attacks.*;

public class Porygon extends Pokemon {
    public Porygon(String name, int level) {
        super(name,level);
        setType(Type.NORMAL);
        setStats(65, 60, 70, 85, 75, 40);
        setMove(new Psybeam(),new Facade());

    }

}

/*
* class A{
*   void foo(){System.out.println(1);}
* }
* */
/*
 * class B extends A{
 *   void foo(){System.out.println(2);}
 * }
 * *//*
 * class C extends B{
 *   void foo(){System.out.println(3);}
 * }
 * */
/*
*Scanner scanner = new Scanner(System.in);
*int x = scanner.nextInt();
*A a;
*if (x  >  0) a = new B();
*else a = new C();
*/