package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.render.animation.MK18MOD1AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class mk18_mod1_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay)
    {
        MK18MOD1AnimationController controller = MK18MOD1AnimationController.getInstance();
        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.MK18_MOD1_BODY.getModel(), MK18MOD1AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.MK18_MOD1_BASIC_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                RenderUtil.renderLaserModuleModel(SpecialModels.MK18_MOD1_BASIC_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
            }
            else if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() != ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem()) || Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack).getItem() == ModItems.IR_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.MK18_MOD1_IR_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, light, overlay);
                matrices.pushPose();
                if(transformType.firstPerson()) {
                    // TODO: Build some sort of scaler for this
                    matrices.translate(0, 0, -0.55);
                    matrices.scale(1, 1, 9);
                    matrices.translate(0, 0, 0.55);
                    RenderUtil.renderLaserModuleModel(SpecialModels.MK18_MOD1_IR_LASER.getModel(), Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
                }
                matrices.popPose();
            }
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_SIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_SIGHT_FOLDED.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.M4_LIGHT_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_B5_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.M4_H_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_LIGHT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);

            } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_TAC_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            else{
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_DEFAULT_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(SpecialModels.MK18_MOD1_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);

            matrices.pushPose();
            {
                if(transformType.firstPerson()) {
                    ItemCooldowns tracker = Minecraft.getInstance().player.getCooldowns();
                    float cooldownOg = tracker.getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());

                    if (Gun.hasAmmo(stack)) {
                        // Math provided by Bomb787 on GitHub and Curseforge!!!
                        matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    } else if (!Gun.hasAmmo(stack)) {
                        {
                            matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                        }
                    }
                }
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.MK18_MOD1_BODY.getModel(), MK18MOD1AnimationController.INDEX_MAGAZINE, transformType, matrices);

            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.MK18_MOD1_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();
        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
