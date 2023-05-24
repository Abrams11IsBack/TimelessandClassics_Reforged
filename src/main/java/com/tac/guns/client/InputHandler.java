package com.tac.guns.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.screen.TaCSettingsScreen;
import com.tac.guns.client.settings.GunOptions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Static handler for {@link KeyBind}s
 *
 * @author Giant_Salted_Fish
 */
@OnlyIn( Dist.CLIENT )
@EventBusSubscriber( modid = Reference.MOD_ID, value = Dist.CLIENT )
public final class InputHandler
{
	/**
	 * Universal keys. These keys will always update
	 */
	public static final KeyBind
			PULL_TRIGGER = new KeyBind( "key.tac.pull_trigger", GLFW.GLFW_MOUSE_BUTTON_LEFT, Type.MOUSE ),
			AIM_HOLD = new KeyBind( "key.tac.aim_hold", GLFW.GLFW_MOUSE_BUTTON_RIGHT, Type.MOUSE ),
			AIM_TOGGLE = new KeyBind( "key.tac.aim_toggle", InputConstants.UNKNOWN.getValue()),
			ARMOR_REPAIRING = new KeyBind( "key.tac.armor_repairing", GLFW.GLFW_MOUSE_BUTTON_RIGHT, Type.MOUSE);

	/**
	 * Normal keys. These keys will update when {@link #CO} is not down.
	 */
	public static final KeyBind
			RELOAD = new KeyBind( "key.tac.reload", GLFW.GLFW_KEY_R ),
			UNLOAD = new KeyBind( "key.tac.unload", InputConstants.UNKNOWN.getValue() ),
			ATTACHMENTS = new KeyBind( "key.tac.attachments", GLFW.GLFW_KEY_Z ),

			FIRE_SELECT = new KeyBind( "key.tac.fireSelect", GLFW.GLFW_KEY_G ),
			INSPECT = new KeyBind( "key.tac.inspect", GLFW.GLFW_KEY_H ),
			SIGHT_SWITCH = new KeyBind( "key.tac.sight_switch", GLFW.GLFW_KEY_V ),
			//ACTIVATE_SIDE_RAIL = new KeyBind( "key.tac.activateSideRail", GLFW.GLFW_KEY_B ),
			MORE_INFO_HOLD = new KeyBind( "key.tac.moreInfoHold", GLFW.GLFW_KEY_LEFT_SHIFT );

	/**
	 * Co-keys. These keys will update when {@link #CO} is down.
	 */
	public static final KeyBind
			CO = new KeyBind( "key.tac.co", GLFW.GLFW_KEY_LEFT_ALT ),
			CO_UNLOAD = new KeyBind( "key.tac.co_unload", GLFW.GLFW_KEY_R ),
			CO_INSPECT = new KeyBind( "key.tac.co_inspect", -1 );

	/**
	 * These keys are development only
	 */
	public static final KeyBind
			SHIFTY = new KeyBind( "key.tac.ss", GLFW.GLFW_KEY_LEFT_SHIFT ),
			CONTROLLY = new KeyBind( "key.tac.cc", GLFW.GLFW_KEY_LEFT_CONTROL ),
			ALTY = new KeyBind( "key.tac.aa", GLFW.GLFW_KEY_LEFT_ALT ),
			SHIFTYR = new KeyBind( "key.tac.ssr", GLFW.GLFW_KEY_RIGHT_SHIFT ),
			CONTROLLYR = new KeyBind( "key.tac.ccr", GLFW.GLFW_KEY_RIGHT_CONTROL ),
			ALTYR = new KeyBind( "key.tac.aar", GLFW.GLFW_KEY_RIGHT_ALT ),
			SIZE_OPT = new KeyBind( "key.tac.sizer", GLFW.GLFW_KEY_PERIOD ),

