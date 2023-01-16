package dev.bsmp.bouncestyles;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.item.StyleArmorMaterial;
import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Mod(BounceStyles.modId)
public class BounceStyles {
    public static final String modId = "bounce_styles";
    public static final ArmorMaterial STYLE_MATERIAL = new StyleArmorMaterial();

    public BounceStyles() {
        GeckoLib.initialize();

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        MOD_BUS.register(this);

        MOD_BUS.addListener(this::enqueueInterModComms);
        MOD_BUS.addListener(BounceStylesClient::onRegisterRenderers);
        MinecraftForge.EVENT_BUS.addListener(this::onCurioEquip);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        try {
            ItemLoader.init(event);
        }
        catch (IOException | NoSuchMethodException | InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void enqueueInterModComms(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(StyleItem.HeadStyleItem.curioSlot).build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(StyleItem.BodyStyleItem.curioSlot).build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(StyleItem.LegsStyleItem.curioSlot).build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(StyleItem.FeetStyleItem.curioSlot).build());
    }

    public void onCurioEquip(final CurioEquipEvent event) {
        Item item = event.getStack().getItem();
        if(item instanceof StyleItem) {
            event.setResult(((StyleItem) item).canEquip(event.getSlotContext().getIdentifier(), event.getEntityLiving(), event.getStack()) ? Event.Result.ALLOW : Event.Result.DEFAULT);
        }
    }

}
