package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BigConfigSerializationTest {

    @Test
    public void testBigConfigSerialization() throws IOException, ClassNotFoundException {
        String bigString = Collections.nCopies(30000, "a").toString();
        assert bigString.length() > 65535;

        Map<String, String> map = new HashMap<>();
        map.put("key", bigString);
        map.put("key2", SerializedConfigValue.LONG_STRING_KEY);

        Config config = ConfigFactory.parseMap(map);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);

        out.writeObject(config);
        out.close();

        ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(bIn);

        Config loadedCfg = (Config) objIn.readObject();

        assertEquals(bigString, loadedCfg.getString("key"));
        assertEquals(SerializedConfigValue.LONG_STRING_KEY, loadedCfg.getString("key2"));
    }
}
