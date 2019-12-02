package hackee12.strukt.core.swagger20;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import hackee12.strukt.core.SO;
import hackee12.strukt.core.SO.SOB;
import hackee12.strukt.json.BOMSkipperKt;

class Swagger21Test {

  private static final String EMPTY_STRING = "";
  private static final String RESOURCES_DIR = "src/test/resources/";

  private List<SO> parse(String fileName, String definedIn, String entryName) {
    final JsonReader jr = BOMSkipperKt.jsonReader(new File(RESOURCES_DIR + fileName));
    final JsonElement rootJE = new JsonParser().parse(jr);
    final Swagger21 parser = new Swagger21(rootJE);
    return parser.parse(EMPTY_STRING, rootJE.getAsJsonObject().get(definedIn), entryName);
  }

  @Test
  void fromPlain() {
    final List<SO> sos = parse("swagger21-plain.json", "definitions", "action");
    assertEquals(1, sos.size());
    final SO action = new SOB()
        .entryName("action")
        .description("Describe action")
        .type("string")
        .authorizedValues("[\"create\",\"delete\"]")
        .build();
    assertEquals(action, sos.get(0), "Action doesn't match");
  }

  @Test
  void fromContainer() {
    final List<SO> sos = parse("swagger21-container.json", "definitions", "error");
    assertEquals(4, sos.size());
    final SO error = new SOB()
        .entryName("error")
        .description("Describe error")
        .title("Error")
        .type("object")
        .build();
    final SO errorCode = new SOB()
        .parentPath("error")
        .entryName("code")
        .type("integer")
        .description("Describe error code")
        .inRequired(true)
        .build();
    final SO errorMessage = new SOB()
        .parentPath("error")
        .entryName("message")
        .type("string")
        .title("Error Message")
        .inRequired(true)
        .build();
    final SO errorOrigin = new SOB()
        .parentPath("error")
        .entryName("origin")
        .type("string")
        .example("web")
        .build();
    assertEquals(error, sos.get(0), "Error doesn't match");
    assertEquals(errorCode, sos.get(1), "Error.code doesn't match");
    assertEquals(errorMessage, sos.get(2), "Error.message doesn't match");
    assertEquals(errorOrigin, sos.get(3), "Error.origin doesn't match");
  }

  @Test
  public void implicitObjectType() {
    final List<SO> sos =
        parse("swagger21-implicit-object-type.json", "definitions", "container");
    assertEquals(3, sos.size());
    final SO container = new SOB()
        .type("object")
        .entryName("container")
        .description("Describe container")
        .build();
    final SO containerCode = new SOB()
        .parentPath("container")
        .entryName("code")
        .type("integer")
        .description("Container code")
        .inRequired(true)
        .build();
    final SO containerText = new SOB()
        .parentPath("container")
        .entryName("text")
        .type("string")
        .title("Container text")
        .build();
    assertEquals(container, sos.get(0), "Container doesn't match");
    assertEquals(containerCode, sos.get(1), "ContainerCode doesn't match");
    assertEquals(containerText, sos.get(2), "ContainerText doesn't match");
  }

  @Test
  void additionalProperties() {
    final List<SO> entitySOs =
        parse("swagger21-additionalProperties.json", "definitions", "entity");
    assertEquals(4, entitySOs.size());
    final SO entity = new SOB()
        .entryName("entity")
        .type("object")
        .description("Describe entity")
        .build();
    final SO entityCode = new SOB()
        .parentPath("entity")
        .entryName("code")
        .description("Describe entityCode")
        .type("integer")
        .inRequired(true)
        .build();
    final SO entityText = new SOB()
        .parentPath("entity")
        .entryName("text")
        .description("Describe entityText")
        .type("string")
        .build();
    final SO entityKey = new SOB()
        .parentPath("entity")
        .entryName("<key>")
        .type("boolean")
        .description("Describe additionalProperty")
        .build();
    assertEquals(entity, entitySOs.get(0), "Entity doesn't match");
    assertEquals(entityCode, entitySOs.get(1), "EntityCode doesn't match");
    assertEquals(entityText, entitySOs.get(2), "EntityText doesn't match");
    assertEquals(entityKey, entitySOs.get(3), "EntityKey doesn't match");

    final List<SO> string2array =
        parse("swagger21-additionalProperties.json", "definitions", "string2array");
    assertEquals(4, entitySOs.size());
    final SO s2a = new SOB()
        .entryName("string2array")
        .type("object")
        .description("Describe string2array")
        .build();
    final SO s2aKey = new SOB()
        .parentPath("string2array")
        .entryName("<key>")
        .type("array")
        .description("(string) -> (array of string)")
        .maxItems(99)
        .build();
    final SO s2aValue = new SOB()
        .parentPath("string2array.<key>[]")
        .entryName(EMPTY_STRING)
        .type("string")
        .minLength(1)
        .maxLength(999)
        .build();
    assertEquals(s2a, string2array.get(0), "string2array doesn't match");
    assertEquals(s2aKey, string2array.get(1), "string2arrayKey doesn't match");
    assertEquals(s2aValue, string2array.get(2), "string2arrayValue doesn't match");
  }

