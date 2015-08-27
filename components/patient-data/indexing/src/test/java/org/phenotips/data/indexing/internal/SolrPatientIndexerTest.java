package org.phenotips.data.indexing.internal;

import javafx.fxml.Initializable;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.indexing.PatientIndexer;
import org.phenotips.data.internal.PhenoTipsFeature;
import org.phenotips.data.permissions.PatientAccess;
import org.phenotips.data.permissions.PermissionsManager;
import org.phenotips.data.permissions.Visibility;
import org.phenotips.data.permissions.internal.DefaultPatientAccess;
import org.phenotips.data.permissions.internal.visibility.PublicVisibility;
import org.phenotips.vocabulary.SolrCoreContainerHandler;
import org.slf4j.Logger;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.util.ReflectionUtils;
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

        this.server = mock(SolrClient.class);

        this.permissions = this.mocker.getInstance(PermissionsManager.class);

        this.patient = mock(Patient.class);

        doReturn(mock(CoreContainer.class)).when(this.cores).getContainer();

        this.solrPatientIndexer = (SolrPatientIndexer) this.mocker.getComponentUnderTest();
        ReflectionUtils.setFieldValue(this.solrPatientIndexer, "server", this.server);
    }

    @Test
    public void indexDefaultBehaviour() throws IOException, SolrServerException {

        Set<Feature> patientFeatures = new HashSet<>();
        Feature testFeature = mock(PhenoTipsFeature.class);
        patientFeatures.add(testFeature);
        DocumentReference patientDocReference = new DocumentReference("wiki", "patient", "P0000001");
        DocumentReference reporterReference = new DocumentReference("xwiki", "XWiki", "user");
        PatientAccess patientAccess = mock(DefaultPatientAccess.class);
        Visibility patientVisibility =  new PublicVisibility();

        doReturn(patientDocReference).when(this.patient).getDocument();
        doReturn(reporterReference).when(this.patient).getReporter();

        doReturn(patientFeatures).when(this.patient).getFeatures();
        doReturn(true).when(testFeature).isPresent();
        doReturn("type").when(testFeature).getType();
        doReturn("id").when(testFeature).getId();

        doReturn(patientAccess).when(this.permissions).getPatientAccess(this.patient);
        doReturn(patientVisibility).when(patientAccess).getVisibility();

        this.solrPatientIndexer.index(this.patient);
        verify(this.server).add(any(SolrInputDocument.class));
    }
}
