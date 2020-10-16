package org.jojo.flow.test.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.DataBundle;
import org.jojo.flow.model.data.DataTypeIncompatException;
import org.jojo.flow.model.data.DataVector;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.RawDataSet;
import org.jojo.flow.model.data.StringDataSet;
import org.junit.*;

public class RecursiveCheckablesTest {
    @Test
    public void createDataArray() throws IllegalUnitOperationException {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final RawDataSet dataTwo = new RawDataSet(new byte[] {3, 4, 5});
        try {
            final DataArray arr = new DataArray(new Data[] {dataOne, dataTwo}, dataOne.getDataSignature());
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
            
            final DataArray arrTwo = new DataArray(new Data[] {
                    new RawDataSet(new byte[] {0, 1, 2}), 
                    new RawDataSet(new byte[] {3, 4, 5})}, dataOne.getDataSignature().getCopy());
            Assert.assertEquals(arr, arrTwo);
        } catch (DataTypeIncompatException e) {
            e.printStackTrace();
            assert false;
        }
    }
    
    @Test
    public void createDataVector() throws IllegalUnitOperationException {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final RawDataSet dataTwo = new RawDataSet(new byte[] {3, 4, 5});
        final RawDataSet dataThree = new RawDataSet(new byte[] {6, 7, 8});
        try {
            final List<IData> vecList = new ArrayList<>(Arrays.asList(new Data[] {dataOne, dataTwo}));
            final DataVector vec = new DataVector(vecList, dataOne.getDataSignature());
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
            
            final DataVector vecTwo = new DataVector(Arrays.asList(new Data[] {
                    new RawDataSet(new byte[] {0, 1, 2}), 
                    new RawDataSet(new byte[] {3, 4, 5}),
                    new RawDataSet(new byte[] {6, 7, 8})}), dataOne.getDataSignature().getCopy());
            Assert.assertEquals(vec, vecTwo);
        } catch (DataTypeIncompatException e) {
            e.printStackTrace();
            assert false;
        }
    }
    
    @Test
    public void createDataBundle() throws IllegalUnitOperationException {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final StringDataSet dataTwo = new StringDataSet("Set 2");
        try {
            final DataBundle arr = new DataBundle(new Data[] {dataOne, dataTwo});
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
            
            final DataBundle arrTwo = new DataBundle(new Data[] {
                    new RawDataSet(new byte[] {0, 1, 2}), 
                    new StringDataSet("Set 2")});
            Assert.assertEquals(arr, arrTwo);
        } catch (DataTypeIncompatException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
