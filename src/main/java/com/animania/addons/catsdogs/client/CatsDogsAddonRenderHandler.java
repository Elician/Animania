package com.animania.addons.catsdogs.client;

import com.animania.Animania;
import com.animania.addons.catsdogs.client.render.cats.RenderTomRagdoll;
import com.animania.addons.catsdogs.common.entity.cats.EntityTomRagdoll;
import com.leviathanstudio.craftstudio.client.registry.CraftStudioLoader;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CatsDogsAddonRenderHandler
{

	
	public static void preInit()
	{
		//registerCraftStudioAssets();
		renderEntitiesFactory();
		
	}

	/**
	 * Render TileEntities
	 */
	public static void init()
	{
		
	}
	
	@CraftStudioLoader
	public static void registerCraftStudioAssets()
	{
		//CSRegistryHelper csRegistry = new CSRegistryHelper(Animania.MODID);
		//csRegistry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "model_ragdoll");
		
	}
	
	@SideOnly(Side.CLIENT)
	static void renderEntitiesFactory()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityTomRagdoll.class, RenderTomRagdoll.FACTORY);
		
	}

	@SideOnly(Side.CLIENT)
	private static void register(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private static void register(Item item, String name, int meta)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Animania.MODID + ":" + name, "inventory"));
	}

	@SideOnly(Side.CLIENT)

	private static void registerColored(Item item, String name)
	{
		for (int meta = 0; meta < 16; meta++)
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Animania.MODID + ":" + name + "_" + EnumDyeColor.byDyeDamage(meta).getName(), "inventory"));
	}
	
	@SideOnly(Side.CLIENT)
    private static <T extends Entity> void registerEntityRender(Class<T> entityClass, IRenderFactory<? super T> renderFactory)
	{
		RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends TileEntity> void registerTileEntityRender(Class<T> tileEntityClass, TileEntitySpecialRenderer<? super T> specialRenderer)
	{
		 ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
	}

}
