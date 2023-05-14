package com.tac.guns.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mrcrayfish.framework.common.data.SyncedEntityData;
import com.tac.guns.client.handler.GunRenderingHandler;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.container.AttachmentContainer;
import com.tac.guns.init.ModSyncedDataKeys;
import com.tac.guns.item.GunItem;
import com.tac.guns.item.IrDeviceItem;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.SideRailItem;
import com.tac.guns.item.TransitionalTypes.TimelessOldRifleGunItem;
import com.tac.guns.item.TransitionalTypes.TimelessPistolGunItem;
import com.tac.guns.item.attachment.IAttachment;
import com.tac.guns.item.attachment.impl.IrDevice;
import com.tac.guns.util.GunModifierHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentScreen extends AbstractContainerScreen<AttachmentContainer>
{
    private static final ResourceLocation GUN_GUI_TEXTURES = new ResourceLocation("tac:textures/gui/attachments.png");
    private static final ResourceLocation SCOPE_GUI_TEXTURES = new ResourceLocation("tac:textures/gui/scope_attachments.png");

    private final Inventory playerInventory;
    private final Container weaponInventory;

    private boolean showHelp = true;
    private int windowZoom = 14;
    private int windowX, windowY;
    private float windowRotationX, windowRotationY;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX, mouseClickedY;

    public AttachmentScreen(AttachmentContainer screenContainer, Inventory playerInventory, Component titleIn)
    {
        super(screenContainer, playerInventory, titleIn);
        this.playerInventory = playerInventory;
        this.weaponInventory = screenContainer.getWeaponInventory();
        this.imageHeight = 184;
    }

    @Override
    public void containerTick()
    {
        super.containerTick();
        if(this.minecraft != null && this.minecraft.player != null)
        {
            if(SyncedEntityData.instance().get(Minecraft.getInstance().player, ModSyncedDataKeys.RELOADING))
                Minecraft.getInstance().setScreen(null);
            if(!(this.minecraft.player.getMainHandItem().getItem() instanceof GunItem) && !(this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem))
            {
                Minecraft.getInstance().setScreen(null);
            }
        }
    }
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY); //Render tool tips
    }
    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        this.font.draw(matrixStack, this.title, (float)this.titleLabelX+30, (float)this.titleLabelY, 4210752);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        if((this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem) || (this.minecraft.player.getMainHandItem().getItem() instanceof SideRailItem) || (this.minecraft.player.getMainHandItem().getItem() instanceof IrDeviceItem))
            RenderUtil.scissor(left + 97, top + 17, 67, 67);
        else
            RenderUtil.scissor(left + 26, top + 17, 123, 70);

        PoseStack stack = RenderSystem.getModelViewStack();
        stack.pushPose();
        {
            stack.translate(96, 50, 100);
            stack.translate(this.windowX + (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseX - this.mouseClickedX : 0), 0, 0);
            stack.translate(0, this.windowY + (this.mouseGrabbed && this.mouseGrabbedButton == 0 ? mouseY - this.mouseClickedY : 0), 0);
            stack.mulPose(Vector3f.XP.rotationDegrees(-30F));
            stack.mulPose(Vector3f.XP.rotationDegrees(this.windowRotationY - (this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseY - this.mouseClickedY : 0)));
            stack.mulPose(Vector3f.YP.rotationDegrees(this.windowRotationX + (this.mouseGrabbed && this.mouseGrabbedButton == 1 ? mouseX - this.mouseClickedX : 0)));
            stack.mulPose(Vector3f.YP.rotationDegrees(150F));
            stack.scale(this.windowZoom / 10F, this.windowZoom / 10F, this.windowZoom / 10F);
            stack.scale(90F, -90F, 90F);
            stack.mulPose(Vector3f.XP.rotationDegrees(5F));
            stack.mulPose(Vector3f.YP.rotationDegrees(90F));

            RenderSystem.applyModelViewMatrix();

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            MultiBufferSource.BufferSource buffer = this.minecraft.renderBuffers().bufferSource();
            if(!(this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem))
            {
                matrixStack.translate(0.0,0.0,-0.4);
                GunRenderingHandler.get().renderWeapon(this.minecraft.player, this.minecraft.player.getMainHandItem(), ItemTransforms.TransformType.GROUND, matrixStack, buffer, 15728880, 0F);
            }
            else
            {
                matrixStack.pushPose();
                matrixStack.scale(1.25f,1.25f,1.25f);
                GunRenderingHandler.get().renderScope(this.minecraft.player, this.minecraft.player.getMainHandItem(), ItemTransforms.TransformType.FIXED, matrixStack, buffer, 15728880, 0F); // GROUND, matrixStack, buffer, 15728880, 0F);
                matrixStack.popPose();
            }
            buffer.endBatch();
        }
        stack.popPose();
        RenderSystem.applyModelViewMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if(this.showHelp)
        {
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            minecraft.font.draw(matrixStack, I18n.get("container.tac.attachments.window_help"), 56, 38, 0xFFFFFF);
            matrixStack.popPose();
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        if(!(this.minecraft.player.getMainHandItem().getItem() instanceof SideRailItem) && !(this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem))
            RenderSystem.setShaderTexture(0, GUN_GUI_TEXTURES);
        else
            RenderSystem.setShaderTexture(0, SCOPE_GUI_TEXTURES);

        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, left, top, 0, 0, this.imageWidth, this.imageHeight);

        if((this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem) || (this.minecraft.player.getMainHandItem().getItem() instanceof SideRailItem))
            for(int i = 9; i < IAttachment.Type.values().length; i++) {
                if (i == 10 && !this.menu.getSlot(i).isActive()) {
                    this.blit(matrixStack, left + 70, top + 50 + (i - 9) * 18, 176, 16, 16, 16);
                } else if (i == 10 && this.weaponInventory.getItem(i).isEmpty()) {
                    this.blit(matrixStack, left + 70, top + 50 + (i - 9) * 18, 176, 16, 16, 16);
                }
                if (i == 12 && !this.menu.getSlot(i).isActive()) {
                    this.blit(matrixStack, left + 40, top + 50 + (i - 11) * 18, 176, 32, 16, 16);
                } else if (i == 12 && this.weaponInventory.getItem(i).isEmpty()) {
                    this.blit(matrixStack, left + 10, top + 50 + (i - 11) * 18, 176, 32, 16, 16);
                }
                if (i == 11 && !this.menu.getSlot(i).isActive()) {
                    this.blit(matrixStack, left + 10, top + 53 + (i - 13) * 18, 176, 0, 16, 16);
                } else if (i == 11 && this.weaponInventory.getItem(i).isEmpty()) {
                    this.blit(matrixStack, left + 40, top + 53 + (i - 13) * 18, 176, 0, 16, 16);
                }
            }
        else
            for(int i = 0; i < IAttachment.Type.values().length-7; i++)
            {
                if(!this.menu.getSlot(i).isActive())
                {
                    if (i > 3)
                        this.blit(matrixStack, left + 155, top + 17 + (i-4) * 18, 176, 0, 16, 16);
                    else
                        this.blit(matrixStack, left + 5, top + 17 + i * 18, 176, 0, 16, 16);
                }
                else if (i > 3)
                {
                    this.blit(matrixStack, left + 155, top + 17 + (i-4) * 18, 176, 16 + i * 16, 16, 16);
                }
                else if(this.weaponInventory.getItem(i).isEmpty())
                {
                    this.blit(matrixStack, left + 5, top + 17 + i * 18, 176, 16 + i * 16, 16, 16);
                }
            }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if(RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 142, 70))
        {
            if(scroll < 0 && this.windowZoom > 0)
            {
                this.showHelp = false;
                this.windowZoom--;
            }
            else if(scroll > 0)
            {
                this.showHelp = false;
                this.windowZoom++;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        int startX = (this.width - this.imageWidth) / 2;
        int startY = (this.height - this.imageHeight) / 2;
        if((this.minecraft.player.getMainHandItem().getItem() instanceof ScopeItem) || (this.minecraft.player.getMainHandItem().getItem() instanceof SideRailItem)) {
            if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 93, startY + 18, 65, 67)) {
                if (!this.mouseGrabbed && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                    this.mouseGrabbed = true;
                    this.mouseGrabbedButton = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? 1 : 0;
                    this.mouseClickedX = (int) mouseX;
                    this.mouseClickedY = (int) mouseY;
                    this.showHelp = false;
                    return true;
                }
            }
        }
        else{
            if(RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 126, 70))
            {
                if(!this.mouseGrabbed && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                    this.mouseGrabbed = true;
                    this.mouseGrabbedButton = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? 1 : 0;
                    this.mouseClickedX = (int) mouseX;
                    this.mouseClickedY = (int) mouseY;
                    this.showHelp = false;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.mouseGrabbed)
        {
            if(this.mouseGrabbedButton == 0 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.mouseGrabbed = false;
                this.windowX += (mouseX - this.mouseClickedX - 1);
                this.windowY += (mouseY - this.mouseClickedY);
            }
            else if(mouseGrabbedButton == 1 && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                this.mouseGrabbed = false;
                this.windowRotationX += (mouseX - this.mouseClickedX);
                this.windowRotationY -= (mouseY - this.mouseClickedY);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
