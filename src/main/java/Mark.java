/**
 * @author: Bauka23
 * @date: 26.10.2016
 */
public class Mark {
    public int x;
    public int y;
    public int value;

    public Mark(){}

    public Mark(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public boolean isLessThan(Mark mark) {
        if (value < mark.value) return true;
        return false;
    }
}
