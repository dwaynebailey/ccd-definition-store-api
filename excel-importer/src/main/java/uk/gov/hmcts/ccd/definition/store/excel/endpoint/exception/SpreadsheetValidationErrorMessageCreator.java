package uk.gov.hmcts.ccd.definition.store.excel.endpoint.exception;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.ccd.definition.store.domain.validation.SimpleValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.ValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.ValidationErrorMessageCreator;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casefield
    .CaseFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casefield.CaseFieldEntityInvalidCrudValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casefield.CaseFieldEntityInvalidUserRoleValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casefield
    .CaseFieldEntityMissingSecurityClassificationValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casetype.CaseTypeEntityInvalidCrudValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casetype.CaseTypeEntityInvalidUserRoleValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.casetype
    .CaseTypeEntityMissingSecurityClassificationValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.complexfield
    .ComplexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.complexfield
    .ComplexFieldEntityMissingSecurityClassificationValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.complexfield.ComplexFieldInvalidShowConditionError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.complexfield
    .ComplexFieldShowConditionReferencesInvalidFieldError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.displaygroup.*;
import uk.gov.hmcts.ccd.definition.store.domain.validation.event.*;
import uk.gov.hmcts.ccd.definition.store.domain.validation.eventcasefield.EventCaseFieldDisplayContextValidatorImpl;
import uk.gov.hmcts.ccd.definition.store.domain.validation.eventcasefield.EventCaseFieldEntityInvalidShowConditionError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.eventcasefield
    .EventCaseFieldEntityWithShowConditionReferencesInvalidCaseFieldError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.eventcasefield.LabelTypeCannotBeEditableValidationError;
import uk.gov.hmcts.ccd.definition.store.domain.validation.genericlayout.GenericLayoutEntityValidatorImpl;
import uk.gov.hmcts.ccd.definition.store.domain.validation.state.StateEntityCrudValidatorImpl;
import uk.gov.hmcts.ccd.definition.store.domain.validation.state.StateEntityUserRoleValidatorImpl;
import uk.gov.hmcts.ccd.definition.store.domain.validation.userprofile.UserProfileValidatorImpl;
import uk.gov.hmcts.ccd.definition.store.excel.parser.EntityToDefinitionDataItemRegistry;
import uk.gov.hmcts.ccd.definition.store.excel.parser.model.DefinitionDataItem;
import uk.gov.hmcts.ccd.definition.store.excel.util.mapper.ColumnName;
import uk.gov.hmcts.ccd.definition.store.excel.util.mapper.SheetName;

import java.util.Optional;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static uk.gov.hmcts.ccd.definition.store.excel.util.mapper.SheetName.CASE_FIELD;
import static uk.gov.hmcts.ccd.definition.store.excel.util.mapper.SheetName.CASE_TYPE;

@Component
public class SpreadsheetValidationErrorMessageCreator implements ValidationErrorMessageCreator {

    private EntityToDefinitionDataItemRegistry entityToDefinitionDataItemRegistry;

    public SpreadsheetValidationErrorMessageCreator(EntityToDefinitionDataItemRegistry
                                                        entityToDefinitionDataItemRegistry) {
        this.entityToDefinitionDataItemRegistry = entityToDefinitionDataItemRegistry;
    }

    @Override
    public String createErrorMessage(CaseTypeEntityMissingSecurityClassificationValidationError
                                             caseTypeEntityMissingSecurityClassificationValidationError) {
        return createMissingOrInvalidSecurityClassificationMessage(
            caseTypeEntityMissingSecurityClassificationValidationError.getCaseTypeEntity(),
            caseTypeEntityMissingSecurityClassificationValidationError.getCaseTypeEntity().getReference(),
            caseTypeEntityMissingSecurityClassificationValidationError.getDefaultMessage());
    }

    @Override
    public String createErrorMessage(CaseFieldEntityMissingSecurityClassificationValidationError
                                             caseFieldEntityMissingSecurityClassificationValidationError) {
        return createMissingOrInvalidSecurityClassificationMessage(
            caseFieldEntityMissingSecurityClassificationValidationError.getCaseFieldEntity(),
            caseFieldEntityMissingSecurityClassificationValidationError.getCaseFieldEntity().getReference(),
            caseFieldEntityMissingSecurityClassificationValidationError.getDefaultMessage());
    }

    @Override
    public String createErrorMessage(CaseFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                             validationError) {
        return String.format("%s values cannot have lower security classification than case type; " + "%s entry with " +
                                 "" + "" + "id '%s' has a security classification of '%s' " + "but %s '%s' has a " +
                                 "security " + "" + "classification of '%s'",
                             CASE_FIELD.getName(),
                             CASE_FIELD.getName(),
                             validationError.getCaseFieldEntity().getReference(),
                             capitalize(validationError.getCaseFieldEntity()
                                            .getSecurityClassification()
                                            .toString()
                                            .toLowerCase()),
                             CASE_TYPE.getName(),
                             validationError.getCaseFieldEntityValidationContext().getCaseName(),
                             capitalize(validationError.getCaseFieldEntityValidationContext()
                                            .getParentSecurityClassification()
                                            .toString()
                                            .toLowerCase()));
    }

