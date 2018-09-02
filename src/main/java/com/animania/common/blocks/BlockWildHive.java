package com.animania.common.blocks;

import com.animania.Animania;
import com.animania.common.handler.BlockHandler;
import com.animania.common.tileentities.TileEntityHive;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockWildHive extends BlockContainer
{
	private String name = "block_wild_hive";
	public static final PropertyDirection FACING = BlockDirectional.FACING;
	
	public BlockWildHive()
	{
		super(Material.LEAVES, MapColor.YELLOW);
		this.setRegistryName(new ResourceLocation(Animania.MODID, this.name));
		this.setUnlocalizedName(Animania.MODID + "_" + this.name);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(Animania.TabAnimaniaResources);
		this.setHardness(1.3f);
		this.setResistance(0.3f);
		BlockHandler.blocks.add(this);
		Item item = new ItemBlock(this);
		item.setRegistryName(new ResourceLocation(Animania.MODID, "wild_hive"));
		ForgeRegistries.ITEMS.register(item);
		
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{

		ItemStack heldItem = player.getHeldItem(hand);
		TileEntityHive hive = (TileEntityHive) world.getTileEntity(pos);

		if (hive != null)
		{
			// EMPTY LIQUIDS
			if (!heldItem.isEmpty() && FluidUtil.getFluidHandler(heldItem) != null && FluidUtil.getFluidContained(heldItem) == null && hive.fluidHandler.getFluid() != null && hive.fluidHandler.getFluid().amount >= 1000)
			{
				FluidStack fluidStack = hive.fluidHandler.drain(1000, true);
				if (!player.isCreative())
				{
					IFluidHandlerItem handler;
					if (heldItem.getCount() >= 1)
					{
						ItemStack heldItem1 = heldItem.copy();
						heldItem1.setCount(1);
						handler = FluidUtil.getFluidHandler(heldItem1);

						handler.fill(fluidStack, true);
						ItemStack newstack = handler.getContainer();

						if (heldItem.getCount() > 1)
						{
							heldItem.shrink(1);
							player.inventory.addItemStackToInventory(newstack);
						}
						else
							player.setHeldItem(hand, newstack);

						return true;
					}

				}

				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 0.6F, 0.8F);

				return true;

			}

			if (!world.isRemote && player.isSneaking() && hand == EnumHand.MAIN_HAND)
			{
				int honey = hive.fluidHandler.getFluidAmount();

				player.sendStatusMessage(new TextComponentString("Honey stored: " + honey + "mB"), true);
			}

		}
		
		return true;
	}

	// Yeah, I know it's ugly, but it's the only way that it works... //Looks like Purp code :)
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		switch (meta)
		{
		case 0:
			return getDefaultState().withProperty(FACING, EnumFacing.NORTH);
		case 1:
			return getDefaultState().withProperty(FACING, EnumFacing.EAST);
		case 2:
			return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
		case 3:
			return getDefaultState().withProperty(FACING, EnumFacing.WEST);
		case 4:
			return getDefaultState().withProperty(FACING, EnumFacing.NORTH);
		default:
			return getDefaultState().withProperty(FACING, EnumFacing.NORTH);
		}

	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int rot = 0;

		switch (state.getValue(FACING))
		{
		case NORTH:
			rot = 2;
			break;
		case EAST:
			rot = 1;
			break;
		case SOUTH:
			rot = 3;
			break;
		case WEST:
			rot = 4;
			break;
		default:
			break;
		}
		return rot;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityHive();
	}

}
