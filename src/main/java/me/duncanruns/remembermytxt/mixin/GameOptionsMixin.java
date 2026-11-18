package me.duncanruns.remembermytxt.mixin;

import me.duncanruns.remembermytxt.RememberMyTxt;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow
    @Final
    private File optionsFile;

    @Shadow
    protected abstract void accept(GameOptions.Visitor visitor);

    private NbtCompound loadedData;
    private Map<String, String> unacceptedOptions;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;update(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getKeysMixin(CallbackInfo info, NbtCompound nbtCompound2) {
        loadedData = nbtCompound2;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void endLoadMixin(CallbackInfo info) {
        Set<String> unacceptedKeys = this.loadedData.getKeys();
        this.accept(new GameOptions.Visitor() {
            @Override
            public int visitInt(String key, int current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public boolean visitBoolean(String key, boolean current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public String visitString(String key, String current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public double visitDouble(String key, double current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public float visitFloat(String key, float current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public <T> T visitObject(String key, T current, IntFunction<T> decoder, ToIntFunction<T> encoder) {
                unacceptedKeys.remove(key);
                return current;
            }
        });
        unacceptedKeys.remove("version");
        unacceptedOptions = new HashMap<>();
        for (String key : unacceptedKeys.toArray(new String[0])) {
            RememberMyTxt.log(Level.INFO, "Unaccepted Key: \"" + key + "\" with value: " + loadedData.get(key));
            unacceptedOptions.put(key, loadedData.getString(key));
        }
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void refreshUnacceptedMixin(CallbackInfo info) {
        if (unacceptedOptions == null || unacceptedOptions.isEmpty() || !optionsFile.exists()) return;
        try {
            for (String line : Files.readAllLines(optionsFile.toPath(), StandardCharsets.UTF_8)) {
                int split = line.indexOf(':');
                if (split < 0) continue;
                String key = line.substring(0, split);
                if (unacceptedOptions.containsKey(key)) {
                    unacceptedOptions.put(key, line.substring(split + 1));
                }
            }
        } catch (IOException ignored) {
        }
    }

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;accept(Lnet/minecraft/client/option/GameOptions$Visitor;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void writeUnacceptedMixin(CallbackInfo info, PrintWriter printWriter) {
        // Unaccepted variables will be placed at the top in case they weren't accepted by the visitor during reading.
        // This probably means that they will be written a second time later in the file, and for duplicate keys, the
        // lowest one in the file is the one which will be loaded.
        for (Map.Entry<String, String> entry : unacceptedOptions.entrySet()) {
            printWriter.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
