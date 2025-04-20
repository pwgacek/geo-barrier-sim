package pl.edu.agh.miss.geobarriersim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import pl.edu.agh.miss.geobarriersim.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.IMapElement;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;
import pl.edu.agh.miss.geobarriersim.simulation.Simulation;
import pl.edu.agh.miss.geobarriersim.simulation.SimulationSettings;

import java.util.Optional;

public class SimulationScreen implements Screen {
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1920;

    private final float timeScale = 20f;  // 20 days per second (1 day per 50 ms)
    private final float cellSize;

    private boolean isPaused = false;
    private float simulationTime = 0;  // Accumulated simulation time in days

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private final Simulation simulation;
    private Skin skin;
    private Stage stage;
    private TextButton startStopSimulationButton;
    private Viewport viewport;

    public SimulationScreen(SimulationSettings settings) {
        this.simulation = new Simulation(settings);
        this.cellSize = (float) SCREEN_HEIGHT / settings.getMapSize();
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        skin  = new Skin(Gdx.files.internal("uiskin.json"));
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);

        startStopSimulationButton = new TextButton("Stop Simulation", skin);
        startStopSimulationButton.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2 - 50,  400);
        startStopSimulationButton.setSize(200, 60);

        startStopSimulationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPaused) {
                    resume();
                    startStopSimulationButton.setText("Stop Simulation");
                } else {
                    pause();
                    startStopSimulationButton.setText("Start Simulation");
                }
            }
        });

        stage.addActor(startStopSimulationButton);
    }



    public void render(float delta) {
        // Render the simulation
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (!isPaused) {
            simulationTime += delta * timeScale;

            while (simulationTime >= 1) {
                simulationTime -= 1;  // Subtract 1 day (advance 1 day in simulation)
                simulation.simulateOneDay();
            }

        }

        renderWorldMap(shapeRenderer);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();


        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float screenX = Gdx.input.getX();
            float screenY = Gdx.input.getY();
            Vector3 unprojected = camera.unproject(new Vector3(screenX, screenY, 0f));
            System.out.println(unprojected);
        }
    }

    private void renderWorldMap(ShapeRenderer shapeRenderer) {
        WorldMap worldMap = simulation.getWorldMap();
        Color backgroundColor = Color.valueOf("#3b1f15");

        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect( 0, - SCREEN_HEIGHT / 2f, cellSize * worldMap.getWidth(), cellSize * worldMap.getHeight());


        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                Optional<IMapElement> optionalIMapElement = worldMap.objectAt(new Vector2d(x, y));
                if (optionalIMapElement.isPresent()) {
                    IMapElement mapElement = optionalIMapElement.get();
                    shapeRenderer.setColor(mapElement.getColor());
                    if (mapElement instanceof Animal) {
                        shapeRenderer.circle(x * cellSize +  (cellSize) / 2 ,y*cellSize + (cellSize - SCREEN_HEIGHT) / 2,cellSize /2);
                    } else {
                        shapeRenderer.rect(x*cellSize, y*cellSize - SCREEN_HEIGHT / 2f, cellSize, cellSize);
                    }
                }
            }
        }
        shapeRenderer.end();


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);

        float viewportHeight, viewportWidth, aspectRatio;
        if ( width * 9 > height * 16) {
            viewportHeight = SCREEN_HEIGHT;
            aspectRatio = (float) width / height;
            viewportWidth = viewportHeight * aspectRatio;
        } else {
            viewportWidth = SCREEN_WIDTH;
            aspectRatio = (float) height / width;
            viewportHeight = viewportWidth * aspectRatio;
        }


        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.position.set(viewportWidth / 2f, 0, 0);
        camera.update();
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    // other Screen methods: render, resize, hide, dispose...
}
