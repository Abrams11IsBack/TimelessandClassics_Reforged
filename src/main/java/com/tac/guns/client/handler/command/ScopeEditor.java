package com.tac.guns.client.handler.command;

import static com.tac.guns.GunMod.LOGGER;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.tac.guns.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

import com.google.gson.GsonBuilder;
import com.tac.guns.Config;
import com.tac.guns.client.InputHandler;
import com.tac.guns.client.handler.command.data.ScopeData;
import com.tac.guns.common.Gun;
import com.tac.guns.common.tooling.CommandsHandler;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.item.attachment.impl.Scope;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ScopeEditor
{
    private static ScopeEditor instance;

    public static ScopeEditor get()
    {
        if(instance == null)
        {
            instance = new ScopeEditor();
        }
        return instance;
    }
    private ScopeEditor() {}

    public HashMap<String, ScopeData> map = new HashMap<>();
    private ScopeData scopeData;
    public ScopeData getScopeData() {return scopeData;}

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event)
    {
        if(!Config.COMMON.development.enableTDev.get())
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        CommandsHandler ch = CommandsHandler.get();
       if(ch == null || ch.getCatCurrentIndex() != 2)
            return;
        if ((mc.player.getMainHandItem() == null || mc.player.getMainHandItem() == ItemStack.EMPTY || !(mc.player.getMainHandItem().getItem() instanceof TimelessGunItem)))
            return;
        if(((TimelessGunItem)mc.player.getMainHandItem().getItem()).isIntegratedOptic()) {
            if(!this.map.containsKey(mc.player.getMainHandItem().getItem().getDescriptionId()))
                this.map.put(mc.player.getMainHandItem().getItem().getDescriptionId(), new ScopeData(mc.player.getMainHandItem().getItem().getDescriptionId()));
            this.handleScopeMod(event, this.map.get(mc.player.getMainHandItem().getItem().getDescriptionId()));
            this.scopeData = this.map.get(mc.player.getMainHandItem().getItem().getDescriptionId());
        }
        else {
            Scope scopeItem = Gun.getScope(mc.player.getMainHandItem());
            if (scopeItem == null)
                return;
            if(!this.map.containsKey(scopeItem.getTagName()))
                this.map.put(scopeItem.getTagName(), new ScopeData(scopeItem.getTagName()));
            this.handleScopeMod(event, this.map.get(scopeItem.getTagName()));
            this.scopeData = this.map.get(scopeItem.getTagName());
        }

    }
    
    private void handleScopeMod(InputEvent.KeyInputEvent event, ScopeData data)
    {
        double stepModifier = 1;
        boolean isLeft = InputHandler.LEFT.down;
        boolean isRight = InputHandler.RIGHT.down;
        boolean isUp = InputHandler.UP.down;
        boolean isDown = InputHandler.DOWN.down;
        boolean isControlDown = InputHandler.CONTROLLY.down || InputHandler.CONTROLLYR.down; // Increase Module Size
        boolean isShiftDown = InputHandler.SHIFTY.down || InputHandler.SHIFTYR.down; // Increase Step Size
        boolean isAltDown = InputHandler.ALTY.down || InputHandler.ALTYR.down; // Swap X -> Z modify

        if(isShiftDown)
            stepModifier*=10;
        if(isControlDown)
            stepModifier/=10;

        boolean isPeriodDown = InputHandler.SIZE_OPT.down;

        PlayerEntity player = Minecraft.getInstance().player;
        if(InputHandler.P.down) // P will be for adjusting double render
        {
            if(isShiftDown)
                stepModifier*=10;
            if(isControlDown)
                stepModifier/=10;

            player.displayClientMessage(new TranslationTextComponent("DR X: "+data.getDrXZoomMod()+" | DR Y: "+data.getDrYZoomMod()+" | DR Z: "+data.getDrZZoomMod()), true);

            if (isAltDown && isUp) {
                data.setDrZZoomMod( data.getDrZZoomMod() + 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR Z: "+data.getDrZZoomMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isAltDown && isDown) {
                data.setDrZZoomMod( data.getDrZZoomMod() - 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR Z: "+data.getDrZZoomMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isPeriodDown && isUp) {
                stepModifier*=10;
                data.setDrZoomSizeMod((float) (data.getDrZoomSizeMod() + 0.0075f * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("DR Size: "+data.getDrZoomSizeMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isPeriodDown && isDown) {
                stepModifier*=10;
                data.setDrZoomSizeMod((float) (data.getDrZoomSizeMod() - 0.0075f * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("DR Size: "+data.getDrZoomSizeMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isUp) {
                data.setDrYZoomMod( data.getDrYZoomMod() + 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR Y: "+data.getDrYZoomMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isDown) {
                data.setDrYZoomMod( data.getDrYZoomMod() - 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR Y: "+data.getDrYZoomMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isLeft) {
                data.setDrXZoomMod( data.getDrXZoomMod() + 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR X: "+data.getDrXZoomMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isRight) {
                data.setDrXZoomMod( data.getDrXZoomMod() - 0.025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("DR X: "+data.getDrXZoomMod()).withStyle(TextFormatting.DARK_RED), true);
            }
        }
        else if(InputHandler.L.down) // L will be for adjusting reticle pos
        {
            if(isShiftDown)
                stepModifier*=5;
            if(isControlDown)
                stepModifier/=20;

            player.displayClientMessage(new TranslationTextComponent("Reticle X: "+data.getDrXZoomMod()+" | Reticle Y: "+data.getDrYZoomMod()+" | Reticle Z: "+data.getDrZZoomMod()+" | Reticle Size: "+data.getReticleSizeMod()), true);

            if (isAltDown && isUp) {
                data.setReticleZMod( data.getReticleZMod() + 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle Z: "+data.getReticleZMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isAltDown && isDown) {
                data.setReticleZMod( data.getReticleZMod() - 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle Z: "+data.getReticleZMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isPeriodDown && isUp) {
                data.setReticleSizeMod((float) (data.getReticleSizeMod() + 0.0075f * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("Reticle Size: "+data.getReticleSizeMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isPeriodDown && isDown) {
                data.setReticleSizeMod((float) (data.getReticleSizeMod() - 0.0075f * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("Reticle Size: "+data.getReticleSizeMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isUp) {
                data.setReticleYMod( data.getReticleYMod() + 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle Y: "+data.getReticleYMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isDown) {
                data.setReticleYMod( data.getReticleYMod() - 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle Y: "+data.getReticleYMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isLeft) {
                data.setReticleXMod( data.getReticleXMod() - 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle X: "+data.getReticleXMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isRight) {
                data.setReticleXMod( data.getReticleXMod() + 0.00025 * stepModifier );
                player.displayClientMessage(new TranslationTextComponent("Reticle X: "+data.getReticleXMod()).withStyle(TextFormatting.DARK_RED), true);
            }
        }
        else if(InputHandler.M.down) // L will be for adjusting reticle pos
        {
            if(isShiftDown)
                stepModifier*=10;
            if(isControlDown)
                stepModifier/=10;

            player.displayClientMessage(new TranslationTextComponent("Crop: "+data.getDrZoomCropMod()+" | FOV zoom: "+data.getAdditionalZoomMod()), true);


            if (isUp) {
                data.setDrZoomCropMod((float)(data.getDrZoomCropMod() + 0.025 * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("Crop: "+data.getDrZoomCropMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isDown) {
                data.setDrZoomCropMod( (float)(data.getDrZoomCropMod() - 0.025 * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("Crop: "+data.getDrZoomCropMod()).withStyle(TextFormatting.DARK_RED), true);
            }
            else if (isLeft) {
                data.setAdditionalZoomMod((float)(data.getAdditionalZoomMod() - 0.025 * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("FOV zoom: "+data.getAdditionalZoomMod()).withStyle(TextFormatting.GREEN), true);
            }
            else if (isRight) {
                data.setAdditionalZoomMod((float)(data.getAdditionalZoomMod() + 0.025 * stepModifier));
                player.displayClientMessage(new TranslationTextComponent("FOV zoom: "+data.getAdditionalZoomMod()).withStyle(TextFormatting.DARK_RED), true);
            }
        }
        //this.map.put(scope.getTagName(), data);
    }
    public void resetData() {}
    public void exportData() {
        this.map.forEach((name, scope) ->
        {
            if(this.map.get(name) == null) {LOGGER.log(Level.ERROR, "SCOPE EDITOR FAILED TO EXPORT THIS BROKEN DATA. CONTACT CLUMSYALIEN."); return;}
            GsonBuilder gsonB = new GsonBuilder().setLenient().addSerializationExclusionStrategy(Gun.strategy).setPrettyPrinting();
            String jsonString = gsonB.create().toJson(scope);
            this.writeExport(jsonString, name);
        });
    }
    private void writeExport(String jsonString, String name)
    {
        try
        {
            File dir = new File(Config.COMMON.development.TDevPath.get()+"\\tac_export\\scope_export");
            dir.mkdir();
            FileWriter dataWriter = new FileWriter (dir.getAbsolutePath() +"\\"+ name + "_export.json");
            dataWriter.write(jsonString);
            dataWriter.close();
            LOGGER.log(Level.INFO, "SCOPE EDITOR EXPORTED FILE ( "+name + "export.txt ). BE PROUD!");
        }
        catch (IOException e)
        {
            LOGGER.log(Level.ERROR, "SCOPE EDITOR FAILED TO EXPORT, NO FILE CREATED!!! NO ACCESS IN PATH?. CONTACT CLUMSYALIEN.");
        }
    }
}
