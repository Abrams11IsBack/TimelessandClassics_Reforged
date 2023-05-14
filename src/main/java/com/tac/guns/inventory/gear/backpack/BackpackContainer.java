package com.tac.guns.inventory.gear.backpack;

import com.tac.guns.init.ModContainers;
import com.tac.guns.inventory.gear.GearSlotsHandler;
import com.tac.guns.inventory.gear.WearableCapabilityProvider;
import com.tac.guns.inventory.gear.armor.AmmoSlot;
import com.tac.guns.item.TransitionalTypes.wearables.ArmorRigItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackContainer extends Container {

    private ItemStack item;
    private int numRows = 2;

    public BackpackContainer(int windowId, PlayerInventory inv, ItemStack item) {
        super(ModContainers.ARMOR_TEST.get(), windowId);
        this.item = item;
        GearSlotsHandler itemHandler = (GearSlotsHandler)this.item.getCapability(WearableCapabilityProvider.capability).resolve().get();
        int i = (this.numRows - 4) * 18;

        for(int j = 0; j < this.numRows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));
        }

        //this.setAll(itemHandler.getStacks());
    }

    public BackpackContainer(int windowId, PlayerInventory inv) {
        super(ModContainers.ARMOR_TEST.get(), windowId);
        this.item = item;
        int i = (this.numRows - 4) * 18;

        ItemStackHandler itemHandler = new ItemStackHandler(18);
        for(int j = 0; j < this.numRows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));
        }
    }


    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if(slotId <= 0) return super.clicked(slotId, dragType, clickTypeIn, player);
        Slot slot = this.slots.get(slotId);
        if(slot.hasItem()) {
            if(slot.getItem().getItem() instanceof ArmorRigItem) return ItemStack.EMPTY;
        }
        return super.clicked(slotId, dragType, clickTypeIn, player);
    }

    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.numRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.numRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.numRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public int getNumRows() {
        return numRows;
    }
}
