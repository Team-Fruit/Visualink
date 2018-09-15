package net.teamfruit.visualink.addons.jabba;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import net.teamfruit.visualink.Log;

public class JABBAModuleServer {
	public static Class<?> BSpaceStorageHandler = null;
	public static Method BSpaceStorageHandler_Instance = null;

	public static void register() {
		if (!JABBAModule.installed)
			return;

		try {
			BSpaceStorageHandler = Class.forName("mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler");
			BSpaceStorageHandler_Instance = BSpaceStorageHandler.getMethod("instance");
		} catch (final ClassNotFoundException arg0) {
			Log.log.log(Level.WARN, "[JABBA] Class not found. "+arg0);
			return;
		} catch (final NoSuchMethodException arg1) {
			Log.log.log(Level.WARN, "[JABBA] Method not found."+arg1);
			return;
		} catch (final Exception arg3) {
			Log.log.log(Level.WARN, "[JABBA] Unhandled exception."+arg3);
			return;
		}
	}
}
