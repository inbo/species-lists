package au.org.ala.listsapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The migration progress doc is stored and looked up under the fixed id "_".
 * Lombok @Builder.Default does not apply in hand-written constructors, so the
 * constructor must pin the id explicitly or progress is silently lost.
 */
class MigrateProgressItemTest {

  @Test
  void constructorSetsFixedId() {
    MigrateProgressItem item = new MigrateProgressItem(42);
    assertEquals("_", item.getId());
    assertEquals(42, item.getTotal());
  }
}
