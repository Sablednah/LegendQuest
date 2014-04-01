package me.sablednah.legendquest.mechanics;


public enum Attribute {
    STR ("Strength"),
    DEX ("Dexterity"),
    CON ("Constitution"),
    INT ("Intelegence"),
    WIS ("Wisdom"),
    CHR ("Charisa");
    private String label;
    Attribute(String label) {
        this.setLabel(label);
    }
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
