package pl.edu.agh.miss.geobarriersim.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import pl.edu.agh.miss.geobarriersim.logic.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.logic.map.element.IMapElement;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Vector2d;
import pl.edu.agh.miss.geobarriersim.logic.simulation.Simulation;
import pl.edu.agh.miss.geobarriersim.logic.simulation.SimulationSettings;
import pl.edu.agh.miss.geobarriersim.logic.statistics.AverageGenes;

import java.util.List;
import java.util.Optional;

public class SimulationScreen implements Screen {
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1920;

    private float timeScale = 50f;  // 20 days per second (1 day per 50 ms)
    private final float cellSize;

    private boolean isPaused = true;
    private float simulationTime = 0;  // Accumulated simulation time in days

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private final Simulation simulation;
    private Skin skin;
    private Stage stage;
    private Label dayCounterLabel;
    private TextButton togglePauseButton;
    private Slider simulationSpeedSlider;
    private Label simulationSpeedLabel;
    private final Color backgroundColor = Color.valueOf("#3b1f15");

    private Table statisticsTable;
    private Label[] avgSpeedLabel;
    private Label[] avgRoam;
    private Label[] avgHungerThreshold;

    private final Vector2d[][] positions;
    private final Vector2[][] circles;
    private final Vector2[][] rectangles;

    public SimulationScreen(SimulationSettings settings) {
        this.simulation = new Simulation(settings);
        this.cellSize = (float) SCREEN_HEIGHT / settings.getMapSize();

        positions = new Vector2d[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        circles = new Vector2[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        rectangles = new Vector2[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        for (int x = 0; x < simulation.getWorldMap().getWidth(); x++) {
            for (int y = 0; y < simulation.getWorldMap().getHeight(); y++) {
                positions[x][y] = new Vector2d(x, y);
                circles[x][y] = new Vector2(x * cellSize + (cellSize) / 2, y * cellSize + (cellSize - SCREEN_HEIGHT) / 2);
                rectangles[x][y] = new Vector2(x * cellSize, y * cellSize - SCREEN_HEIGHT / 2f);
            }
        }

    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        skin  = new Skin(Gdx.files.internal("uiskin.json"));
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera));

        Gdx.input.setInputProcessor(stage);

        dayCounterLabel = new Label("Year: " + (simulation.getDayCounter() / 365) + " Day: " + (simulation.getDayCounter() % 365 + 1) , new Label.LabelStyle(Fonts.getFont(48), Color.WHITE));
        dayCounterLabel.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2 - 150,  440);
        dayCounterLabel.setSize(400, 30);
        stage.addActor(dayCounterLabel);

        togglePauseButton = new TextButton("Start Simulation", skin);
        togglePauseButton.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2 - 50,  200);
        togglePauseButton.setSize(200, 60);

        togglePauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        stage.addActor(togglePauseButton);

