package com.webank.blockchain.data.export.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class PropertiesUtilsTest {
    @Mock
    private Environment mockEnvironment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockEnvironment.getProperty("com.webank.blockchain")).thenReturn("expectedEnv");
    }

    @Test
    public void testGetProperty() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        ReflectionTestUtils.setField(propertiesUtils, "environment", mockEnvironment);
        final String env = propertiesUtils.getProperty("com", "webank", "blockchain");
        assertEquals("expectedEnv", env);
    }
}
