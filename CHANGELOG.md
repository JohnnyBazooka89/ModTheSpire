## Changelog ##
#### dev ####
* `Loader` class has been renamed to `ModTheSpire`
* ByRef2
* SpireMethod
* Add localization support for MTS strings
* Track workshop mod playtimes
* Patch mts-launcher to allow command line arguments to be passed to ModTheSpire
* Fix lowercase mod names sorted after capitalized mod names
* Fix crash from compile order not considering interfaces
* Allow dependencies to specify required version number
* Add mod ID to UI
  * Only shown in moddder mode
* Add dependencies to UI
* Rearrange info in the UI
* Fix status message being wiped when rearranging mod order
* Divide mod list and info panels, allowing for resizing
* Add settings menu
* Add UI scale setting
* Update in-game Mod Settings menu
  * Make Config button more prominent
  * Add scrollbar when mod list is long
* Fix being able to click on non-visble mod entries in the Mod Settings menu
* FlatLaf UI theme
* Add theme setting
* Make mod list clip long mod names instead of scrolling horizontally
* Add UI to switch between play, out-jar, and package
  * Only shown in moddder mode
* Make mod description box scrollable for long text
* Make Steam icon on mod open the mod's workshop page when clicked
* Display multiple icons for mods that have local and workshop versions
* Add button to open logs folder
* Add button to open StS folder
* Add ability to edit profile names
* Add requiredModId to SpireEnum
* Fix SpirePatch requiredModId not accounting for sideloading
* Add splash screen while ModTheSpire is loading
* Add mnemonic shortcut for Play button
* Add About dialog
* Fix some mod info text being selectable
* Remove MTS version from window title
* Convert profile editing UI into separate window, renamed to Mod Lists
* Add alias for profile flag: mod-list
* Convert toggle all mods button to menu items
* Save mod list changes immediately rather than on pressing Play
* Display mod dependencies by name when not in Modder Mode
* Add default value options to SpireConfig get methods
* Add setting to enable achievements
* Add setting to unlock A20 for all characters
* Read and display StS build distributor

#### v3.30.3 ####
* Fix package information being lost for patched classes (Alchyr)

#### v3.30.2 ####
* Fix being unable to enable ImGui when not on Steam Deck

#### v3.30.1 ####
* Add missing property for imgui flag
* Fix crash on Steam Deck by forcing LWJGL3 mode

#### v3.30.0 ####
* Make `Loader.ARGS` public so mods can check program arguments
* Optionally include LWJGL3 for Dear ImGui usage

#### v3.29.0 ####
* Add clear error if patch method is non-static
* Fix last profile used not being selected properly

#### v3.28.0 ####
* Bump gson from 2.8.2 to 2.8.9
* Add properties for all flags (mechinn)

#### v3.27.0 ####
* Add flag for closing MTS when done
  * For automating out-jar/package

#### v3.26.0 ####
* Add ability to create pre-patched version of the game
* Show list of mods in stacktrace on crash (Chaofan)
* Add textbox to filter mods (Chaofan)

#### v3.25.2 ####
* Make workshop support still work on older versions of Slay the Spire
* Update steamworks4j version to match Slay the Spire hotfix

#### v3.25.1 ####
* Fix steamworks4j version for Slay the Spire v2.3

#### v3.25.0 ####
* Update how workshop info is saved and give mods access to it
* Add flag for selecting specific mods
  * Implies --skip-launcher

#### v3.24.0 ####
* Change how out-jar backend works
* Fix very specific SpireField crash
  * If two SpireFields of different types are defined in the same class, one of them using a custom subclass of SpireField and the other not, the non-subclassed SpireField will cause a crash when used

#### v3.23.4 ####
* Stop Log4j exploit in mods

#### v3.23.3 ####
* Fix patch ordering being affected by SpirePatch, SpirePatches, SpirePatch2, and SpirePatches2 type
* Patch to not require achievements to unlock Watcher
* Fix crash if a mod's MTS version is invalid

#### v3.23.2 ####
* Revert v3.23.0 changes:
  * Colorize errors in log window
  * Limit log window to 5000 lines

#### v3.23.1 ####
* Fix finding Steam library folders after latest Steam Beta

#### v3.23.0 ####
* Colorize errors in log window
* Limit log window to 5000 lines

#### v3.22.0 ####
* Copy annotations from SpireEnums

#### v3.21.0 ####
* Keep log window open when game crashes
* SpirePatch2: Parameter name `__args`
  * Receives all original arguments as an array

#### v3.20.0 ####
* SpirePatch2
* Add Return overload for simpler SpireReturn from void methods
* Fix ClassPool not returning modified versions of classes after patching is complete
* Add ModTheSpire application icon
* Alter Slay the Spire window title
* Add option for a SpirePatch to require another mod
  * If the other mod isn't loaded, the patch is ignored

#### v3.19.1 ####
* Fix crash if mod list file is corrupted

#### v3.19.0 ####
* Improve speed of out-jar
* Add flag for skipping intro slash screen

