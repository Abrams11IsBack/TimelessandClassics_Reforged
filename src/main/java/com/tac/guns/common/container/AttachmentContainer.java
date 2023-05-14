package com.tac.guns.common.container;

import com.tac.guns.common.Gun;
import com.tac.guns.common.container.slot.AttachmentSlot;
import com.tac.guns.init.ModContainers;
import com.tac.guns.item.*;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.item.TransitionalTypes.TimelessOldRifleGunItem;
import com.tac.guns.item.TransitionalTypes.TimelessPistolGunItem;
import com.tac.guns.item.attachment.IAttachment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AttachmentContainer extends AbstractContainerMenu
{
    private ItemStack weapon;
    private Container playerInventory;
    private Container weaponInventory = new SimpleContainer(IAttachment.Type.values().length)
    {
        @Override
        public void setChanged()
        {
            super.setChanged();
            AttachmentContainer.this.slotsChanged(this);
        }
    };

    private boolean loaded = false;

    public static ItemStack[] getAttachments(ItemStack stack)
    {
        ItemStack[] attachments = new ItemStack[IAttachment.Type.values().length];
        if(stack.getItem() instanceof ScopeItem)
        {
            for (int i = 8; i < attachments.length; i++) {
                attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
        }
        else if(stack.getItem() instanceof TimelessOldRifleGunItem)
        {
            for (int i = 0; i < attachments.length-6; i++)
            {
                if(i==0)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.OLD_SCOPE, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
        }
        else if(stack.getItem() instanceof TimelessPistolGunItem)
        {
            for (int i = 0; i < attachments.length-6; i++)
            {
                if(i==0)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack);
                else if(i==1)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
        }
        else if (stack.getItem() instanceof TimelessGunItem)
        {
            for (int i = 0; i < IAttachment.Type.values().length-7; i++)
            {
                //if(i==0) {
                if(Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack) != ItemStack.EMPTY)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack);
                else if(Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack) != ItemStack.EMPTY)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
                //}
                //attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
        }
        return attachments;
    }
    public AttachmentContainer(int windowId, Inventory playerInventory, ItemStack stack) // reads from attachments inv
    {
        this(windowId, playerInventory);
        ItemStack[] attachments = new ItemStack[IAttachment.Type.values().length];
        if(this.weapon.getItem() instanceof ScopeItem || this.weapon.getItem() instanceof SideRailItem || this.weapon.getItem() instanceof IrDeviceItem)
        {
            for (int i = 9; i < attachments.length; i++) {
                attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
            for (int i = 9; i < attachments.length; i++) {
                this.weaponInventory.setItem(i, attachments[i]);
            }
        }
        else if(this.weapon.getItem() instanceof TimelessOldRifleGunItem)
        {
            for (int i = 0; i < attachments.length-6; i++)
            {
                if(i==0)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.OLD_SCOPE, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
            for (int i = 0; i < attachments.length-6; i++)
            {
                if(i==0)
                    this.weaponInventory.setItem(0, attachments[0]);
                else
                    this.weaponInventory.setItem(i, attachments[i]);
            }
        }
        else if(this.weapon.getItem() instanceof TimelessPistolGunItem)
        {
            for (int i = 0; i < attachments.length-6; i++)
            {
                if(i==0)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack);
                else if(i==1)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_BARREL, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
            for (int i = 0; i < attachments.length-6; i++)
            {
                this.weaponInventory.setItem(i, attachments[i]);
            }
        }
        else if (this.weapon.getItem() instanceof TimelessGunItem)
        {
            for (int i = 0; i < attachments.length-7; i++)
            {
                if(Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack) != ItemStack.EMPTY && i == 0)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.PISTOL_SCOPE, stack);
                else if (Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack) != ItemStack.EMPTY && i == 4)
                    attachments[i] = Gun.getAttachment(IAttachment.Type.IR_DEVICE, stack);
                else
                    attachments[i] = Gun.getAttachment(IAttachment.Type.values()[i], stack);
            }
            for (int i = 0; i < attachments.length-7; i++) {
                this.weaponInventory.setItem(i, attachments[i]);
            }
        }
        this.loaded = true;
    }

    public AttachmentContainer(int windowId, Inventory playerInventory)
    {
        super(ModContainers.ATTACHMENTS.get(), windowId);
        this.weapon = playerInventory.getSelected();
        this.playerInventory = playerInventory;

        if(this.weapon.getItem() instanceof ScopeItem || this.weapon.getItem() instanceof SideRailItem || this.weapon.getItem() instanceof IrDeviceItem)
        {
            // So this is pretty much me f'ing around with a single enum, likely should just adjust the properties of this enum in order to get the position in Index separately depending on the type of slot, instead of implementation requiring this
            for (int i = 10; i < IAttachment.Type.values().length; i++)
            {
                int itorationAdjustment = i;
                if(i==10)
                {
                    itorationAdjustment = i-8;
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SCOPE_RETICLE_COLOR, playerInventory.player, i, 70, 32 + (itorationAdjustment) * 18){
                        @Override
                        public boolean mayPickup(Player playerIn)
                        {
                            return true;
                        }
                    });;
                }
                if(i==11)
                {
                    itorationAdjustment = i-10;
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SCOPE_BODY_COLOR, playerInventory.player, i, 40, -1 + (itorationAdjustment) * 18){
                        @Override
                        public boolean mayPickup(Player playerIn)
                        {
                            return true;
                        }
                    });;
                }
                if(i==12 /*&& this.weapon.getItem() instanceof ScopeItem*/)
                {
                    itorationAdjustment = i-11;
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.SCOPE_GLASS_COLOR, playerInventory.player, i, 10, 50 + (itorationAdjustment) * 18){
                        @Override
                        public boolean mayPickup(Player playerIn)
                        {
                            return true;
                        }
                    });;
                }
            }
        }
        else if(this.weapon.getItem() instanceof TimelessOldRifleGunItem)
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++)
            {
                if(i==0)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.OLD_SCOPE, playerInventory.player, 0, 5, 17 + 0 * 18));
                else if (i > 4)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 155, 17 + (i-4) * 18));
                else
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 5, 17 + i * 18));
            }
        }
        else if(this.weapon.getItem() instanceof TimelessPistolGunItem)
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++)
            {
                if(i==0)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.PISTOL_SCOPE, playerInventory.player, 0, 5, 17 + 0 * 18));
                else if(i==1)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.PISTOL_BARREL, playerInventory.player, 1, 5, 17 + 1 * 18));
                else if (i > 3)
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 155, 17 + (i-4) * 18));
                else
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 5, 17 + i * 18));
            }
        }
        else if(this.weapon.getItem() instanceof TimelessGunItem)// && !(this.weapon.getItem() instanceof TimelessOldRifleGunItem))
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++)
            {
                if(i==0 && ((TimelessGunItem)this.weapon.getItem()).getGun().canAttachType(IAttachment.Type.PISTOL_SCOPE))
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, new IAttachment.Type[]{IAttachment.Type.values()[i], IAttachment.Type.PISTOL_SCOPE}, playerInventory.player, 0, 5, 17 + 0 * 18));
                else if(IAttachment.Type.values()[i] == IAttachment.Type.SIDE_RAIL && ((TimelessGunItem)this.weapon.getItem()).getGun().canAttachType(IAttachment.Type.IR_DEVICE))
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, new IAttachment.Type[]{IAttachment.Type.values()[i], IAttachment.Type.IR_DEVICE}, playerInventory.player, i, 155, 17 + (i - 4) * 18));
                else if (i > 3) {
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 155, 17 + (i - 4) * 18));
                }
                else
                    this.addSlot(new AttachmentSlot(this, this.weaponInventory, this.weapon, IAttachment.Type.values()[i], playerInventory.player, i, 5, 17 + i * 18));
            }
        }

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            if(i == playerInventory.selected)
            {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160)
                {
                    @Override
                    public boolean mayPickup(Player playerIn)
                    {
                        return true;
                    }
                });
            }
            else
            {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160){
                    @Override
                    public boolean mayPickup(Player playerIn)
                    {
                        return true;
                    }
                });;
            }
        }
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }
    @Override
    public void slotsChanged(Container inventoryIn) // something with this...
    {
        CompoundTag attachments = new CompoundTag();

        if(!(this.weapon.getItem() instanceof GunItem)/* ScopeItem || this.weapon.getItem() instanceof SideRailItem*/)
        {
            for (int i = 0; i < this.getWeaponInventory().getContainerSize(); i++)
            {
                ItemStack attachment = this.getSlot(i).getItem();
                if(attachment.getItem() instanceof DyeItem)
                {
                    if(i == 0)
                        attachments.put(IAttachment.Type.SCOPE_RETICLE_COLOR.getTagKey(), attachment.save(new CompoundTag()));
                    if(i == 1)
                        attachments.put(IAttachment.Type.SCOPE_BODY_COLOR.getTagKey(), attachment.save(new CompoundTag()));
                    if(i == 2)
                        attachments.put(IAttachment.Type.SCOPE_GLASS_COLOR.getTagKey(), attachment.save(new CompoundTag()));
                }
            }
        }
        else if(this.weapon.getItem() instanceof TimelessOldRifleGunItem)
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++)
            {
                if(i == 0)
                {
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof OldScopeItem) {
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                    }
                }
                else {
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof IAttachment) {
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                    }
                }
            }
        }
        else if(this.weapon.getItem() instanceof TimelessPistolGunItem)
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++)
            {
                if(i == 0)
                {
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof PistolScopeItem) {
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                    }
                }
                else if (i == 1)
                {
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof PistolBarrelItem) {
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                    }
                }
                else {
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof IAttachment) {
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                    }
                }
            }
        }
        else if(this.weapon.getItem() instanceof TimelessGunItem)// && !(this.weapon.getItem() instanceof TimelessOldRifleGunItem))
        {
            for (int i = 0; i < IAttachment.Type.values().length-6; i++) {
                /*if (i == 0) {
                    ItemStack attachment = this.getSlot(i).getStack();
                    if (attachment.getItem() instanceof PistolScopeItem) {
                        attachments.put(((ScopeItem) attachment.getItem()).getType().getTagKey(), attachment.write(new CompoundNBT()));
                        attachments.put(((PistolScopeItem) attachment.getItem()).getType().getTagKey(), attachment.write(new CompoundNBT()));
                    }
                } else */{
                    ItemStack attachment = this.getSlot(i).getItem();
                    if (attachment.getItem() instanceof IAttachment)
                        attachments.put(((IAttachment) attachment.getItem()).getType().getTagKey(), attachment.save(new CompoundTag()));
                }
            }
        }


        CompoundTag tag = this.weapon.getOrCreateTag();
        tag.put("Attachments", attachments);
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        ItemStack copyStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (this.weapon.getItem() instanceof ScopeItem || this.weapon.getItem() instanceof SideRailItem)
        {
            if (slot != null && slot.hasItem()) {
                ItemStack slotStack = slot.getItem();
                copyStack = slotStack.copy();

                if (index == 0) {
                    if (!this.moveItemStackTo(slotStack, 0, 36, true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (slotStack.getItem() instanceof DyeItem) {
                        if (!this.moveItemStackTo(slotStack, 0, 3, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 28) {
                        if (!this.moveItemStackTo(slotStack, 28, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index <= 36 && !this.moveItemStackTo(slotStack, 0, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (slotStack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                if (slotStack.getCount() == copyStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, slotStack);
            }
        }
        else {
            if (slot != null && slot.hasItem()) {
                ItemStack slotStack = slot.getItem();
                copyStack = slotStack.copy();
                if (index < this.weaponInventory.getContainerSize()) {
                    if (!this.moveItemStackTo(slotStack, this.weaponInventory.getContainerSize(), this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(slotStack, 0, this.weaponInventory.getContainerSize(), false)) {
                    return ItemStack.EMPTY;
                }
                if (slotStack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }

        return copyStack;
    }

    public Container getPlayerInventory()
    {
        return this.playerInventory;
    }

    public Container getWeaponInventory()
    {
        return this.weaponInventory;
    }
}
