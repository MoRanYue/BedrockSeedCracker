package me.miran.bedrockcracker.cracker;

import me.miran.bedrockcracker.cracker.util.BedrockType;
import me.miran.bedrockcracker.util.BedrockCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.List;

public class OverworldCracker {
    // brute forces the top bits of the world seed from the structure seed
    // it is mimicking the bedrock pattern spawning in overworld and checking against previously cached blocks
    public static void addOverworldSeedToList(long structureSeed, List<Long> resultList) {
        List<BlockPos> list = BedrockCollector.getOverworldBedrock();
        BedrockType bedrockType = BedrockType.FLOOR_OVERWORLD;

        seedLoop:
        for (long i = 0; i < (1L << 16); i++) {
            long seed = (i << 48) | structureSeed;

            XoroshiroRandomSource xoroshiroRandomSource = new XoroshiroRandomSource(seed);
            PositionalRandomFactory splitter = xoroshiroRandomSource.forkPositional();

            PositionalRandomFactory bedrockSplitter = splitter
                    .fromHashOf(bedrockType.name)
                    .forkPositional();

            for (BlockPos pos : list) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();

                double d = Mth.map(y, bedrockType.startY, bedrockType.endY, 1.0, 0.0);
                RandomSource random = bedrockSplitter.at(x, y, z);

                if (((double) random.nextFloat()) >= d) continue seedLoop;
            }

            resultList.add(seed);
        }
    }
}