package com.destinedtowander.hephaestus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyReturnValue(method = "getXpNeededForNextLevel", at = @At("RETURN"))
    private int constantScaling(int original){
        return 25;
    }
}