        simulationSpeedSlider = new Slider(1, 100, 1, false, skin);
        simulationSpeedSlider.setValue(timeScale);
        simulationSpeedSlider.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2 - 150,  300);
        simulationSpeedSlider.setSize(400, 60);

        simulationSpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                timeScale = simulationSpeedSlider.getValue();
                simulationSpeedLabel.setText("Days per one second: " + (int) timeScale);
            }
        });

        stage.addActor(simulationSpeedSlider);

        simulationSpeedLabel = new Label("Days per one second: " + (int) timeScale, new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        simulationSpeedLabel.setAlignment(Align.center);
        simulationSpeedLabel.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2 - 150,  340);
        simulationSpeedLabel.setSize(400, 30);
        stage.addActor(simulationSpeedLabel);

        statisticsTable = new Table();

        statisticsTable.setPosition(cellSize * simulation.getWorldMap().getWidth() + (SCREEN_WIDTH - cellSize * simulation.getWorldMap().getWidth()) / 2,  0);
        stage.addActor(statisticsTable);
        statisticsTable.top();
        setupTable();

    }

    private void setupTable() {
        statisticsTable.clear();

        List<AverageGenes> averageGenesList = simulation.getAverageGenes();

        avgSpeedLabel = new Label[averageGenesList.size()];
        avgRoam = new Label[averageGenesList.size()];
        avgHungerThreshold = new Label[averageGenesList.size()];

        for (int i = 0; i < averageGenesList.size(); i++ ){
            statisticsTable.row();
            avgSpeedLabel[i] = new Label("Avg speed: " + String.format("%.3f", averageGenesList.get(i).avgSpeed())  , new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            statisticsTable.add(avgSpeedLabel[i]).width(150).height(50);
            avgRoam[i] = new Label("Avg roam: " + String.format("%.3f", averageGenesList.get(i).avgRoamTendency())  , new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            statisticsTable.add(avgRoam[i]).width(150).height(50);
            avgHungerThreshold[i] = new Label("Avg hunger threshold: " + String.format("%.3f", averageGenesList.get(i).avgHungerThreshold())  , new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            statisticsTable.add(avgHungerThreshold[i]).width(150).height(50);
        }
    }

    private void togglePause() {
        if (isPaused) {
            isPaused = false;
            togglePauseButton.setText("Stop Simulation");
            simulation.setAreas();
            setupTable();
        } else {
            isPaused = true;
            togglePauseButton.setText("Start Simulation");
        }
    }



    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            togglePause();
        }

        if (!isPaused) {
            simulationTime += delta * timeScale;

            while (simulationTime >= 1) {
                simulationTime -= 1;  // Subtract 1 day (advance 1 day in simulation)
                simulation.simulateOneDay();
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                Vector2 worldPos =  getMouseCoordinates();
                int x = (int) (worldPos.x / cellSize);
                int y = (int) ((worldPos.y + SCREEN_HEIGHT / 2f) / cellSize);
                WorldMap worldMap = simulation.getWorldMap();
                if (x >= 0 && x < worldMap.getWidth() && y >= 0 && y < worldMap.getHeight()) {
                    if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        worldMap.addMountain(new Vector2d(x, y));
                    } else {
                        worldMap.removeMountain(new Vector2d(x, y));
                    }
                }
            }
        }

        dayCounterLabel.setText("Year: " + (simulation.getDayCounter() / 365) + " Day: " + (simulation.getDayCounter() % 365 + 1));
        List<AverageGenes> averageGenesList = simulation.getAverageGenes();
        for (int i = 0; i < averageGenesList.size(); i++ ){
            avgSpeedLabel[i].setText("Avg speed: " + String.format("%.3f", averageGenesList.get(i).avgSpeed()));
            avgRoam[i].setText("Avg roam: " + String.format("%.3f", averageGenesList.get(i).avgRoamTendency()));
            avgHungerThreshold[i].setText("Avg hunger threshold: " + String.format("%.3f", averageGenesList.get(i).avgHungerThreshold()));

        }

        renderWorldMap(shapeRenderer);
        stage.act(delta);
        stage.draw();
    }

    private Vector2 getMouseCoordinates() {
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        Vector3 unprojected = camera.unproject(new Vector3(screenX, screenY, 0f));
        return new Vector2(unprojected.x, unprojected.y);
    }

    private void renderWorldMap(ShapeRenderer shapeRenderer) {
        WorldMap worldMap = simulation.getWorldMap();

        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect( 0, - SCREEN_HEIGHT / 2f, cellSize * worldMap.getWidth(), cellSize * worldMap.getHeight());

        float radius = cellSize / 2;
        for (int x = 0; x < worldMap.getWidth(); x++) {
            for (int y = 0; y < worldMap.getHeight(); y++) {
                Optional<IMapElement> optionalIMapElement = worldMap.objectAt(positions[x][y]);
                if (optionalIMapElement.isPresent()) {
                    IMapElement mapElement = optionalIMapElement.get();
                    shapeRenderer.setColor(mapElement.getColor());
                    if (mapElement instanceof Animal) {
                        Vector2 circle = circles[x][y];
                        shapeRenderer.circle(circle.x,circle.y,radius);
                    } else {
                        Vector2 rectangle = rectangles[x][y];
                        shapeRenderer.rect(rectangle.x, rectangle.y, cellSize, cellSize);
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

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        Fonts.dispose();
    }

    // other Screen methods: render, resize, hide, dispose...
}
