package com.animania.addons.catsdogs.common.entity.felids;

import java.util.UUID;

import javax.annotation.Nullable;

import com.animania.Animania;
import com.animania.api.data.EntityGender;
import com.animania.api.interfaces.IChild;
import com.animania.common.ModSoundEvents;
import com.animania.common.entities.generic.GenericBehavior;
import com.animania.common.entities.generic.ai.GenericAIFollowParents;
import com.animania.compat.top.providers.entity.TOPInfoProviderChild;
import com.google.common.base.Optional;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityKittenBase extends EntityAnimaniaCat implements TOPInfoProviderChild, IChild
{

	protected static final DataParameter<Optional<UUID>> PARENT_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityKittenBase.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Float> AGE = EntityDataManager.<Float>createKey(EntityKittenBase.class, DataSerializers.FLOAT);
	protected int ageTimer;

	
	public EntityKittenBase(World worldIn)
	{
		super(worldIn);
		this.setSize(0.8f, 0.8f); 
		this.width = 0.8f;
		this.height = 0.8f;
		this.stepHeight = 1.1F;
		this.ageTimer = 0;
		this.gender = EntityGender.CHILD;
		this.tasks.addTask(1, new GenericAIFollowParents<EntityKittenBase, EntityQueenBase>(this, 1.1D, EntityQueenBase.class));


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
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.315D);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(AGE, Float.valueOf(0));
		this.dataManager.register(PARENT_UNIQUE_ID, Optional.<UUID>absent());

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
	
	//TODO: SOUND
	@Override
	protected SoundEvent getAmbientSound()
	{
		return GenericBehavior.getAmbientSound(this);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return GenericBehavior.getRandomSound();
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return GenericBehavior.getRandomSound();
	}
	
	@Override
	public void onLivingUpdate()
	{
		GenericBehavior.livingUpdateChild(this, EntityQueenBase.class);

		super.onLivingUpdate();
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
