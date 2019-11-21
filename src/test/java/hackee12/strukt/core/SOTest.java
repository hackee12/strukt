package hackee12.strukt.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import hackee12.strukt.core.SO.SOB;

public class SOTest {

  @Test
  public void testBuilder() {
    final SO parent = new SOB()
        .isRequired(true)
        .entryName("parent")
        .type("object")
        .build();
    final SO child = new SOB()
        .parentPath(parent.getPath())
        .entryName("child")
        .type("integer")
        .build();
    Assertions.assertEquals("parent", parent.asText());
    Assertions.assertEquals("parent.child", child.asText());
  }
}
