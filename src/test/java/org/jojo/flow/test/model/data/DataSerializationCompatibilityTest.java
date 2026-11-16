package org.jojo.flow.test.model.data;

import java.util.Base64;

import org.jojo.flow.model.api.IXMLSerialTransform;
import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.data.StringDataSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataSerializationCompatibilityTest {

    @Test
    public void loadsArrayClassDescriptorsWithSlashSeparatedNames() throws Exception {
        final byte[] serialized = Base64.getDecoder()
                .decode(IXMLSerialTransform.serialize(new StringDataSet("")));
        Assertions.assertTrue(useLegacyArrayClassNames(serialized),
                "serialized data does not contain an array class descriptor");

        final Data data = Data.ofSerializedString(Base64.getEncoder().encodeToString(serialized));

        Assertions.assertInstanceOf(StringDataSet.class, data);
    }

    private static boolean useLegacyArrayClassNames(final byte[] serialized) {
        final int classDescMarker = 0x72;
        for (int i = 0; i + 3 < serialized.length; i++) {
            if (serialized[i] != classDescMarker) {
                continue;
            }
            final int length = ((serialized[i + 1] & 0xFF) << 8) | (serialized[i + 2] & 0xFF);
            final int start = i + 3;
            final int end = start + length;
            if (length <= 0 || end > serialized.length || serialized[start] != '['
                    || serialized[end - 1] != ';') {
                continue;
            }
            boolean changed = false;
            for (int j = start; j < end; j++) {
                if (serialized[j] == '.') {
                    serialized[j] = '/';
                    changed = true;
                }
            }
            if (changed) {
                return true;
            }
        }
        return false;
    }
}
