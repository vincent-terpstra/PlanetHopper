package com.planethopper.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.planethopper.PlanetHopperGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		//com.texturepacker.TexturePacker.process("images");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new PlanetHopperGame(), config);
	}
}
