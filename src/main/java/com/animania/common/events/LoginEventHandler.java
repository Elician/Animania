package com.animania.common.events;

import com.animania.Animania;
import com.animania.config.AnimaniaConfig;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@EventBusSubscriber(modid = Animania.MODID)
public class LoginEventHandler
{

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (UpdateHandler.show && AnimaniaConfig.gameRules.showModUpdateNotification == true) {
            event.player.sendMessage(new TextComponentString(UpdateHandler.updateStatus));

            ITextComponent component = ITextComponent.Serializer.jsonToComponent(I18n.translateToLocal("animania.update.download"));
            event.player.sendMessage(component);
        }
    }
}
