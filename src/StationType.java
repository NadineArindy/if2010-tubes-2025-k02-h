package src;
enum StationType {
    CUTTING('C'),
    COOKING('R'),
    ASSEMBLY('A'),
    SERVING('S'),
    WASHING('W'),
    INGREDIENT('I'),
    PLATE('P'),
    TRASH('T');

    private final char symbol;

    StationType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() { return symbol; }

    public static StationType fromSymbol(char c) {
        for (StationType t : values()) {
            if (t.symbol == c) return t;
        }
        return null;
    }
}