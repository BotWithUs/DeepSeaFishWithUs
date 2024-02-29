package net.botwithus.skilling.fishing.deepsea.enums;

public enum Jellyfish {

    GREEN_BLUBBER("Green blubber jellyfish"),
    BLUE_BLUBBER("Blue blubber jellyfish");

    private String npcName;
    Jellyfish(String npmName) {
        this.npcName = npmName;
    }

    public static String[] toStringArray() {
        String [] jellies = new String[values().length];
        int index = 0;
        for (Jellyfish jelly : values())
            jellies[index++] = jelly.toString();
        return jellies;
    }

    public String getNpcName() {
        return npcName;
    }

}
