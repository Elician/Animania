package com.animania.common.entities.peacocks;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PeafowlWhite
{

	public static class EntityPeachickWhite extends EntityPeachickBase
	{
	
		public EntityPeachickWhite(World worldIn)
		{
			super(worldIn);
			this.type = PeacockType.WHITE;
			this.resourceLocation = new ResourceLocation("animania:textures/entity/peacocks/peachick_white.png");
			this.resourceLocationBlink = new ResourceLocation("animania:textures/entity/peacocks/peachick_blink.png");
			this.lidCol = 0xCCCCCC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 15658734;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 13421772;
		}
	}

	public static class EntityPeacockWhite extends EntityPeacockBase
	{
	
		public EntityPeacockWhite(World worldIn)
		{
			super(worldIn);
			this.type = PeacockType.WHITE;
			this.resourceLocation = new ResourceLocation("animania:textures/entity/peacocks/peacock_white.png");
			this.resourceLocationBlink = new ResourceLocation("animania:textures/entity/peacocks/peacock_white_blink.png");
			this.lidCol = 0xCCCCCC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 15658734;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 13421772;
		}
	}

	public static class EntityPeafowlWhite extends EntityPeafowlBase
	{
	
		public EntityPeafowlWhite(World worldIn)
		{
			super(worldIn);
			this.type = PeacockType.WHITE;
			this.resourceLocation = new ResourceLocation("animania:textures/entity/peacocks/peafowl_white.png");
			this.resourceLocationBlink = new ResourceLocation("animania:textures/entity/peacocks/peafowl_white_blink.png");
			this.lidCol = 0xCCCCCC;
		}
		
		@Override
		public int getPrimaryEggColor()
		{
			return 15658734;
		}
		
		@Override
		public int getSecondaryEggColor()
		{
			return 13421772;
		}
	}

}
