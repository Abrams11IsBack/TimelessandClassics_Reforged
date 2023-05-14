package com.tac.guns.item.TransitionalTypes.wearables;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.InputHandler;
import com.tac.guns.common.NetworkRigManager;
import com.tac.guns.common.Rig;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigContainerProvider;
import com.tac.guns.inventory.gear.armor.ArmorRigInventoryCapability;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.util.RigEnchantmentHelper;
import com.tac.guns.util.WearableHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.Coerce;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.common.capability.CurioItemCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import net.minecraft.item.Item.Properties;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ArmorRigItem extends Item implements IArmoredRigItem {
    public ArmorRigItem(Properties properties) {
        super(properties);
        numOfRows = 1;
    }

    private final int numOfRows;
    public int getNumOfRows() {
        return this.numOfRows;
    }

    public ArmorRigItem(/*String model, */int rows, Properties properties)
    {
        super(properties);
        //this.armorModelName = model;
        this.numOfRows = rows
        ;
    }
    private ArmorRigContainerProvider containerProvider;
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(world.isClientSide) return super.use(world, player, hand);
        if(hand != Hand.MAIN_HAND) return ActionResult.pass(player.getItemInHand(hand));
        containerProvider = new ArmorRigContainerProvider(player.getItemInHand(hand));
        NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider);
        super.use(world, player, hand);
        return ActionResult.pass(player.getItemInHand(hand));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        if(GunMod.curiosLoaded)
        {
            return createBackpackProvider(stack);
        }
        return new ArmorRigInventoryCapability();
    }
    public static ICapabilityProvider createBackpackProvider(ItemStack stack)
    {
        return CurioItemCapability.createProvider(new ICurio()
        {
            @Nonnull
            @Override
            public SoundInfo getEquipSound(SlotContext slotContext)
            {
                return new SoundInfo(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
            }

            @Override
            public boolean canUnequip(String identifier, LivingEntity livingEntity)
            {
                /*if(!Config.SERVER.lockBackpackIntoSlot.get())
                    return true;*/
                CompoundNBT tag = stack.getTag();
                return tag == null || tag.getList("Items", Constants.NBT.TAG_COMPOUND).isEmpty();
            }

            @Nonnull
            @Override
            public DropRule getDropRule(LivingEntity livingEntity)
            {
                return DropRule.DEFAULT;
            }

            @Nullable
            @Override
            public CompoundNBT writeSyncData() {
                return stack.getShareTag();
            }
        });
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

    /*@OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
       super.addInformation(stack, worldIn, tooltip, flag);

       tooltip.add(new TranslationTextComponent("info.tac.current_armor_amount").append(new TranslationTextComponent(ItemStack.DECIMALFORMAT.format(WearableHelper.GetCurrentDurability(stack))+"")).mergeStyle(TextFormatting.BLUE));
       int scancode = GLFW.glfwGetKeyScancode(InputHandler.ARMOR_REPAIRING.getKeyCode());
       if(GLFW.glfwGetKeyName(InputHandler.ARMOR_REPAIRING.getKeyCode(),scancode) != null)
           tooltip.add((new TranslationTextComponent("info.tac.tac_armor_repair1").append(new TranslationTextComponent(GLFW.glfwGetKeyName(InputHandler.ARMOR_REPAIRING.getKeyCode(), scancode)).mergeStyle(TextFormatting.AQUA)).append(new TranslationTextComponent("info.tac.tac_armor_repair2"))).mergeStyle(TextFormatting.YELLOW));
    }*/

    @Override
    public boolean shouldOverrideMultiplayerNbt() {return true;}

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack)
    {
        stack.getOrCreateTag();
        CompoundNBT nbt = super.getShareTag(stack);
        if (stack.getItem() instanceof ArmorRigItem) {
            RigSlotsHandler itemHandler = (RigSlotsHandler) stack.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            nbt.put("storage", itemHandler.serializeNBT());
        }

        return nbt;
    }



    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity)
    {
        return true;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks)
    {
        if(this.allowdedIn(group))
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
