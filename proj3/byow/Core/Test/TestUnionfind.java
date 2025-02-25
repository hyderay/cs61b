package byow.Core.Test;

import static org.junit.Assert.*;
import org.junit.Test;

import byow.Core.UnionFind;

public class TestUnionfind {
    @Test
    public void testUnion() {
        UnionFind uf = new UnionFind(10);
        uf.union(2, 3);
        assertEquals(2, uf.find(3));
    }

    @Test
    public void testMultipleUnion() {
        UnionFind uf = new UnionFind(10);
        uf.union(2, 3);
        uf.union(2, 4);
        uf.union(2, 5);
        assertEquals(uf.find(3), uf.find(5));
    }

    @Test
    public void testUnionWithCompression() {
        UnionFind uf = new UnionFind(10);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(3, 5);
        assertEquals(uf.find(5), uf.find(3));
    }
}
