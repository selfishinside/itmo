import ru.ifmo.se.pokemon.*;
import pokemons.*;

public class program {

    //var - 2689
    //https://pokemondb.net/pokedex/girafarig
    //https://pokemondb.net/pokedex/karrablast
    //https://pokemondb.net/pokedex/escavalier
    //https://pokemondb.net/pokedex/porygon
    //https://pokemondb.net/pokedex/porygon2
    //https://pokemondb.net/pokedex/porygon-z

   

    public static void main(String[] args) {

        Battle b = new Battle();

        Pokemon karrablast;

        karrablast = new Karrablast("karrablast", 1);
        Girafarig girafarig = new Girafarig("girafarig",1);
        Escavalier escavalier = new Escavalier("escavalier",1);
        Porygon porygon = new Porygon("porygon",1);
        Porygon2 porygon2 = new Porygon2("porygon2",1);
        Porygonz porygonz = new Porygonz("porygonz",1);

        b.addAlly(karrablast);
        b.addFoe(girafarig);
        b.addAlly(escavalier);
        b.addFoe(porygon);
        b.addAlly(porygon2);
        b.addFoe(porygonz);

        b.go();




    }
}