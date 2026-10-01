package physics;

public class Point {
    private Force force_sum = new Force();
    private float x = 0.0f;
    private float V = 0.0f;
    private float a = 0.0f;
    private float m = 0.0f;

    // Объявляем силу
    private Force_elastic F_el = new Force_elastic();
    private Force_gravity F_g = new Force_gravity();

    // Конструктор
    public Point(float k, float x, float m){
        this.F_el.set_k(k);
        this.set_x(x);
        this.set_m(m);
    }

    // Масса
    public void set_m(float m){
        this.m = m;
    }

    public float get_m(){
        return this.m;
    }

    // Ускорение
    public void calculate_a(){
        this.a = force_sum.get_value() / this.m;
    }

    public float get_a(){
        return this.a;
    }

    // Скорость
    public void calculate_V(float t){
        this.V += this.a * t;
    }

    public float get_V(){
        return this.V;
    }

    // Положение
    public void calculate_x(float t){
        this.x += this.V * t;
    }

    public void set_x(float x){
        this.x = x;
    }

    public float get_x(){
        return this.x;
    }

    // Сила общая
    public void calculate_force(){
        F_el.calculate_value(this.x);
        F_g.calculate_value(this.m);
        force_sum.set_value(F_el.get_value() + F_g.get_value());
    }

    // Рассчитываем точку
    public void calculate_point(float t){
        this.calculate_force();
        this.calculate_a();
        this.calculate_V(t);
        this.calculate_x(t);
    }
}
