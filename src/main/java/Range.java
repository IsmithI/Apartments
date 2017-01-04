/**
 * Created by smith on 03.01.17.
 */
public class Range {

    private float min;
    private float max;

    public Range(float min, float max) {
        if (min > max) {
            this.max = min;
            this.min = max;
        }
        else {
            this.min = min;
            this.max = max;
        }
    }

    public boolean inRange(float num) {
        return num > min && num < max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
