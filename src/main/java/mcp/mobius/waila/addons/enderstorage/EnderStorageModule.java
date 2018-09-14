package mcp.mobius.waila.addons.enderstorage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class EnderStorageModule {
	public static Class TileFrequencyOwner = null;
	public static Field TileFrequencyOwner_Freq = null;
	public static Field TileFrequencyOwner_Owner = null;
	public static Class EnderStorageManager = null;
	public static Method GetColourFromFreq = null;
	public static Class TileEnderTank = null;

	public static void register() {
		try {
			final Class e = Class.forName("codechicken.enderstorage.EnderStorage");
			Waila.log.log(Level.INFO, "EnderStorage mod found.");
		} catch (final ClassNotFoundException arg4) {
			Waila.log.log(Level.INFO, "[EnderStorage] EnderStorage mod not found.");
			return;

		}

		try {
			TileFrequencyOwner = Class.forName("codechicken.enderstorage.common.TileFrequencyOwner");
			TileFrequencyOwner_Freq = TileFrequencyOwner.getField("freq");
			TileFrequencyOwner_Owner = TileFrequencyOwner.getField("owner");

			EnderStorageManager = Class.forName("codechicken.enderstorage.api.EnderStorageManager");
			GetColourFromFreq = EnderStorageManager.getDeclaredMethod("getColourFromFreq", new Class[] { Integer.TYPE, Integer.TYPE });

			TileEnderTank = Class.forName("codechicken.enderstorage.storage.liquid.TileEnderTank");

		} catch (final ClassNotFoundException arg0) {
			Waila.log.log(Level.WARN, "[EnderStorage] Class not found. "+arg0);
			return;
		} catch (final NoSuchMethodException arg1) {
			Waila.log.log(Level.WARN, "[EnderStorage] Method not found."+arg1);
			return;
		} catch (final NoSuchFieldException arg2) {
			Waila.log.log(Level.WARN, "[EnderStorage] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Waila.log.log(Level.WARN, "[EnderStorage] Unhandled exception."+arg3);
			return;
		}

		ModuleRegistrar.instance().addConfig("EnderStorage", "enderstorage.colors");
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerStorage(), TileFrequencyOwner);
		ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerStorage(), TileFrequencyOwner);
	}
}