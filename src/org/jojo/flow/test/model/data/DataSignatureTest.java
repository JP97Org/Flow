package org.jojo.flow.test.model.data;

import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.data.BasicSignatureComponents;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.DataTypeIncompatException;
import org.jojo.flow.model.data.IllegalUnitOperationException;
import org.jojo.flow.model.data.RawDataSet;
import org.junit.*;

public class DataSignatureTest {
    @Test
    public void testDeactivateChecking() throws IllegalUnitOperationException {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final RawDataSet dataTwo = new RawDataSet(new byte[] {3, 4, 5});
        final IDataSignature copy = dataOne.getDataSignature().getCopy();
        Assert.assertEquals(dataOne.getDataSignature(), copy);
        Assert.assertFalse(dataOne.getDataSignature() == copy);
        Assert.assertTrue(dataOne.hasSameType(copy));
        copy.getComponent(BasicSignatureComponents.SIZES.index).deactivateChecking();
        Assert.assertTrue(dataOne.hasSameType(copy));
        final RawDataSet dataThree = new RawDataSet(new byte[] {0, 1, 2, 3, 4});
        Assert.assertTrue(dataThree.hasSameType(copy));
        Assert.assertFalse(dataThree.hasSameType(dataOne.getDataSignature()));
        try {
            final DataArray arr = new DataArray(new Data[] {dataOne, dataTwo}, dataOne.getDataSignature());
            final IDataSignature arrCopy = arr.getDataSignature().getCopy();
            Assert.assertEquals(arr.getDataSignature(), arrCopy);
            Assert.assertFalse(arr.getDataSignature() == arrCopy);
            Assert.assertEquals(2, arrCopy.size());
            Assert.assertEquals(dataOne.getDataSignature(), arrCopy.getComponent(0));
            Assert.assertEquals(dataTwo.getDataSignature(), arrCopy.getComponent(1));
            int i = 0;
            for (final IDataSignature data : arrCopy) {
                Assert.assertEquals(data, arrCopy.getComponent(i));
                i++;
            }
            Assert.assertEquals(2, i);
            arrCopy.getComponent(0).deactivateChecking();
            Assert.assertTrue(dataOne.hasSameType(arrCopy.getComponent(0)));
            arrCopy.deactivateChecking();
            Assert.assertTrue(dataOne.hasSameType(arrCopy));
            
        } catch (DataTypeIncompatException e) {
            e.printStackTrace();
            assert false;
        }
    }
    
    @Test
    public void testHashEfficientCopy() {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final IDataSignature heCopy = dataOne.getDataSignature().tryGetHashEfficientCopy();
        System.out.println("HashCode of HE-Copy: " + heCopy.hashCode());
        Assert.assertTrue(heCopy.isHashEfficient());
        final IDataSignature heCopyTwo = heCopy.tryGetHashEfficientCopy();
        System.out.println("HashCode of HE-Copy2: " + heCopyTwo.hashCode());
        Assert.assertEquals(heCopy, heCopyTwo);
        Assert.assertEquals(heCopy.hashCode(), heCopyTwo.hashCode());
        heCopy.deactivateChecking();
        Assert.assertFalse(heCopy.isHashEfficient());
        Assert.assertEquals(1, heCopy.hashCode());
    }
}
