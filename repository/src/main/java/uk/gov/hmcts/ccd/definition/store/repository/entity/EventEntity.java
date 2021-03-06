package uk.gov.hmcts.ccd.definition.store.repository.entity;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import uk.gov.hmcts.ccd.definition.store.repository.PostgreSQLEnumType;
import uk.gov.hmcts.ccd.definition.store.repository.SecurityClassification;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Table(name = "event")
@Entity
@TypeDef(
    name = "pgsql_securityclassification_enum",
    typeClass = PostgreSQLEnumType.class,
    parameters = @Parameter(name="type", value="uk.gov.hmcts.ccd.definition.store.repository.SecurityClassification")
)
public class EventEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "live_from")
    private LocalDate liveFrom;

    @Column(name = "live_to")
    private LocalDate liveTo;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "can_create", nullable = false)
    private Boolean canCreate = Boolean.FALSE;

    @Column(name = "display_order")
    private Integer order;

    @Column(name = "security_classification")
    @Type( type = "pgsql_securityclassification_enum" )
    private SecurityClassification securityClassification;

    @Column(name = "show_summary")
    private Boolean showSummary;

    @Column(name = "end_button_label")
    private String endButtonLabel;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "post_state_id")
    private StateEntity postState;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "case_type_id", nullable=false)
    private CaseTypeEntity caseType;

    @ManyToMany(fetch = EAGER)
    @Fetch(value = SUBSELECT)
    @JoinTable(
        name = "event_pre_state",
        joinColumns = @JoinColumn(name="event_id", referencedColumnName="id"),
        inverseJoinColumns = @JoinColumn(name="state_id", referencedColumnName="id")
    )
    private final List<StateEntity> preStates = new ArrayList<>();

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "webhook_start_id")
    private WebhookEntity webhookStart;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "webhook_pre_submit_id")
    private WebhookEntity webhookPreSubmit;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "webhook_post_submit_id")
    private WebhookEntity webhookPostSubmit;

    @OneToMany(mappedBy = "event", fetch = EAGER, cascade = ALL, orphanRemoval = true)
    @Fetch(value = SUBSELECT)
    private final List<EventCaseFieldEntity> eventCaseFields = new ArrayList<>();

    @OneToMany(fetch = EAGER, cascade = ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "event_id")
    private final List<EventUserRoleEntity> eventUserRoles = new ArrayList<>();

    @Column(name = "show_event_notes")
    private Boolean showEventNotes;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public LocalDate getLiveFrom() {
        return liveFrom;
    }

    public void setLiveFrom(final LocalDate liveFrom) {
        this.liveFrom = liveFrom;
    }

    public LocalDate getLiveTo() {
        return liveTo;
    }

    public void setLiveTo(final LocalDate liveTo) {
        this.liveTo = liveTo;
    }

    public boolean isCanCreate() {
        return canCreate;
    }

    public boolean getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(final boolean canCreate) {
        this.canCreate = canCreate;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public SecurityClassification getSecurityClassification() {
        return securityClassification;
    }

    public void setSecurityClassification(SecurityClassification securityClassification) {
        this.securityClassification = securityClassification;
    }

    public Boolean getShowSummary() {
        return showSummary;
    }

    public void setShowSummary(final Boolean showSummary) {
        this.showSummary = showSummary;
    }

    public String getEndButtonLabel() {
        return endButtonLabel;
    }

    public void setEndButtonLabel(String endButtonLabel) {
        this.endButtonLabel = endButtonLabel;
    }

    public CaseTypeEntity getCaseType() {
        return caseType;
    }

    public void setCaseType(final CaseTypeEntity caseType) {
        this.caseType = caseType;
    }

    public StateEntity getPostState() {
        return postState;
    }

    public void setPostState(final StateEntity postState) {
        this.postState = postState;
    }

    public List<StateEntity> getPreStates() {
        return preStates;
    }

    public void addPreState(@NotNull final StateEntity state) {
        preStates.add(state);
    }

    public WebhookEntity getWebhookStart() {
        return webhookStart;
    }

    public void setWebhookStart(final WebhookEntity webhookStart) {
        this.webhookStart = webhookStart;
    }

    public WebhookEntity getWebhookPreSubmit() {
        return webhookPreSubmit;
    }

    public void setWebhookPreSubmit(final WebhookEntity webhookPreSubmit) {
        this.webhookPreSubmit = webhookPreSubmit;
    }

    public WebhookEntity getWebhookPostSubmit() {
        return webhookPostSubmit;
    }

    public void setWebhookPostSubmit(final WebhookEntity webhookPostSubmit) {
        this.webhookPostSubmit = webhookPostSubmit;
    }

    public void addEventCaseField(@NotNull final EventCaseFieldEntity eventCaseField) {
        eventCaseField.setEvent(this);
        eventCaseFields.add(eventCaseField);
    }

    public void addEventCaseFields(@NotNull final Collection<EventCaseFieldEntity> eventCaseFields) {
        for (EventCaseFieldEntity eventCaseField : eventCaseFields) {
            addEventCaseField(eventCaseField);
        }
    }

    public List<EventCaseFieldEntity> getEventCaseFields() {
        return eventCaseFields;
    }

    public List<EventUserRoleEntity> getEventUserRoles() {
        return eventUserRoles;
    }

    public EventEntity addEventUserRole(final EventUserRoleEntity eventUserRole) {
        eventUserRole.setEventEntity(this);
        eventUserRoles.add(eventUserRole);
        return this;
    }

    public EventEntity addEventUserRoles(final Collection<EventUserRoleEntity> entities) {
        entities.forEach(e -> addEventUserRole(e));
        return this;
    }

    public boolean hasField(String fieldReference) {
        return eventCaseFields.stream().anyMatch(ecf -> ecf.getCaseField().getReference().equals(fieldReference));
    }

    public Boolean getShowEventNotes() {
        return showEventNotes;
    }

    public void setShowEventNotes(Boolean showEventNotes) {
        this.showEventNotes = showEventNotes;
    }
}
