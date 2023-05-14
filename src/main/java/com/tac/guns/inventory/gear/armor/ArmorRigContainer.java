package com.tac.guns.inventory.gear.armor;

import com.tac.guns.init.ModContainers;
import com.tac.guns.inventory.gear.InventoryListener;
import com.tac.guns.item.TransitionalTypes.wearables.ArmorRigItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ArmorRigContainer extends AbstractContainerMenu {

    private ItemStack item;
    private int numRows = 2;

    public ArmorRigContainer(int windowId, Inventory inv, ItemStack item) {
        super(ModContainers.ARMOR_TEST.get(), windowId);
        this.item = item;

        RigSlotsHandler itemHandler = (RigSlotsHandler) this.item.getCapability(InventoryListener.RIG_HANDLER_CAPABILITY).resolve().get();
        this.numRows = ((ArmorRigItem)inv.player.getMainHandItem().getItem()).getNumOfRows();
        int i = (this.numRows - 4) * 18;
        //RigSlotsHandler itemHandler = new RigSlotsHandler(maxSlots);

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

    public ArmorRigContainer(int windowId, Inventory inv) {
        super(ModContainers.ARMOR_TEST.get(), windowId);
        this.item = item;

        int i = (2 - 4) * 18;
        //int i = (this.numRows - 4) * 18;

        ItemStackHandler itemHandler = new ItemStackHandler(27);
        for(int j = 0; j < this.numRows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        /*for(int j = 0; j < this.numRows; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }*/

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
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if(slotId <= 0) super.clicked(slotId, dragType, clickTypeIn, player);
        Slot slot = this.slots.get(slotId);
        if(slot.hasItem()) {
            if(slot.getItem().getItem() instanceof ArmorRigItem) return;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
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
    private static boolean isAmmo(ItemStack stack, ResourceLocation id)
    {
        return stack != null && stack.getItem().getRegistryName().equals(id);
    }
    public int getNumRows() {
        return numRows;
    }


}
