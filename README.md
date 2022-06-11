# RememberMyTxt
Remember My Txt makes sure unrecognized values in the options.txt file get saved when changing other options, preventing things like losing keybinds when you are waiting for a mod to port to a newer version of Minecraft.

## So what's the problem?
[The way options.txt works can be found here](https://github.com/DuncanRuns/RememberMyTxt/blob/1.19/howoptionsworks.md), but can be summed up as follows:
1. Read the file
2. Take only the values Minecraft wants
3. Save only the values Minecraft has

Imagine you have a custom option called `key_gui.xaero_toggle_waypoints` in the options file.
This option, along with many others, is usually handled by Minecraft if you have the fabric mod [Xaero's Minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap) installed.

The problem is when you don't have the mod on for even a single launch of Minecraft, the options.txt will lose the `key_gui.xaero_toggle_waypoints` option, or any other options that the mod provides.
This can very easily happen when you update your Minecraft to a newer version, as you might not be able get all the mods you want immediately.

## So what's the solution?

The solution is very simple. All unrecognized options will simply be remembered, hence the name of the mod.
The remembered options which are not already recognized by Minecraft will then be saved along with the recognized ones.

Directly after minecraft loads options.txt, it temporarily stores all the options.
At this point, Remember My Txt comes in and stores all those options in memory in a seperate set of options called the "unaccepted".
For every single option that Minecraft actually reads, that options gets removed from the "unaccepted" options.
After this, we have a list of options which we know Minecraft will not save to the options.txt.
So when Minecraft starts to save options.txt again, we can use this list of unused options and put them back in the options.txt

## Usage/Legal Notes

If you want to include this in your modpack, go ahead!

If you want to implement the code into your own mod, please ask permission first.

The license is MIT, so these rules do not have legal implications by any means, however I ask that you respect them.

I was unable to find a mod which accomplishes what this mod does, so apologies if it appears that I have stolen from another developer.
