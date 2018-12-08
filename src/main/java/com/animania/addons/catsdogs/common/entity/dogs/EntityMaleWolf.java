package com.animania.addons.catsdogs.common.entity.dogs;

import net.minecraft.world.World;

public class EntityMaleWolf extends EntityMaleDogBase
{

	public EntityMaleWolf(World world)
	{
		super(world);
		this.type = DogType.WOLF;
	}
	
	@Override
	public int getPrimaryEggColor()
	{
		return -4409680;
	}
	
	@Override
	public int getSecondaryEggColor()
	{
		return -13488852;
	}
	
	@Override
	public int getVariantCount()
	{
		return 8;
	}
}
