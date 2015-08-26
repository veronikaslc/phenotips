package org.phenotips.data.indexing.internal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.phenotips.data.Patient;
import org.phenotips.data.events.PatientDeletedEvent;
import org.phenotips.data.events.PatientDeletingEvent;
import org.phenotips.data.events.PatientEvent;
import org.phenotips.data.indexing.PatientIndexer;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class PatientEventListenerTest {

    @Rule
    public MockitoComponentMockingRule<EventListener> mocker =
            new MockitoComponentMockingRule<EventListener>(PatientEventListener.class);

    @Mock
    private PatientIndexer patientIndexer;

    @Mock
    private Patient patient;

    private PatientEventListener patientEventListener;

    @Before
    public void setUp() throws ComponentLookupException {
        MockitoAnnotations.initMocks(this);

        this.patientEventListener = (PatientEventListener) this.mocker.getComponentUnderTest();
        this.patientIndexer = this.mocker.getInstance(PatientIndexer.class);
    }

    @Test
    public void deletePatientTest() {
        Event patientDeleteEvent = mock(PatientDeletedEvent.class);
        doReturn(this.patient).when((PatientEvent) patientDeleteEvent).getPatient();

        this.patientEventListener.onEvent(patientDeleteEvent, mock(Object.class), mock(Object.class));
        verify(this.patientIndexer).delete(this.patient);
    }

    @Test
    public void indexPatientTest() {
        Event patientEvent = mock(PatientEvent.class);
        doReturn(this.patient).when((PatientEvent) patientEvent).getPatient();

        this.patientEventListener.onEvent(patientEvent, mock(Object.class), mock(Object.class));
        verify(this.patientIndexer).index(this.patient);
    }
}
