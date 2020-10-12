/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.DijkstraAllPairsSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

public class SeamCarver {

    private final Picture picture;
    private final EdgeWeightedDigraph G;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = picture;
        this.G = createDigraph();
    }

    private EdgeWeightedDigraph createDigraph() {
        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(picture.height() * picture.width());
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                int leftNeighborX = x - 1;
                int rightNeighborX = x + 1;
                int neighborY = y + 1;

                if (!validateIndexByHeight(neighborY)) continue;

                digraph.addEdge(createEdgeFor(x, y, x, neighborY));
                if (validateIndexByWidth(leftNeighborX))
                    digraph.addEdge(createEdgeFor(x, y, leftNeighborX, neighborY));
                if (validateIndexByWidth(rightNeighborX))
                    digraph.addEdge(createEdgeFor(x, y, rightNeighborX, neighborY));
            }
        }

        return digraph;
    }

    private DirectedEdge createEdgeFor(int vX, int vY, int wX, int wY) {
        return new DirectedEdge(
                coordinatesToNodeIndex(vX, vY),
                coordinatesToNodeIndex(wX, wY),
                energy(wX, wY)
        );
    }

    private int coordinatesToNodeIndex(int x, int y) {
        return y * width() + x;
    }

    private int nodeIndexToCoordinateX(int nodeIndex) {
        return nodeIndex % width();
    }

    private int nodeIndexToCoordinateY(int nodeIndex) {
        return nodeIndex / width();
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return picture().width();
    }

    // height of current picture
    public int height() {
        return picture().height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateIndices(x, y);
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) return 1000;
        return calculateEnergyOfPixel(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] coordinates = new int[width()];
        Arrays.fill(coordinates, 0);
        return coordinates;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        DijkstraAllPairsSP allPairsSP = new DijkstraAllPairsSP(G);

        int shortestPathOrigin = 0;
        int shortestPathDestination = 0;
        double shortestPathDistance = Double.POSITIVE_INFINITY;
        for (int x1 = 0; x1 < width(); x1++) {
            for (int x2 = 0; x2 < width(); x2++) {
                int nodeOrigin = coordinatesToNodeIndex(x1, 0);
                int nodeDest = coordinatesToNodeIndex(x2, height() - 1);
                double dist = allPairsSP.dist(nodeOrigin, nodeDest);

                if (dist < shortestPathDistance) {
                    shortestPathDistance = dist;
                    shortestPathOrigin = nodeOrigin;
                    shortestPathDestination = nodeDest;
                }
            }
        }

        Iterable<DirectedEdge> path = allPairsSP.path(shortestPathOrigin, shortestPathDestination);
        int[] coordinates = new int[height()];
        coordinates[0] = shortestPathOrigin;

        Iterator<DirectedEdge> iterator = path.iterator();
        int count = 1;
        while (iterator.hasNext()) {
            DirectedEdge edge = iterator.next();
            coordinates[count++] = nodeIndexToCoordinateX(edge.to());
        }

        return coordinates;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateParamIsNotNull(seam);
        validateSeam(seam, false);

        if (height() <= 1) throw new IllegalArgumentException("nothing to remove");
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateParamIsNotNull(seam);
        validateSeam(seam, true);

        if (width() <= 1) throw new IllegalArgumentException("nothing to remove");
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

    // Validation

    private void validateIndices(int x, int y) {
        if (!validateIndexByWidth(x)) throw new IllegalArgumentException("x is out of bounds");
        if (!validateIndexByHeight(y)) throw new IllegalArgumentException("y is out of bounds");
    }

    private boolean validateIndexByWidth(int x) {
        if (x < 0 || x >= width()) return false;
        return true;
    }

    private boolean validateIndexByHeight(int y) {
        if (y < 0 || y >= height()) return false;
        return true;
    }

    private void validateParamIsNotNull(Object object) {
        if (object == null) throw new IllegalArgumentException("Param can't be null");
    }

    private void validateSeam(int[] seam, boolean isVertical) {
        int size = isVertical ? height() : width();
        if (size != seam.length) throw new IllegalArgumentException("seam isn't complete");

        int prevPixel = seam[0];
        for (int pixel : seam) {
            if (isVertical) validateIndexByWidth(pixel);
            else validateIndexByHeight(pixel);

            if (pixel - prevPixel > 1) throw new IllegalArgumentException("seam isn't complete");
            prevPixel = pixel;
        }
    }

    // Calculation

    private double calculateEnergyOfPixel(int x, int y) {
        double dX = calculateGradient(x - 1, y, x + 1, y);
        double dY = calculateGradient(x, y - 1, x, y + 1);
        return Math.sqrt(dX + dY);
    }

    private double calculateGradient(int firstX, int firstY, int secondX, int secondY) {
        Color firstColor = picture.get(firstX, firstY);
        Color secondColor = picture.get(secondX, secondY);
        int rX = secondColor.getRed() - firstColor.getRed();
        int gX = secondColor.getGreen() - firstColor.getGreen();
        int bX = secondColor.getBlue() - firstColor.getBlue();
        return Math.pow(rX, 2) + Math.pow(gX, 2) + Math.pow(bX, 2);
    }
}
