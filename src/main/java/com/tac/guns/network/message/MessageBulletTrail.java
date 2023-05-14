package com.tac.guns.network.message;

import com.tac.guns.client.network.ClientPlayHandler;
import com.tac.guns.common.Gun;
import com.tac.guns.entity.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class MessageBulletTrail implements IMessage
{
    private int[] entityIds;
    private Vector3d[] positions;
    private Vector3d[] motions;
    private float[] shooterYaws;
    private float[] shooterPitches;
    private ItemStack item;
    private int trailColor;
    private double trailLengthMultiplier;
    private int life;
    private double gravity;
    private int shooterId;
    private float size;

    public MessageBulletTrail() {}

    public MessageBulletTrail(ProjectileEntity[] spawnedProjectiles, Gun.Projectile projectileProps, int shooterId, float size)
    {
        this.positions = new Vector3d[spawnedProjectiles.length];
        this.motions = new Vector3d[spawnedProjectiles.length];
        this.shooterYaws = new float[spawnedProjectiles.length];
        this.shooterPitches = new float[spawnedProjectiles.length];
        this.entityIds = new int[spawnedProjectiles.length];
        for(int i = 0; i < spawnedProjectiles.length; i++)
        {
            ProjectileEntity projectile = spawnedProjectiles[i];
            this.positions[i] = projectile.position();
            this.motions[i] = projectile.getDeltaMovement();
            this.shooterYaws[i] = projectile.getShooter().getViewYRot(1);
            this.shooterPitches[i] = projectile.getShooter().getViewXRot(1);
            this.entityIds[i] = projectile.getId();
        }
        this.item = spawnedProjectiles[0].getItem();
        this.trailColor = projectileProps.getTrailColor();
        this.trailLengthMultiplier = projectileProps.getTrailLengthMultiplier();
        this.life = projectileProps.getLife();
        this.gravity = spawnedProjectiles[0].getModifiedGravity(); //It's possible that projectiles have different gravity
        this.shooterId = shooterId;
        this.size = size;
    }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(this.entityIds.length);
        for(int i = 0; i < this.entityIds.length; i++)
        {
            buffer.writeInt(this.entityIds[i]);

            Vector3d position = this.positions[i];
            buffer.writeDouble(position.x);
            buffer.writeDouble(position.y);
            buffer.writeDouble(position.z);

            Vector3d motion = this.motions[i];
            buffer.writeDouble(motion.x);
            buffer.writeDouble(motion.y);
            buffer.writeDouble(motion.z);

            buffer.writeFloat(this.shooterYaws[i]);
            buffer.writeFloat(this.shooterPitches[i]);
        }
        buffer.writeItem(this.item);
        buffer.writeVarInt(this.trailColor);
        buffer.writeDouble(this.trailLengthMultiplier);
        buffer.writeInt(this.life);
        buffer.writeDouble(this.gravity);
        buffer.writeInt(this.shooterId);
        buffer.writeFloat(this.size);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        int size = buffer.readInt();
        this.entityIds = new int[size];
        this.positions = new Vector3d[size];
        this.motions = new Vector3d[size];
        this.shooterYaws = new float[size];
        this.shooterPitches = new float[size];
        for(int i = 0; i < size; i++)
        {
            this.entityIds[i] = buffer.readInt();
            this.positions[i] = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            this.motions[i] = new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            this.shooterYaws[i] = buffer.readFloat();
            this.shooterPitches[i] = buffer.readFloat();
        }
        this.item = buffer.readItem();
        this.trailColor = buffer.readVarInt();
        this.trailLengthMultiplier = buffer.readDouble();
        this.life = buffer.readInt();
        this.gravity = buffer.readDouble();
        this.shooterId = buffer.readInt();
        this.size = buffer.readFloat();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleMessageBulletTrail(this));
        supplier.get().setPacketHandled(true);
    }

    public int getCount()
    {
        return this.entityIds.length;
    }

    public int[] getEntityIds()
    {
        return this.entityIds;
    }

    public Vector3d[] getPositions()
    {
        return this.positions;
    }

    public Vector3d[] getMotions()
    {
        return this.motions;
    }

    public int getTrailColor()
    {
        return this.trailColor;
    }

    public double getTrailLengthMultiplier()
    {
        return this.trailLengthMultiplier;
    }

    public int getLife()
    {
        return this.life;
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    public double getGravity()
    {
        return this.gravity;
    }

    public int getShooterId()
    {
        return this.shooterId;
    }

    public float[] getShooterYaws() { return shooterYaws; }

    public float[] getShooterPitches() { return shooterPitches; }
    public float getSize() { return size; }
}
