package com.tac.guns.client.render.armor.VestLayer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public abstract class ArmorBase extends Model
{
    public ArmorBase()
    {
        super(RenderType::entityCutoutNoCull);
    }

    public ArmorBase(Function<ResourceLocation, RenderType> renderType)
    {
        super(renderType);
    }

    protected static void setRotationAngle(ModelRenderer renderer, float x, float y, float z)
    {
        renderer.xRot = x;
        renderer.yRot = y;
        renderer.zRot = z;
    }

    public void rotateToPlayerBody(ModelRenderer body)
    {
        ModelRenderer root = this.getModel();
        root.copyFrom(body);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        this.getModel().render(matrixStack, builder, p_225598_3_, OverlayTexture.NO_OVERLAY, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
    protected abstract ModelRenderer getModel();{}

    protected abstract ResourceLocation getTexture();{}
    protected abstract void setTexture(String modId, String path);{}
}