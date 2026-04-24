package me.duncanruns.remembermytxt.mixin;

import me.duncanruns.remembermytxt.RememberMyTxt;
import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Unique
    private CompoundTag loadedData;
    @Unique
    private Map<String, String> unacceptedOptions = null;

    @Shadow
    protected abstract void processOptions(Options.FieldAccess visitor);

    @Inject(method = "dataFix", at = @At("RETURN"))
    private void storeLoadedData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        loadedData = cir.getReturnValue();
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void endLoadMixin(CallbackInfo info) {
        Set<String> unacceptedKeys = new HashSet<>(this.loadedData.keySet());
        this.processOptions(new Options.FieldAccess() {
            @Override
            public <T> void process(String key, OptionInstance<T> option) {
                unacceptedKeys.remove(key);
            }

            @Override
            public int process(String key, int current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public boolean process(String key, boolean current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public String process(String key, String current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public float process(String key, float current) {
                unacceptedKeys.remove(key);
                return current;
            }

            @Override
            public <T> T process(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                unacceptedKeys.remove(key);
                return current;
            }
        });
        unacceptedKeys.remove("version");
        unacceptedOptions = new HashMap<>();
        for (String key : unacceptedKeys.toArray(new String[0])) {
            loadedData.getString(key).ifPresent(s -> {
                RememberMyTxt.log(Level.INFO, "Unaccepted Key: \"" + key + "\" with value: " + loadedData.get(key));
                unacceptedOptions.put(key, s);
            });
        }
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;processOptions(Lnet/minecraft/client/Options$FieldAccess;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void writeUnacceptedMixin(CallbackInfo info, PrintWriter printWriter) {
        if (unacceptedOptions == null) return;
        // Unaccepted variables will be placed at the top in case they weren't accepted by the visitor during reading.
        // This probably means that they will be written a second time later in the file, and for duplicate keys, the
        // lowest one in the file is the one which will be loaded.
        for (Map.Entry<String, String> entry : unacceptedOptions.entrySet()) {
            printWriter.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
