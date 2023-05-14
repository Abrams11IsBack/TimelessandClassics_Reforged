package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.ShootingHandler;
import com.tac.guns.client.render.animation.SPR15AnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class spr_15_animation implements IOverrideModel {

    @Override
    public void render(float v, ItemTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay) {
        SPR15AnimationController controller = SPR15AnimationController.getInstance();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.SPR_15_BODY.getModel(), SPR15AnimationController.INDEX_BODY, transformType, matrices);
            if (Gun.getScope(stack) == null) {
                RenderUtil.renderModel(SpecialModels.SPR_15_SIGHT.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SPR_15_SIGHT_FOLDED.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack).getItem() == ModItems.BASIC_LASER.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderLaserModuleModel(SpecialModels.SPR_15_B_LASER_DEVICE.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, light, overlay);
                RenderUtil.renderLaserModuleModel(SpecialModels.SPR_15_B_LASER.getModel(), Gun.getAttachment(IAttachment.Type.SIDE_RAIL, stack), matrices, renderBuffer, 15728880, overlay); // 15728880 For fixed max light
            }

            if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.LIGHT_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_LIGHT_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.TACTICAL_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_TACTICAL_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.STOCK, stack).getItem() == ModItems.WEIGHTED_STOCK.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_HEAVY_STOCK.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.SILENCER.orElse(ItemStack.EMPTY.getItem())) {
                matrices.pushPose();
                matrices.translate(0, 0, -0.335);
                RenderUtil.renderModel(SpecialModels.SPR_15_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.popPose();
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_COMPENSATOR.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_COMPENSATOR.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.BARREL, stack).getItem() == ModItems.MUZZLE_BRAKE.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_BRAKE.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SPR_15_DEFAULT_BARREL.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.SPECIALISED_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_TAC_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else if (Gun.getAttachment(IAttachment.Type.UNDER_BARREL, stack).getItem() == ModItems.LIGHT_GRIP.orElse(ItemStack.EMPTY.getItem())) {
                RenderUtil.renderModel(SpecialModels.SPR_15_LIGHT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SPR_15_DEFAULT_GRIP.getModel(), stack, matrices, renderBuffer, light, overlay);
            }

            RenderUtil.renderModel(SpecialModels.SPR_15_BODY.getModel(), stack, matrices, renderBuffer, light, overlay);

            matrices.pushPose();
            {
                Gun gun = ((GunItem) stack.getItem()).getGun();
                float cooldownOg = ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate()) < 0 ? 1 : ShootingHandler.get().getshootMsGap() / ShootingHandler.calcShootTickGap(gun.getGeneral().getRate());

                if(transformType.firstPerson()) {
                    if (Gun.hasAmmo(stack)) {
                        // Math provided by Bomb787 on GitHub and Curseforge!!!
                        matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                    } else if (!Gun.hasAmmo(stack)) {
                        {
                            matrices.translate(0, 0, 0.185f * (-4.5 * Math.pow(0.5 - 0.5, 2) + 1.0));
                        }
                    }
                }
                RenderUtil.renderModel(SpecialModels.SPR_15_BOLT.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
            matrices.popPose();
        }
        matrices.popPose();

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.SPR_15_BODY.getModel(), SPR15AnimationController.INDEX_MAGAZINE, transformType, matrices);

            if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                RenderUtil.renderModel(SpecialModels.SPR_15_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(SpecialModels.SPR_15_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.popPose();

        //if(controller.isAnimationRunning(GunAnimationController.AnimationLabel.RELOAD_NORMAL)) {
            matrices.pushPose();
            {
                controller.applySpecialModelTransform(SpecialModels.SPR_15_BODY.getModel(), SPR15AnimationController.INDEX_EXTRA_MAGAZINE, transformType, matrices);

                if (GunModifierHelper.getAmmoCapacity(stack) > -1) {
                    RenderUtil.renderModel(SpecialModels.SPR_15_EXTENDED_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                } else {
                    RenderUtil.renderModel(SpecialModels.SPR_15_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
                }
            }
            matrices.popPose();
        //}

        matrices.pushPose();
        {
            controller.applySpecialModelTransform(SpecialModels.SPR_15_BODY.getModel(), SPR15AnimationController.INDEX_HANDLE, transformType, matrices);
            RenderUtil.renderModel(SpecialModels.SPR_15_PULL_HANDLE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.popPose();

        PlayerHandAnimation.render(controller, transformType, matrices, renderBuffer, light);
    }
}
