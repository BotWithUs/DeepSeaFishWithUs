package net.botwithus.skilling.fishing.deepsea.enums;

public enum FishingActivity {

    MINNOW,
    MAGICAL,
    SWARM,
    JELLYFISH,
    FRENZY,
    SAILFISH;

    public static String[] toStringArray() {
        int i = 0;
        String[] activityStrings = new String[FishingActivity.values().length];
        for (FishingActivity activity : FishingActivity.values()) {
            activityStrings[i++] = activity.toString();
        }
        return activityStrings;
    }


}
