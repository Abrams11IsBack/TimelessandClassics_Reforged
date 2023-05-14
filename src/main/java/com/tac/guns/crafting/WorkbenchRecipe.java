package com.tac.guns.crafting;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.tac.guns.init.ModRecipeSerializers;
import com.tac.guns.tileentity.WorkbenchTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class WorkbenchRecipe implements IRecipe<WorkbenchTileEntity>
{
    private final ResourceLocation id;
    private final ItemStack item;
    private final ImmutableList<Pair<Ingredient, Integer>> materials;
    private final String group;

    public WorkbenchRecipe(ResourceLocation id, ItemStack item, ImmutableList<Pair<Ingredient, Integer>> materials, String group)
    {
        this.id = id;
        this.item = item;
        this.materials = materials;
        this.group = group;
    }

    public ItemStack getItem()
    {
        return this.item.copy();
    }

    public ImmutableList<Pair<Ingredient, Integer>> getMaterials()
    {
        return this.materials;
    }

    @Override
    public boolean matches(WorkbenchTileEntity inv, World worldIn)
    {
        return false;
    }

    @Override
    public ItemStack assemble(WorkbenchTileEntity inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem()
    {
        return this.item.copy();
    }

    @Override
    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.WORKBENCH.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return com.tac.guns.crafting.RecipeType.WORKBENCH;
    }

    @Override
    public String getGroup()
    {
        return group;
    }
}
