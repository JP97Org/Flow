package org.jojo.flow.model.api;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.jojo.flow.model.data.DataArray;
import org.jojo.flow.model.data.DataBundle;
import org.jojo.flow.model.data.DataSignature;
import org.jojo.flow.model.data.DataVector;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.MathMatrix;
import org.jojo.flow.model.data.Matrix;
import org.jojo.flow.model.data.MultiMatrix;
import org.jojo.flow.model.data.RawDataSet;
import org.jojo.flow.model.data.ScalarDataSet;
import org.jojo.flow.model.data.StringDataSet;
import org.jojo.flow.model.data.Tensor;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader;

public interface IAPI {
    static final Map<Class<?>, Class<?>> apiToDefaultImplementationMap = new HashMap<>();
    
    static void initialize() { 
        // Basic Types
        apiToDefaultImplementationMap.put(IFraction.class, Fraction.class);
        // Data Signatures
        apiToDefaultImplementationMap.put(IDataSignature.class, DataSignature.DontCareDataSignature.class);
        // Basic Checkables
        apiToDefaultImplementationMap.put(IScalar.class, ScalarDataSet.class);
        apiToDefaultImplementationMap.put(IStringData.class, StringDataSet.class);
        apiToDefaultImplementationMap.put(IRaw.class, RawDataSet.class);
        apiToDefaultImplementationMap.put(IMatrix.class, Matrix.class);
        apiToDefaultImplementationMap.put(IMathMatrix.class, MathMatrix.class);
        apiToDefaultImplementationMap.put(ITensor.class, Tensor.class);
        apiToDefaultImplementationMap.put(IMultiMatrix.class, MultiMatrix.class);
        // Recursive Checkables
        apiToDefaultImplementationMap.put(IDataArray.class, DataArray.class);
        apiToDefaultImplementationMap.put(IDataVector.class, DataVector.class);
        apiToDefaultImplementationMap.put(IDataBundle.class, DataBundle.class);
    }
    
    static IAPI defaultImplementationOfThisApi(final Class<?>[] parameterTypes, final Object... initArgs) {
        final String iClassName;
        try { 
            throw new RuntimeException();
        } catch (RuntimeException e) {
            iClassName = e.getStackTrace()[1].getClassName();
        }
        try {
            final Class<?> iClass = Class.forName(iClassName);
            final Class<?> implClass = apiToDefaultImplementationMap.get(iClass);
            if (implClass != null) {
                final Object o = DynamicObjectLoader.load(IAPI.class.getClassLoader(), implClass.getName(), 
                            parameterTypes, initArgs);
                if (IAPI.class.isInstance(o) && implClass.isInstance(o)) {
                    return (IAPI) o;
                } else {
                    System.err.println("wrong API to default impl. mapping: " + iClass + " -> " + implClass);
                }
            }
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException e) {
            // should not happen
            e.printStackTrace();
        }
        return null;
    }
}
