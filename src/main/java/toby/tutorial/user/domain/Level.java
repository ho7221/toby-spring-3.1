package toby.tutorial.user.domain;

public enum Level {
    GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);

    private final int value;
    private final Level next;

    Level(int level, Level next){
        this.value = level;
        this.next = next;
    }

    public int intValue(){
        return value;
    }

    public static Level valueOf(int value){
        switch(value){
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value:" + value);
        }
    }

    public Level getNext(){
        return this.next;
    }
}
