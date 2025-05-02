package pl.edu.agh.miss.geobarriersim.logic.map.element;

public record Pair<T>( T first, T second) {

    public static <T> Pair<T> of(T first, T second) {
        return new Pair<>(first, second);
    }
}
