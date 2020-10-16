package org.jojo.flow.test.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.MultiMatrix;
import org.junit.*;

public class MultiMatrixTest {
    @Test
    public void create() throws IllegalUnitOperationException {
        final MultiMatrix<Double> matrix = new MultiMatrix<>(new int[] {1,2,3}, new Double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6}, UnitSignature.FARAD);
        Assert.assertEquals(BasicType.DOUBLE, matrix.getBasicType());
        Assert.assertEquals("[[[0.1, 0.2, 0.3], [0.4, 0.5, 0.6]]] m^-2 * kg^-1 * s^4 * A^2", matrix.toString());
        
        Assert.assertEquals(0.1, matrix.get(new int[] {0,0,0}).doubleValue(), 0);
        Assert.assertEquals(0.2, matrix.get(new int[] {0,0,1}).doubleValue(), 0);
        Assert.assertEquals(0.3, matrix.get(new int[] {0,0,2}).doubleValue(), 0);
        Assert.assertEquals(0.4, matrix.get(new int[] {0,1,0}).doubleValue(), 0);
        Assert.assertEquals(0.5, matrix.get(new int[] {0,1,1}).doubleValue(), 0);
        Assert.assertEquals(0.6, matrix.get(new int[] {0,1,2}).doubleValue(), 0);
        Assert.assertArrayEquals(new Double[] {0.1, 0.2, 0.3}, matrix.getRow(new int[] {0,0}));
        Assert.assertArrayEquals(new Double[] {0.4, 0.5, 0.6}, matrix.getRow(new int[] {0,1}));
        matrix.set(2., new int[] {0, 1, 1});
        Assert.assertEquals(2., matrix.get(new int[] {0,1,1}).doubleValue(), 0);
        Assert.assertEquals(UnitSignature.FARAD, matrix.getUnitSignature());
    }
    
    @Test
    public void createBig() {
        final int[] sizes = new int[] {2, 4, 7, 6, 3, 9};
        final Integer[][][][][][] arr6d = 
                new Integer[sizes[0]][sizes[1]][sizes[2]][sizes[3]][sizes[4]][sizes[5]];
        final List<Integer> data = new ArrayList<>();
        final int len = Arrays.stream(sizes).reduce(1, (a, b) -> a * b);
        final int[][] allIndices = new int[len][sizes.length];
        final int[] indices = new int[sizes.length];
        for (int i = 0; i < len; i++) {
            final int val = Integer.parseInt("" + Arrays
                    .stream(indices)
                    .mapToObj(x -> Integer.toString(x))
                    .reduce("", (a, b) -> a + b));
            allIndices[i] = new int[]{indices[0], indices[1], indices[2], indices[3], indices[4], indices[5]};
            arr6d[indices[0]][indices[1]][indices[2]][indices[3]][indices[4]][indices[5]] = val;
            data.add(val);
            incrementIndex(indices, sizes, indices.length - 1);
        }
        final MultiMatrix<Integer> matrix = new MultiMatrix<>(sizes, data.toArray(Integer[]::new), UnitSignature.FARAD);
        for (final int[] getIndices : allIndices) {
            final int val = Integer.parseInt("" + Arrays
                    .stream(getIndices)
                    .mapToObj(x -> Integer.toString(x))
                    .reduce("", (a, b) -> a + b));
            Assert.assertEquals(val, matrix.get(getIndices).intValue());
        }
        Assert.assertArrayEquals(arr6d, matrix.toArray());
        Assert.assertEquals(Arrays.deepToString(arr6d) + " " + UnitSignature.FARAD, matrix.toString());
    }

    private void incrementIndex(final int[] indices, final int[] sizes, final int indicesIndex) {
        if (indicesIndex == -1) {
            return;
        }
        
        indices[indicesIndex]++;
        if (indices[indicesIndex] == sizes[indicesIndex]) {
            indices[indicesIndex] = 0;
            incrementIndex(indices, sizes, indicesIndex - 1);
        }
    }
}
