package physics;

public class Force_elastic extends Force {
    private float k = 0.0f;

    public void set_k(float k){
        this.k = k;
    }

    public float get_k(){
        return this.k;
    }

    public void calculate_value(float x) {
        this.value = - k * x;
    }
}
