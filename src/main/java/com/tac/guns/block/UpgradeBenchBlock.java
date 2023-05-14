package com.tac.guns.block;

import com.google.gson.GsonBuilder;
import com.tac.guns.Config;
import com.tac.guns.client.util.RenderUtil;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModBlocks;
import com.tac.guns.init.ModTileEntities;
import com.tac.guns.item.TransitionalTypes.TimelessGunItem;
import com.tac.guns.network.PacketHandler;
import com.tac.guns.network.message.MessageFireMode;
import com.tac.guns.network.message.MessageSaveItemUpgradeBench;
import com.tac.guns.tileentity.UpgradeBenchTileEntity;
import com.tac.guns.tileentity.WorkbenchTileEntity;
import com.tac.guns.util.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.tac.guns.GunMod.LOGGER;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class UpgradeBenchBlock extends RotatedObjectBlock
{
    private final Map<BlockState, VoxelShape> SHAPES = new HashMap<>();

    public UpgradeBenchBlock(Properties properties)
    {
        super(properties);
    }

    private VoxelShape getShape(BlockState state)
    {
        if(SHAPES.containsKey(state))
        {
            return SHAPES.get(state);
        }
        Direction direction = state.getValue(FACING);
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(Block.box(0.5, 0, 0.5, 15.5, 13, 15.5));
        shapes.add(Block.box(0, 13, 0, 16, 15, 16));
        shapes.add(VoxelShapeHelper.getRotatedShapes(VoxelShapeHelper.rotate(Block.box(0, 15, 0, 16, 16, 2), Direction.SOUTH))[direction.get2DDataValue()]);
        VoxelShape shape = VoxelShapeHelper.combineAll(shapes);
        SHAPES.put(state, shape);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
    {
        return this.getShape(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return this.getShape(state);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(!world.isClientSide())
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof INamedContainerProvider)
            {
                PacketHandler.getPlayChannel().sendToServer(new MessageSaveItemUpgradeBench(pos));
                tileEntity.setChanged();
            }
        }
        return ActionResultType.SUCCESS;

    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && state.getBlock() != newState.getBlock())
        {
            popResource(worldIn, pos,
                    ((UpgradeBenchTileEntity)worldIn.getBlockEntity(pos)).getInventory().get(0));
            popResource(worldIn, pos,
                    ((UpgradeBenchTileEntity)worldIn.getBlockEntity(pos)).getInventory().get(1));
            worldIn.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new UpgradeBenchTileEntity();
    }
}
