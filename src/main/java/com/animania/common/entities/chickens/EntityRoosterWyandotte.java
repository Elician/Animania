package com.animania.common.entities.chickens;

import com.animania.common.handler.ItemHandler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityRoosterWyandotte extends EntityRoosterBase
{

	public EntityRoosterWyandotte(World worldIn)
	{
		super(worldIn);
		this.type = ChickenType.WYANDOTTE;
		this.resourceLocation = new ResourceLocation("animania:textures/entity/chickens/rooster_brown.png");
		this.resourceLocationBlink = new ResourceLocation("animania:textures/entity/chickens/chicken_blink.png");
		this.oldDropRaw = ItemHandler.rawWyandotteChicken;
		this.oldDropCooked = ItemHandler.cookedWyandotteChicken;
		this.dropRaw = ItemHandler.rawPrimeChicken;
		this.dropCooked = ItemHandler.cookedPrimeChicken;
		this.lidCol = 0x362018;
	}
	
	@Override
	public int getPrimaryEggColor()
	{
		return 8219743;
	}
	
	@Override
	public int getSecondaryEggColor()
	{
		return 5129532;
	}
}