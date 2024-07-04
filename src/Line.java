import java.awt.*;

public class Line {
    private final MyPoint point1;
    private final MyPoint point2;
    public Line(MyPoint point1,MyPoint point2){
        this.point1 = point1;
        this.point2 = point2;
    }
    public void setCoordinates(int x1,int y1, int x2, int y2){
        point1.setX(x1);
        point1.setY(y1);
        point2.setX(x2);
        point2.setY(y2);
    }
    public int getX(){
        if (point1.getX() == point2.getX())
            return point1.getX();
        return -1;
    }
    public void drawLine(Graphics graphics){
        graphics.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }
}
