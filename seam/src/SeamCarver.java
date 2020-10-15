/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private Picture picture;
    private boolean isTransposed = false;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Pic can't be null");
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateIndices(x, y);
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) return 1000;
        return calculateEnergyOfPixel(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transposePicture();
        int[] verticalSeam = findVerticalSeam();
        transposePicture();
        return verticalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findShortestPath();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateParamIsNotNull(seam);
        validateSeam(seam, false);

        if (height() <= 1) throw new IllegalArgumentException("nothing to remove");

        transposePicture();
        removeVerticalSeam(seam);
        transposePicture();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateParamIsNotNull(seam);
        validateSeam(seam, true);

        if (width() <= 1) throw new IllegalArgumentException("nothing to remove");

        int width = width();
        int height = height();

        Picture compressedPic = new Picture(width - 1, height);

        for (int y = 0; y < height; y++) {
            int seamX = seam[y];
            for (int x = 0; x < width; x++) {
                int newX = x;
                if (seamX == x) continue;
                if (x > seamX) newX = x - 1;

                compressedPic.setRGB(newX, y, picture.getRGB(x, y));
            }
        }

        picture = compressedPic;
        // this.energyMatrix = createEnergyMatrix();
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

            if (Math.abs(pixel - prevPixel) > 1)
                throw new IllegalArgumentException("seam isn't complete");
            prevPixel = pixel;
        }
    }

    // Calculation

    private double[][] createEnergyMatrix() {
        double[][] en = new double[height()][width()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                en[y][x] = energy(x, y);
            }
        }

        return en;
    }

    private int[] findShortestPath() {
        double[][] energyMatrix = createEnergyMatrix();
        int[][] edgeFrom = new int[height()][width()];
        double[][] costToReach = new double[height()][width()];

        for (double[] arr : costToReach) {
            Arrays.fill(arr, Double.POSITIVE_INFINITY);
        }

        Arrays.fill(costToReach[0], 1000);

        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                relaxChildrenOf(x, y, energyMatrix[y][x], costToReach, edgeFrom);
            }
        }

        double[] pathsCosts = costToReach[height() - 1];
        int shortestPathDest = -1;
        double shortestPathCost = Double.POSITIVE_INFINITY;

        for (int i = 0; i < pathsCosts.length; i++) {
            double pathCost = pathsCosts[i];
            if (pathCost < shortestPathCost) {
                shortestPathCost = pathCost;
                shortestPathDest = i;
            }
        }

        int[] sp = new int[height()];
        int xCoord = shortestPathDest;
        sp[height() - 1] = xCoord;
        for (int i = height() - 2; i >= 0; i--) {
            sp[i] = edgeFrom[i + 1][sp[i + 1]];
        }

        return sp;
    }

    private void relaxChildrenOf(int x, int y, double currentEnergy,
                                 double[][] costToReach, int[][] edgeFrom) {
        double currentCostToReach = costToReach[y][x];

        // i should check costToReach of all children
        // if i able to provide shortest costToReach
        // update costToReach and edgeFrom

        // relax left child
        for (int i = 0; i < 3; i++) {
            int childX = x - 1 + i;
            if (childX < 0 || childX > width() - 1) continue;

            double currentCostToReachChild = costToReach[y + 1][childX];
            double proposingCostToReachChild = currentEnergy + currentCostToReach;
            if (proposingCostToReachChild < currentCostToReachChild) {
                costToReach[y + 1][childX] = proposingCostToReachChild;
                edgeFrom[y + 1][childX] = x;
            }
        }
    }

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

    private void transposePicture() {
        isTransposed = !isTransposed;

        int width = width();
        int height = height();
        Picture trPic = new Picture(height, width);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                trPic.setRGB(y, x, picture.getRGB(x, y));
            }
        }

        picture = trPic;
    }
}
