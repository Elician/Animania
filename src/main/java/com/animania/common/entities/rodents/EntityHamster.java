package com.animania.common.entities.rodents;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.animania.Animania;
import com.animania.api.data.AnimalContainer;
import com.animania.api.data.EntityGender;
import com.animania.api.interfaces.AnimaniaType;
import com.animania.api.interfaces.IAnimaniaAnimalBase;
import com.animania.common.ModSoundEvents;
import com.animania.common.capabilities.CapabilityRefs;
import com.animania.common.capabilities.ICapabilityPlayer;
import com.animania.common.entities.generic.GenericBehavior;
import com.animania.common.entities.generic.ai.GenericAIFindFood;
import com.animania.common.entities.generic.ai.GenericAIFindWater;
import com.animania.common.entities.generic.ai.GenericAIFollowOwner;
import com.animania.common.entities.generic.ai.GenericAIPanic;
import com.animania.common.entities.generic.ai.GenericAISwimmingSmallCreatures;
import com.animania.common.entities.generic.ai.GenericAITempt;
import com.animania.common.entities.generic.ai.GenericAIWanderAvoidWater;
import com.animania.common.entities.generic.ai.GenericAIWatchClosest;
import com.animania.common.entities.rodents.ai.EntityAILookIdleRodent;
import com.animania.common.entities.rodents.ai.EntityAISleepHamsters;
import com.animania.common.handler.ItemHandler;
import com.animania.common.handler.PatreonHandler;
import com.animania.common.helper.AnimaniaHelper;
import com.animania.common.items.ItemEntityEgg;
import com.animania.compat.top.providers.entity.TOPInfoProviderRodent;
import com.animania.config.AnimaniaConfig;
import com.animania.network.client.CapSyncPacket;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class EntityHamster extends EntityTameable implements TOPInfoProviderRodent, IAnimaniaAnimalBase
{
	private static final DataParameter<Boolean> IN_BALL = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
//	private static final DataParameter<Boolean> SITTING = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> RIDING = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
//	private static final DataParameter<Boolean> TAMED = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> COLOR_NUM = EntityDataManager.<Integer>createKey(EntityHamster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FOOD_STACK_COUNT = EntityDataManager.<Integer>createKey(EntityHamster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> IN_LOVE = EntityDataManager.<Integer>createKey(EntityHamster.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> BALL_COLOR = EntityDataManager.<Integer>createKey(EntityHamster.class, DataSerializers.VARINT);
	public static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(AnimaniaHelper.getItemArray(AnimaniaConfig.careAndFeeding.hamsterFood));
	private static final DataParameter<Boolean> FED = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> WATERED = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityHamster.class, DataSerializers.VARINT);
	protected static final DataParameter<Boolean> SLEEPING = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Float> SLEEPTIMER = EntityDataManager.<Float>createKey(EntityHamster.class, DataSerializers.FLOAT);
	protected static final DataParameter<Boolean> INTERACTED = EntityDataManager.<Boolean>createKey(EntityHamster.class, DataSerializers.BOOLEAN);

	private static final String[] HAMSTER_TEXTURES = new String[] { "black", "brown", "darkbrown", "darkgray", "gray", "plum", "tarou", "white", "gold" };

	private int fedTimer;
	private int wateredTimer;
	private int happyTimer;
	private int tamedTimer;
	public int blinkTimer;
	private long rideCount;
	private int delayCount;

	private int stackCount;
	private int eatCount;
	private int foodStackCount;
	private int standCount;
	private EntityItem targetFood;
	private boolean looksWithInterest;
	private boolean isStanding;
	private float field_25048_b;
	private float field_25054_c;
	private static List hamsterColorList;
	private EntityPlayer givemeEntity;
	private int breeding;
	private boolean mountFlag;
	private double yOffset;
	private ResourceLocation resourceLocation;
	private ResourceLocation resourceLocationBlink;
	private int damageTimer;

	public EntityHamster(World world)
	{
		super(world);
		this.setHealth(6);
		this.yOffset = 0.1F;
		this.setSize(0.5F, 0.3F); 
		this.width = 0.5F;
		this.height = 0.3F;
		this.stepHeight = 1.0F;
		this.fedTimer = AnimaniaConfig.careAndFeeding.feedTimer + this.rand.nextInt(100);
		this.wateredTimer = (AnimaniaConfig.careAndFeeding.waterTimer * 4) + this.rand.nextInt(200);
		this.looksWithInterest = false;
		this.stackCount = 20;
		this.eatCount = 5000;
		this.standCount = 30;
		this.isStanding = false;
		this.breeding = 0;
		this.happyTimer = 60;
		this.tamedTimer = 120;
		this.blinkTimer = 70 + this.rand.nextInt(70);
		this.delayCount = 5;
		this.enablePersistence();
	}

	@Override
	protected void initEntityAI()
	{

		this.tasks.addTask(1, new GenericAIPanic<EntityHamster>(this, 1.4D));
		this.tasks.addTask(2, new GenericAISwimmingSmallCreatures(this));
		if (!AnimaniaConfig.gameRules.ambianceMode)
		{
			this.tasks.addTask(3, new GenericAIFindWater<EntityHamster>(this, 1.0D, null, EntityHamster.class, true));
			this.tasks.addTask(3, new GenericAIFindFood<EntityHamster>(this, 1.0D, null, false));
		}
		this.tasks.addTask(4, new EntityAIFleeSun(this, 1.0D));
		this.tasks.addTask(5, new GenericAIWanderAvoidWater(this, 1.1D));
		this.tasks.addTask(6, new GenericAITempt<EntityHamster>(this, 1.2D, false, EntityHamster.TEMPTATION_ITEMS));
		this.tasks.addTask(7, new GenericAIFollowOwner<EntityHamster>(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(8, new GenericAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(9, new EntityAILookIdleRodent(this));
		if (AnimaniaConfig.gameRules.animalsSleep)
		{
			this.tasks.addTask(10, new EntityAISleepHamsters(this, 0.8));
		}
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false, new Class[0]));

	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();
	}

	@Override
	public int getVerticalFaceSpeed()
	{
		return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
	}

	@Override
	public void setInLove(EntityPlayer player)
	{
		this.world.setEntityState(this, (byte) 18);
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		super.setPosition(x, y, z);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
	}

	public ResourceLocation getResourceLocation()
	{
		return this.resourceLocation;
	}

	public ResourceLocation getResourceLocationBlink()
	{
		return this.resourceLocationBlink;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(EntityHamster.IN_BALL, false);
//		this.dataManager.register(EntityHamster.SITTING, false);
//		this.dataManager.register(EntityHamster.TAMED, false);
		this.dataManager.register(EntityHamster.COLOR_NUM, Integer.valueOf(this.getRNG().nextInt(8)));
		this.dataManager.register(EntityHamster.FOOD_STACK_COUNT, Integer.valueOf(0));
		this.dataManager.register(EntityHamster.IN_LOVE, Integer.valueOf(0));
		this.dataManager.register(EntityHamster.BALL_COLOR, Integer.valueOf(0));
		this.dataManager.register(EntityHamster.FED, true);
		this.dataManager.register(EntityHamster.WATERED, true);
		this.dataManager.register(EntityHamster.RIDING, false);
		this.dataManager.register(EntityHamster.AGE, Integer.valueOf(0));
		this.dataManager.register(EntityHamster.SLEEPING, false);
		this.dataManager.register(EntityHamster.SLEEPTIMER, Float.valueOf(0.0F));
		this.dataManager.register(INTERACTED, false);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("IsSitting", this.isSitting());
		nbttagcompound.setBoolean("InBall", this.isInBall());
		nbttagcompound.setInteger("ColorNumber", this.getColorNumber());
		nbttagcompound.setInteger("foodStackCount", this.getFoodStackCount());
		nbttagcompound.setInteger("BallColor", this.getBallColor());
		nbttagcompound.setBoolean("IsTamed", this.isTamed());
		nbttagcompound.setBoolean("IsRiding", this.getIsRiding());
		
		GenericBehavior.writeCommonNBT(nbttagcompound, this);
		
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readEntityFromNBT(nbttagcompound);
		this.setSitting(nbttagcompound.getBoolean("IsSitting"));
		this.setInBall(nbttagcompound.getBoolean("InBall"));
		this.setColorNumber(nbttagcompound.getInteger("ColorNumber"));
		this.setFoodStackCount(nbttagcompound.getInteger("foodStackCount"));
		this.setBallColor(nbttagcompound.getInteger("BallColor"));
		this.setTamed(nbttagcompound.getBoolean("IsTamed"));
		this.setIsRiding(nbttagcompound.getBoolean("IsRiding"));
		
		GenericBehavior.readCommonNBT(nbttagcompound, this);
	}

	@Override
	public DataParameter<Integer> getAgeParam()
	{
		return AGE;
	}

	@Override
	public DataParameter<Boolean> getSleepingParam()
	{
		return SLEEPING;
	}

	@Override
	public DataParameter<Float> getSleepTimerParam()
	{
		return SLEEPTIMER;
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public float getEyeHeight()
	{
		return this.height * 0.8F;
	}

	@Override
	public double getYOffset()
	{
		return this.yOffset;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack itemstack = player.getHeldItem(hand);

		if (this.isBreedingItem(itemstack) && !this.getSleeping())
		{
			this.doPatreonCheck(player);
			this.setHamsterStanding(true);
			this.standCount = 100;
			this.addFoodStack();

		}
		
		if (itemstack == ItemStack.EMPTY && this.isTamed() && player.isSneaking() && delayCount == 0 && !this.isInBall() && !this.getSleeping())
		{
			ICapabilityPlayer props = CapabilityRefs.getPlayerCaps(player);
			if (!props.isCarrying())
			{
				props.setAnimal(this.writeToNBT(new NBTTagCompound()));
				props.setCarrying(true);
				props.setType("hamster");
				this.setDead();
				player.swingArm(EnumHand.MAIN_HAND);
				Animania.network.sendToAllAround(new CapSyncPacket(props, player.getEntityId()), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), 64));
				return true;
			}

		}

		if (!itemstack.isEmpty() && itemstack.getItem() == ItemHandler.hamsterBallColored && !isInBall() && !this.getSleeping())
		{
			if (this.isSitting())
			{
				this.setSitting(false);
			}
			setInBall(true);
			int meta = itemstack.getMetadata();
			setBallColor(meta);
			if (!player.isCreative())
				itemstack.shrink(1);
			return true;
		}
		if (!itemstack.isEmpty() && itemstack.getItem() == ItemHandler.hamsterBallClear && !isInBall() && !this.getSleeping())
		{
			if (this.isSitting())
			{
				this.setSitting(false);
				this.setSitting(false);
			}
			setInBall(true);
			int meta = itemstack.getMetadata();
			setBallColor(16);
			if (!player.isCreative())
				itemstack.shrink(1);
			return true;
		}
		else if (itemstack.isEmpty() && isInBall() && !this.getSleeping())
		{
			int color = this.getBallColor();
			setInBall(false);
			if (!player.isCreative())
			{
				if (color == 16)
					AnimaniaHelper.addItem(player, new ItemStack(ItemHandler.hamsterBallClear));
				else
					AnimaniaHelper.addItem(player, new ItemStack(ItemHandler.hamsterBallColored, 1, color));
			}
			return true;
		}

		return GenericBehavior.interactCommon(this, player, hand, null) ? true : super.processInteract(player, hand);
	}

	@Override
	public boolean canRiderInteract()
	{
		return true;
	}

	private boolean interactOthersTamed()
	{
		if (this.isHamsterStanding() || !this.isSitting())
			this.setSitting(true);
		else if (this.isSitting())
			this.setSitting(false);
		this.isJumping = false;
		this.navigator.clearPath();

		return true;
	}

	@Override
	public void heal(float f)
	{
		super.heal(f);
		this.hurtResistantTime = this.maxHurtResistantTime / 2;
	}

	@Override
	public boolean canBeCollidedWith()
	{

		if (this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) this.getRidingEntity();
			ItemStack itemstack = player.inventory.getCurrentItem();
			if (itemstack != null)
				return false;
		}

		return super.canBeCollidedWith();
	}

	@Override
	public boolean isEntityInsideOpaqueBlock()
	{

		if (this.getRidingEntity() != null)
			return false;
		else
			return super.isEntityInsideOpaqueBlock();

	}

	@Override
	protected void jump()
	{
		this.motionY = 0.29999999999999999D;
	}

	@Override
	public boolean canPassengerSteer()
	{
		return false;
	}

	@Override
	public void onLivingUpdate()
	{
		delayCount--;
		if (delayCount <= 0)
		{
			delayCount = 0;
		}

		if (this.blinkTimer > -1)
		{
			this.blinkTimer--;
			if (this.blinkTimer == 0)
				this.blinkTimer = 80 + this.rand.nextInt(80);
		}
		
		GenericBehavior.livingUpdateCommon(this);
		
		this.setResourceLoc();
		super.onLivingUpdate();

		if (this.getHealth() < 10)
		{
			this.eatFood();
			this.eatCount = 5000;
		}
		if (!this.isHamsterStanding() && !this.isSitting() && !this.getSleeping())
		{
			if (this.rand.nextInt(20) == 0 && this.rand.nextInt(20) == 0)
			{
				this.setHamsterStanding(true);
				this.standCount = 30;
				this.navigator.clearPath();
				this.isJumping = false;
			}
		}
		else if (this.isHamsterStanding() && this.standCount-- <= 0 && this.rand.nextInt(10) == 0)
			this.setHamsterStanding(false);
		if (this.getFoodStackCount() > 0)
			if (this.eatCount == 0)
			{
				if (this.rand.nextInt(30) == 0 && this.rand.nextInt(30) == 0)
				{
					this.eatFood();
					this.eatCount = 5000;
				}
			}
			else
				this.eatCount--;
		this.looksWithInterest = false;
		if (!this.hasPath())
		{
			Entity entity = this.getAttackTarget();
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer) entity;
				ItemStack itemstack = entityplayer.inventory.getCurrentItem();
				if (itemstack != ItemStack.EMPTY && itemstack.getItem() == Items.WHEAT_SEEDS)
					this.looksWithInterest = true;
			}
		}

		if (this.isSitting() | this.isHamsterStanding() && this.getNavigator() != null)
			this.getNavigator().clearPath();


		if (this.tamedTimer > -1)
		{
			this.tamedTimer--;
			if (this.tamedTimer == 0)
			{
				this.tamedTimer = 120;

				if (this.isTamed() && AnimaniaConfig.gameRules.showUnhappyParticles && !this.isRiding())
				{
					double d = this.rand.nextGaussian() * 0.02D;
					double d1 = this.rand.nextGaussian() * 0.02D;
					double d2 = this.rand.nextGaussian() * 0.02D;
					// this.world.spawnParticle(EnumParticleTypes.HEART,
					// this.posX + this.rand.nextFloat() * this.width -
					// this.width, this.posY + 1D + this.rand.nextFloat() *
					// this.height, this.posZ + this.rand.nextFloat() *
					// this.width - this.width, d, d1, d2);
				}
			}
		}

	}

	@Override
	public DataParameter<Boolean> getWateredParam()
	{
		return WATERED;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.field_25054_c = this.field_25048_b;
		if (this.looksWithInterest)
			this.field_25048_b = this.field_25048_b + (1.0F - this.field_25048_b) * 0.4F;
		else
			this.field_25048_b = this.field_25048_b + (0.0F - this.field_25048_b) * 0.4F;
		if (this.looksWithInterest)
		{
			// numTicksToChaseTarget = 10;
		}
		if (this.rand.nextInt(10) == 5)
			this.ticksExisted++;

		// Animania.mDebug("worldObj.isRemote="+worldObj.isRemote+"
		// posX="+posX+" posY="+posY+" poxZ="+posZ);
	}

	public float getInterestedAngle(float f)
	{
		return (this.field_25054_c + (this.field_25048_b - this.field_25054_c) * f) * 0.15F * 3.141593F;
	}

	@Nullable
	public UUID getHamsterOwner()
	{
		return (UUID) ((Optional) this.dataManager.get(EntityTameable.OWNER_UNIQUE_ID)).orNull();
	}

	public void setHamsterOwner(@Nullable UUID uniqueId)
	{
		this.dataManager.set(EntityTameable.OWNER_UNIQUE_ID, Optional.fromNullable(uniqueId));
	}

	public boolean isInBall()
	{
		return this.getBoolFromDataManager(IN_BALL);
	}

	public void setInBall(boolean ball)
	{
		if (ball)
		{
			this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.30F, 1.6F);
			this.dataManager.set(EntityHamster.IN_BALL, true);
		}
		else
		{
			this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.30F, 1.3F);
			this.dataManager.set(EntityHamster.IN_BALL, false);
		}
	}

	public int getBallColor()
	{
		return this.getIntFromDataManager(BALL_COLOR);
	}

	public void setBallColor(int color)
	{
		this.dataManager.set(EntityHamster.BALL_COLOR, Integer.valueOf(color));
	}

