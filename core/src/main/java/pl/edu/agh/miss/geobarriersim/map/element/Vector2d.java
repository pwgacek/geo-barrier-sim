package pl.edu.agh.miss.geobarriersim.map.element;

public record Vector2d(int x, int y) {

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }
    public Vector2d addX(int x) {
        return new Vector2d(this.x + x, this.y);
    }

    public Vector2d addY(int y) {
        return new Vector2d(this.x, this.y + y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

}