    @Override
    public String createErrorMessage(ComplexFieldEntityMissingSecurityClassificationValidationError
                                             complexFieldEntityMissingSecurityClassificationValidationError) {

        return createMissingOrInvalidSecurityClassificationMessage(
            complexFieldEntityMissingSecurityClassificationValidationError.getComplexFieldEntity(),
            complexFieldEntityMissingSecurityClassificationValidationError.getComplexFieldEntity().getReference(),
            complexFieldEntityMissingSecurityClassificationValidationError.getDefaultMessage());

    }

    @Override
    public String createErrorMessage
        (ComplexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
             complexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError) {
        return String.format("%s values cannot have lower security classification than case field; " + "%s entry " +
                                 "with" + " id '%s' has a security classification of '%s' " + "but %s entry with id "
                                 + "'%s' has a" + " security classification of '%s'",
                             SheetName.COMPLEX_TYPES.getName(),
                             SheetName.COMPLEX_TYPES.getName(),
                             complexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                 .getComplexFieldEntity()
                                 .getReference(),
                             capitalize(
                                 complexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                     .getComplexFieldEntity()
                                     .getSecurityClassification()
                                     .toString()
                                     .toLowerCase()),
                             CASE_FIELD.getName(),
                             complexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                 .getComplexFieldEntityValidationContext()
                                 .getCaseFieldReference(),
                             capitalize(
                                 complexFieldEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                     .getComplexFieldEntityValidationContext()
                                     .getParentSecurityClassification()
                                     .toString()
                                     .toLowerCase()));
    }

    @Override
    public String createErrorMessage(EventEntityMissingSecurityClassificationValidationError
                                             eventEntityMissingSecurityClassificationValidationError) {

        return createMissingOrInvalidSecurityClassificationMessage(
            eventEntityMissingSecurityClassificationValidationError.getEventEntity(),
            eventEntityMissingSecurityClassificationValidationError.getEventEntity().getReference(),
            eventEntityMissingSecurityClassificationValidationError.getDefaultMessage());

    }

    @Override
    public String createErrorMessage(EventEntityHasLessRestrictiveSecurityClassificationThanParentValidationError
                                             validationError) {
        return String.format("%s values cannot have lower security classification than case type; " + "%s entry with " +
                                 "" + "id '%s' has a security classification of '%s' " + "but %s '%s' has a security " +
                                 "" + "classification of '%s'",
                             SheetName.CASE_EVENT.getName(),
                             SheetName.CASE_EVENT.getName(),
                             validationError.getEventEntity().getReference(),
                             capitalize(validationError.getEventEntity()
                                            .getSecurityClassification()
                                            .toString()
                                            .toLowerCase()),
                             CASE_TYPE.getName(),
                             validationError.getEventEntityValidationContext().getCaseName(),
                             capitalize(validationError.getEventEntityValidationContext()
                                            .getParentSecurityClassification()
                                            .toString()
                                            .toLowerCase()));
    }

