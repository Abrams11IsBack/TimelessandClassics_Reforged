package com.tac.guns.client.render.gun.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tac.guns.Config;
import com.tac.guns.client.SpecialModel;
import com.tac.guns.client.SpecialModels;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.handler.ReloadHandler;
import com.tac.guns.client.render.animation.Glock17AnimationController;
import com.tac.guns.client.render.animation.M1911AnimationController;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.client.render.animation.module.PlayerHandAnimation;
import com.tac.guns.client.render.gun.IOverrideModel;
import com.tac.guns.client.render.gun.ModelOverrides;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.init.ModItems;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.math.vector.Vector3f;

/*
 * Because the revolver has a rotating chamber, we need to render it in a
 * different way than normal items. In this case we are overriding the model.
 */

/**
 * Author: Timeless Development, and associates.
 */
public class m1911_animation implements IOverrideModel {

    private final SpecialModel M1911 = new SpecialModel("m1911");
    private final SpecialModel M1911_SLIDE = new SpecialModel("m1911_slide");
    private final SpecialModel M1911_STANDARD_MAG = new SpecialModel("m1911_standard_mag");
    private final SpecialModel M1911_LONG_MAG = new SpecialModel("m1911_long_mag");

    //The render method, similar to what is in DartEntity. We can render the item
    @Override
    public void render(float v, ItemCameraTransforms.TransformType transformType, ItemStack stack, ItemStack parent, LivingEntity entity, MatrixStack matrices, IRenderTypeBuffer renderBuffer, int light, int overlay)
    {
        M1911AnimationController controller = M1911AnimationController.getInstance();
        matrices.push();
        {
            controller.applySpecialModelTransform(M1911.getModel(),M1911AnimationController.INDEX_BODY,transformType,matrices);
            /*if (Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack).getItem() == ModItems.PISTOL_SILENCER.get()) {
                matrices.push();
                matrices.translate(0, 0, -0.0475);
                RenderUtil.renderModel(M1911_SUPPRESSOR.getModel(), stack, matrices, renderBuffer, light, overlay);
                matrices.translate(0, 0, 0.0475);
                matrices.pop();
            }*/
            RenderUtil.renderModel(M1911.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();
        matrices.push();
        {
            controller.applySpecialModelTransform(M1911.getModel(),M1911AnimationController.INDEX_MAG,transformType,matrices);
            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.OVER_CAPACITY.get(), stack) > 0) {
                RenderUtil.renderModel(M1911_LONG_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            } else {
                RenderUtil.renderModel(M1911_STANDARD_MAG.getModel(), stack, matrices, renderBuffer, light, overlay);
            }
        }
        matrices.pop();

        CooldownTracker tracker = Minecraft.getInstance().player.getCooldownTracker(); // getCooldownTracker();
        float cooldownOg = tracker.getCooldown(stack.getItem(), Minecraft.getInstance().getRenderPartialTicks()); // getRenderPartialTicks()); // getCooldown(stack.getItem(), Minecraft.getInstance().getFrameTime());
        matrices.push();
        {
            controller.applySpecialModelTransform(M1911.getModel(),M1911AnimationController.INDEX_SLIDE,transformType,matrices);
            AnimationMeta reloadEmpty = controller.getAnimationFromLabel(GunAnimationController.AnimationLabel.RELOAD_EMPTY);
            boolean shouldOffset = reloadEmpty != null && reloadEmpty.equals(controller.getPreviousAnimation()) && controller.isAnimationRunning();

            if(Gun.hasAmmo(stack) || shouldOffset)
            {
                matrices.translate(0, 0, 0.1925f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0));
                GunRenderingHandler.get().opticMovement = 0.1925f * (-4.5 * Math.pow(cooldownOg - 0.5, 2) + 1.0);
            }
            else if(!Gun.hasAmmo(stack))
            {
                matrices.translate(0, 0, 0.1925f * (-4.5 * Math.pow(0.5-0.5, 2) + 1.0));
                GunRenderingHandler.get().opticMovement = 0.1925f * (-4.5 * Math.pow(0.5-0.5, 2) + 1.0);
            }

            matrices.translate(0.00, 0.0, -0.008);
            RenderUtil.renderModel(M1911_SLIDE.getModel(), stack, matrices, renderBuffer, light, overlay);
        }
        matrices.pop();

        PlayerHandAnimation.render(controller,transformType,matrices,renderBuffer,light);
    }
}
