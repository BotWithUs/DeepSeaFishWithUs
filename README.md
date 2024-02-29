### BotWithUs Script: DeepSeaFishWithUs
- This is an IntelliJ IDEA project for a BotWithUs script. It is a Kotlin project, and is set up to use the BotWithUs API.
- You can subscribe to it on our marketplace here: [BotWithUs Website](https://botwithus.net/sdn)
- Here is the script's Wiki page: [DeepSeaFishWithUs](https://wiki.botwithus.net/DeepSeaFishWithUs)

### Description
- Completes all fishing activities at the Deep Sea Fishing hub.
- Simulates AFK moments
- Stat tracking in bot UI.
- Persistent UI settings / bot state

### Info
- The easiest way to get setup is to clone/download this repo, and from IntelliJ IDEA, File > open the folder.
- When you first open the code, it's usually a good idea to refresh gradle dependencies to resolve any dependency errors.
- You'll find the script itself located at ``src/main/java/net/botwithus/skilling/fishing/deepsea/DeepSeaFishWithUs.java``
- You'll find the graphics context (which allows you to draw UI with ImGui) at ``src/main/java/net/botwithus/skilling/fishing/deepsea/DeepSeaFishWithUsGraphicsContext.java``
- Come ask us questions in our discord: [BotWithUs Discord](https://discord.gg/botwithus)

### After downloading/copying for repurposing
- You should change your project name in ``settings.gradle.kts``
- You should make sure gradle is configured with JDK 20 (OpenJDK or Corretto) ``File > Settings > Build, Execution, Deployment > Build Tools > Gradle``
- Rename the script and graphics context to something appropriate.
- Update script.ini to relevant info