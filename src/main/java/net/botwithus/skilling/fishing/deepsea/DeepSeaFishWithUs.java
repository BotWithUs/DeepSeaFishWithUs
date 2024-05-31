package net.botwithus.skilling.fishing.deepsea;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.SkillUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.NativeBoolean;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.skilling.fishing.deepsea.enums.DeepSeaFisherBotState;
import net.botwithus.skilling.fishing.deepsea.enums.DeepSeaBoost;
import net.botwithus.skilling.fishing.deepsea.enums.FishingActivity;
import net.botwithus.skilling.fishing.deepsea.enums.Jellyfish;
import net.botwithus.skilling.fishing.deepsea.enums.MinnowBait;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class DeepSeaFishWithUs extends LoopingScript {

    public int totalFishCaught, totalFishCaughtPerHour, xpGained, xpPerHour, levelsGained;
    public String ttl = "You ain't gonna level sitting around!";
    private Random rand;
    private DeepSeaFisherBotState botState = DeepSeaFisherBotState.IDLE;
    public NativeBoolean debugMode = new NativeBoolean(false);
    public NativeBoolean claimBoosts = new NativeBoolean(false);
    public NativeInteger fishingActivity = new NativeInteger(0);
    public FishingActivity selectedActivity = FishingActivity.MINNOW;
    public NativeInteger boostActivity = new NativeInteger(0);
    public DeepSeaBoost selectedBoost = DeepSeaBoost.ADDITIONAL_XP;
    public NativeInteger baitType = new NativeInteger(0);
    public MinnowBait selectedBait = MinnowBait.SEA_TURTLE;
    public NativeInteger magicalBaitType = new NativeInteger(0);
    public MinnowBait selectedMagicalBait = MinnowBait.SEA_TURTLE;
    public NativeInteger jellyfishType = new NativeInteger(0);
    public Jellyfish selectedJellyfish = Jellyfish.GREEN_BLUBBER;
    public NativeBoolean turnMinnowIntoBait = new NativeBoolean(false);
    private int animationDeadCount = 0;
    public Long startTime;

    public DeepSeaFishWithUs(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
    }

    @Override
    public boolean initialize() {
        super.initialize();
        setActive(false);
        sgc = new DeepSeaFisherGraphicsContext(this, getConsole());
        rand = new Random();
        subscribe(SkillUpdateEvent.class, skillUpdateEvent -> {
            if (skillUpdateEvent.getId() == Skills.FISHING.getId()) {
                if (skillUpdateEvent.getExperience() - skillUpdateEvent.getOldExperience() > 0) {
                    totalFishCaught++;
                    xpGained += skillUpdateEvent.getExperience() - skillUpdateEvent.getOldExperience();
                }
                if (skillUpdateEvent.getActualLevel() > skillUpdateEvent.getOldActualLevel()) {
                    levelsGained++;
                }
            }
        });
        startTime = System.currentTimeMillis();
        loadConfiguration();
        return true;
    }

    @Override
    public void onLoop() {
        Player player = Client.getLocalPlayer();
        if (player == null || botState == DeepSeaFisherBotState.IDLE
                || Client.getGameState() != Client.GameState.LOGGED_IN) {
            delay(5000);
            return;
        }
        updateStats();
        switch (botState) {
            case BANKING:
                delay(handleBanking(player));
                return;
            case FISHING:
                delay(handleFishing(player));
        }
    }

    public int handleBanking(Player player) {
        if (player.getAnimationId() != -1 || player.isMoving())
            return rand.nextInt(1250, 3425);
        SimulateRandomAfk();
        if (Backpack.isFull()) {
            SceneObject bank = SceneObjectQuery.newQuery().name("Rowboat", "Magical net", "Bank boat")
                    .option("Deposit all fish").results().nearest();
            if (selectedActivity == FishingActivity.SAILFISH) {
                // dumb workaround since sailfish can think the entrace boat is closer.
                bank = SceneObjectQuery.newQuery().name("Magical net").option("Deposit all fish").results().nearest();
            }
            if (bank != null)
                println("Interacted bank: " + bank.interact("Deposit all fish"));
        } else {
            botState = DeepSeaFisherBotState.FISHING;
        }
        return rand.nextInt(945, 1668);
    }

    public int handleFishing(Player player) {
        return switch (selectedActivity) {
            case MINNOW -> handleCatchingMinnow(player);
            case MAGICAL -> handleCatchingMagical(player);
            case SWARM -> handleCatchingSwarm(player);
            case JELLYFISH -> handleCatchingJellyfish(player);
            case FRENZY -> handleCatchingFrenzy(player);
            case SAILFISH -> handleCatchingSailfish(player);
        };
    }

    public int handleCatchingMinnow(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving() || player.getAnimationId() != -1)
            animationDeadCount = 0;
        if (player.getAnimationId() == -1 && animationDeadCount > 2) {
            animationDeadCount = 0;
        } else {
            animationDeadCount++;
            return rand.nextInt(450, 710);
        }
        SimulateRandomAfk();
        int minnowCount = Backpack.getQuantity(Pattern.compile("Magnetic minnow"));
        if (turnMinnowIntoBait.get() && minnowCount >= 300) {
            if (Interfaces.isOpen(1370)) {
                Component currentBait = ComponentQuery.newQuery(1370).item(selectedBait.getItemId()).results().first();
                if (currentBait == null) {
                    // todo idk wtf this is
                    if (selectedBait == MinnowBait.MANTA_RAY) {
                        println("Selected bait type manta: "
                                + MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 5, 89849878));
                        delay(rand.nextLong(875, 1765));
                    }
                    if (selectedBait == MinnowBait.SEA_TURTLE) {
                        println("Selected bait type turtle: "
                                + MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 1, 89849878));
                        delay(rand.nextLong(875, 1765));
                    }
                    if (selectedBait == MinnowBait.GREAT_WHITE) {
                        println("Selected bait type great white: "
                                + MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 9, 89849878));
                        delay(rand.nextLong(875, 1765));
                    }
                } else {
                    println(currentBait.toString());
                }
                println("Confirmed bait type: "
                        + MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350));
                return rand.nextInt(1045, 2345);
            } else {
                Component minnow = ComponentQuery.newQuery(1473).itemName("Magnetic minnow").results().first();
                if (minnow != null)
                    println("Interacted magnetic minnow: " + minnow.interact(2));
            }
            return rand.nextInt(1134, 1857);
        } else {
            Npc minnowShoal = NpcQuery.newQuery().name("Minnow shoal").option("Catch").results().nearest();
            if (minnowShoal == null) {
                println("No minnow shoal was found. Please contact staff.");
            } else {
                SimulateRandomAfk();
                println("Interacted minnow shoal: " + minnowShoal.interact("Catch"));
            }
        }
        return rand.nextInt(2345, 3789);
    }

    public int handleCatchingMagical(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving() || player.getAnimationId() != -1)
            animationDeadCount = 0;
        if (player.getAnimationId() == -1 && animationDeadCount > 2) {
            animationDeadCount = 0;
        } else {
            animationDeadCount++;
            return rand.nextInt(450, 710);
        }
        if (Backpack.isFull()) {
            println("Inventory is full! Banking fish.");
            botState = DeepSeaFisherBotState.BANKING;
            return rand.nextInt(1743, 4642);
        }
        SimulateRandomAfk();
        Npc fishableSpot = NpcQuery.newQuery().name(selectedMagicalBait.getNpcName()).option("Catch").results()
                .nearest();
        if (fishableSpot == null) {
            int targetBaitCount = Backpack.getCount(selectedMagicalBait.getItemId());
            if (targetBaitCount == 0) {
                println("Magical fishing spots configured, but player is out of bait.");
                println("Switching to minnows.");
                selectedActivity = FishingActivity.MINNOW;
                fishingActivity.set(selectedActivity.ordinal());
                return rand.nextInt(985, 1875);
            } else {
                if (Interfaces.isOpen(720)) {
                    switch (selectedMagicalBait) {
                        case SEA_TURTLE -> MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185921);
                        case MANTA_RAY -> MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
                        case GREAT_WHITE -> MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185943);
                    }
                    return rand.nextInt(1013, 2425);
                }
            }
            Npc magicalSpot = NpcQuery.newQuery().name("Magical fishing spot")/* .option("Throw bait") */.results()
                    .nearest();
            if (magicalSpot != null) {
                println("Interacted magical spot: " + magicalSpot.interact("Throw bait"));
            }
        } else {
            println("Interacted fishabble spot: " + fishableSpot.interact("Catch"));
        }
        return rand.nextInt(2345, 3754);
    }

    public int handleCatchingSwarm(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving() || player.getAnimationId() != -1)
            animationDeadCount = 0;
        if (player.getAnimationId() == -1 && animationDeadCount > 2) {
            animationDeadCount = 0;
        } else {
            animationDeadCount++;
            return rand.nextInt(450, 710);
        }
        if (Backpack.isFull()) {
            println("Inventory is full! Banking fish.");
            botState = DeepSeaFisherBotState.BANKING;
            return rand.nextInt(1743, 4642);
        }
        List<Npc> fishableSpot = NpcQuery.newQuery().name("Swarm").option("Net").results().stream()
                .filter(npc -> npc.getCoordinate().getX() > 2094).toList();
        int random = rand.nextInt(0, fishableSpot.size());
        fishableSpot.get(random).interact("Net");
        return rand.nextInt(2456, 4642);
    }

    public int handleCatchingJellyfish(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving() || player.getAnimationId() != -1)
            animationDeadCount = 0;
        if (player.getAnimationId() == -1 && animationDeadCount > 2) {
            animationDeadCount = 0;
        } else {
            animationDeadCount++;
            return rand.nextInt(450, 710);
        }
        if (Backpack.isFull()) {
            println("Inventory is full! Banking fish.");
            botState = DeepSeaFisherBotState.BANKING;
            return rand.nextInt(1743, 4642);
        }
        Npc jelly = NpcQuery.newQuery().name(selectedJellyfish.getNpcName()).option("Catch").results().nearest();
        if (jelly != null) {
            println("Jelly interacted: " + jelly.interact("Catch"));
        }
        return rand.nextInt(1743, 4642);
    }

    public int handleCatchingFrenzy(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving())
            return rand.nextInt(1432, 2453);
        if (rand.nextInt(200) == 0)
            SimulateRandomAfk();
        Npc fish = NpcQuery.newQuery().name("Fish").option("Fling").results().random();
        if (fish != null) {
            println("Interacted frenzy fish: " + fish.interact("Fling"));
            delayUntil(3000, () -> NpcQuery.newQuery().id(fish.getId()).results().isEmpty());
            delay(rand.nextInt(35, 150));
        }
        return rand.nextInt(125, 470);
    }

    public int handleCatchingSailfish(Player player) {
        Execution.delay(handleBoosts());
        if (player.isMoving() || player.getAnimationId() != -1)
            animationDeadCount = 0;
        if (player.getAnimationId() == -1 && animationDeadCount > 2) {
            animationDeadCount = 0;
        } else {
            animationDeadCount++;
            return rand.nextInt(450, 710);
        }
        if (Backpack.isFull()) {
            println("Inventory is full! Banking fish.");
            botState = DeepSeaFisherBotState.BANKING;
            return rand.nextInt(1743, 4642);
        }
        SimulateRandomAfk();
        Npc sailfish = NpcQuery.newQuery().name("Swift sailfish").option("Catch").results().nearest();
        if (sailfish != null)
            println("Interacted sailfish spot: " + sailfish.interact("Catch"));
        return rand.nextInt(1765, 2875);
    }

    void updateStats() {
        totalFishCaughtPerHour = (int) (totalFishCaught / ((System.currentTimeMillis() - startTime) / 3600000.0));
        xpPerHour = (int) (xpGained / ((System.currentTimeMillis() - startTime) / 3600000.0));
        if (xpPerHour != 0) {
            int totalSeconds = Skills.FISHING.getExperienceToNextLevel() * 3600 / xpPerHour;
            int hours = totalSeconds / 3600;
            int minutes = totalSeconds % 3600 / 60;
            int seconds = totalSeconds % 60;
            ttl = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public long handleBoosts() {
        String[] boosts = { "Broken fishing rod", "Barrel of bait", "Message in a bottle", "Tangled fishbowl" };
        if (Backpack.contains(boosts)) {
            println("A deepsea boost was found, continuing.");
            ResultSet<Item> buff = InventoryItemQuery.newQuery(93).name(boosts).option("Interact").results();
            Execution.delay(rand.nextLong(600, 1100));
            if (buff != null) {
                for (Item item : buff) {
                    println("Found item: " + item.getName());
                    Backpack.interact(item.getName(), "Interact");
                    if (item.getId() == 42282) {
                        println("Is a message in a bottle: true");
                        Execution.delayUntil(7000, () -> Interfaces.isOpen(1186));
                        Execution.delay(rand.nextLong(650, 750));
                        MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 77725704);
                        Execution.delayUntil(7000, () -> Interfaces.isOpen(751));
                        switch (selectedBoost) {
                            case INCREASE_CATCH_RATE:
                                println("10% increase to catch rate");
                                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 49217586);
                                break;
                            case ADDITIONAL_CATCH:
                                println("10% change to gain an additional catch");
                                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 49217594); //
                                break;
                            case ADDITIONAL_XP:
                                println("5% XP increase");
                                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 49217602); //
                                break;
                        }
                    } else {
                        Execution.delayUntil(7000, () -> Interfaces.isOpen(847));
                        MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 55509014);
                        Execution.delayUntil(7000, () -> !Interfaces.isOpen(847));
                    }
                }
                Execution.delay(rand.nextLong(2100, 2600));
            }
            return rand.nextLong(1800, 2250);
        } else {
            return rand.nextLong(100, 250);
        }
    }

    public void SimulateRandomAfk() {
        // human will not always react right away.
        double random = rand.nextDouble();
        if (random * 100.0 > 97) {
            int milliSeconds = rand.nextInt(5000, 25000);
            if (rand.nextDouble() * 100 < 0.5)
                milliSeconds += rand.nextInt(15000, 50000);
            println("[DeepSeaFishWithUs] Unlucky 3% roll! Simulating human afk for " + milliSeconds / 1000
                    + " seconds.");
            delay(milliSeconds);
        }
    }

    void saveConfiguration() {
        try {
            configuration.addProperty("fishingActivity", String.valueOf(selectedActivity.ordinal()));
            configuration.addProperty("boostActivity", String.valueOf(selectedBoost.ordinal()));
            configuration.addProperty("baitType", String.valueOf(selectedBait.ordinal()));
            configuration.addProperty("magicalBaitType", String.valueOf(selectedMagicalBait.ordinal()));
            configuration.addProperty("jellyfishType", String.valueOf(selectedJellyfish.ordinal()));
            configuration.addProperty("turnMinnowIntoBait", String.valueOf(turnMinnowIntoBait.get()));
            configuration.addProperty("debugMode", String.valueOf(debugMode.get()));
            configuration.addProperty("claimBoosts", String.valueOf(claimBoosts.get()));
            configuration.addProperty("botState", botState.name());
            configuration.save();
        } catch (Exception e) {
            println("Error saving configuration: \n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    void loadConfiguration() {
        try {
            botState = DeepSeaFisherBotState.valueOf(configuration.getProperty("botState"));
            selectedActivity = FishingActivity.values()[Integer.parseInt(configuration.getProperty("fishingActivity"))];
            fishingActivity = new NativeInteger(selectedActivity.ordinal());
            selectedBoost = DeepSeaBoost.values()[Integer.parseInt(configuration.getProperty("boostActivity"))];
            boostActivity = new NativeInteger(selectedBoost.ordinal());
            selectedBait = MinnowBait.values()[Integer.parseInt(configuration.getProperty("baitType"))];
            baitType = new NativeInteger(selectedBait.ordinal());
            selectedMagicalBait = MinnowBait.values()[Integer.parseInt(configuration.getProperty("magicalBaitType"))];
            magicalBaitType = new NativeInteger(selectedMagicalBait.ordinal());
            selectedJellyfish = Jellyfish.values()[Integer.parseInt(configuration.getProperty("jellyfishType"))];
            jellyfishType = new NativeInteger(selectedJellyfish.ordinal());
            turnMinnowIntoBait.set(Boolean.parseBoolean(configuration.getProperty("turnMinnowIntoBait")));
            debugMode.set(Boolean.parseBoolean(configuration.getProperty("debugMode")));
            claimBoosts.set(Boolean.parseBoolean(configuration.getProperty("claimBoosts")));
        } catch (Exception e) {
            println("Error loading configuration: \n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    public DeepSeaFisherBotState getBotState() {
        return botState;
    }

    public void setBotState(DeepSeaFisherBotState state) {
        this.botState = state;
    }
}
