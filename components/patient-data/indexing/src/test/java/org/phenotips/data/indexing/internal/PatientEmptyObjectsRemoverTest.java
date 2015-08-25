package org.phenotips.data.indexing.internal;

import com.xpn.xwiki.XWikiContext;
import org.junit.Before;
import org.junit.Rule;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.EventListener;

public class PatientEmptyObjectsRemoverTest {

    @Rule
    public MockitoComponentMockingRule<AbstractEventListener> mocker =
            new MockitoComponentMockingRule<AbstractEventListener>(PatientEmptyObjectsRemover.class);

    private XWikiContext context;

    

    @Before
    public void setUp() {

    }
}
