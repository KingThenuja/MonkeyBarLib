package dev.thenu.mk;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class MonkeyBar {

    public MonkeyBar() {


        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    }
}