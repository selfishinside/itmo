package first;

public class UnhealthyFood extends Food {
    private static final int StateValue = -5;

    public UnhealthyFood(String name) {
        super(name, StateValue);
    }
}