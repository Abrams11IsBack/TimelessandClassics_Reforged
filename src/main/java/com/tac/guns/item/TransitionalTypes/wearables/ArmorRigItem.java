package com.tac.guns.item.TransitionalTypes.wearables;

import com.tac.guns.Reference;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.common.Rig;
import com.tac.guns.inventory.gear.InventoryListener;
import com.tac.guns.inventory.gear.WearableCapabilityProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigContainerProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigInventoryCapability;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.util.RigEnchantmentHelper;
import com.tac.guns.util.WearableHelper;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ArmorRigItem extends Item implements IArmoredRigItem {
    public ArmorRigItem(Properties properties) {
        super(properties);
        numOfSlots = 9;
    }

    private final int numOfSlots;
    public int getSlots() {
        return this.numOfSlots;
    }

    public ArmorRigItem(/*String model, */int slots, Properties properties)
    {
        super(properties);
        //this.armorModelName = model;
        this.numOfSlots = slots;
    }
    private ArmorRigContainerProvider containerProvider;
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if(world.isRemote) return super.onItemRightClick(world, player, hand);
        if(hand != Hand.MAIN_HAND) return ActionResult.resultPass(player.getHeldItem(hand));
        containerProvider = new ArmorRigContainerProvider(player.getHeldItem(hand));
        NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider);
        super.onItemRightClick(world, player, hand);
        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ArmorRigInventoryCapability();
    }

    private WeakHashMap<CompoundNBT, Rig> modifiedRigCache = new WeakHashMap<>();

    private Rig rig = new Rig();

    public void setRig(NetworkRigManager.Supplier supplier)
    {
        this.rig = supplier.getRig();
    }

    public Rig getRig()
    {
        return this.rig;
    }

    /*@Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        if(this.rig != null || stack.getTag() != null) {
            tooltip.add(new TranslationTextComponent("info.tac.attachment_help",
                    new KeybindTextComponent("key.tac.attachments").getString().toUpperCase(Locale.ENGLISH) + " | " + stack.getOrCreateTag().getFloat("RigDurability") + " | " + this.rig.getRepair().getItem()).mergeStyle(TextFormatting.YELLOW));
        }
    }*/

    @Override
    public boolean shouldSyncTag() {return true;}

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack)
    {
        CompoundNBT nbt = super.getShareTag(stack);
        RigSlotsHandler itemHandler = (RigSlotsHandler) stack.getCapability(InventoryListener.RIG_HANDLER_CAPABILITY).resolve().get();
        nbt.put("storage", itemHandler.serializeNBT());



        /*CompoundNBT nbt = super.getShareTag(inputStack);
        GearSlotsHandler ammoItemHandler = (GearSlotsHandler) player.getCapability(InventoryListener.ITEM_HANDLER_CAPABILITY).resolve().get();
        for(ItemStack stack : ammoItemHandler.getStacks()) {
            if (stack.getItem() instanceof ArmorRigItem) {
                RigSlotsHandler itemHandler = (RigSlotsHandler) stack.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
                nbt.put("storage", itemHandler.serializeNBT());
            }
        }*/

        return nbt;
    }



    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        return true;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks)
    {
        if(this.isInGroup(group))
        {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag();
            WearableHelper.FillDefaults(stack, this.rig);
            stacks.add(stack);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        stack.getOrCreateTag();
        Rig modifiedRig = this.getModifiedRig(stack);
        return 1.0 - (WearableHelper.GetCurrentDurability(stack) / (double) RigEnchantmentHelper.getModifiedDurability(stack, modifiedRig));
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return Objects.requireNonNull(TextFormatting.AQUA.getColor());
    }

    public Rig getModifiedRig(ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getTag();
        if(tagCompound != null && tagCompound.contains("Rig", Constants.NBT.TAG_COMPOUND))
        {
            return this.modifiedRigCache.computeIfAbsent(tagCompound, item ->
            {
                if(tagCompound.getBoolean("Custom"))
                {
                    return Rig.create(tagCompound.getCompound("Rig"));
                }
                else
                {
                    Rig gunCopy = this.rig.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Rig"));
                    return gunCopy;
                }
            });
        }
        return this.rig;
    }

    /*@Override
    public ArmorBase getArmorModelName() {
        return this.armorModelName == null ? new ModernArmor() : this.armorModelName;
    }*/
}