//	public boolean isHamsterSitting()
//	{
//		return this.getBoolFromDataManager(SITTING);
//	}
//
//	public void setHamsterSitting(boolean flag)
//	{
//		this.dataManager.set(EntityHamster.SITTING, Boolean.valueOf(flag));
//	}

	void showHeartsOrSmokeFX(String s, int i, boolean flag)
	{
		for (int j = 0; j < i; j++)
		{
			double d = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;

			if (this.rand.nextInt(2) > 0)
				this.world.playSound(null, this.posX, this.posY + 1, this.posZ, ModSoundEvents.hamsterEat1, SoundCategory.PLAYERS, 0.6F, 0.8F);
			else
				this.world.playSound(null, this.posX, this.posY + 1, this.posZ, ModSoundEvents.hamsterEat2, SoundCategory.PLAYERS, 0.6F, 0.8F);
		}

	}

//	public boolean getIsTamed()
//	{
//		return this.getBoolFromDataManager(TAMED);
//	}

//	public void setIsTamed(boolean tamed)
//	{
//		if (tamed)
//			this.dataManager.set(EntityHamster.TAMED, true);
//		else
//			this.dataManager.set(EntityHamster.TAMED, false);
//	}

	public boolean getIsRiding()
	{
		return this.getBoolFromDataManager(RIDING);
	}

	public void setIsRiding(boolean riding)
	{
		this.dataManager.set(EntityHamster.RIDING, Boolean.valueOf(riding));
	}

	public boolean isHamsterStanding()
	{
		return this.isStanding;
	}

	public void setHamsterStanding(boolean flag)
	{
		this.isStanding = flag;
	}

	@Override
	protected void dropFewItems(boolean hit, int lootlevel)
	{
		if (this.isInBall())
		{
			int color = this.getBallColor();
			if (color == 16)
				this.entityDropItem(new ItemStack(ItemHandler.hamsterBallClear), 0f);
			else
				this.entityDropItem(new ItemStack(ItemHandler.hamsterBallColored, 1, color), 0f);

		}
	}

	@Override
	protected ResourceLocation getLootTable()
	{
		return new ResourceLocation(Animania.MODID, "hamster");
	}

	private ItemStack getItem(String moditem)
	{

		ItemStack foundStack = null;
		String item = "";
		String mod = "";
		int sepLoc = 0;
		int metaLoc = 0;
		boolean metaFlag = false;
		String metaVal = "";

		sepLoc = moditem.indexOf(":");
		metaLoc = moditem.indexOf("#");

		if (!moditem.contains(":"))
		{
			return new ItemStack(Blocks.AIR, 1);
		}

		mod = moditem.substring(0, sepLoc);

		if (metaLoc > 0)
		{
			item = moditem.substring(sepLoc + 1, metaLoc);
		}
		else
		{
			item = moditem.substring(sepLoc + 1, moditem.length());
		}
		if (metaLoc > 0)
		{
			metaFlag = true;
			metaVal = moditem.substring(metaLoc + 1, moditem.length());
		}

		Item bob = Item.getByNameOrId(item);

		if (bob != null)
		{

			if (metaFlag)
			{
				foundStack = new ItemStack(bob, 1, Integer.parseInt(metaVal));
			}
			else
			{
				foundStack = new ItemStack(bob, 1);
			}
		}
		else
		{
			foundStack = new ItemStack(Blocks.AIR, 1);
		}

		return foundStack;
	}

	public int getFoodStackCount()
	{
		return this.foodStackCount;
	}

	public void setFoodStackCount(int i)
	{
		this.foodStackCount = i;
	}

	public int getColorNumber()
	{
		try
		{
			// System.out.print(this.getIntFromDataManager(COLOR_NUM));
			return (this.getIntFromDataManager(COLOR_NUM));
		}
		catch (Exception e)
		{
			// System.out.print(e);
			return this.getRNG().nextInt(8);
		}
	}

	public void setColorNumber(int color)
	{
		this.dataManager.set(EntityHamster.COLOR_NUM, Integer.valueOf(color));
	}

	private boolean addFoodStack()
	{
		if (this.foodStackCount != 5)
		{
			this.foodStackCount++;
			return true;
		}
		else
		{
			this.heal(1);
			return false;
		}
	}

	private boolean eatFood()
	{
		if (this.foodStackCount != 0)
		{
			this.foodStackCount--;
			this.heal(1);
			return true;
		}
		else
			return false;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityanimal)
	{
		return null;
	}

	@Override
	public double getMountedYOffset()
	{

		return this.height;
	}

	@Override
	public boolean isBreedingItem(@Nullable ItemStack stack)
	{
		return stack != ItemStack.EMPTY && EntityHamster.TEMPTATION_ITEMS.contains(stack.getItem());
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		int happy = 0;
		int num = 1;

		if (this.getWatered())
			happy++;
		if (this.getFed())
			happy++;

		if (happy == 2)
			num = 6;
		else if (happy == 1)
			num = 12;
		else
			num = 24;

		int chooser = Animania.RANDOM.nextInt(num);

		if (chooser == 0)
			return ModSoundEvents.hamsterLiving1;
		else if (chooser == 1)
			return ModSoundEvents.hamsterLiving2;
		else if (chooser == 2)
			return ModSoundEvents.hamsterLiving3;
		else
			return null;

	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return ModSoundEvents.hamsterHurt1;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return null;
	}

	@Override
	public void playLivingSound()
	{
		SoundEvent soundevent = this.getAmbientSound();

		if (soundevent != null && !this.getSleeping())
			this.playSound(soundevent, this.getSoundVolume() - .2F, this.getSoundPitch());
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.02F, 1.8F);
	}

	private void doPatreonCheck(EntityPlayer player)
	{
		if (player.isSneaking())
		{
			if (PatreonHandler.isPlayerPatreon(player))
			{
				this.setColorNumber(8);
				this.resourceLocation = null;
			}
		}
	}

	public void setResourceLoc()
	{

		if (this.getColorNumber() == 9)
		{
			this.setColorNumber(8);
		}
		else if (this.getColorNumber() > 9)
		{
			this.setColorNumber(0);
		}

		if (this.resourceLocation == null)
		{
			this.resourceLocation = new ResourceLocation("animania:textures/entity/rodents/hamster_" + EntityHamster.HAMSTER_TEXTURES[this.getColorNumber()] + ".png");
			this.resourceLocationBlink = new ResourceLocation("animania:textures/entity/rodents/hamster_" + EntityHamster.HAMSTER_TEXTURES[this.getColorNumber()] + "_blink.png");
		}
	}

	@Override
	public Item getSpawnEgg()
	{
		return ItemEntityEgg.ANIMAL_EGGS.get(new AnimalContainer(HamsterType.STANDARD, EntityGender.NONE));
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target)
	{
		return new ItemStack(getSpawnEgg());
	}

	@Override
	public int getPrimaryEggColor()
	{
		return 14603464;
	}

	@Override
	public int getSecondaryEggColor()
	{
		return 14317391;
	}

	@Override
	public EntityGender getEntityGender()
	{
		return EntityGender.NONE;
	}

	@Override
	public Set<Item> getFoodItems()
	{
		return TEMPTATION_ITEMS;
	}

	@Override
	public void setSleepingPos(BlockPos pos)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public BlockPos getSleepingPos()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBlinkTimer()
	{
		return blinkTimer;
	}
	

	@Override
	public void setBlinkTimer(int i)
	{
		blinkTimer = i;
	}
	
	@Override
	public int getEatTimer()
	{
		return 0;
	}

	@Override
	public void setEatTimer(int i)
	{
	}

	@Override
	public int getFedTimer()
	{
		return fedTimer;
	}

	@Override
	public void setFedTimer(int i)
	{
		fedTimer = i;
	}
	
	@Override
	public DataParameter<Boolean> getInteractedParam()
	{
		return INTERACTED;
	}

	@Override
	public int getWaterTimer()
	{
		return wateredTimer;
	}

	@Override
	public void setWaterTimer(int i)
	{
		wateredTimer = i;
	}

	@Override
	public int getDamageTimer()
	{
		return damageTimer;
	}

	@Override
	public void setDamageTimer(int i)
	{
		damageTimer = i;
	}
	
	@Override
	public int getHappyTimer()
	{
		return happyTimer;
	}
	
	@Override
	public void setHappyTimer(int i)
	{
		happyTimer = i;
	}
	
	@Override
	public AnimaniaType getAnimalType()
	{
		return HamsterType.STANDARD;
	}

	@Override
	public DataParameter<Boolean> getHandFedParam()
	{
		return null;
	}

	@Override
	public DataParameter<Boolean> getFedParam()
	{
		return FED;
	}

}
