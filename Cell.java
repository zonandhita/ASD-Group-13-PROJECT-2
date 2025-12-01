import java.util.ArrayList;
import java.util.List;

public class Cell {
    private int x;
    private int y;
    private boolean topWall = true;
    private boolean rightWall = true;
    private boolean bottomWall = true;
    private boolean leftWall = true;
    private boolean visited = false;
    private List<Cell> neighbors = new ArrayList<>();

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean hasTopWall() { return topWall; }
    public boolean hasRightWall() { return rightWall; }
    public boolean hasBottomWall() { return bottomWall; }
    public boolean hasLeftWall() { return leftWall; }

    public void setTopWall(boolean value) { topWall = value; }
    public void setRightWall(boolean value) { rightWall = value; }
    public void setBottomWall(boolean value) { bottomWall = value; }
    public void setLeftWall(boolean value) { leftWall = value; }

    public boolean isVisited() { return visited; }
    public void setVisited(boolean value) { visited = value; }

    public List<Cell> getNeighbors() { return neighbors; }
    public void addNeighbor(Cell cell) {
        if (!neighbors.contains(cell)) {
            neighbors.add(cell);
        }
    }
}
