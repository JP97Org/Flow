package org.jojo.flow.test.model.data;

import org.jojo.flow.exc.DataTypeIncompatException;
import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.data.BasicSignatureComponents;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.RawDataSet;
import org.junit.jupiter.api.*;

public class DataSignatureTest {
    @Test
    public void testDeactivateChecking() throws IllegalUnitOperationException {
        final RawDataSet dataOne = new RawDataSet(new byte[] {0, 1, 2});
        final RawDataSet dataTwo = new RawDataSet(new byte[] {3, 4, 5});
        final IDataSignature copy = dataOne.getDataSignature().getCopy();
        Assertions.assertEquals(dataOne.getDataSignature(), copy);
        Assertions.assertFalse(dataOne.getDataSignature() == copy);
        Assertions.assertTrue(dataOne.hasSameType(copy));
        copy.getComponent(BasicSignatureComponents.SIZES.index).deactivateChecking();
        Assertions.assertTrue(dataOne.hasSameType(copy));
        final RawDataSet dataThree = new RawDataSet(new byte[] {0, 1, 2, 3, 4});
        Assertions.assertTrue(dataThree.hasSameType(copy));
        Assertions.assertFalse(dataThree.hasSameType(dataOne.getDataSignature()));
        try {
            final DataArray arr = new DataArray(new Data[] {dataOne, dataTwo}, dataOne.getDataSignature());
            final IDataSignature arrCopy = arr.getDataSignature().getCopy();
            Assertions.assertEquals(arr.getDataSignature(), arrCopy);
            Assertions.assertFalse(arr.getDataSignature() == arrCopy);
            Assertions.assertEquals(2, arrCopy.size());
            Assertions.assertEquals(dataOne.getDataSignature(), arrCopy.getComponent(0));
            Assertions.assertEquals(dataTwo.getDataSignature(), arrCopy.getComponent(1));
            int i = 0;
            for (final IDataSignature data : arrCopy) {
                Assertions.assertEquals(data, arrCopy.getComponent(i));
                i++;
            }
            Assertions.assertEquals(2, i);
            arrCopy.getComponent(0).deactivateChecking();
            Assertions.assertTrue(dataOne.hasSameType(arrCopy.getComponent(0)));
            arrCopy.deactivateChecking();
            Assertions.assertTrue(dataOne.hasSameType(arrCopy));
            
        } catch (DataTypeIncompatException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