			P = new KeyBind( "key.tac.p", GLFW.GLFW_KEY_P ),
			L = new KeyBind( "key.tac.l", GLFW.GLFW_KEY_L ),
			O = new KeyBind( "key.tac.o", GLFW.GLFW_KEY_O ),
			K = new KeyBind( "key.tac.k", GLFW.GLFW_KEY_K ),
			M = new KeyBind( "key.tac.m", GLFW.GLFW_KEY_M ),
			I = new KeyBind( "key.tac.i", GLFW.GLFW_KEY_I ),
			J = new KeyBind( "key.tac.j", GLFW.GLFW_KEY_J ),
			N = new KeyBind( "key.tac.n", GLFW.GLFW_KEY_N ),

			UP = new KeyBind( "key.tac.bbb", GLFW.GLFW_KEY_UP ),
			RIGHT = new KeyBind( "key.tac.vvv", GLFW.GLFW_KEY_RIGHT ),
			LEFT = new KeyBind( "key.tac.ccc", GLFW.GLFW_KEY_LEFT ),
			DOWN = new KeyBind( "key.tac.zzz", GLFW.GLFW_KEY_DOWN );

	private static final ArrayList< KeyBind >
			GLOBAL_KEYS = new ArrayList<>(),
			INCO_KEYS = new ArrayList<>(),
			CO_KEYS = new ArrayList<>();

	private static final HashMultimap< Key, KeyBind >
			GLOBAL_MAPPER = HashMultimap.create(),
			INCO_MAPPER = HashMultimap.create(),
			CO_MAPPER = HashMultimap.create();

	/**
	 * Is used to save and read key bindings
	 */
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

	public static void initKeys()
	{
		regisAll(
				GLOBAL_KEYS,

				PULL_TRIGGER,

				AIM_HOLD,
				AIM_TOGGLE,
				CO,
				ARMOR_REPAIRING
		);

		regisAll(
				INCO_KEYS,

				RELOAD,
				UNLOAD,
				ATTACHMENTS,
				FIRE_SELECT,
				INSPECT,
				SIGHT_SWITCH,
				MORE_INFO_HOLD
		);

		regisAll(
				CO_KEYS,

				CO_UNLOAD,
				CO_INSPECT
		);

		// Only register dev keys for dev mode
		if ( Config.COMMON.development.enableTDev.get() )
		{
			regisAll(
					GLOBAL_KEYS,

					SHIFTY,
					CONTROLLY,
					ALTY,
					SHIFTYR,
					CONTROLLYR,
					ALTYR,
					SIZE_OPT,

					P, L, O, K, M, I, J, N,
					UP, RIGHT, LEFT, DOWN
			);
		}
	}

	/**
	 * Original TAC implementation seems to try to prevent gun from destroying block and bobbing on
	 * use by canceling the {@link RawMouseEvent} event. Hence to receive the update, we need a
	 * higher priority to receive event ahead. But I have to say this kind of implementation is a
	 * bit of ugly. There are actually methods on {@link} that can be override to control the
	 * behavior of item on mouse Key.
	 *
	 * TODO: maybe refactor this part
	 */
	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void onMouseInput( InputEvent.RawMouseEvent evt )
	{
		final Key button = InputConstants.Type.MOUSE.getOrCreate( evt.getButton() );
		final boolean down = evt.getAction() == GLFW.GLFW_PRESS;
		GLOBAL_MAPPER.get( button ).forEach( kb -> kb.update( down ) );
		( CO.down ? CO_MAPPER : INCO_MAPPER ).get( button ).forEach( kb -> kb.update( down ) );
		( CO.down ? INCO_MAPPER : CO_MAPPER ).get( button )
				.forEach( kb -> kb.inactiveUpdate( down ) );
	}

	/*@SubscribeEvent
	public static void onScreenInit(ScreenEvent.InitScreenEvent.Post event)
	{
		if(event.getScreen() instanceof ControlsScreen)
		{
			PauseScreen screen = (PauseScreen) event.getScreen();;

			event.addListener((new Button(screen.width / 2 - 215, 10, 75, 20, new TranslatableComponent("tac.options.gui_settings"), (p_213126_1_) -> {
				Minecraft.getInstance().setScreen(new TaCSettingsScreen(screen, Minecraft.getInstance().options));
			})));
		}
	}*/

