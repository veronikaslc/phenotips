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
package org.phenotips.data.internal.controller;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.phenotips.data.Patient;
import org.phenotips.data.PatientData;
import org.phenotips.data.PatientDataController;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Test for the {@link VariantListController} Component, only the overridden methods from
 * {@link AbstractComplexController} are tested here
 */
public class VariantListControllerTest
{

    @Rule
    public MockitoComponentMockingRule<PatientDataController<Map<String, String>>> mocker =
        new MockitoComponentMockingRule<PatientDataController<Map<String, String>>>(VariantListController.class);

    @Mock
    private DocumentAccessBridge documentAccessBridge;

    @Mock
    private Patient patient;

    @Mock
    private XWikiDocument doc;

    @Mock
    private BaseObject obj1;

    @Mock
    private BaseObject obj2;

    @Mock
    private BaseObject obj3;

    private List<BaseObject> variantXWikiObjects;

    private static final String VARIANTS_STRING = "variants";

    private static final String CONTROLLER_NAME = VARIANTS_STRING;

    private static final String VARIANTS_ENABLING_FIELD_NAME = VARIANTS_STRING;

    private static final String VARIANTS_GENESYMBOL_ENABLING_FIELD_NAME = "variants_genesymbol";

    private static final String VARIANTS_PROTEIN_ENABLING_FIELD_NAME = "variants_protein";

    private static final String VARIANTS_TRANSCRIPT_ENABLING_FIELD_NAME = "variants_transcript";

    private static final String VARIANTS_DBSNP_ENABLING_FIELD_NAME = "variants_dbsnp";

    private static final String VARIANTS_ZYGOSITY_ENABLING_FIELD_NAME = "variants_zygosity";

    private static final String VARIANTS_EFFECT_ENABLING_FIELD_NAME = "variants_effect";

    private static final String VARIANTS_INTERPRETATION_ENABLING_FIELD_NAME = "variants_interpretation";

    private static final String VARIANTS_INHERITANCE_ENABLING_FIELD_NAME = "variants_inheritance";

    private static final String VARIANTS_EVIDENCE_ENABLING_FIELD_NAME = "variants_evidence";

    private static final String VARIANTS_SEGREGATION_ENABLING_FIELD_NAME = "variants_segregation";

    private static final String VARIANTS_SANGER_ENABLING_FIELD_NAME = "variants_sanger";

    private static final String VARIANTS_RESOLUTION_ENABLING_FIELD_NAME = "variants_resolution";

    private static final String VARIANT_KEY = "cdna";

    private static final String GENESYMBOL_KEY = "genesymbol";

    private static final String PROTEIN_KEY = "protein";

    private static final String TRANSCRIPT_KEY = "transcript";

    private static final String DBSNP_KEY = "dbsnp";

    private static final String ZYGOSITY_KEY = "zygosity";

    private static final String EFFECT_KEY = "effect";

    private static final String INTERPRETATION_KEY = "interpretation";

    private static final String INHERITANCE_KEY = "inheritance";

    private static final String EVIDENCE_KEY = "evidence";

    private static final String SEGREGATION_KEY = "segregation";

    private static final String SANGER_KEY = "sanger";

    private static final String RESOLUTION_KEY = "resolution";

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        this.documentAccessBridge = this.mocker.getInstance(DocumentAccessBridge.class);

        DocumentReference patientDocument = new DocumentReference("wiki", "patient", "00000001");
        doReturn(patientDocument).when(this.patient).getDocument();
        doReturn(this.doc).when(this.documentAccessBridge).getDocument(patientDocument);
    }

    @Test
    public void checkGetName() throws ComponentLookupException
    {
        Assert.assertEquals(CONTROLLER_NAME, this.mocker.getComponentUnderTest().getName());
    }

    @Test
    public void checkGetJsonPropertyName() throws ComponentLookupException
    {
        Assert.assertEquals(CONTROLLER_NAME,
            ((AbstractComplexController) this.mocker.getComponentUnderTest()).getJsonPropertyName());
    }

    @Test
    public void checkGetProperties() throws ComponentLookupException
    {
        List<String> result =
            ((AbstractComplexController<Map<String, String>>) this.mocker.getComponentUnderTest()).getProperties();

        Assert.assertEquals(13, result.size());
        Assert.assertThat(result, Matchers.hasItem(VARIANT_KEY));
        Assert.assertThat(result, Matchers.hasItem(GENESYMBOL_KEY));
        Assert.assertThat(result, Matchers.hasItem(PROTEIN_KEY));
        Assert.assertThat(result, Matchers.hasItem(TRANSCRIPT_KEY));
        Assert.assertThat(result, Matchers.hasItem(DBSNP_KEY));
        Assert.assertThat(result, Matchers.hasItem(ZYGOSITY_KEY));
        Assert.assertThat(result, Matchers.hasItem(EFFECT_KEY));
        Assert.assertThat(result, Matchers.hasItem(INTERPRETATION_KEY));
        Assert.assertThat(result, Matchers.hasItem(INHERITANCE_KEY));
        Assert.assertThat(result, Matchers.hasItem(EVIDENCE_KEY));
        Assert.assertThat(result, Matchers.hasItem(SEGREGATION_KEY));
        Assert.assertThat(result, Matchers.hasItem(SANGER_KEY));
        Assert.assertThat(result, Matchers.hasItem(RESOLUTION_KEY));
    }

    @Test
    public void checkGetBooleanFields() throws ComponentLookupException
    {
        Assert.assertTrue(
            ((AbstractComplexController) this.mocker.getComponentUnderTest()).getBooleanFields().isEmpty());
    }

    @Test
    public void checkGetCodeFields() throws ComponentLookupException
    {
        Assert.assertTrue(((AbstractComplexController) this.mocker.getComponentUnderTest()).getCodeFields().isEmpty());
    }

    // --------------------load() is Overridden from AbstractSimpleController--------------------

    @Test
    public void loadCatchesExceptionFromDocumentAccess() throws Exception
    {
        Exception exception = new Exception();
        doThrow(exception).when(this.documentAccessBridge).getDocument(any(DocumentReference.class));

        PatientData<Map<String, String>> result = this.mocker.getComponentUnderTest().load(this.patient);

        Assert.assertNull(result);
        verify(this.mocker.getMockedLogger()).error("Could not find requested document or some unforeseen "
            + "error has occurred during controller loading ", exception.getMessage());
    }

    @Test
    public void loadCatchesExceptionWhenPatientDoesNotHaveVariantClass() throws ComponentLookupException
    {
        doReturn(null).when(this.doc).getXObjects(any(EntityReference.class));

        PatientData<Map<String, String>> result = this.mocker.getComponentUnderTest().load(this.patient);

        Assert.assertNull(result);
    }

    // --------------------writeJSON() is Overridden from AbstractSimpleController--------------------



}
