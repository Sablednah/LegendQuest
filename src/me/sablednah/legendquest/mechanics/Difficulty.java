package me.sablednah.legendquest.mechanics;


public enum Difficulty {
    VERY_EASY (0),
    EASY (5),
    AVERAGE (10),
    TOUGH (15),
    CHALLENGING (20);

    private int difficulty;
    Difficulty (int difficulty){
        this.setDifficulty(difficulty);
    }
    /**
     * @return the difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }
    /**
     * @param difficulty the difficulty to set
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
