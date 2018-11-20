package com.animania.common.entities.generic.ai;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class GenericAISearchBlock extends EntityAIBase
{
	protected final EntityCreature creature;
	protected final double movementSpeed;
	protected BlockPos destinationBlock = NO_POS;
	private boolean isAtDestination;
	protected final int searchRange;
	protected World world;	
	protected List<EnumFacing> destinationOffset;
	protected BlockPos seekingBlockPos = NO_POS;

	protected BlockPos oldBlockPos = NO_POS;
	private boolean hasSecondary;
	private int walkTries = 0;
	private boolean isDone = false;
	private Set<BlockPos> nonValidPositions = new HashSet<BlockPos>();
	
	public static final BlockPos NO_POS = new BlockPos(-1, -1, -1);

	public GenericAISearchBlock(EntityCreature creature, double speedIn, int range, boolean hasSecondary, EnumFacing... destinationOffset)
	{
		this.creature = creature;
		this.movementSpeed = speedIn;
		this.searchRange = range;
		this.destinationOffset = new ArrayList<EnumFacing>();
		for (EnumFacing f : destinationOffset)
			this.destinationOffset.add(f);
		this.world = creature.world;
		this.hasSecondary = hasSecondary;
		// this.setMutexBits(5);
	}
	
	public GenericAISearchBlock(EntityCreature creature, double speedIn, int range, EnumFacing... destinationOffset)
	{
		this(creature, speedIn, range, false, destinationOffset);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if (this.seekingBlockPos == NO_POS)
			return this.searchForDestination();
		
		return false;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting()
	{
		return destinationBlock != NO_POS && seekingBlockPos != NO_POS && !isDone && (this.shouldMoveTo(this.creature.world, this.seekingBlockPos) || (this.hasSecondary ? this.shouldMoveToSecondary(world, seekingBlockPos) : false));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.creature.getNavigator().tryMoveToXYZ((double) ((float) this.destinationBlock.getX()) + 0.5D, (double) (this.destinationBlock.getY()), (double) ((float) this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
		this.walkTries = 0;
	}

	@Override
	public void resetTask()
	{
		this.isAtDestination = false;
		this.destinationBlock = NO_POS;
		this.seekingBlockPos = NO_POS;
		this.walkTries = 0;
		this.isDone = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask()
	{
		if (!shouldContinueExecuting())
			this.resetTask();
	
		if (!this.destinationBlock.equals(NO_POS))
		{
			double distance = this.creature.getDistanceSqToCenter(this.destinationBlock);

			if (distance > 1.95D)
			{
				this.isAtDestination = false;
				this.walkTries++;

				boolean isStandingStill = this.creature.prevPosX == this.creature.posX && this.creature.prevPosY == this.creature.posY && this.creature.prevPosZ == this.creature.posZ;
				
				if (this.walkTries % 40 == 0 || isStandingStill)
				{
					this.creature.getNavigator().tryMoveToXYZ((double) ((float) this.destinationBlock.getX()) + 0.5D, (double) (this.destinationBlock.getY()), (double) ((float) this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
					this.creature.getLookHelper().setLookPosition((double) this.seekingBlockPos.getX() + 0.5D, (double) (this.seekingBlockPos.getY()), (double) this.seekingBlockPos.getZ() + 0.5D, 10.0F, (float) this.creature.getVerticalFaceSpeed());
				}					
				
				if(isStandingStill && this.walkTries > 100) 
				{
					this.nonValidPositions.add(seekingBlockPos);
					this.resetTask();
					this.searchForDestination();
				}
			}
			else
			{
				this.isAtDestination = true;
				this.walkTries = 0;
			}
			
			if(this.isAtDestination)
			{
				this.nonValidPositions.clear();
				this.isDone = true;
			}
		}
	}

	protected boolean isAtDestination()
	{
		return this.isAtDestination;
	}
	
	public void onArriveAtDestination()
	{
		this.nonValidPositions.clear();
	}

	@Override
	public boolean isInterruptible()
	{
		return true;
	}

	/**
	 * Searches and sets new destination block and returns true if a suitable
	 * block (specified in
	 * {@link net.minecraft.entity.ai.EntityAIMoveToBlock#shouldMoveTo(World, BlockPos)
	 * EntityAIMoveToBlock#shouldMoveTo(World, BlockPos)}) can be found.
	 */
	protected boolean searchForDestination()
	{
		BlockPos blockpos = new BlockPos(this.creature);

		if (blockpos.equals(oldBlockPos))
			return false;
		oldBlockPos = blockpos;

		int i = this.searchRange;

		for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k)
		{
			for (int l = 0; l < i; ++l)
			{
				for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1)
				{
					for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1)
					{
						BlockPos blockpos1 = blockpos.add(i1, k - 1, j1);

						if (this.shouldMoveTo(this.creature.world, blockpos1) && !this.nonValidPositions.contains(blockpos1))
						{
							Collections.shuffle(destinationOffset);

							for (EnumFacing facing : destinationOffset)
							{
								BlockPos offsetPos = blockpos1.offset(facing);

								AxisAlignedBB aabb = world.getBlockState(blockpos1).getCollisionBoundingBox(world, blockpos1);

								if (aabb == Block.NULL_AABB)
									offsetPos = blockpos1;

								if (this.creature.getNavigator().getPathToXYZ(offsetPos.getX() + 0.5, offsetPos.getY(), offsetPos.getZ() + 0.5) != null)
								{
									this.destinationBlock = offsetPos;
									this.seekingBlockPos = blockpos1;
									return true;
								}
							}
							
							if(destinationOffset.isEmpty())
							{
								if (this.creature.getNavigator().getPathToXYZ(blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5) != null)
								{
									this.destinationBlock = blockpos1;
									this.seekingBlockPos = blockpos1;
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		if(this.hasSecondary)
		{
			for (int k = 0; k <= 1; k = k > 0 ? -k : 1 - k)
			{
				for (int l = 0; l < i; ++l)
				{
					for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1)
					{
						for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1)
						{
							BlockPos blockpos1 = blockpos.add(i1, k - 1, j1);

							if (this.shouldMoveToSecondary(this.creature.world, blockpos1) && !this.nonValidPositions.contains(blockpos1))
							{
								Collections.shuffle(destinationOffset);

								for (EnumFacing facing : destinationOffset)
								{
									BlockPos offsetPos = blockpos1.offset(facing);

									AxisAlignedBB aabb = world.getBlockState(blockpos1).getCollisionBoundingBox(world, blockpos1);

									if (aabb == Block.NULL_AABB)
										offsetPos = blockpos1;

									if (this.creature.getNavigator().getPathToXYZ(offsetPos.getX() + 0.5, offsetPos.getY(), offsetPos.getZ() + 0.5) != null)
									{
										this.destinationBlock = offsetPos;
										this.seekingBlockPos = blockpos1;
										return true;
									}
								}
								
								if(destinationOffset.isEmpty())
								{
									if (this.creature.getNavigator().getPathToXYZ(blockpos1.getX() + 0.5, blockpos1.getY(), blockpos1.getZ() + 0.5) != null)
									{
										this.destinationBlock = blockpos1;
										this.seekingBlockPos = blockpos1;
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	protected boolean pathExists(BlockPos start, BlockPos end)
	{
		try
		{
			PathPoint startPoint = new PathPoint(start.getX(), start.getY(), start.getZ());
			PathPoint endPoint = new PathPoint(end.getX(), end.getY(), end.getZ());

			PathNavigate navigate = this.creature.getNavigator();
			Method getPathFinder = ReflectionHelper.findMethod(PathNavigate.class, "getPathFinder", "func_179679_a");
			Method getPath = ReflectionHelper.findMethod(PathFinder.class, "findPath", "func_186336_a", PathPoint.class, PathPoint.class, float.class);

			getPathFinder.setAccessible(true);
			getPath.setAccessible(true);

			PathFinder finder = (PathFinder) getPathFinder.invoke(navigate);
			Path p = (Path) getPath.invoke(finder, startPoint, endPoint, (float) searchRange);

			return p != null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Return true to set given position as destination
	 */
	protected abstract boolean shouldMoveTo(World worldIn, BlockPos pos);
	
	protected boolean shouldMoveToSecondary(World worldIn, BlockPos pos)
	{
		return false;
	}

}