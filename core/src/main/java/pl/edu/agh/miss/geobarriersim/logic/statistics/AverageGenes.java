package pl.edu.agh.miss.geobarriersim.logic.statistics;

import pl.edu.agh.miss.geobarriersim.logic.map.element.Genes;

import java.util.List;

public record AverageGenes(double avgSpeed, double avgRoamTendency, double avgHungerThreshold) {
    public static AverageGenes fromGenesList(List<Genes> genesList) {

        double totalSpeed = 0;
        double totalRoam = 0;
        double totalHunger = 0;

        for (Genes gene : genesList) {
            totalSpeed += gene.speed;
            totalRoam += gene.roamTendency;
            totalHunger += gene.hungerThreshold;
        }

        int size = genesList.size();
        return new AverageGenes(
            totalSpeed / size,
            totalRoam / size,
            totalHunger / size
        );
    }
}
