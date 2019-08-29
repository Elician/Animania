package com.animania.addons.catsdogs.common.entity.canids;

import java.util.UUID;

import javax.annotation.Nullable;

import com.animania.api.data.EntityGender;
import com.animania.api.interfaces.IChild;
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

public class EntityPuppyBase extends EntityAnimaniaDog  implements TOPInfoProviderChild, IChild
{

	protected static final DataParameter<Optional<UUID>> PARENT_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityPuppyBase.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Float> AGE = EntityDataManager.<Float>createKey(EntityPuppyBase.class, DataSerializers.FLOAT);
	protected int ageTimer;

	
	public EntityPuppyBase(World worldIn)
	{
		super(worldIn);
		this.setSize(0.8f, 0.8f); 
		this.width = 0.8f;
		this.height = 0.8f;
		this.stepHeight = 1.1F;
		this.ageTimer = 0;
		this.gender = EntityGender.CHILD;
		this.tasks.addTask(1, new GenericAIFollowParents<EntityPuppyBase, EntityFemaleDogBase>(this, 1.1D, EntityFemaleDogBase.class));


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
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setFloat("Age", this.getEntityAge());
		if (this.getParentUniqueId() != null)
			if (this.getParentUniqueId() != null)
				compound.setString("ParentUUID", this.getParentUniqueId().toString());

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.setEntityAge(compound.getFloat("Age"));
		String s;

		if (compound.hasKey("ParentUUID", 8))
			s = compound.getString("ParentUUID");
		else
		{
			String s1 = compound.getString("Parent");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}

		if (!s.isEmpty())
			this.setParentUniqueId(UUID.fromString(s));

	}
	
	@Nullable
	public UUID getParentUniqueId()
	{
		try
		{
			UUID id = (UUID) ((Optional) this.dataManager.get(PARENT_UNIQUE_ID)).orNull();
			return id;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public void setParentUniqueId(@Nullable UUID uniqueId)
	{
		this.dataManager.set(PARENT_UNIQUE_ID, Optional.fromNullable(uniqueId));
	}

	public float getEntityAge()
	{
		return this.getFloatFromDataManager(AGE);
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
	
	public void setEntityAge(float age)
	{
		this.dataManager.set(AGE, Float.valueOf(age));
	}

	
	//TODO: SOUND
	@Override
	protected SoundEvent getAmbientSound()
	{
		return GenericBehavior.getAmbientSound(this, new SoundEvent[] {});
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return GenericBehavior.getRandomSound(new SoundEvent[] {});
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return GenericBehavior.getRandomSound(new SoundEvent[] {});
	}
	
	@Override
	public void onLivingUpdate()
	{
		GenericBehavior.livingUpdateChild(this, null);

		super.onLivingUpdate();
	}

	
}
