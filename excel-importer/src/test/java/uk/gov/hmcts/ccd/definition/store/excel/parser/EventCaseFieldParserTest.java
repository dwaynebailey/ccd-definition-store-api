package uk.gov.hmcts.ccd.definition.store.excel.parser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.ccd.definition.store.domain.showcondition.InvalidShowConditionException;
import uk.gov.hmcts.ccd.definition.store.domain.showcondition.ShowCondition;
import uk.gov.hmcts.ccd.definition.store.domain.showcondition.ShowConditionParser;
import uk.gov.hmcts.ccd.definition.store.excel.parser.model.DefinitionDataItem;
import uk.gov.hmcts.ccd.definition.store.excel.parser.model.DisplayContextColumn;
import uk.gov.hmcts.ccd.definition.store.excel.util.mapper.ColumnName;
import uk.gov.hmcts.ccd.definition.store.repository.DisplayContext;
import uk.gov.hmcts.ccd.definition.store.repository.entity.CaseFieldEntity;
import uk.gov.hmcts.ccd.definition.store.repository.entity.EventCaseFieldEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EventCaseFieldParserTest {

    @Mock
    private EntityToDefinitionDataItemRegistry entityToDefinitionDataItemRegistry;

    @Mock
    private ShowConditionParser showConditionParser;

    @Mock
    private ParseContext parseContext;

    @InjectMocks
    private EventCaseFieldParser classUnderTest;

    private static final String PARSED_SHOW_CONDITION = "Parsed Show Condition";

    private static final CaseFieldEntity CASE_FIELD = new CaseFieldEntity();

    @Before
    public void setUp() throws InvalidShowConditionException {
        MockitoAnnotations.initMocks(this);
        when(parseContext.getCaseFieldForCaseType(any(),any())).thenReturn(CASE_FIELD);
        when(showConditionParser.parseShowCondition(any())).thenReturn(
            new ShowCondition.Builder().showConditionExpression(PARSED_SHOW_CONDITION).build()
        );
    }

    @Test
    public void definitionDataItemHasValidShowCondition_parseEventCaseFieldCalled_parsedEventEntityWithShowConditionSetToResultOfShowConditionParsingAddedToRegistryAndReturned() {

        String caseFieldId = "Case Field Id";
        String caseTypeId = "Case Type Id";
        String originalShowCondition = "Original Show Condition";
        DisplayContextColumn displayContext = new DisplayContextColumn("OPTIONAL", DisplayContext.OPTIONAL);

        DefinitionDataItem definitionDataItem = definitionDataItem(caseFieldId, displayContext, originalShowCondition);

        EventCaseFieldEntity eventCaseFieldEntity = classUnderTest.parseEventCaseField(caseTypeId, definitionDataItem);

        verify(entityToDefinitionDataItemRegistry)
            .addDefinitionDataItemForEntity(eq(eventCaseFieldEntity),eq(definitionDataItem));
        verify(parseContext).getCaseFieldForCaseType(eq(caseTypeId), eq(caseFieldId));

        assertEquals(CASE_FIELD, eventCaseFieldEntity.getCaseField());
        assertEquals(displayContext.getDisplayContext(), eventCaseFieldEntity.getDisplayContext());
        assertEquals(PARSED_SHOW_CONDITION, eventCaseFieldEntity.getShowCondition());

    }

    @Test
    public void definitionDataItemHasInValidShowCondition_parseEventCaseFieldCalled_parsedEventEntityWithShowConditionSetToOriginalShowConditionAddedToRegistryAndReturned()
        throws InvalidShowConditionException {

        String caseFieldId = "Case Field Id";
        String caseTypeId = "Case Type Id";
        String originalShowCondition = "Original Show Condition";
        DisplayContextColumn displayContext = new DisplayContextColumn("OPTIONAL", DisplayContext.OPTIONAL);

        DefinitionDataItem definitionDataItem = definitionDataItem(caseFieldId, displayContext, originalShowCondition);

        when(showConditionParser.parseShowCondition(any())).thenThrow(
            new InvalidShowConditionException("")
        );

        EventCaseFieldEntity eventCaseFieldEntity = classUnderTest.parseEventCaseField(caseTypeId, definitionDataItem);

        verify(entityToDefinitionDataItemRegistry)
            .addDefinitionDataItemForEntity(eq(eventCaseFieldEntity),eq(definitionDataItem));
        verify(parseContext).getCaseFieldForCaseType(eq(caseTypeId), eq(caseFieldId));

        assertEquals(CASE_FIELD, eventCaseFieldEntity.getCaseField());
        assertEquals(displayContext.getDisplayContext(), eventCaseFieldEntity.getDisplayContext());
        assertEquals(originalShowCondition, eventCaseFieldEntity.getShowCondition());

    }

    private DefinitionDataItem definitionDataItem(String caseFieldId, DisplayContextColumn displayContext, String showCondition) {
        DefinitionDataItem definitionDataItem = mock(DefinitionDataItem.class);

        when(definitionDataItem.getString(eq(ColumnName.CASE_FIELD_ID))).thenReturn(caseFieldId);
        when(definitionDataItem.getDisplayContext()).thenReturn(displayContext);
        when(definitionDataItem.getString(eq(ColumnName.FIELD_SHOW_CONDITION))).thenReturn(showCondition);

        return definitionDataItem;
    }
}
