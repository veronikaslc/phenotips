package org.phenotips.data.indexing.internal;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContext;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;

public class PatientEmptyObjectsRemoverTest {

    @Rule
    public MockitoComponentMockingRule<AbstractEventListener> mocker =
            new MockitoComponentMockingRule<AbstractEventListener>(PatientEmptyObjectsRemover.class);
    @Mock
    private XWikiContext context;

    @Mock
    private XWiki xWiki;

    @Mock
    private ExecutionContext executionContext;

    private Execution execution;

    private PatientEmptyObjectsRemover patientEmptyObjectsRemover;

    private XWikiDocument xWikiDocument;

    @Before
    public void setUp() throws ComponentLookupException {
        MockitoAnnotations.initMocks(this);

        xWikiDocument = mock(XWikiDocument.class);

        this.execution = this.mocker.getInstance(Execution.class);
        doReturn(this.executionContext).when(this.execution).getContext();
        doReturn(this.context).when(this.executionContext).getProperty("xwikicontext");
        doReturn(this.xWiki).when(this.context).getWiki();

        this.patientEmptyObjectsRemover = (PatientEmptyObjectsRemover) this.mocker.getComponentUnderTest();

    }

    @Test
    public void emptyObjectRemovalTest() throws Exception {
        whenNew(XWikiDocument.class).withArguments(Object.class).thenReturn(xWikiDocument);
        doReturn(null).when(xWikiDocument).getXObject(any(EntityReference.class));
        this.patientEmptyObjectsRemover.onEvent(mock(Event.class), null, mock(Object.class));
        verify(this.xWiki).saveDocument(null, "Removed empty object", true, context);
    }
}
