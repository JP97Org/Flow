package org.jojo.flow.test.model.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jojo.flow.exc.DataTypeIncompatException;
import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.DataBundle;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.DataVector;
import org.jojo.flow.model.data.RawDataSet;
import org.jojo.flow.model.data.StringDataSet;
import org.junit.*;

public class RecursiveCheckablesTest {
    private RawDataSet dataOne;
    private RawDataSet dataTwo;
    private RawDataSet dataThree;
    private RawDataSet bundleDataOne;
    private StringDataSet bundleDataTwo;

    @Before
    public void setUp() {
        this.dataOne = new RawDataSet(new byte[] { 0, 1, 2 });
        this.dataTwo = new RawDataSet(new byte[] { 3, 4, 5 });
        this.dataThree = new RawDataSet(new byte[] { 6, 7, 8 });
        this.bundleDataOne = new RawDataSet(new byte[] { 0, 1, 2 });
        this.bundleDataTwo = new StringDataSet("Set 2");
    }

    @Test
    public void createDataArrayTest() throws IllegalUnitOperationException, ClassNotFoundException,
            IllegalArgumentException, IOException, DataTypeIncompatException {
        final DataArray arr = createDataArray();
        Assert.assertEquals(2, arr.size());
        Assert.assertEquals(dataOne, arr.get(0));
        Assert.assertEquals(dataTwo, arr.get(1));
        int i = 0;
        for (final IData data : arr) {
            Assert.assertEquals(data, arr.get(i));
            i++;
        }
        Assert.assertEquals(2, i);
        Assert.assertTrue(arr.isSizeConstant());

        final DataArray arrTwo = new DataArray(
                new Data[] { new RawDataSet(new byte[] { 0, 1, 2 }), new RawDataSet(new byte[] { 3, 4, 5 }) },
                dataOne.getDataSignature().getCopy());
        Assert.assertEquals(arr, arrTwo);

        Assert.assertEquals(arr.getDataSignature(), DataSignature.of(arr.getDataSignature().toString()));
    }

    private DataArray createDataArray() throws DataTypeIncompatException {
        return new DataArray(new Data[] { dataOne, dataTwo }, dataOne.getDataSignature());
    }
    
    private IDataSignature getDataArraySignature() throws DataTypeIncompatException {
        return createDataArray().getDataSignature();
    }

    @Test
    public void dataArrayNestedTest() throws DataTypeIncompatException {
        final DataArray nested = new DataArray(new Data[] {createDataArray(), createDataArray()}, getDataArraySignature());
        System.out.println(nested.getDataSignature());
        Assert.assertEquals(nested.getDataSignature(), DataSignature.of(nested.getDataSignature().toString()));
    }

    @Test
    public void createDataVectorTest() throws IllegalUnitOperationException, DataTypeIncompatException {
        final DataVector vec = createDataVector();
        Assert.assertEquals(2, vec.size());
        Assert.assertEquals(dataOne, vec.get(0));
        Assert.assertEquals(dataTwo, vec.get(1));
        int i = 0;
        for (final IData data : vec) {
            Assert.assertEquals(data, vec.get(i));
            i++;
        }
        Assert.assertEquals(2, i);
        Assert.assertFalse(vec.isSizeConstant());
        vec.add(dataThree);
        Assert.assertEquals(3, vec.size());
        Assert.assertEquals(dataThree, vec.get(2));
        i = 0;
        for (final IData data : vec) {
            Assert.assertEquals(data, vec.get(i));
            i++;
        }
        Assert.assertEquals(3, i);

        final DataVector vecTwo = new DataVector(
                Arrays.asList(new Data[] { new RawDataSet(new byte[] { 0, 1, 2 }),
                        new RawDataSet(new byte[] { 3, 4, 5 }), new RawDataSet(new byte[] { 6, 7, 8 }) }),
                dataOne.getDataSignature().getCopy());
        Assert.assertEquals(vec, vecTwo);

        Assert.assertEquals(vec.getDataSignature(), DataSignature.of(vec.getDataSignature().toString()));
    }
    
    private DataVector createDataVector() throws DataTypeIncompatException {
        return new DataVector(new ArrayList<>(Arrays.asList(new Data[] { dataOne, dataTwo })), dataOne.getDataSignature());
    }
    
    private IDataSignature getDataVectorSignature() throws DataTypeIncompatException {
        return createDataVector().getDataSignature();
    }
    
    @Test
    public void dataVectorNestedTest() throws DataTypeIncompatException {
        final DataVector nested = new DataVector(new ArrayList<>(Arrays.asList(new Data[] {createDataVector(), createDataVector()})), getDataVectorSignature());
        System.out.println(nested.getDataSignature());
        Assert.assertEquals(nested.getDataSignature(), DataSignature.of(nested.getDataSignature().toString()));
    }

    @Test
    public void createDataBundleTest() throws IllegalUnitOperationException, DataTypeIncompatException {
        final DataBundle arr = createDataBundle();
        Assert.assertEquals(2, arr.size());
        Assert.assertEquals(bundleDataOne, arr.get(0));
        Assert.assertEquals(bundleDataTwo, arr.get(1));
        int i = 0;
        for (final IData data : arr) {
            Assert.assertEquals(data, arr.get(i));
            i++;
        }
        Assert.assertEquals(2, i);
        Assert.assertTrue(arr.isSizeConstant());

        final DataBundle arrTwo = new DataBundle(
                new Data[] { new RawDataSet(new byte[] { 0, 1, 2 }), new StringDataSet("Set 2") });
        Assert.assertEquals(arr, arrTwo);

        DataSignature.of(arr.getDataSignature().toString());
        Assert.assertEquals(arr.getDataSignature(), DataSignature.of(arr.getDataSignature().toString()));
    }
    
    private DataBundle createDataBundle() throws DataTypeIncompatException {
        return new DataBundle(new Data[] { bundleDataOne, bundleDataTwo });
    }
    
    @Test
    public void dataBundleNestedTest() throws DataTypeIncompatException {
        final DataBundle nested = new DataBundle(new Data[] {createDataBundle(), createDataBundle()});
        System.out.println(nested.getDataSignature());
        Assert.assertEquals(nested.getDataSignature(), DataSignature.of(nested.getDataSignature().toString()));
    }
    
    @Test
    public void crossbundledNestedTest() throws DataTypeIncompatException {
        final DataBundle nested = new DataBundle(new Data[] {new DataBundle(new Data[] {createDataBundle(), createDataVector(), createDataArray()})});
        System.out.println(nested.getDataSignature());
        System.out.println(DataSignature.of(nested.getDataSignature().toString()));
        Assert.assertEquals(nested.getDataSignature().toString(), DataSignature.of(nested.getDataSignature().toString()).toString());
        Assert.assertEquals(nested.getDataSignature(), DataSignature.of(nested.getDataSignature().toString()));
    }
}
