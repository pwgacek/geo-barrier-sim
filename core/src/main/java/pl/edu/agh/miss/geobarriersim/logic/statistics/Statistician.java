package pl.edu.agh.miss.geobarriersim.logic.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.List;

public class Statistician {

    private final FileHandle file;

    private Statistician (){
        file = Gdx.files.local(String.format("statistics-%d.csv", getId()));
        file.writeString("id,speed,roam,hunger\n", false);
    }

    private int getId() {
        Preferences prefs = Gdx.app.getPreferences("geo-barrier-sim");
        int id = prefs.getInteger("simulationId", 0);
        prefs.putInteger("simulationId", id + 1);
        prefs.flush();
        return id;
    }


    private static Statistician instance;

    public static Statistician getInstance() {
        if (instance == null) {
            instance = new Statistician();
        }
        return instance;
    }

    public void save(List<AverageGenes> averageGenesList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < averageGenesList.size(); i++) {
            sb.append(i+1);
            sb.append(",");
            sb.append(averageGenesList.get(i).avgSpeed());
            sb.append(",");
            sb.append(averageGenesList.get(i).avgRoamTendency());
            sb.append(",");
            sb.append(averageGenesList.get(i).avgHungerThreshold());
            sb.append('\n');
        }
        file.writeString(sb.toString(), true);

    }

}