	@SubscribeEvent( priority = EventPriority.HIGH )
	public static void onKeyInput( InputEvent.KeyInputEvent evt )
	{
		final Key key = InputConstants.Type.KEYSYM.getOrCreate( evt.getKey() );
		final boolean down = evt.getAction() != GLFW.GLFW_RELEASE;
		GLOBAL_MAPPER.get( key ).forEach( kb -> kb.update( down ) );
		( CO.down ? CO_MAPPER : INCO_MAPPER ).get( key ).forEach( kb -> kb.update( down ) );
		( CO.down ? INCO_MAPPER : CO_MAPPER ).get( key )
				.forEach( kb -> kb.inactiveUpdate( down ) );
	}

	private static KeyBind oriAimKey;
	static void restoreKeyBinds()
	{
		oriAimKey = AIM_HOLD.keyCode() != InputConstants.UNKNOWN ? AIM_HOLD : AIM_TOGGLE;
		KeyBind.REGISTRY.values().forEach( KeyBind::restoreKeyBind );
	}

	// We shouldn't care it changed or not, saves a few bytes during screen swaps?
	static void clearKeyBinds( File file )
	{
		for ( KeyBind key : KeyBind.REGISTRY.values() ) {
			key.clearKeyBind();
		}

		// Make sure only one aim key is bounden
		final Key none = InputConstants.UNKNOWN;
		if ( AIM_HOLD.keyCode() != none && AIM_TOGGLE.keyCode() != none )
		{
			oriAimKey.setKeyCode( none );
		}
		updateMappers();
		saveTo( file );
	}

	/**
	 * Save key binds into given ".json" file
	 */
	static void saveTo( File file )
	{
		try ( FileWriter out = new FileWriter( file ) )
		{
			final HashMap< String, String > mapper = new HashMap<>();
			KeyBind.REGISTRY.values().forEach( kb -> mapper.put( kb.name(), "" + kb.keyCode() ) );
			out.write( GSON.toJson( mapper ) );
		}
		catch ( IOException e ) { GunMod.LOGGER.error( "Fail write key bindings", e ); }
	}

	/**
	 * Read key binds from given ".json" file
	 */
	static void readFrom( File file )
	{
		try ( FileReader in = new FileReader( file ) )
		{
			final JsonObject obj = GSON.fromJson( in, JsonObject.class );
			obj.entrySet().forEach( e -> {
				try
				{
					final KeyBind keyBind = KeyBind.REGISTRY.get( e.getKey() );
					final Key Key = InputConstants.getKey( e.getValue().getAsString() );
					keyBind.setKeyCode( Key );
				}
				catch ( NullPointerException ee ) {
					GunMod.LOGGER.error( "Key bind " + e.getKey() + " do not exist" );
				}
				catch ( IllegalArgumentException ee ) {
					GunMod.LOGGER.error( "Bad key code: " + e );
				}
			} );
		}
		catch ( IOException e ) { GunMod.LOGGER.error( "Fail read key bind", e ); }

		updateMappers();
	}

	public static void updateMappers()
	{
		updateMapper( GLOBAL_KEYS, GLOBAL_MAPPER );
		updateMapper( CO_KEYS, CO_MAPPER );
		updateMapper( INCO_KEYS, INCO_MAPPER );
	}

	private static void updateMapper(
			Collection< KeyBind > group,
			Multimap< Key, KeyBind > mapper
	) {
		mapper.clear();
		group.forEach( kb -> {
			final Key code = kb.keyCode();
			if ( code != InputConstants.UNKNOWN ) { mapper.put( code, kb ); }
		} );
	}

	private static void regisAll( Collection< KeyBind > updateGroup, KeyBind... keys )
	{
		final List< KeyBind > keyList = Arrays.asList( keys );

		keyList.forEach( KeyBind::regis );
		updateGroup.addAll( keyList );
	}
}
