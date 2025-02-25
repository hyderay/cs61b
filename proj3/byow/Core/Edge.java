package byow.Core;

public class Edge {
    private double weight;
    private int room1;          //The index of first room.
    private int room2;          //The index of second room.

    public Edge(double weight, int room1, int room2) {
        this.weight = weight;
        this.room1 = room1;
        this.room2 = room2;
    }

    public double getWeight() {
        return this.weight;
    }

    public int getRoom1() {
        return room1;
    }

    public int getRoom2() {
        return room2;
    }
}
