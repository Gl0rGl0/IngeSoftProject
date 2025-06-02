package V5.Ingsoft.controller.item.statuses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StatusItem.class, name = "StatusItem"),
        @JsonSubTypes.Type(value = StatusVisita.class, name = "StatusVisita")
})
public interface Statuses {
}