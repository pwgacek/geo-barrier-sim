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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import pl.edu.agh.miss.geobarriersim.logic.statistics.Statistician;

import java.util.List;
import java.util.Optional;

public class SimulationScreen implements Screen {
    private static final int SCREEN_HEIGHT = 1080;
    private static final int SCREEN_WIDTH = 1920;

    private float timeScale = 50f;
    private final float cellSize;

    private boolean isPaused = true;
    private float simulationTime = 0;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private final Simulation simulation;
    private final Statistician statistician;
    private Skin skin;
    private Stage stage;
    private Label dayCounterLabel;
    private TextButton togglePauseButton;
    private Slider simulationSpeedSlider;
    private Label simulationSpeedLabel;
    private final Color backgroundColor = Color.valueOf("#3b1f15");

    private Table statisticsTable;
    private Label[] avgSpeedLabel;
    private Label[] avgRoamLabel;
    private Label[] avgHungerThresholdLabel;

    private final Vector2d[][] positions;
    private final Vector2[][] circles;
    private final Vector2[][] rectangles;

    public SimulationScreen(SimulationSettings settings) {
        this.simulation = new Simulation(settings);
        this.statistician = Statistician.getInstance();
        this.cellSize = (float) SCREEN_HEIGHT / settings.getMapSize();

        positions = new Vector2d[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        circles = new Vector2[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        rectangles = new Vector2[simulation.getWorldMap().getWidth()][simulation.getWorldMap().getHeight()];
        for (int x = 0; x < simulation.getWorldMap().getWidth(); x++) {
            for (int y = 0; y < simulation.getWorldMap().getHeight(); y++) {
                positions[x][y] = new Vector2d(x, y);
                circles[x][y] = new Vector2(x * cellSize + (cellSize) / 2, y * cellSize + cellSize / 2);
                rectangles[x][y] = new Vector2(x * cellSize, y * cellSize);
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

        togglePauseButton = new TextButton("Start Simulation", skin);
        togglePauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        simulationSpeedSlider = new Slider(1, 100, 1, false, skin);
        simulationSpeedSlider.setValue(timeScale);

        simulationSpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                timeScale = simulationSpeedSlider.getValue();
                simulationSpeedLabel.setText("Days per one second: " + (int) timeScale);
            }
        });

        simulationSpeedLabel = new Label("Days per one second: " + (int) timeScale, new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        simulationSpeedLabel.setAlignment(Align.center);

        statisticsTable = new Table();
        ScrollPane scrollPane = new ScrollPane(statisticsTable);

        statisticsTable.top();
        setupStatisticsTable();

        Table rightTable = new Table();
        rightTable.defaults().pad(50);
        rightTable.setHeight(SCREEN_HEIGHT);
        rightTable.setWidth(SCREEN_WIDTH + cellSize * simulation.getWorldMap().getWidth());
        rightTable.setPosition(0, 0);
        stage.addActor(rightTable);

        rightTable.top();
        rightTable.row();
        rightTable.add(dayCounterLabel);
        rightTable.row();
        rightTable.add(simulationSpeedSlider).width(300).padBottom(10);
        rightTable.row();
        rightTable.add(simulationSpeedLabel).padTop(10);
        rightTable.row();
        rightTable.add(scrollPane).height(500);
        rightTable.row();
        rightTable.add(togglePauseButton).width(300).height(50).align(Align.bottom);

    }

    private void setupStatisticsTable() {
        statisticsTable.clear();

        List<AverageGenes> averageGenesList = simulation.getAverageGenes();

        avgSpeedLabel = new Label[averageGenesList.size()];
        avgRoamLabel = new Label[averageGenesList.size()];
        avgHungerThresholdLabel = new Label[averageGenesList.size()];

        statisticsTable.row();
        Label idLabel = new Label("Id", new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        idLabel.setAlignment(Align.center);
        statisticsTable.add(idLabel).width(50).height(50);
        Label avgSpeedLabel = new Label("Avg speed", new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        avgSpeedLabel.setAlignment(Align.center);
        statisticsTable.add(avgSpeedLabel).width(120).height(50);
        Label avgRoamLabel = new Label("Avg roam", new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        avgRoamLabel.setAlignment(Align.center);
        statisticsTable.add(avgRoamLabel).width(120).height(50);
        Label avgHungerThresholdLabel = new Label("Avg hunger threshold", new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
        avgHungerThresholdLabel.setWrap(true);
        avgHungerThresholdLabel.setAlignment(Align.center);
        statisticsTable.add(avgHungerThresholdLabel).width(120).height(50);


        for (int i = 0; i < averageGenesList.size(); i++ ){
            statisticsTable.row();
            Label positionLabel = new Label(String.valueOf(i + 1), new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            positionLabel.setAlignment(Align.center);
            statisticsTable.add(positionLabel).width(50).height(50);
            this.avgSpeedLabel[i] = new Label(String.format("%.3f", averageGenesList.get(i).avgSpeed()), new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            this.avgSpeedLabel[i].setAlignment(Align.center);
            statisticsTable.add(this.avgSpeedLabel[i]).width(120).height(50);
            this.avgRoamLabel[i] = new Label(String.format("%.3f", averageGenesList.get(i).avgRoamTendency()), new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            this.avgRoamLabel[i].setAlignment(Align.center);
            statisticsTable.add(this.avgRoamLabel[i]).width(120).height(50);
            this.avgHungerThresholdLabel[i] = new Label(String.format("%.3f", averageGenesList.get(i).avgHungerThreshold()), new Label.LabelStyle(Fonts.getFont(17), Color.WHITE));
            this.avgHungerThresholdLabel[i].setAlignment(Align.center);
            statisticsTable.add(this.avgHungerThresholdLabel[i]).width(120).height(50);
        }
    }

    private void togglePause() {
        if (isPaused) {
            isPaused = false;
            togglePauseButton.setText("Stop Simulation");
            simulation.setAreas();
            setupStatisticsTable();
        } else {
            isPaused = true;
            togglePauseButton.setText("Start Simulation");
        }
    }



    public void render(float delta) {
        Gdx.gl.glClearColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);


        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            togglePause();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) {
                Gdx.app.exit();
            }
        }

        List<AverageGenes> averageGenesList = simulation.getAverageGenes();


        if (!isPaused) {
            simulationTime += delta * timeScale;

            while (simulationTime >= 1) {
                simulationTime -= 1;
                simulation.simulateOneDay();
                averageGenesList = simulation.getAverageGenes();
                statistician.save(averageGenesList);
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                Vector2 worldPos =  getMouseCoordinates();
                int x = (int) (worldPos.x / cellSize);
                int y = (int) (worldPos.y / cellSize);
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
        for (int i = 0; i < averageGenesList.size(); i++ ){
            avgSpeedLabel[i].setText(String.format("%.3f", averageGenesList.get(i).avgSpeed()));
            avgRoamLabel[i].setText(String.format("%.3f", averageGenesList.get(i).avgRoamTendency()));
            avgHungerThresholdLabel[i].setText(String.format("%.3f", averageGenesList.get(i).avgHungerThreshold()));

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
        shapeRenderer.rect( 0, 0, cellSize * worldMap.getWidth(), cellSize * worldMap.getHeight());

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
        camera.position.set(viewportWidth / 2f, viewportHeight/ 2f, 0);
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

}