  @Test
  void fromRef() {
    final List<SO> sos = parse("swagger21-ref.json", "definitions", "package");
    assertEquals(2, sos.size());
    final SO pakage = new SOB()
        .entryName("package")
        .type("object")
        .description("Package description")
        .build();
    final SO pakageName = new SOB()
        .parentPath("package")
        .entryName("name")
        .type("string")
        .inRequired(true)
        .description("Describe Package Name")
        .build();
    assertEquals(pakage, sos.get(0), "Pakage doesn't match");
    assertEquals(pakageName, sos.get(1), "Pakage.name doesn't match");
  }

  @Test
  void fromSchema() {
    final List<SO> sos = parse("swagger21-schema.json", "responses", "success");
    assertEquals(4, sos.size());
    final SO success = new SOB()
        .entryName("success")
        .type("object")
        .build();
    final SO successPropertyA = new SOB()
        .parentPath("success")
        .entryName("propertyA")
        .type("string")
        .inRequired(true)
        .example("propA")
        .build();
    final SO successPropertyB = new SOB()
        .parentPath("success")
        .entryName("propertyB")
        .type("integer")
        .description("Describe Success PropertyB")
        .build();
    final SO successPropertyC = new SOB()
        .parentPath("success")
        .entryName("propertyC")
        .type("string")
        .format("date")
        .build();
    assertEquals(success, sos.get(0), "Success doesn't match");
    assertEquals(successPropertyA, sos.get(1), "Success.propertyA doesn't match");
    assertEquals(successPropertyB, sos.get(2), "Success.propertyB doesn't match");
    assertEquals(successPropertyC, sos.get(3), "Success.propertyC doesn't match");
  }

  @Test
  void fromArray() {
    final List<SO> sos = parse("swagger21-array.json", "definitions", "packages");
    assertEquals(3, sos.size());
    final SO arrayOfPakages = new SOB()
        .entryName("packages")
        .type("array")
        .description("Describe packages")
        .minItems(1)
        .maxItems(5)
        .build();
    final SO pakageInPakages = new SOB()
        .parentPath("packages[]")
        .entryName(EMPTY_STRING)
        .type("object")
        .build();
    final SO pakageName = new SOB()
        .parentPath("packages[]")
        .entryName("name")
        .description("Package Name")
        .type("string")
        .build();
    assertEquals(arrayOfPakages, sos.get(0), "Packages doesn't match");
    assertEquals(pakageInPakages, sos.get(1), "Package in Packages doesn't match");
    assertEquals(pakageName, sos.get(2), "Package Name doesn't match");
  }

  @Test
  void fromAllOf() {
    final List<SO> sos = parse("swagger21-allOf.json", "definitions", "complexObject");
    assertEquals(8, sos.size());
    final SO complexObject = new SOB()
        .type("object")
        .entryName("complexObject")
        .description("Describe Complex Object")
        .build();

    final SO commonCode = new SOB()
        .parentPath("complexObject")
        .entryName("code")
        .type("string")
        .inRequired(true)
        .example("ABC123")
        .build();
    final SO commonName = new SOB()
        .parentPath("complexObject")
        .entryName("name")
        .type("string")
        .inRequired(true)
        .description("Describe Common Name")
        .build();
    final SO commonDescription = new SOB()
        .parentPath("complexObject")
        .entryName("description")
        .type("string")
        .description("Common Description")
        .build();

    final SO stringProperty = new SOB()
        .parentPath("complexObject")
        .entryName("stringProperty")
        .type("string")
        .description("Describe stringProperty")
        .build();
    final SO booleanProperty = new SOB()
        .parentPath("complexObject")
        .entryName("booleanProperty")
        .type("boolean")
        .description("Describe booleanProperty")
        .build();
    final SO integerProperty = new SOB()
        .parentPath("complexObject")
        .entryName("integerProperty")
        .type("integer")
        .description("Describe integerProperty")
        .min(0)
        .max(99)
        .build();
    final SO refProperty = new SOB()
        .parentPath("complexObject")
        .entryName("refProperty")
        .type("string")
        .description("Describe refProperty")
        .minLength(1)
        .maxLength(20)
        .pattern("[A-Za-z0-9]{1,20}")
        .build();
    assertEquals(complexObject, sos.get(0), "ComplexObject doesn't match");

    assertEquals(commonCode, sos.get(1), "Common Code doesn't match");
    assertEquals(commonName, sos.get(2), "Common Name doesn't match");
    assertEquals(commonDescription, sos.get(3), "Common Description doesn't match");

    assertEquals(stringProperty, sos.get(4), "StringProperty doesn't match");
    assertEquals(booleanProperty, sos.get(5), "BooleanProperty doesn't match");
    assertEquals(integerProperty, sos.get(6), "IntegerProperty doesn't match");
    assertEquals(refProperty, sos.get(7), "RefProperty doesn't match");
  }
}
