package physics;

public class Force_gravity extends Force {
    private float g = 9.82f;

    public void calculate_value(float m) {
                this.value = m * g;
    }
}