#### v3.18.2 ####
* Fix crash on Linux because java.exe exists

#### v3.18.1 ####
* Fix main menu order

#### v3.18.0 ####
* Include Kotlin in ModTheSpire

#### v3.17.0 ####
* No longer require patch classes and methods to be public
* Improve the efficiency of SpireField

#### v3.16.0 ####
* Fix patch error with ByRef while mixing primitive and wrapper types
* Fix patch error with ByRef and private captures while mixing primitive and wrapper types
* Make ByRef type work for primative/wrapper types
* Fix crash from SpireField of array type
* Add annotation for Raw patches
* Add annotation for Instrument patches

#### v3.15.0 ####
* Allow mods to sideload other mods, loading them even if not selected

#### v3.14.1 ####
* Fix shading an older, incorrect version of javassist

#### v3.14.0 ####
* Add flag for skipping launcher UI
* Add flag for selecting profile

#### v3.13.1 ####
* Fix loading workshop beta mods when local beta mod should be loaded

#### v3.13.0 ####
* Save workshop item locations so they can be used later without Steam running
* Fix regression of ByRef named type parameter

#### v3.12.0 ####
* Make ByRef work on all Insert patch parameters, not just localvars
* Add capturing private fields as patch parameters
* Add ability to load different mod file on beta branch

#### v3.11.0 ####
* Add check to Prefix for ByRef not being array type
* Fix ByRef not working on array types
* Fix some weirdness of UI warnings
* Add toggle all mods button
* Fix SpireReturn memory leak
* Fix crash if update save contains an error
* Add mod profiles
* Fix StS version finding failing on v2 version numbers
* Fix displaying version incorrectly in-game for v2 version numbers

#### v3.10.1 ####
* Fix crash in incomplete last-update code

#### v3.10.0 ####
* Include all of javassist library for use
* Fix SpireReturn not working on constructors in Insert patches
* Allow SpireOverride to work on methods that already have SpireOverride
* Fix StaticSpireField not working

#### v3.9.2 ####
* Fix new version display to work on StS beta

#### v3.9.1 ####
* Fix duplicate SpireEnum crash

#### v3.9.0 ####
* Main menu no longer lists all mods, instead just the number of mods
* Separate MTS from in-game version number and show number of mods
* Make mods use StS version of Gson, not Gson packaged with MTS
* Patch fields onto enums for SpireEnum
  * Allows Gson to work with SpireEnum values instead of crashing

#### v3.8.3 ####
* Fix being unable to SpireOverride the same method on multiple subclasses

#### v3.8.2 ####
* Fix ClassPool changes not propagating

#### v3.8.1 ####
* Fix GameVersionFinder for full release

#### v3.8.0 ####
* Update libraries to use newer ASM
* Reduce MTS size by 30%
* Fix a typo (reina)

#### v3.7.5 ####
* Fix OBS capturing log window instead of the game sometimes

#### v3.7.4 ####
* Fix ModTheSpire hanging if there are too many Steam mods installed

#### v3.7.3 ####
* Restart MTS using jre1.8.0_51 if available
  * Resolves "OpenGL is not supported by the video driver" crash for people with older laptops

#### v3.7.2 ####
* Fix crash if mod is so old it only has ModTheSpire.config

#### v3.7.1 ####
* When opening mods folder, create it if it doesn't exist

#### v3.7.0 ####
* Steam Workshop support
* Fix SpireReturn not working on constructors

#### v3.6.3 ####
* Reroll to avoid duplicate SpireField names

#### v3.6.2 ####
* Fix silent crash in auto-updater if a mod's release has a bad version number

#### v3.6.1 ####
* Fix silent crash if a mod has a bad version number

#### v3.6.0 ####
* Fix ClassLoader to retrieve correct DesktopLauncher when invoked via ClassPool
* Time mod initializers

#### v3.5.0 ####
* Cache updater  to avoid hitting the rate limit

#### v3.4.0 ####
* Fix in-game mods menu not scrolling if you have a lot of mods
* Download and restart now uses same arguments as first launch

#### v3.3.0 ####
* Fix crash if a mod doesn't have an ID
* Copy annotations from SpireFields

#### v3.2.0 ####
* SpireOverride: Allow overriding private methods from superclasses
* Cleanup after the patching process

#### v3.1.0 ####
* Option dependencies field in ModInfo
  * Will be loaded before your mod, but aren't required
* Use SemVer library for version numbers

#### v3.0.0 ####
* More debug print info for SpireField
* Fix SpireField to work with generic types
* Fix SpireField to not use duplicate objects
* Fix NPE in isModLoaded
* Reworked UI
* Store configs in ~/Library/Preferences on Mac
* Make annotationDBMap public for mods to use
* Add some functionality to SpireConfig
* Add extra options for LineFinder
* Fix in-game mod list tooltip position on other resolutions
* Allow multiple Prefix, Postfix, and Insert patches to exist in a single patch class
  * Use the SpirePrefixPatch, SpirePostfixPatch, and SpireInsertPatch annotations to mark methods
  * If using a locator, Insert must specify locator with the `locator` parameter of SpireInsertPatch