    @Override
    public String createErrorMessage(final CaseTypeEntityInvalidUserRoleValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getCaseTypeUserRoleEntity(),
                                            def -> String.format("Invalid IdamRole '%s' in %s tab for case type '%s'",
                                                                 def.getString(ColumnName.USER_ROLE),
                                                                 def.getSheetName(),
                                                                 error.getAuthorisationValidationContext()
                                                                     .getCaseReference()));
    }

    @Override
    public String createErrorMessage(final CaseTypeEntityInvalidCrudValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getCaseTypeUserRoleEntity(),
                                            def -> String.format(
                                                "Invalid CRUD value '%s' in %s tab for case type '%s', user role '%s'",
                                                def.getString(ColumnName.CRUD),
                                                def.getSheetName(),
                                                error.getAuthorisationValidationContext().getCaseReference(),
                                                def.getString(ColumnName.USER_ROLE)));
    }

    @Override
    public String createErrorMessage(final CaseFieldEntityInvalidCrudValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getCaseFieldUserRoleEntity(),
                                            def -> String.format(
                                                "Invalid CRUD value '%s' in %s tab, case type '%s', case field '%s', " +
                                                    "" + "" + "user role '%s'",
                                                defaultString(def.getString(ColumnName.CRUD)),
                                                def.getSheetName(),
                                                def.getString(ColumnName.CASE_TYPE_ID),
                                                def.getString(ColumnName.CASE_FIELD_ID),
                                                defaultString(def.getString(ColumnName.USER_ROLE))));
    }

    @Override
    public String createErrorMessage(final CaseFieldEntityInvalidUserRoleValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getCaseFieldUserRoleEntity(),
                                            def -> String.format(
                                                "Invalid IdamRole '%s' in %s tab, case type '%s', case field '%s', "
                                                    + "crud '%s'",
                                                defaultString(def.getString(ColumnName.USER_ROLE)),
                                                def.getSheetName(),
                                                def.getString(ColumnName.CASE_TYPE_ID),
                                                def.getString(ColumnName.CASE_FIELD_ID),
                                                defaultString(def.getString(ColumnName.CRUD))));
    }

    @Override
    public String createErrorMessage(final EventEntityInvalidCrudValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getEventUserRoleEntity(),
                                            def -> String.format(
                                                "Invalid CRUD value '%s' in %s tab, case type '%s', event '%s', user " +
                                                    "" + "" + "role '%s'",
                                                defaultString(def.getString(ColumnName.CRUD)),
                                                def.getSheetName(),
                                                error.getAuthorisationEventValidationContext().getCaseReference(),
                                                error.getAuthorisationEventValidationContext().getEventReference(),
                                                defaultString(def.getString(ColumnName.USER_ROLE))));
    }

    @Override
    public String createErrorMessage(final EventEntityInvalidUserRoleValidationError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getEventUserRoleEntity(),
                                            def -> String.format(
                                                "Invalid IdamRole '%s' in %s tab, case type '%s', event '%s', crud "
                                                    + "'%s'",
                                                defaultString(def.getString(ColumnName.USER_ROLE)),
                                                def.getSheetName(),
                                                error.getAuthorisationEventValidationContext().getCaseReference(),
                                                error.getAuthorisationEventValidationContext().getEventReference(),
                                                defaultString(def.getString(ColumnName.CRUD))));
    }

    public String createErrorMessage(LabelTypeCannotBeEditableValidationError
                                         labelTypeCannotBeEditableValidationError) {
        return String.format("%s is Label type and cannot be editable for the %s in the tab %s",
                             labelTypeCannotBeEditableValidationError.getEventCaseFieldEntity()
                                 .getCaseField()
                                 .getReference(),
                             labelTypeCannotBeEditableValidationError.getEventCaseFieldEntity()
                                 .getEvent()
                                 .getReference(),
                             SheetName.CASE_EVENT_TO_FIELDS.getName());
    }

    @Override
    public String createErrorMessage(CreateEventDoesNotHavePostStateValidationError error) {
        return newMessageIfDefinitionExists(error, error.getEventEntity(), def -> {
            String postConditionValue = def.getString(ColumnName.POST_CONDITION_STATE);
            return String.format("Event '%s' is invalid create event as Postcondition is %s in %s tab",
                                 error.getEventEntity().getReference(),
                                 StringUtils.isEmpty(postConditionValue) ? "not defined" : postConditionValue,
                                 def.getSheetName());
        });
    }

    @Override
    public String createErrorMessage(EventCaseFieldEntityWithShowConditionReferencesInvalidCaseFieldError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getEventCaseFieldEntity(),
                                            def -> String.format(
                                                "Unknown field '%s' for event '%s' in show condition: '%s' on tab '%s'",
                                                error.getShowConditionField(),
                                                error.getEventId(),
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                def.getSheetName()));

    }

    @Override
    public String createErrorMessage(EventCaseFieldEntityInvalidShowConditionError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getEventCaseFieldEntity(),
                                            def -> String.format(
                                                "Invalid show condition '%s' for event '%s' on tab '%s'",
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                error.getValidationContext().getEventId(),
                                                def.getSheetName()));
    }

    @Override
    public String createErrorMessage(EventEntityMissingForPageTypeDisplayGroupError eventMissingError) {
        return String.format("Event is missing for displayGroup '%s' and with label '%s'",
                             eventMissingError.getCaseTypeUserRoleEntity().getReference(),
                             eventMissingError.getCaseTypeUserRoleEntity().getLabel());
    }

    @Override
    public String createErrorMessage(DisplayGroupInvalidShowConditionError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getDisplayGroupEntity(),
                                            def -> String.format(
                                                "Invalid show condition '%s' for display group '%s' on tab '%s'",
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                error.getDisplayGroupEntity().getReference(),
                                                def.getSheetName()));
    }

    @Override
    public String createErrorMessage(DisplayGroupInvalidEventFieldShowCondition error) {
        return newMessageIfDefinitionExists(error,
                                            error.getDisplayGroup(),
                                            def -> String.format(
                                                "Invalid show condition '%s' for display group '%s' on tab '%s': " +
                                                    "unknown field '%s' for event '%s'",
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                error.getDisplayGroup().getReference(),
                                                def.getSheetName(),
                                                error.getShowConditionField(),
                                                error.getDisplayGroup().getEvent().getReference()));
    }

    @Override
    public String createErrorMessage(DisplayGroupInvalidTabShowCondition error) {
        return newMessageIfDefinitionExists(error,
                                            error.getDisplayGroup(),
                                            error.getShowConditionField() != null ?
                                                def -> String.format(
                                                    "Invalid show condition '%s' for tab '%s' on spreadsheet tab '%s': " +
                                                        "unknown field '%s'",
                                                    def.getString(ColumnName.TAB_SHOW_CONDITION),
                                                    error.getDisplayGroup().getReference(),
                                                    def.getSheetName(),
                                                    error.getShowConditionField()) :
                                                def ->  error.getDefaultMessage());
    }

    @Override
    public String createErrorMessage(DisplayGroupInvalidTabFieldShowCondition error) {
        return newMessageIfDefinitionExists(error,
                                            error.getDisplayGroupCaseField(),
                                            error.getShowConditionField() != null ?
                                                def -> String.format(
                                                    "Invalid show condition '%s' for tab field '%s' on spreadsheet tab '%s': " +
                                                        "unknown field '%s'",
                                                    def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                    error.getDisplayGroupCaseField().getCaseField().getReference(),
                                                    def.getSheetName(),
                                                    error.getShowConditionField()) :
                                                def ->  error.getDefaultMessage()
        );
    }

    @Override
    public String createErrorMessage(ComplexFieldInvalidShowConditionError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getComplexField(),
                                            def -> String.format(
                                                "Invalid show condition '%s' for complex field element '%s' on tab "
                                                    + "'%s'",
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                error.getComplexField().getReference(),
                                                def.getSheetName()));
    }

    @Override
    public String createErrorMessage(ComplexFieldShowConditionReferencesInvalidFieldError error) {
        return newMessageIfDefinitionExists(error,
                                            error.getComplexField(),
                                            def -> String.format(
                                                "Unknown field '%s' of complex field '%s' in show condition: '%s' on " +
                                                    "" + "tab '%s'",
                                                error.getShowConditionField(),
                                                error.getComplexField().getComplexFieldType().getReference(),
                                                def.getString(ColumnName.FIELD_SHOW_CONDITION),
                                                def.getSheetName()));
    }


    @Override
    public String createErrorMessage(EventCaseFieldDisplayContextValidatorImpl.ValidationError validationError) {
        return withWorkSheetName(validationError);
    }

    @Override
    public String createErrorMessage(DisplayGroupColumnNumberValidator.ValidationError error) {
        return withWorkSheetName(error);
    }

    @Override
    public String createErrorMessage(GenericLayoutEntityValidatorImpl.ValidationError error) {
        return withWorkSheetName(error);
    }

    @Override
    public String createErrorMessage(StateEntityUserRoleValidatorImpl.ValidationError error) {
        return withWorkSheetName(error);
    }

    @Override
    public String createErrorMessage(StateEntityCrudValidatorImpl.ValidationError error) {
        return withWorkSheetName(error);
    }

    private String withWorkSheetName(SimpleValidationError<?> error) {
        return newMessageIfDefinitionExists(error,
                                            error.getEntity(),
                                            def -> String.format("%s. WorkSheet '%s'",
                                                                 error.getDefaultMessage(),
                                                                 def.getSheetName()));
    }

    private String newMessageIfDefinitionExists(ValidationError error,
                                                Object entity,
                                                Function<DefinitionDataItem, String> newMessage) {
        Optional<DefinitionDataItem> definitionDataItem = entityToDefinitionDataItemRegistry.getForEntity(entity);
        return definitionDataItem.map(newMessage::apply).orElse(error.getDefaultMessage());
    }

    private String createMissingOrInvalidSecurityClassificationMessage(Object entity,
                                                                       String entryId,
                                                                       String defaultMessage) {

        Optional<DefinitionDataItem> definitionDataItem = entityToDefinitionDataItemRegistry.getForEntity(entity);

        return definitionDataItem.map(def -> {
            String securityClassificationInSpreadsheet = def.getString(ColumnName.SECURITY_CLASSIFICATION);

            if (StringUtils.isEmpty(StringUtils.trimAllWhitespace(securityClassificationInSpreadsheet))) {
                return String.format("SecurityClassification is not defined for entry with id '%s' in '%s'",
                                     entryId,
                                     def.getSheetName());
            } else {
                return String.format("Invalid security classification definition '%s' for entry with id '%s' in '%s'",
                                     securityClassificationInSpreadsheet,
                                     entryId,
                                     def.getSheetName());
            }
        }).orElse(defaultMessage);
    }

    @Override
    public String createErrorMessage(UserProfileValidatorImpl.ValidationError validationError) {
        return withWorkSheetName(validationError);
    }
}
