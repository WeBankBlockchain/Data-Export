package com.webank.blockchain.data.export.grafana;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.webank.blockchain.data.export.config.ServiceConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class GrafanaDashboardParasTest {
    private static String PREFIX = "prefix";
    private static String SUFFIX = "suffix";
    private static List<String> INFOLIST = ImmutableList.of("test-str1");

    @Mock
    private ServiceConfig mockServiceConfig;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockServiceConfig.getTablePrefix()).thenReturn(PREFIX);
        when(mockServiceConfig.getTablePostfix()).thenReturn(SUFFIX);

    }

    @Test
    public void testGetMap() {
        Map<String, Object> expectedMap = ImmutableMap.of(
                "panels", INFOLIST,
                "block_task_pool",  PREFIX + "block_task_pool" + SUFFIX,
                "block_detail_info", PREFIX + "block_detail_info" + SUFFIX,
                "block_tx_detail_info", PREFIX + "block_tx_detail_info" + SUFFIX,
                "deployed_account_info", PREFIX + "deployed_account_info" + SUFFIX
        );
        GrafanaDashboardParas grafanaDashboardParas = new GrafanaDashboardParas();
        ReflectionTestUtils.setField(grafanaDashboardParas, "config", mockServiceConfig);
        Map<String, Object> actualMap = grafanaDashboardParas.getMap(INFOLIST);
        assertTrue(Maps.difference(expectedMap, actualMap).areEqual());
    }
}
