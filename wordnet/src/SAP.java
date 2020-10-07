/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        verifyParam(G);
        this.digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        verifyParams(v, w);
        verifyVertices(v, w);

        int ancestor = ancestor(v, w);
        if (ancestor == -1) return -1;
        return measureCommonAncestorDistance(v, w, ancestor);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        verifyParams(v, w);
        verifyVertices(v, w);

        return findCommonShortestAncestor(v, w);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        verifyParams(v, w);
        verifyVertices(v, w);

        int ancestor = ancestor(v, w);
        if (ancestor == -1) return -1;
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) return -1;
        return measureCommonAncestorDistance(v, w, ancestor);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        verifyParams(v, w);
        verifyVertices(v, w);

        return findCommonShortestAncestor(v, w);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    // Verification
    private void verifyParams(Object paramA, Object paramB) {
        verifyParam(paramA);
        verifyParam(paramB);
    }

    private void verifyParam(Object param) {
        if (param == null) throw new IllegalArgumentException("Param can't be null");
    }

    private void verifyVertices(Iterable<Integer> v, Iterable<Integer> w) {
        verifyVertices(v);
        verifyVertices(w);
    }

    private void verifyVertices(Iterable<Integer> v) {
        if (v == null)
            throw new IllegalArgumentException("Vertices cant be null");

        if (!v.iterator().hasNext())
            throw new IllegalArgumentException("Vertices cant be empty");

        for (Integer vertex : v) {
            if (vertex == null)
                throw new IllegalArgumentException("Vertex cant be null");
            else verifyVertex(vertex);
        }
    }

    private void verifyVertices(int vertexV, int vertexW) {
        verifyVertex(vertexV);
        verifyVertex(vertexW);
    }

    private void verifyVertex(int vertex) {
        if (vertex < 0) throw new IllegalArgumentException("Vertex is incorrect");
        if (vertex > digraph.V()) throw new IllegalArgumentException("Vertex is incorrect");
    }

    // Computation

    private int findCommonShortestAncestor(int v, int w) {
        BreadthFirstDirectedPaths bfsDiPathsV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDiPathsW = new BreadthFirstDirectedPaths(digraph, w);
        return findCommonShortestAncestor(bfsDiPathsV, bfsDiPathsW);
    }

    private int findCommonShortestAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfsDiPathsV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDiPathsW = new BreadthFirstDirectedPaths(digraph, w);
        return findCommonShortestAncestor(bfsDiPathsV, bfsDiPathsW);
    }

    private int findCommonShortestAncestor(BreadthFirstDirectedPaths bfsV,
                                           BreadthFirstDirectedPaths bfsW) {
        int size = digraph.V();
        int ancestor = -1;
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            if (!bfsV.hasPathTo(i) || !bfsW.hasPathTo(i)) continue;

            int vDist = bfsV.distTo(i);
            int wDist = bfsW.distTo(i);
            int iDist = vDist + wDist;

            if (iDist < distance) {
                ancestor = i;
                distance = iDist;
            }
        }

        return ancestor;
    }

    private int measureCommonAncestorDistance(int v, int w, int ancestor) {
        BreadthFirstDirectedPaths bfsDiPathsV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDiPathsW = new BreadthFirstDirectedPaths(digraph, w);
        return measureCommonAncestorDistance(bfsDiPathsV, bfsDiPathsW, ancestor);
    }

    private int measureCommonAncestorDistance(
            Iterable<Integer> v, Iterable<Integer> w, int ancestor) {
        BreadthFirstDirectedPaths bfsDiPathsV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsDiPathsW = new BreadthFirstDirectedPaths(digraph, w);
        return measureCommonAncestorDistance(bfsDiPathsV, bfsDiPathsW, ancestor);
    }

    private int measureCommonAncestorDistance(
            BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW, int ancestor) {

        if (!bfsV.hasPathTo(ancestor) || !bfsW.hasPathTo(ancestor))
            throw new IllegalArgumentException("Invalid vertices");

        return bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
    }
}
