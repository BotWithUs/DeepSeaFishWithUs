package net.botwithus.skilling.fishing.deepsea;

import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.skilling.fishing.deepsea.enums.DeepSeaFisherBotState;
import net.botwithus.skilling.fishing.deepsea.enums.FishingActivity;
import net.botwithus.skilling.fishing.deepsea.enums.Jellyfish;
import net.botwithus.skilling.fishing.deepsea.enums.MinnowBait;

public class DeepSeaFisherGraphicsContext extends ScriptGraphicsContext {

    private DeepSeaFishWithUs deepSeaFishWithUs;
    public DeepSeaFisherGraphicsContext(DeepSeaFishWithUs deepSeaFishWithUs, ScriptConsole console) {
        super(console);
        this.deepSeaFishWithUs = deepSeaFishWithUs;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("DeepSeaFishWithUs", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("Bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Bot State: " + deepSeaFishWithUs.getBotState());
                    if (ImGui.Button("Start!")) {
                        deepSeaFishWithUs.setBotState(DeepSeaFisherBotState.FISHING);
                        deepSeaFishWithUs.saveConfiguration();
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop!")) {
                        deepSeaFishWithUs.setBotState(DeepSeaFisherBotState.IDLE);
                        deepSeaFishWithUs.saveConfiguration();
                    }
                    ImGui.SameLine();
                    deepSeaFishWithUs.debugMode.set(ImGui.Checkbox("Debug mode", deepSeaFishWithUs.debugMode.get()));
                    handleConfigChange();
                    ImGui.Combo("Fishing activity", deepSeaFishWithUs.fishingActivity, FishingActivity.toStringArray());
                    deepSeaFishWithUs.selectedActivity = FishingActivity.values()[deepSeaFishWithUs.fishingActivity.get()];
                    handleConfigChange();
                    if (deepSeaFishWithUs.selectedActivity == FishingActivity.MINNOW) {
                        deepSeaFishWithUs.turnMinnowIntoBait.set(ImGui.Checkbox("Turn minnow into bait", deepSeaFishWithUs.turnMinnowIntoBait.get()));
                        handleConfigChange();
                        if (deepSeaFishWithUs.turnMinnowIntoBait.get()) {
                            ImGui.Combo("Bait type", deepSeaFishWithUs.baitType, MinnowBait.toStringArray());
                            deepSeaFishWithUs.selectedBait = MinnowBait.values()[deepSeaFishWithUs.baitType.get()];
                            handleConfigChange();
                        }
                    }
                    if (deepSeaFishWithUs.selectedActivity == FishingActivity.MAGICAL) {
                        ImGui.Combo("Magical Bait Type", deepSeaFishWithUs.magicalBaitType, MinnowBait.toStringArray());
                        deepSeaFishWithUs.selectedMagicalBait = MinnowBait.values()[deepSeaFishWithUs.magicalBaitType.get()];
                        handleConfigChange();
                    }
                    if (deepSeaFishWithUs.selectedActivity == FishingActivity.JELLYFISH) {
                        ImGui.Combo("Jellyfish type", deepSeaFishWithUs.jellyfishType, Jellyfish.toStringArray());
                        deepSeaFishWithUs.selectedJellyfish = Jellyfish.values()[deepSeaFishWithUs.jellyfishType.get()];
                        handleConfigChange();
                    }
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Stats", ImGuiWindowFlag.None.getValue())) {
                    long elapsedTime = System.currentTimeMillis() - deepSeaFishWithUs.startTime;
                    long seconds = elapsedTime / 1000 % 60;
                    long minutes = elapsedTime / (1000 * 60) % 60;
                    long hours = elapsedTime / (1000 * 60 * 60) % 24;
                    ImGui.Text(
                            "Runtime: %02d:%02d:%02d%n",
                            hours, minutes, seconds
                    );
                    ImGui.Text("Level: " + Skills.FISHING.getActualLevel());
                    ImGui.Text("Levels gained: " + deepSeaFishWithUs.levelsGained);
                    ImGui.Text("TTL: %s", deepSeaFishWithUs.ttl);
                    ImGui.Separator();
                    ImGui.Text("Xp gained: %,d", deepSeaFishWithUs.xpGained);
                    ImGui.Text("Xp per hour: %,d", deepSeaFishWithUs.xpPerHour);
                    ImGui.Separator();
                    ImGui.Text("Total fish caught: " + deepSeaFishWithUs.totalFishCaught);
                    ImGui.Text("Total fish caught per hour: %,d", deepSeaFishWithUs.totalFishCaughtPerHour);
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }

            ImGui.End();
        }
    }

    void handleConfigChange() {
        if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
            deepSeaFishWithUs.saveConfiguration();
        }
    }
}
