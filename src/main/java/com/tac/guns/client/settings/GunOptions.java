package com.tac.guns.client.settings;

import com.tac.guns.Config;
import com.tac.guns.client.handler.CrosshairHandler;
import com.tac.guns.client.render.crosshair.Crosshair;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunOptions
{

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0#");

    public static final ProgressOption ADS_SENSITIVITY = new GunSliderPercentageOption("tac.options.adsSensitivity", 0.0, 1.0, 0.01F, gameSettings -> {
        return Config.CLIENT.controls.aimDownSightSensitivity.get();
    }, (gameSettings, value) -> {
        Config.CLIENT.controls.aimDownSightSensitivity.set(Mth.clamp(value, 0.0, 2.0));
        Config.saveClientConfig();
    }, (gameSettings, option) -> {
        double adsSensitivity = Config.CLIENT.controls.aimDownSightSensitivity.get();
        return new TranslatableComponent("tac.options.adsSensitivity.format", FORMAT.format(adsSensitivity));
    });

   /* public static final BooleanOption BURST_MECH = new BooleanOption("tac.options.burstPress", (settings) -> {
        return Config.CLIENT.controls.burstPress.get();
    }, (settings, value) -> {
        Config.CLIENT.controls.burstPress.set(value);
        Config.saveClientConfig();
    });
    // this.minecraft.displayGuiScreen
*/


    public static final Option CROSSHAIR = new GunListOption<>("tac.options.crosshair", () -> {
        return CrosshairHandler.get().getRegisteredCrosshairs();
    }, () -> {
        return ResourceLocation.tryParse(Config.CLIENT.display.crosshair.get());
    }, (ResourceLocation value) -> {
        Config.CLIENT.display.crosshair.set(value.toString());
        Config.saveClientConfig();
        CrosshairHandler.get().setCrosshair(value);
    }, (Crosshair value) -> {
        ResourceLocation id = value.getLocation();//  getLocation();
        return new TranslatableComponent(id.getNamespace() + ".crosshair." + id.getPath());
    }).setRenderer((button, matrixStack, partialTicks) -> {
        matrixStack.pushPose();
        matrixStack.translate(button.x, button.y, 0);
        matrixStack.translate(button.getWidth() + 2, 2, 0);
        Crosshair crosshair = CrosshairHandler.get().getCurrentCrosshair();
        if(crosshair != null)
        {
            if(crosshair.isDefault())
            {
                Minecraft mc = Minecraft.getInstance();
                mc.getTextureManager().bindForSetup(GuiComponent.GUI_ICONS_LOCATION);
                GuiComponent.blit(matrixStack, (16 - 15) / 2, (16 - 15) / 2, 0, 0, 0, 15, 15, 256, 256);
            }
            else
            {
                crosshair.render(Minecraft.getInstance(), matrixStack, 16, 16, partialTicks);
            }
        }
        matrixStack.popPose();
    });
/*public static final CycleOption GUI_SCALE = CycleOption.create("options.guiScale", () -> {
      return IntStream.rangeClosed(0, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode())).boxed().collect(Collectors.toList());
   }, (p_193700_) -> {
      return (Component)(p_193700_ == 0 ? new TranslatableComponent("options.guiScale.auto") : new TextComponent(Integer.toString(p_193700_)));
   }, (p_193609_) -> {
      return p_193609_.guiScale;
   }, (p_193682_, p_193683_, p_193684_) -> {
      p_193682_.guiScale = p_193684_;
   });*/
    public static final CycleOption<Boolean> DOUBLE_RENDER_EXIST = CycleOption.createOnOff("tac.options.doubleRender", (settings) -> {
        return Config.CLIENT.display.scopeDoubleRender.get();
    }, (settings, option, value) -> {
        Config.CLIENT.display.scopeDoubleRender.set(value);
        Config.saveClientConfig();
    });
    public static final CycleOption<Boolean> SHOW_FPS_TRAILS_EXIST = CycleOption.createOnOff("tac.options.showFirstPersonBulletTrails", (settings) -> {
        return Config.CLIENT.display.showFirstPersonBulletTrails.get();
    }, (settings, option, value) -> {
        Config.CLIENT.display.showFirstPersonBulletTrails.set(value);
        Config.saveClientConfig();
    });






    /*
    public static final BooleanOption REDDOT_SQUISH_EXIST = new BooleanOption("tac.options.reddotSquish", (settings) -> {
        return Config.CLIENT.display.redDotSquishUpdate.get();
    }, (settings, value) -> {
        Config.CLIENT.display.redDotSquishUpdate.set(value);
        Config.saveClientConfig();
    });

    public static final BooleanOption FIREMODE_EXIST = new BooleanOption("tac.options.firemodeExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.set(value);
        Config.saveClientConfig();
    });
    //Firemode positioning

    public static final ProgressOption X_FIREMODE_POS = new GunSliderPercentageOption("tac.options.xFiremodePos", -500, 500, 0.001F,
    gameSettings ->
    {
        return Config.CLIENT.weaponGUI.weaponFireMode.x.get();
        //return Config.CLIENT.controls.aimDownSightSensitivity.get();
    },
    (gameSettings, value) ->
    {
        Config.CLIENT.weaponGUI.weaponFireMode.x.set(Mth.clamp(value, -500, 500));
        Config.saveClientConfig();
    },
    (gameSettings, option) -> {
        double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.x.get();
        return new TranslatableComponent("tac.options.xFiremodePos.format", FORMAT.format(adsSensitivity));
    });
    public static final ProgressOption Y_FIREMODE_POS = new GunSliderPercentageOption("tac.options.yFiremodePos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponFireMode.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponFireMode.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.y.get();
                return new TranslatableComponent("tac.options.yFiremodePos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_FIREMODE_POS = new GunSliderPercentageOption("tac.options.sizeFiremodePos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponFireMode.weaponFireModeSize.get();
                return new TranslatableComponent("tac.options.sizeFiremodePos.format", FORMAT.format(adsSensitivity));
            });

    //AmmoCounter positioning
    public static final BooleanOption AMMOCOUNTER_EXIST = new BooleanOption("tac.options.ammoCounterExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponAmmoCounter.showWeaponAmmoCounter.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.xAmmoCounterPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.x.get();
                return new TranslatableComponent("tac.options.xAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.yAmmoCounterPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                return new TranslatableComponent("tac.options.yAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_AMMOCOUNTER_POS = new GunSliderPercentageOption("tac.options.sizeAmmoCounterPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.weaponAmmoCounterSize.get();
                return new TranslatableComponent("tac.options.sizeAmmoCounterPos.format", FORMAT.format(adsSensitivity));
            });

    //WeaponIcon positioning
    public static final BooleanOption WeaponIcon_EXIST = new BooleanOption("tac.options.iconExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponTypeIcon.showWeaponIcon.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_Icon_POS = new GunSliderPercentageOption("tac.options.xIconPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponTypeIcon.x.get();
                return new TranslatableComponent("tac.options.xIconPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_Icon_POS = new GunSliderPercentageOption("tac.options.yIconPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponAmmoCounter.y.get();
                return new TranslatableComponent("tac.options.yIconPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_Icon_POS = new GunSliderPercentageOption("tac.options.sizeIconPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponTypeIcon.weaponIconSize.get();
                return new TranslatableComponent("tac.options.sizeIconPos.format", FORMAT.format(adsSensitivity));
            });

    //WeaponIcon positioning
    public static final BooleanOption ReloadBar_EXIST = new BooleanOption("tac.options.reloadBarExist", (settings) -> {
        return Config.CLIENT.weaponGUI.weaponReloadTimer.showWeaponReloadTimer.get();
    }, (settings, value) -> {
        Config.CLIENT.weaponGUI.weaponReloadTimer.showWeaponReloadTimer.set(value);
        Config.saveClientConfig();
    });
    public static final ProgressOption X_ReloadBar_POS = new GunSliderPercentageOption("tac.options.xReloadBarPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.x.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.x.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.x.get();
                return new TranslatableComponent("tac.options.xReloadBarPos.format", FORMAT.format(adsSensitivity));
            });

    public static final ProgressOption Fire_Volume = new GunSliderPercentageOption("tac.options.weaponsVolume", 0f, 1f, 0.01F,
            gameSettings ->
            {
                return Config.CLIENT.sounds.weaponsVolume.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.sounds.weaponsVolume.set(Mth.clamp(value, 0f, 1f));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.sounds.weaponsVolume.get();
                return new TranslatableComponent("tac.options.weaponsVolume.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption Y_ReloadBar_POS = new GunSliderPercentageOption("tac.options.yReloadBarPos", -500, 500, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.y.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.y.set(Mth.clamp(value, -500, 500));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.y.get();
                return new TranslatableComponent("tac.options.yReloadBarPos.format", FORMAT.format(adsSensitivity));
            });
    public static final ProgressOption SIZE_ReloadBar_POS = new GunSliderPercentageOption("tac.options.sizeReloadBarPos", 0.1, 4, 0.001F,
            gameSettings ->
            {
                return Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.get();
                //return Config.CLIENT.controls.aimDownSightSensitivity.get();
            },
            (gameSettings, value) ->
            {
                Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.set(Mth.clamp(value, 0.1, 4));
                Config.saveClientConfig();
            },
            (gameSettings, option) -> {
                double adsSensitivity = Config.CLIENT.weaponGUI.weaponReloadTimer.weaponReloadTimerSize.get();
                return new TranslatableComponent("tac.options.sizeReloadBarPos.format", FORMAT.format(adsSensitivity));
            });
    */
}