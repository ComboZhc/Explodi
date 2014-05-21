package org.example.explodi;

public enum Difficulty {
	EASY(0), NORMAL(1), HARD(2);
    private int value = 0;

    private Difficulty(int value) {
        this.value = value;
    }

    public static Difficulty valueOf(int value) {
        switch (value) {
        case 0:
            return EASY;
        case 1:
            return NORMAL;
        case 2:
        	return HARD;
        default:
            return null;
        }
    }

    public int value() {
        return this.value;
    }
}
