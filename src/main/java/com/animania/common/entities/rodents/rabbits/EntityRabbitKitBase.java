package com.animania.common.entities.rodents.rabbits;

import java.util.UUID;

import javax.annotation.Nullable;

import com.animania.api.data.EntityGender;
import com.animania.api.interfaces.IChild;
import com.animania.common.entities.generic.GenericBehavior;
import com.animania.common.entities.generic.ai.GenericAIFollowParents;
import com.animania.compat.top.providers.entity.TOPInfoProviderChild;
import com.google.common.base.Optional;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRabbitKitBase extends EntityAnimaniaRabbit implements TOPInfoProviderChild, IChild
{

	protected static final DataParameter<Optional<UUID>> PARENT_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityRabbitKitBase.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Float> AGE = EntityDataManager.<Float>createKey(EntityRabbitKitBase.class, DataSerializers.FLOAT);
	protected int ageTimer;

	public EntityRabbitKitBase(World worldIn)
	{
		super(worldIn);
		this.setSize(0.8F, 0.8F); 
		this.width = 0.8F;
		this.height = 0.8F;
		this.stepHeight = 1.1F;
		this.ageTimer = 0;
		this.gender = EntityGender.CHILD;
		this.tasks.addTask(1, new GenericAIFollowParents<EntityRabbitKitBase, EntityRabbitDoeBase>(this, 1.1, EntityRabbitDoeBase.class));
	}

	@Override
	public boolean isChild()
	{
		return true;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.315D);

	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(EntityRabbitKitBase.AGE, Float.valueOf(0));
		this.dataManager.register(EntityRabbitKitBase.PARENT_UNIQUE_ID, Optional.<UUID>absent());

	}

	@Override
	public void playLivingSound()
	{
		SoundEvent soundevent = this.getAmbientSound();

		if (soundevent != null && !this.getSleeping())
			this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch() + .2F);
	}


	@Override
	public int getAgeTimer()
	{
		return ageTimer;
	}

	@Override
	public void setAgeTimer(int i)
	{
		ageTimer = i;
	}

	@Override
	public void onLivingUpdate()
	{

		GenericBehavior.livingUpdateChild(this, EntityRabbitDoeBase.class);

		super.onLivingUpdate();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id)
	{
		if (id == 10)
			this.eatTimer = 80;
		else
			super.handleStatusUpdate(id);
	}

	@SideOnly(Side.CLIENT)
	public float getHeadRotationPointY(float p_70894_1_)
	{
		return this.eatTimer <= 0 ? 0.0F : this.eatTimer >= 4 && this.eatTimer <= 76 ? 1.0F : this.eatTimer < 4 ? (this.eatTimer - p_70894_1_) / 4.0F : -(this.eatTimer - 80 - p_70894_1_) / 4.0F;
	}

	@SideOnly(Side.CLIENT)
	public float getHeadRotationAngleX(float p_70890_1_)
	{
		if (this.eatTimer > 4 && this.eatTimer <= 76)
		{
			float f = (this.eatTimer - 4 - p_70890_1_) / 24.0F;
			return (float) Math.PI / 5F + (float) Math.PI * 7F / 150F * MathHelper.sin(f * 28.7F);
		}
		else
			return this.eatTimer > 0 ? (float) Math.PI / 5F : this.rotationPitch * 0.017453292F;
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack)
	{
		return stack != ItemStack.EMPTY && (EntityAnimaniaRabbit.TEMPTATION_ITEMS.contains(stack.getItem()));
	}

	@Override
	protected void dropFewItems(boolean hit, int lootlevel)
	{
		return;
	}

	@Override
	protected Item getDropItem()
	{
		return null;
	}

	@Override
	public DataParameter<Optional<UUID>> getParentUniqueIdParam()
	{
		return PARENT_UNIQUE_ID;
	}

	@Override
	public DataParameter<Float> getEntityAgeParam()
	{
		return AGE;
	}

}
