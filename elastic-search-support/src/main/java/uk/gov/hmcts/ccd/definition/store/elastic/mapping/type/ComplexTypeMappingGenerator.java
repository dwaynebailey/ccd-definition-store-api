package uk.gov.hmcts.ccd.definition.store.elastic.mapping.type;

import com.google.gson.stream.JsonWriter;
import org.jooq.lambda.Unchecked;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.definition.store.repository.entity.ComplexFieldEntity;
import uk.gov.hmcts.ccd.definition.store.repository.entity.FieldEntity;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class ComplexTypeMappingGenerator extends TypeMappingGenerator {

    @Override
    public String dataMapping(FieldEntity field) {
        return dataMapping(complexFields(field));
    }

    public String dataMapping(List<ComplexFieldEntity> complexFields) {
        return newJson(Unchecked.consumer((JsonWriter jw) -> {
            jw.name(PROPERTIES);
            jw.beginObject();
            for (FieldEntity field : complexFields) {
                jw.name(field.getReference());
                TypeMappingGenerator typeMapper = getTypeMapper(field.getBaseTypeString());
                jw.jsonValue(typeMapper.dataMapping(field));
            }
            jw.endObject();
        }));
    }

    @Override
    public String dataClassificationMapping(FieldEntity field) {
        return dataClassificationMapping(complexFields(field));
    }

    public String dataClassificationMapping(List<ComplexFieldEntity> complexFields) {
        return newJson(Unchecked.consumer((JsonWriter jw) -> {
            jw.name(PROPERTIES);
            jw.beginObject();
            jw.name(CLASSIFICATION);
            jw.jsonValue(securityClassificationMapping());
            jw.name(VALUE);
            jw.beginObject();
            jw.name(PROPERTIES);
            jw.beginObject();
            for (FieldEntity field : complexFields) {
                jw.name(field.getReference());
                TypeMappingGenerator typeMapper = getTypeMapper(field.getBaseTypeString());
                jw.jsonValue(typeMapper.dataClassificationMapping(field));
            }
            jw.endObject();
            jw.endObject();
            jw.endObject();
        }));
    }

    @Override
    public List<String> getMappedTypes() {
        return newArrayList(COMPLEX);
    }

    private List<ComplexFieldEntity> complexFields(FieldEntity field) {
        return field.getFieldType().getComplexFields();
    }
}
