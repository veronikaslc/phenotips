/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
package org.phenotips.data.indexing.internal;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.CapturingMatcher;
import org.phenotips.data.Feature;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientRepository;
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
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.test.mockito.MockitoComponentMockingRule;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;


import static org.mockito.Mockito.*;

public class SolrPatientIndexerTest {

    @Rule
    public MockitoComponentMockingRule<PatientIndexer> mocker =
            new MockitoComponentMockingRule<PatientIndexer>(SolrPatientIndexer.class);

    private SolrPatientIndexer solrPatientIndexer;

    private SolrCoreContainerHandler cores;

    @Mock
    private Logger logger;

    @Mock
    private Patient patient;

    @Mock
    private SolrClient server;

    private QueryManager qm;

    private PatientRepository patientRepository;

    private PermissionsManager permissions;

    private DocumentReference patientDocReference;

    @Before
    public void setUp() throws ComponentLookupException {

        MockitoAnnotations.initMocks(this);

        this.cores = this.mocker.getInstance(SolrCoreContainerHandler.class);

        this.logger = this.mocker.getMockedLogger();

        this.permissions = this.mocker.getInstance(PermissionsManager.class);

        this.qm = this.mocker.getInstance(QueryManager.class);

        this.patientRepository = this.mocker.getInstance(PatientRepository.class);

        this.patientDocReference = new DocumentReference("wiki", "patient", "P0000001");

        doReturn(mock(CoreContainer.class)).when(this.cores).getContainer();

        this.solrPatientIndexer = (SolrPatientIndexer) this.mocker.getComponentUnderTest();
        ReflectionUtils.setFieldValue(this.solrPatientIndexer, "server", this.server);
    }

    @Test
    public void indexDefaultBehaviourTest() throws IOException, SolrServerException {

        Set<Feature> patientFeatures = new HashSet<>();
        Feature testFeature = mock(Feature.class);
        patientFeatures.add(testFeature);
        DocumentReference reporterReference = new DocumentReference("xwiki", "XWiki", "user");
        PatientAccess patientAccess = mock(DefaultPatientAccess.class);
        Visibility patientVisibility =  new PublicVisibility();

        CapturingMatcher<SolrInputDocument> capturedArgument = new CapturingMatcher<SolrInputDocument>();
        when(this.server.add(argThat(capturedArgument))).thenReturn(mock(UpdateResponse.class));

        doReturn(patientDocReference).when(this.patient).getDocument();
        doReturn(reporterReference).when(this.patient).getReporter();

        doReturn(patientFeatures).when(this.patient).getFeatures();
        doReturn(true).when(testFeature).isPresent();
        doReturn("type").when(testFeature).getType();
        doReturn("id").when(testFeature).getId();

        doReturn(patientAccess).when(this.permissions).getPatientAccess(this.patient);
        doReturn(patientVisibility).when(patientAccess).getVisibility();

        this.solrPatientIndexer.index(this.patient);
        SolrInputDocument inputDoc = capturedArgument.getLastValue();
        verify(this.server).add(inputDoc);
        Assert.assertEquals(inputDoc.getFieldValue("visibility"), "public");
        Assert.assertEquals(inputDoc.getFieldValue("type"), "id");

    }

    @Test
    public void deleteDefaultBehaviourTest() throws IOException, SolrServerException {

        doReturn(this.patientDocReference).when(this.patient).getDocument();
        this.solrPatientIndexer.delete(this.patient);
        verify(this.server).deleteByQuery("document:"
                + ClientUtils.escapeQueryChars(this.patientDocReference.toString()));
        verify(this.server).commit();
    }

    @Test
    public void reindexDefaultBehaviour() throws QueryException, IOException, SolrServerException {
        List<String> patientDocs = new ArrayList<String>();
        patientDocs.add("P0000001");

        Query testQuery = mock(Query.class);
        doReturn(testQuery).when(this.qm).createQuery("from doc.object(PhenoTips.PatientClass) as patient", Query.XWQL);
        doReturn(patientDocs).when(testQuery).execute();
        doReturn(this.patient).when(this.patientRepository).getPatientById("P0000001");

        Set<Feature> patientFeatures = new HashSet<>();
        Feature testFeature = mock(PhenoTipsFeature.class);
        patientFeatures.add(testFeature);
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

        this.solrPatientIndexer.reindex();

        verify(this.server).deleteByQuery("*:*");
        verify(this.server).commit();

    }
}
