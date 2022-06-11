
## How options.txt works
To understand how the mod works, we first need to understand the behaviour of the options.txt file and how Minecraft handles it.

### options.txt format
Here is what a portion of what a typical options.txt could look like:
```
toggleSprint:false
darkMojangStudiosBackground:false
hideLightningFlashes:false
mouseSensitivity:0.5
fov:0.825
screenEffectScale:1.0
fovEffectScale:1.0
gamma:0.5
renderDistance:12
simulationDistance:12
entityDistanceScaling:1.0
```
It follows the basic format of `key:value` where key is the name of the option and value is what that option is set to.

### Loading and Saving the options
When Minecraft loads the options, it will only check the keys related to options inside the code of the game.
With fabric installed, additional keys may be checked depending on the loaded mods.
Once the keys are checked, their corrosponding values are converted into the appropriate format (integers vs decimals vs true/falses) and stored in the game's memory.

When Minecraft saves the options, it will turn the stored values back into text form to be placed with their keys in the options.txt file.
It is important to note that the entire options.txt file must be saved at the same time due to how files work, and that even if you change a single value, Minecraft will perform the full save.

