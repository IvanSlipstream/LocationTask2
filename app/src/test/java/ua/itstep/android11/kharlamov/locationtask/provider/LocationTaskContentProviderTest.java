package ua.itstep.android11.kharlamov.locationtask.provider;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Slipstream on 02.10.2016 in LocationTask.
 */
public class LocationTaskContentProviderTest {

    private LocationTaskContentProvider provider;

    @Before
    public void before(){
//        provider = mock(LocationTaskContentProvider.class);
        provider = new LocationTaskContentProvider();
    }


    @Test
    public void testStatementConjunction() throws Exception {
//        when(Uri.parse(anyString())).thenReturn(null);
        String conjunction = provider.statementConjunction("clause1");
        assertEquals("clause1", conjunction);
        conjunction = provider.statementConjunction("clause1", "clause2");
        assertEquals("clause1 and clause2", conjunction);
        conjunction = provider.statementConjunction("clause1", "");
        assertEquals("clause1", conjunction);
        conjunction = provider.statementConjunction("clause1", "  ", "clause3");
        assertEquals("clause1 and clause3", conjunction);
        conjunction = provider.statementConjunction("", "clause 2");
        assertEquals("clause 2", conjunction);
    }
}