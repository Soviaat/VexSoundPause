package net.soviaat.vexsoundpause.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.soviaat.vexsoundpause.VexSoundPauseModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class VexSoundDetectionMixin {

    @Inject(at = @At("HEAD"), method = "playSound")
    private void onPlaySound(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (sound == SoundEvents.ENTITY_VEX_AMBIENT) {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc != null && mc.player != null && mc.world != null) {
                if(!VexSoundPauseModClient.isCooldownActive() && VexSoundPauseModClient.isListening) {
                    VexSoundPauseModClient.triggerPopup();
                }
            }
        }
    }
}
