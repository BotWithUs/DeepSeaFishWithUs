package net.botwithus.skilling.fishing.deepsea.enums;

public enum DeepSeaBoost {

    INCREASE_CATCH_RATE,
    ADDITIONAL_CATCH,
    ADDITIONAL_XP;

    public static String[] toStringArray() {
        int i = 0;
        String[] activityStrings = new String[DeepSeaBoost.values().length];
        for (DeepSeaBoost activity : DeepSeaBoost.values()) {
            activityStrings[i++] = activity.toString();
        }
        return activityStrings;
    }
}
