package com.evacipated.cardcrawl.modthespire;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.evacipated.cardcrawl.modthespire.steam.SteamWorkshopRunner;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.vdurmont.semver4j.Semver;
import javassist.ClassPath;
import javassist.ClassPool;
import org.objectweb.asm.ClassReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ModTheSpire
{
    public static boolean DEBUG = false;
    public static boolean OUT_JAR = false;
    public static boolean PACKAGE = false;
    public static boolean CLOSE_WHEN_FINISHED = false;

    public static Semver MTS_VERSION;
    public static String MOD_DIR = "mods/";
    private static String BETA_SUBDIR = "beta/";
    public static String STS_JAR = "desktop-1.0.jar";
    private static String MAC_STS_JAR = "SlayTheSpire.app/Contents/Resources/" + STS_JAR;
    private static String STS_JAR2 = "SlayTheSpire.jar";
    public static String COREPATCHES_JAR = "/corepatches.jar";
    private static final String COREPATCHES_LWJGL3_JAR = "/corepatches-lwjgl3.jar";
    static String KOTLIN_JAR = "/kotlin.jar";
    static String LWJGL3_JAR = "/lwjgl3.jar";
    public static String STS_PATCHED_JAR = "desktop-1.0-patched.jar";
    public static String JRE_51_DIR = "jre1.8.0_51";
    public static ModInfo[] MODINFOS;
    static ModInfo[] ALLMODINFOS;
    private static ClassPool POOL;
    private static List<SteamSearch.WorkshopInfo> WORKSHOP_INFOS;

    public static SpireConfig MTS_CONFIG;
    public static String STS_VERSION = null;
    public static boolean STS_BETA = false;
    public static boolean allowBeta = false;
    public static String profileArg = null;
    public static List<String> manualModIds = null;

    public static String[] ARGS;
    public static boolean SKIP_INTRO = false;
    public static boolean LWJGL3_ENABLED = false;
    public static boolean AGENT_ENABLED = false;
    private static ModSelectWindow ex;

    private static final List<URL> extraJars = new ArrayList<>();

    public static boolean isModLoaded(String modID)
    {
        for (int i=0; i<MODINFOS.length; ++i) {
            if (modID.equals(MODINFOS[i].ID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isModSideloaded(String modID)
    {
        modID = "__sideload_" + modID;
        for (int i=0; i<MODINFOS.length; ++i) {
            if (modID.equals(MODINFOS[i].ID)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isModLoadedOrSideloaded(String modID)
    {
        return isModLoaded(modID) || isModSideloaded(modID);
    }

    public static ClassPool getClassPool()
    {
        return POOL;
    }

    public static List<SteamSearch.WorkshopInfo> getWorkshopInfos()
    {
        return WORKSHOP_INFOS;
    }

    public static boolean isAchievementsEnabled()
    {
        return MTS_CONFIG.getBool("achievements", false);
    }

    public static void main(String[] args)
    {
        List<String> argList = Arrays.asList(args);

        // Restart MTS if jre1.8.0_51 is detected
        // For those people with old laptops and OpenGL problems
        if (!argList.contains("--jre51") && new File(JRE_51_DIR).exists()) {
            System.out.println("JRE 51 exists, restarting using it...");
            try {
                String path = ModTheSpire.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                path = URLDecoder.decode(path, "utf-8");
                path = new File(path).getPath();

                String[] newArgs = new String[args.length + 4];
                newArgs[0] = SteamSearch.findJRE51();
                newArgs[1] = "-jar";
                newArgs[2] = path;
                newArgs[3] = "--jre51";
                System.arraycopy(args, 0, newArgs, 4, args.length);
                ProcessBuilder pb = new ProcessBuilder(
                    newArgs
                );
                pb.redirectOutput(new File("sendToDevs", "mts_process_launch.log"));
                pb.redirectErrorStream(true);
                pb.start();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(3);
            }
        } else if (argList.contains("--jre51")) {
            System.out.println("Launched using JRE 51");
        }

        ARGS = args;
        try {
            Properties defaults = new Properties();
            defaults.setProperty("debug", Boolean.toString(false));
            defaults.setProperty("out-jar", Boolean.toString(false));
            defaults.setProperty("package", Boolean.toString(false));
            defaults.setProperty("close-when-finished", Boolean.toString(false));
            defaults.setProperty("allow-beta", Boolean.toString(true));
            defaults.setProperty("skip-launcher", Boolean.toString(false));
            defaults.setProperty("skip-intro", Boolean.toString(false));
            defaults.setProperty("mods", "");
            defaults.putAll(ModSelectWindow.getDefaults());
            MTS_CONFIG = new SpireConfig(null, "ModTheSpire", defaults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DEBUG = MTS_CONFIG.getBool("debug");
        OUT_JAR = MTS_CONFIG.getBool("out-jar");
        PACKAGE = MTS_CONFIG.getBool("package");
        CLOSE_WHEN_FINISHED = MTS_CONFIG.getBool("close-when-finished");
        allowBeta = MTS_CONFIG.getBool("allow-beta");
        boolean skipLauncher = MTS_CONFIG.getBool("skip-launcher");
        SKIP_INTRO = MTS_CONFIG.getBool("skip-intro");
        profileArg = MTS_CONFIG.getString("mod-list");
        if (profileArg == null) {
            profileArg = MTS_CONFIG.getString("profile");
        }
        String modIds = MTS_CONFIG.getString("mods");
        if (!LWJGL3_ENABLED) {
            LWJGL3_ENABLED = MTS_CONFIG.getBool("imgui");
        }

        if (argList.contains("--debug")) {
            DEBUG = true;
        }
        
        if (argList.contains("--out-jar")) {
            OUT_JAR = true;
        }
        if (argList.contains("--package")) {
            PACKAGE = true;
        }
        if (argList.contains("--close-when-finished")) {
            CLOSE_WHEN_FINISHED = true;
        }

        if (argList.contains("--allow-beta")) {
            allowBeta = true;
        }

        if (argList.contains("--skip-launcher")) {
            skipLauncher = true;
        }
        if (argList.contains("--skip-intro")) {
            SKIP_INTRO = true;
        }
        if (argList.contains("--imgui")) {
            LWJGL3_ENABLED = true;
        }

        int profileArgIndex = Math.max(argList.indexOf("--profile"), argList.indexOf("--mod-list"));
        if (profileArgIndex >= 0 && argList.size() > profileArgIndex + 1) {
            profileArg = argList.get(profileArgIndex+1);
        }

        int modIdsIndex = argList.indexOf("--mods");
        if (modIdsIndex >= 0 && argList.size() > modIdsIndex + 1) {
            modIds = argList.get(modIdsIndex+1);
        }
        if (!modIds.isEmpty()) {
            manualModIds = Arrays.asList(modIds.split(","));
            profileArg = null;
            skipLauncher = true;
        }

        loadMTSVersion();

        // Check if we are desktop-1.0.jar
        try {
            String thisJarName = new File(ModTheSpire.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName();
            if (thisJarName.equals(STS_JAR)) {
                STS_JAR = STS_JAR2;
            }
        } catch (URISyntaxException e) {
            // NOP
        }
        // Check that desktop-1.0.jar exists
        {
            File tmp = new File(STS_JAR);
            if (!tmp.exists()) {
                // Search for Steam install
                String steamJar = SteamSearch.findDesktopJar();
                if (steamJar != null && new File(steamJar).exists()) {
                    STS_JAR = steamJar;
                } else {
                    // Check if for the Mac version
                    tmp = new File(MAC_STS_JAR);
                    checkFileInfo(tmp);
                    if (!tmp.exists()) {
                        checkFileInfo(new File("SlayTheSpire.app"));
                        checkFileInfo(new File("SlayTheSpire.app/Contents"));
                        checkFileInfo(new File("SlayTheSpire.app/Contents/Resources"));

                        JOptionPane.showMessageDialog(null, "Unable to find '" + STS_JAR + "'");
                        return;
                    } else {
                        System.out.println("Using Mac version at: " + MAC_STS_JAR);
                        STS_JAR = MAC_STS_JAR;
                    }
                }
            }
        }

        List<SteamSearch.WorkshopInfo> workshopInfos = SteamWorkshopRunner.findWorkshopInfos();
        System.out.println("Got " + workshopInfos.size() + " workshop items");

        convertOldWorkshopInfoFiles(workshopInfos);

        try {
            List<SteamSearch.WorkshopInfo> oldWorkshopInfos = null;
            String path = SpireConfig.makeFilePath(null, "WorkshopInfo", "json");
            if (new File(path).isFile()) {
                String data = new String(Files.readAllBytes(Paths.get(path)));
                Gson gson = new Gson();
                Type type = new TypeToken<List<SteamSearch.WorkshopInfo>>(){}.getType();
                try {
                    oldWorkshopInfos = gson.fromJson(data, type);
                } catch (JsonSyntaxException ignore) {
                }
            }
            if (oldWorkshopInfos == null) {
                oldWorkshopInfos = new ArrayList<>();
            }

            for (SteamSearch.WorkshopInfo info : workshopInfos) {
                if (info == null) {
                    continue;
                }
                int savedTime = oldWorkshopInfos.stream()
                    .filter(x -> Objects.equals(info.getID(), x.getID()))
                    .findFirst()
                    .map(SteamSearch.WorkshopInfo::getTimeUpdated)
                    .orElse(0);
                if (savedTime < info.getTimeUpdated()) {
                    if (savedTime != 0) {
                        System.out.println(info.getTitle() + " WAS UPDATED!");
                    }
                }
            }

            if (!workshopInfos.isEmpty()) {
                // if steam, save this workshop info
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String data = gson.toJson(workshopInfos);
                Files.write(Paths.get(SpireConfig.makeFilePath(null, "WorkshopInfo", "json")), data.getBytes());
            } else {
                // if no steam, use saved workshop info
                workshopInfos = oldWorkshopInfos;
            }
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }

        WORKSHOP_INFOS = workshopInfos;

        findGameVersion();

        LauncherPatch.patch();

        final boolean finalSkipLauncher = skipLauncher;
        EventQueue.invokeLater(() -> {
            ALLMODINFOS = getAllMods(getWorkshopInfos());
            ex = new ModSelectWindow(ALLMODINFOS, finalSkipLauncher);
            ex.setVisible(true);

            ex.warnAboutMissingVersions();

            String java_version = System.getProperty("java.version");
            if (!java_version.startsWith("1.8")) {
                String msg = "ModTheSpire requires Java version 8 to run properly.\nYou are currently using Java " + java_version;
                JOptionPane.showMessageDialog(null, msg, "Warning", JOptionPane.WARNING_MESSAGE);
            }

            ex.startCheckingForMTSUpdate();
        });
    }

    public static void closeWindow()
    {
        ex.dispatchEvent(new WindowEvent(ex, WindowEvent.WINDOW_CLOSING));
    }

    public static void restoreWindowOnCrash()
    {
        ex.setState(Frame.NORMAL);
        ex.setVisible(true);
        ex.toFront();
        ex.requestFocus();
    }

    // runMods - sets up the ClassLoader, sets the isModded flag and launches the game
    public static void runMods(List<String> modIds)
    {
        if (ModTheSpire.DEBUG) {
            System.out.println("Running with debug mode turned ON...");
            System.out.println();
        }
        try {
            {
                if (manualModIds != null) {
                    modIds = manualModIds;
                }
                ModInfo[] modInfos = buildInfoArray(modIds);
                checkDependencies(modInfos);
                modInfos = orderDependencies(modInfos);
                MODINFOS = modInfos;
            }

            printMTSInfo(System.out);

            MTSAgentLoader.loadAgent();

            unpackJar(KOTLIN_JAR);
            if (LWJGL3_ENABLED) {
                COREPATCHES_JAR = COREPATCHES_LWJGL3_JAR;
                unpackJar(LWJGL3_JAR);
            }

            MTSClassLoader loader = new MTSClassLoader(ModTheSpire.class.getResourceAsStream(COREPATCHES_JAR), buildUrlArray(MODINFOS), ModTheSpire.class.getClassLoader());

            if (modIds.size() > 0) {
                MTSClassLoader tmpPatchingLoader = new MTSClassLoader(ModTheSpire.class.getResourceAsStream(COREPATCHES_JAR), buildUrlArray(MODINFOS), ModTheSpire.class.getClassLoader());

                System.out.println("Begin patching...");
                MTSClassPool pool = new MTSClassPool(tmpPatchingLoader);

                MODINFOS = Patcher.sideloadMods(tmpPatchingLoader, loader, pool, ALLMODINFOS, MODINFOS);

                // Patch enums
                System.out.printf("Patching enums...");
                Patcher.patchEnums(tmpPatchingLoader, pool, ModTheSpire.class.getResource(ModTheSpire.COREPATCHES_JAR));
                // Patch SpireEnums from mods
                Patcher.patchEnums(tmpPatchingLoader, pool, MODINFOS);
                System.out.println("Done.");

                // Find and inject core patches
                System.out.println("Finding core patches...");
                Patcher.injectPatches(tmpPatchingLoader, pool, Patcher.findPatches(new URL[]{ModTheSpire.class.getResource(ModTheSpire.COREPATCHES_JAR)}));
                // Find and inject mod patches
                System.out.println("Finding patches...");
                Patcher.injectPatches(tmpPatchingLoader, pool, Patcher.findPatches(MODINFOS));

                Patcher.patchOverrides(tmpPatchingLoader, pool, MODINFOS);

                Patcher.finalizePatches(tmpPatchingLoader, pool);

                ClassPath cp = Patcher.compilePatches(loader, pool);

                tmpPatchingLoader.close();

                pool.resetClassLoader(loader);
                pool.insertClassPath(cp);
                POOL = pool;
                POOL.childFirstLookup = true;

                // Bust enums
                System.out.printf("Busting enums...");
                Patcher.bustEnums(loader, ModTheSpire.class.getResource(ModTheSpire.COREPATCHES_JAR));
                // Bust SpireEnums from mods
                Patcher.bustEnums(loader, MODINFOS);
                System.out.println("Done.");
                System.out.println();

                // Create pre-modded JAR
                if (ModTheSpire.PACKAGE) {
                    System.out.println("Creating prepackaged JAR...");
                    PackageJar.packageJar(pool, "desktop-1.0-modded.jar");
                    System.out.println("Done.");
                    return;
                }
                // Output JAR if requested
                if (ModTheSpire.OUT_JAR) {
                    System.out.printf("Dumping JAR...");
                    OutJar.dumpJar(pool, STS_PATCHED_JAR);
                    System.out.println("Done.");
                    return;
                }

                // Set Settings.isModded = true
                System.out.printf("Setting isModded = true...");
                System.out.flush();
                Class<?> Settings = loader.loadClass("com.megacrit.cardcrawl.core.Settings");
                Field isModded = Settings.getDeclaredField("isModded");
                isModded.set(null, true);
                System.out.println("Done.");
                System.out.println();

                Field isDev = Settings.getDeclaredField("isDev");
                isDev.set(null, false);

                // Add ModTheSpire section to CardCrawlGame.VERSION_NUM
                System.out.printf("Adding ModTheSpire to version...");
                System.out.flush();
                Class<?> CardCrawlGame = loader.loadClass("com.megacrit.cardcrawl.core.CardCrawlGame");
                Field VERSION_NUM = CardCrawlGame.getDeclaredField("VERSION_NUM");
                String oldVersion = (String) VERSION_NUM.get(null);
                VERSION_NUM.set(null, oldVersion + " [ModTheSpire " + MTS_VERSION + "]");
                System.out.println("Done.");
                System.out.println();

                // Initialize any mods that implement SpireInitializer.initialize()
                System.out.println("Initializing mods...");
                Patcher.initializeMods(loader, MODINFOS);
                System.out.println("Done.");
                System.out.println();
            }

            System.out.println("Starting game...");
            Class<?> cls = loader.loadClass("com.megacrit.cardcrawl.desktop.DesktopLauncher");
            Method method = cls.getDeclaredMethod("main", String[].class);
            method.invoke(null, (Object) ARGS);
            if (!DEBUG) {
                new Timer().schedule(
                    new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            ex.setState(Frame.ICONIFIED);
                        }
                    },
                    1000
                );
            }
        } catch (MissingDependencyException e) {
            System.err.println("ERROR: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Missing Dependency", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateModIDException e) {
            System.err.println("ERROR: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Duplicate Mod ID", JOptionPane.ERROR_MESSAGE);
        } catch (MissingModIDException e) {
            System.err.println("ERROR: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "Missing Mod ID", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Manifest getManifest()
    {
        try {
            Enumeration<URL> manifests = ModTheSpire.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (manifests.hasMoreElements()) {
                Manifest manifest = new Manifest(manifests.nextElement().openStream());
                Attributes main = manifest.getMainAttributes();
                if (main == null) continue;
                String title = main.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
                if (Objects.equals(title, "ModTheSpire")) {
                    return manifest;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void loadMTSVersion()
    {
        loadMTSVersion(null);
    }

    static void loadMTSVersion(String suffix)
    {
        Manifest manifest = getManifest();
        if (manifest == null) {
            System.exit(-1);
            return;
        }

        String version = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        if (suffix != null) {
            version += "+" + suffix;
        }
        MTS_VERSION = ModInfo.safeVersion(version);
    }

    public static void setGameVersion(String versionString)
    {
        if (versionString.startsWith("(") && versionString.endsWith(")")) {
            versionString = versionString.substring(1, versionString.length()-1);
        }
        STS_VERSION = versionString;
    }

    private static void findGameVersion()
    {
        try {
            URLClassLoader tmpLoader = new URLClassLoader(new URL[]{new File(STS_JAR).toURI().toURL()});
            // Read CardCrawlGame.VERSION_NUM
            InputStream in = tmpLoader.getResourceAsStream("com/megacrit/cardcrawl/core/CardCrawlGame.class");
            ClassReader classReader = new ClassReader(in);

            classReader.accept(new GameVersionFinder(), 0);

            // Read Settings.isBeta
            InputStream in2 = tmpLoader.getResourceAsStream("com/megacrit/cardcrawl/core/Settings.class");
            ClassReader classReader2 = new ClassReader(in2);

            classReader2.accept(new GameBetaFinder(), 0);

            // Read distributor
            try {
                Properties prop = new Properties();
                prop.load(tmpLoader.getResourceAsStream("build.properties"));
                ModSelectWindow.stsDistributor = prop.getProperty("distributor", "unknown");
            } catch (Exception ignored) {
                ModSelectWindow.stsDistributor = "unknown";
            }

            tmpLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Path unpackAs(String name, Path file) throws IOException
    {
        InputStream input = ModTheSpire.class.getResourceAsStream(name);
        if (input == null) {
            throw new FileNotFoundException(name);
        }
        OutputStream output = new FileOutputStream(file.toFile());

        byte[] buf = new byte[8192];
        int length;
        while ((length = input.read(buf)) > 0) {
            output.write(buf, 0, length);
        }

        output.close();
        input.close();

        return file;
    }

    static Path unpackTo(String name, Path dir) throws IOException
    {
        String filename = Paths.get(name).getFileName().toString();
        Path tmpFile = dir.resolve(filename);

        return unpackAs(name, tmpFile);
    }

    static Path unpack(String name) throws IOException
    {
        return unpackTo(name, getTmpDir());
    }

    static void unpackJar(String name)
    {
        try {
            Path tmpFile = unpack(name);
            extraJars.add(tmpFile.toUri().toURL());
        } catch (Exception e) {
            System.out.println("Failed to unpack " + name);
            e.printStackTrace();
        }
    }

    static Path getTmpDir()
    {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "ModTheSpire");
        File f = tmpDir.toFile();
        if (!f.exists()) {
            f.mkdirs();
        }
        return tmpDir;
    }

    // buildUrlArray - builds the URL array to pass to the ClassLoader
    private static URL[] buildUrlArray(ModInfo[] modInfos) throws MalformedURLException
    {
        List<URL> urls = new ArrayList<>(modInfos.length + 1);

        // Kotlin + maybe Lwjgl3
        urls.addAll(extraJars);

        // Mods
        for (ModInfo modInfo : modInfos) {
            urls.add(modInfo.jarURL);
        }

        // Slay the Spire
        urls.add(new File(STS_JAR).toURI().toURL());

        return urls.toArray(new URL[0]);
    }

    private static ModInfo[] buildInfoArray(List<String> modIds) throws MissingModIDException
    {
        ModInfo[] infos;
        infos = new ModInfo[modIds.size()];
        for (int i = 0; i < modIds.size(); ++i) {
            ModInfo info = null;
            for (ModInfo allInfo : ALLMODINFOS) {
                if (Objects.equals(modIds.get(i), allInfo.ID)) {
                    info = allInfo;
                    break;
                }
            }
            if (info == null) {
                throw new MissingModIDException(modIds.get(i));
            }
            infos[i] = info;
        }
        return infos;
    }

    // getAllModFiles - returns a File array containing all of the JAR files in the mods directory
    private static File[] getAllModFiles(String directory)
    {
        File file = new File(directory);
        if (!file.exists() || !file.isDirectory()) {
            return new File[0];
        }

        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });

        if (files == null || files.length == 0) {
            return new File[0];
        }
        return files;
    }

    private static ModInfo[] getAllMods(List<SteamSearch.WorkshopInfo> workshopInfos)
    {
        List<ModInfo> modInfos = new ArrayList<>();

        // Beta version of mods
        if (STS_BETA) {
            for (File f : getAllModFiles(MOD_DIR + BETA_SUBDIR)) {
                ModInfo info = ModInfo.ReadModInfo(f);
                if (info != null) {
                    if (modInfos.stream().noneMatch(i -> i.ID == null || i.ID.equals(info.ID))) {
                        modInfos.add(info);
                    }
                }
            }
        }
        // "mods/" directory
        for (File f : getAllModFiles(MOD_DIR)) {
            ModInfo info = ModInfo.ReadModInfo(f);
            if (info != null) {
                if (modInfos.stream().noneMatch(i -> i.ID == null || i.ID.equals(info.ID))) {
                    modInfos.add(info);
                }
            }
        }

        TriConsumer<File, SteamSearch.WorkshopInfo, Boolean> lambda = (f, workshopInfo, beta) -> {
            ModInfo info = ModInfo.ReadModInfo(f);
            if (info != null) {
                // Disable the update json url for workshop content
                info.UpdateJSON = null;
                info.isWorkshop = true;
                info.workshopInfo = workshopInfo;

                // If the workshop item is a newer version, use it instead of the local mod
                boolean doAdd = true;
                Iterator<ModInfo> it = modInfos.iterator();
                while (it.hasNext()) {
                    ModInfo modInfo = it.next();
                    if (modInfo.ID != null && modInfo.ID.equals(info.ID)) {
                        if (modInfo.ModVersion == null || info.ModVersion == null) {
                            doAdd = false;
                            break;
                        }
                        if (info.ModVersion.isGreaterThan(modInfo.ModVersion)) {
                            it.remove();
                        } else {
                            modInfo.workshopInfo = info.workshopInfo;
                            doAdd = false;
                            break;
                        }
                    }
                }
                if (doAdd) {
                    modInfos.add(info);
                }
            }
        };
        // Workshop content
        for (SteamSearch.WorkshopInfo workshopInfo : workshopInfos) {
            // Normal
            for (File f : getAllModFiles(workshopInfo.getInstallPath())) {
                lambda.accept(f, workshopInfo, false);
            }
            // Beta
            if (STS_BETA) {
                for (File f : getAllModFiles(Paths.get(workshopInfo.getInstallPath(), BETA_SUBDIR).toString())) {
                    lambda.accept(f, workshopInfo, true);
                }
            }
        }

        modInfos.sort(Comparator.comparing(m -> m.Name.toLowerCase()));

        return modInfos.toArray(new ModInfo[0]);
    }

    public static void printMTSInfo(PrintStream out)
    {
        out.println("Version Info:");
        out.printf(" - Java version (%s)\n", System.getProperty("java.version"));
        out.printf(" - Slay the Spire (%s %s)", STS_VERSION, ModSelectWindow.stsDistributor);
        if (STS_BETA) {
            out.printf(" BETA");
        }
        out.printf("\n");
        out.printf(" - ModTheSpire (%s)\n", MTS_VERSION);
        out.printf("Mod list:\n");
        for (ModInfo info : MODINFOS) {
            out.printf(" - %s", info.getIDName());
            if (info.ModVersion != null) {
                out.printf(" (%s)", info.ModVersion);
            }
            out.println();
        }
        out.println();
    }

    private static void checkDependencies(ModInfo[] modinfos) throws MissingDependencyException, DuplicateModIDException
    {
        Map<String, ModInfo> dependencyMap = new HashMap<>();
        for (final ModInfo info : modinfos) {
            if (info.ID != null) {
                if (!dependencyMap.containsKey(info.ID)) {
                    dependencyMap.put(info.ID, info);
                } else {
                    throw new DuplicateModIDException(dependencyMap.get(info.ID), info);
                }
            }
        }

        for (final ModInfo info : modinfos) {
            for (ModInfo.Dependency dependency : info.Dependencies) {
                boolean has = false;
                for (final ModInfo dependinfo : modinfos) {
                    if (dependinfo.ID != null && dependinfo.ID.equals(dependency.id)) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    throw new MissingDependencyException(info, dependency.id);
                }
            }
        }
    }

    private static int findDependencyIndex(ModInfo[] modInfos, String dependencyID)
    {
        for (int i=0; i<modInfos.length; ++i) {
            if (modInfos[i] != null && modInfos[i].ID != null) {
                if (modInfos[i].ID.equals(dependencyID)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static ModInfo[] orderDependencies(ModInfo[] modInfos) throws CyclicDependencyException
    {
        GraphTS<ModInfo> g = new GraphTS<>();

        for (final ModInfo info : modInfos) {
            g.addVertex(info);
        }

        for (int i=0; i<modInfos.length; ++i) {
            for (ModInfo.Dependency dependency : modInfos[i].Dependencies) {
                g.addEdge(findDependencyIndex(modInfos, dependency.id), i);
            }
            for (String optionalDependency : modInfos[i].OptionalDependencies) {
                int idx = findDependencyIndex(modInfos, optionalDependency);
                if (idx != -1) {
                    g.addEdge(idx, i);
                }
            }
        }

        g.tsortStable();

        return g.sortedArray.toArray(new ModInfo[g.sortedArray.size()]);
    }

    private static void checkFileInfo(File file)
    {
        System.out.printf(file.getName() + ": ");
        System.out.println(file.exists() ? "Exists" : "Does not exist");

        if (file.exists()) {
            System.out.printf("Type: ");
            if (file.isFile()) {
                System.out.println("File");
            } else if (file.isDirectory()) {
                System.out.println("Directory");
                System.out.println("Contents:");
                for (File subfile : Objects.requireNonNull(file.listFiles())) {
                    System.out.println("  " + subfile.getName());
                }
            } else {
                System.out.println("Unknown");
            }
        }
    }

    private static void convertOldWorkshopInfoFiles(List<SteamSearch.WorkshopInfo> workshopInfos)
    {
        if (workshopInfos.isEmpty()) {
            // don't attempt conversion if steam isn't on
            return;
        }

        try {
            String pathUpdated = SpireConfig.makeFilePath(null, "WorkshopUpdated", "json");
            String pathLocations = SpireConfig.makeFilePath(null, "WorkshopLocations", "json");
            Files.deleteIfExists(Paths.get(pathUpdated));
            Files.deleteIfExists(Paths.get(pathLocations));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String data = gson.toJson(workshopInfos);
            Files.write(Paths.get(SpireConfig.makeFilePath(null, "WorkshopInfo", "json")), data.getBytes());
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
}