* Allow Class types to be used in SpirePatch
  * No longer have to type the fully qualified class name
* Allow Class types in locator Matchers
  * No longer have to type the fully qualified class name
* Always print patch debug info on patching error
* More understandable errors for some patching errors
* Force defining paramtypes on overloaded methods
* Stricter error when method to patch isn't found

#### v2.9.1 ####
* Patch to always enable Custom mode

#### v2.9.0 ####
* Allow Prefix patches to skip the original method
* Allow Insert patches to skip the remainder of the original method
* Method for mods to check if another mod is loaded
* Fix finding Steam install on Mac and Linux

#### v2.8.0 ####
* SpireField: For adding new fields to existing classes

#### v2.7.0 ####
* Fix for week 29
* Option for modders to dump patched JAR for inspection (test447)
* Format logs nicer (test447)
* Mod update checker (test447/kiooeht)
* Make constants for patching constructors and static initializers
* Make Play button default for keyboard use
* Warning banner if using beta branch of StS

#### v2.6.0 ####
* Change ModInfo to use JSON
* Update checker for ModTheSpire
* Warn if ModTheSpire is in the mod list and don't load it as a mod
* Add useful debug info to start of log
* Mod dependencies: Load dependencies first
* Search for desktop-1.0.jar in Steam installation directory
* Mod screen in game
* Locator for Insert patches (test447)
* Fix: Disable checkboxes for mods that need newer MTS version

#### v2.5.0 ####
* **Merge ModTheSpire and ModTheSpireLib. They are now one project**
* Maintain launcher window size and position between uses
* When not using debug mode, close log window when game closes
* Retain debug mode between uses
* Mods can specify an exact StS version they support
* Warn in launcher if mod specifies a specific StS version that doesn't match the current
* SpireConfig: Save/load mod config options from user directory
* Fix: Launcher UI for long lists of mod authors or long descriptions
* Fix: UTF-8 support in ModInfo (pk27602017)

#### v2.4.0 ####
* Allow multiple @SpirePatches on single class
* Warn if not running with Java 8
* Fix: NullPointerException when no/empty mods folder
* Fix?: Unable to find `desktop-1.0.jar` on Mac
* Fix: Sometimes crashing when patching a superclass and subclass

#### v2.3.0 ####
* Allow patching static initializers (`"<staticinit>"`)
* Replace patches, completely replace a method
* Raw patches, gives complete access to Javassist API
* Patch loading order now: Insert, Instrument, Replace, Prefix, Postfix, Raw
* Include mod author and description in launcher (test447)
* Debug mode: Displays some additional info for modders
  * Enable with `--debug` flag or checkbox in GUI
* ByRef can auto-determine parameter type for Prefix patches
* Fix: ModTheSpire can now be run through SlayTheSpire.exe

#### v2.2.1 ####
* Fix: ByRef can now specify the real type name when using `Object` as parameter type

#### v2.2.0 ####
* Inject patches in mod load order (kiooeht)
* Include dependency licenses (kiooeht)
* Mod list when hovering over version string in-game (kiooeht)
* Debug log window in launcher (kiooeht)
* Relative line numbers for insert patches (kiooeht)
* Allow @ByRef for prefixes (kiooeht)
* Instrument (ExprEditor) patches (kiooeht)
* SpireEnum to add new enum values (kiooeht)
* Mods can specify minimum ModTheSpire version needed (kiooeht)
* Mods can tag a class @SpireInitializer, and the class's `initialize()` method will be called (kiooeht)
* Fix: Stop code patches from stopping mod patches (kiooeht)
* Fix: Can now prefix constructors (kiooeht)
* Fix NullPointerException if mod doesn't contain `ModTheSpire.config` (kiooeht)

#### v2.1.0 ####
* Display mods on main menu (kiooeht)
* Insert patches (kiooeht)
* Warn if unable to find `desktop-1.0.jar` (kiooeht)
* Popup error messages (kiooeht)
* Allow running ModTheSpire as `desktop-1.0.jar` (kiooeht)

#### v2.0.0 ####
* Credits injection (kiooeht)
* Mod code injection (kiooeht)
  * Prefix
  * Postfix
* Merge t-larson's changes
* Add checkboxes to mod select list (kiooeht)

#### v1.1.2 ####
* Fix exception that occured when mods folder is either not found or empty (t-larson)

#### v1.1.1 ####
* Fix support for mods that do not contain `modname.ModName` (FlipskiZ)
* Switch build to Maven (reckter)

#### v1.1.0 ####
* Change buttons to multi-select list (t-larson)
* Add support for loading multiple mods at the same time (t-larson)
* Add support for mod initialization (t-larson)
* General code cleanup (t-larson)

#### v1.0.0 ####
* Initial release (kiooeht)
