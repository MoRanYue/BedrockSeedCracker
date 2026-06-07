package me.miran.bedrockcracker.api;

import me.miran.bedrockcracker.BedrockCracker;
import me.miran.bedrockcracker.api.settings.BedrockCrackerSettings;
import me.miran.bedrockcracker.api.settings.CrackStartType;
import net.minecraft.network.chat.Component;

/**
 * the default controller, present mainly for the cases that there aren't any other ones
 * should initialize ALL settings
 * */
public class DefaultBedrockCrackerController implements BedrockCrackerController {


    @Override
    public void seedCrackedEvent(long worldSeed) {
        BedrockCracker.sendChatMessage(Component.literal("World seed: " + worldSeed));
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
