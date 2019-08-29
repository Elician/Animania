package com.animania.common.entities.pigs;

import net.minecraft.world.World;

public class PigDuroc
{

	public static class EntitySowDuroc extends EntitySowBase
	{
	
		public EntitySowDuroc(World world)
		{
			super(world);
			this.pigType = PigType.DUROC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 9399147;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 6896443;
		}
	
	}

	public static class EntityPigletDuroc extends EntityPigletBase
	{
	
		public EntityPigletDuroc(World world)
		{
			super(world);
			this.pigType = PigType.DUROC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 9399147;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 6896443;
		}
	
	}

	public static class EntityHogDuroc extends EntityHogBase
	{
	
		public EntityHogDuroc(World world)
		{
			super(world);
			this.pigType = PigType.DUROC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 9399147;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 6896443;
		}
	
	}

}
