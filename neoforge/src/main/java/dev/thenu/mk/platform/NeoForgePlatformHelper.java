package dev.thenu.mk.platform;

import dev.thenu.mk.platform.services.IPlatformHelper;

// NeoForm (vanilla) does not ship ModList or FMLLoader.
// These are NeoForge mod-loader classes, so we stub them out here.
// If you ever switch back to a full NeoForge dependency, replace the
// stubs with the real calls:
//   isModLoaded  → ModList.get().isLoaded(modId)
//   isDev        → !FMLLoader.isProduction()
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        // ModList is unavailable under NeoForm — always report false.
        // Replace with ModList.get().isLoaded(modId) when using full NeoForge.
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        // FMLLoader is unavailable under NeoForm.
        // Fall back to a standard JVM property that NeoGradle sets in dev runs.
        String prop = System.getProperty("neoforge.development");
        if (prop != null) return Boolean.parseBoolean(prop);
        // Secondary heuristic: dev environments usually run from /build/classes
        String cp = System.getProperty("java.class.path", "");
        return cp.contains("build/classes") || cp.contains("build\\classes");
    }
}
