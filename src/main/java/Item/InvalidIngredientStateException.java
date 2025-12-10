package Item;

public class InvalidIngredientStateException extends ItemException { // kelas exception untuk  state ingredient yang salah

    public InvalidIngredientStateException(String message) {
        super(message);
    }
    
}
