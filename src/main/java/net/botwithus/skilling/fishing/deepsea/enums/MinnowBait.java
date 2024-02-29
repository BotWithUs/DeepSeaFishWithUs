package net.botwithus.skilling.fishing.deepsea.enums;

public enum MinnowBait {

    SEA_TURTLE("Sea turtle", 42238),
    MANTA_RAY("Manta ray", 42239),
    GREAT_WHITE("Great white shark", 42240);


    private int itemId;
    private String npcName;
    MinnowBait(String npcName, int itemId) {
        this.itemId = itemId;
        this.npcName = npcName;
    }

    public static String[] toStringArray() {
        int i = 0;
        String[] baitStrings = new String[MinnowBait.values().length];
        for (MinnowBait bait : MinnowBait.values()) {
            baitStrings[i++] = bait.toString();
        }
        return baitStrings;
    }

    public String getNpcName() {
        return npcName;
    }

    public int getItemId() {
        return itemId;
    }

}
