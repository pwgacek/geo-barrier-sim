package pl.edu.agh.miss.geobarriersim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.Graphics;
import pl.edu.agh.miss.geobarriersim.simulation.SimulationSettings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SettingsScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final SimulationSettings settings;

    public SettingsScreen(Game game) {
        stage = new Stage(new ScreenViewport());
        skin  = new Skin(Gdx.files.internal("uiskin.json"));
        settings = new SimulationSettings();

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        Label label = new Label("Settings", skin);
        label.setFontScale(3,3);
        table.add(label).colspan(4).padBottom(30);
        table.row();


        addRow(table, "Map size", settings::getMapSize, settings::setMapSize,10,200);
        addRow(table, "Start energy", settings::getStartEnergy, settings::setStartEnergy,1,1000);
        addRow(table, "Energy loss per move", settings::getEnergyLossPerMove, settings::setEnergyLossPerMove,1,1000);
        addRow(table, "Energy from plant", settings::getEnergyFromPlant, settings::setEnergyFromPlant,1,1000);
        addRow(table, "Initial animal count", settings::getInitialAnimalCount, settings::setInitialAnimalCount,2,40000);
        addRow(table, "Plant growth chance per 1000", settings::getPlantGrowthChancePer1000, settings::setPlantGrowthChancePer1000,1,100);


        TextButton startButton = getTextButton(game);

        table.add(startButton).colspan(4).pad(20);
    }

    private TextButton getTextButton(Game game) {
        TextButton startButton = new TextButton("Start", skin);
        startButton.pad(0,30, 0, 30);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
                game.setScreen(new SimulationScreen(settings));
            }
        });
        return startButton;
    }

    private void addRow(Table table, String title, Supplier<Integer> getter, Consumer<Integer> setter, int minValue, int maxValue) {
        Label titleLabel = new Label(title, skin);
        titleLabel.setWrap(true);
        Label valueLabel = new Label(Integer.toString(getter.get()), skin);
        valueLabel.setAlignment(Align.center);
        TextButton minusButton = new TextButton("-", skin);
        TextButton plusButton = new TextButton("+", skin);


        final Timer.Task plusRepeatTask = new Timer.Task() {
            @Override
            public void run() {
                if (getter.get() < maxValue) {
                    setter.accept(getter.get() + 1);
                    valueLabel.setText(String.valueOf(getter.get()));
                }
            }
        };

        final Timer.Task minusRepeatTask = new Timer.Task() {
            @Override
            public void run() {
                if (getter.get() > minValue) {
                    setter.accept(getter.get() - 1);
                    valueLabel.setText(String.valueOf(getter.get()));
                }
            }
        };

        plusButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getter.get() < maxValue) {
                    setter.accept(getter.get() + 1);
                    valueLabel.setText(String.valueOf(getter.get()));
                }

                Timer.schedule(plusRepeatTask, 0.4f, 0.1f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                plusRepeatTask.cancel();
            }
        });

        minusButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getter.get() > minValue) {
                    setter.accept(getter.get() - 1);
                    valueLabel.setText(String.valueOf(getter.get()));
                }

                Timer.schedule(minusRepeatTask, 0.4f, 0.1f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                minusRepeatTask.cancel();
            }
        });



        table.add(titleLabel).width(150);
        table.add(minusButton).width(28).padLeft(80);
        table.add(valueLabel).width(80).pad(10);
        table.add(plusButton).width(28).padRight(10);

        table.row();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
