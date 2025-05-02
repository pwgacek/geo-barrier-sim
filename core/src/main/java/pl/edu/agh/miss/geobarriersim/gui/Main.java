package pl.edu.agh.miss.geobarriersim.gui;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new SettingsScreen(this));
    }

    @Override
    public void dispose() {
    }
}
