package org.phenotips.data.indexing.internal;

import javafx.fxml.Initializable;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.indexing.PatientIndexer;
import org.phenotips.data.internal.PhenoTipsFeature;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.vocabulary.SolrCoreContainerHandler;
import org.slf4j.Logger;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SolrPatientIndexerTest {

    @Rule
    public MockitoComponentMockingRule<PatientIndexer> mocker =
            new MockitoComponentMockingRule<PatientIndexer>(SolrPatientIndexer.class);

    private SolrPatientIndexer solrPatientIndexer;

    @Mock
    private SolrCoreContainerHandler cores;

    @Mock
    private Logger logger;

    @Mock
    private Patient patient;

    @Mock
    private SolrClient server;

    @Mock
    private PermissionsManager permissions;

    @Before
    public void setUp() throws ComponentLookupException {

        MockitoAnnotations.initMocks(this);

        this.cores = this.mocker.getInstance(SolrCoreContainerHandler.class);

        this.logger = this.mocker.getMockedLogger();

        this.server = this.mocker.getInstance(SolrClient.class);

        this.permissions = this.mocker.getInstance(PermissionsManager.class);

        this.patient = mock(Patient.class);

        this.solrPatientIndexer = (SolrPatientIndexer) this.mocker.getComponentUnderTest();
    }

    @Test
    public void indexDefaultBehaviour() throws IOException, SolrServerException {

        DocumentReference reporterReference = mock(DocumentReference.class);
        Set<Feature> patientFeatures = new HashSet<>();
        Feature testFeature = mock(PhenoTipsFeature.class);
        patientFeatures.add(testFeature);

        doReturn("patient document reference").when(this.patient).getDocument().toString();
        doReturn(reporterReference).when(this.patient).getReporter();
        doReturn("reporter reference").when(this.patient).getReporter().toString();

        doReturn(patientFeatures).when(this.patient).getFeatures();
        doReturn(true).when(testFeature).isPresent();
        doReturn("type").when(testFeature).getType();
        doReturn("id").when(testFeature).getId();

        doReturn("visibility level").when(this.permissions).getPatientAccess(this.patient).getVisibility().getName();
        doReturn("access level").when(this.permissions).getPatientAccess(this.patient).getVisibility().getPermissiveness();

        this.solrPatientIndexer.index(this.patient);
        verify(this.server).add(any(SolrInputDocument.class));

    }
}
