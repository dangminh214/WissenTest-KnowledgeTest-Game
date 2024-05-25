package de.hda.fbi.db2.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.hda.fbi.db2.api.Lab02EntityManager;
import de.hda.fbi.db2.controller.Controller;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

/**
 * Created by l.koehler on 05.08.2019.
 */
// Disable on CI server because it requires database access
@DisabledIfSystemProperty(
    named = "ci-server",
    matches = "true",
    disabledReason = "Running this test on the CI server is not supported"
)
@TestMethodOrder(MethodOrderer.MethodName.class)
class Lab02Test {

  private static EntityManager entityManager;

  private static Metamodel metaData;

  private static EntityType<?> categoryEntity;

  private static EntityType<?> questionEntity;

  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private static EntityType<?> answerEntity;

  private static Controller controller;

  /**
   * Lab02Test init.
   */
  @BeforeAll
  static void init() throws URISyntaxException, IOException {
    controller = Controller.getInstance();
    Lab02EntityManager impl = controller.getLab02EntityManager();
    // Skip test if students have not implemented class yet
    assumeTrue(impl != null, "Lab02EntityManager implementation does not exist");

    String expectedPackage = "de.hda.fbi.db2.stud.impl";
    // Check startsWith to also allow subpackages
    if (!impl.getClass().getName().startsWith(expectedPackage + '.')) {
      fail("Implementation class should be in package " + expectedPackage);
    }

    if (!controller.isCsvRead()) {
      controller.readCsv();
    }

    if (!controller.isPersisted()) {
      controller.persistData();
    }
  }

  @AfterAll
  static void cleanUp() {
    if (entityManager != null) {
      entityManager.close();
    }
  }

  @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  @Test
  void test1EntityManager() {
    try {
      Lab02EntityManager lab02EntityManager = controller.getLab02EntityManager();

      entityManager = lab02EntityManager.getEntityManager();
      assertNotNull(entityManager, "Lab02EntityManager.getEntityManager() returns null");
      assertTrue(entityManager.isOpen(), "EntityManager should be open");

      // Should create new instances; should not always return the same instance
      EntityManager otherEntityManager = lab02EntityManager.getEntityManager();
      assertNotSame(entityManager, otherEntityManager,
          "Lab02EntityManager.getEntityManager() should create new instances");
      otherEntityManager.close();

      metaData = entityManager.getMetamodel();
    } catch (Exception e) {
      fail("Exception during entityManager creation", e);
    }
  }

  @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  @Test
  void test2FindCategory() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    for (EntityType<?> current : metaData.getEntities()) {
      if (current.getName().equalsIgnoreCase("category")) {
        categoryEntity = current;

        List<?> categories = controller.getLab01Data().getCategories();
        if (categories != null && !categories.isEmpty()) {
          Class<?> javaType = current.getJavaType();
          assertSame(categories.get(0).getClass(), javaType,
              "Category class used by Lab01Data should be same as entity type");
        }

        return;
      }
    }
    fail("Could not find Category entity");
  }

  @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
  @Test
  void test3FindQuestion() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    for (EntityType<?> current : metaData.getEntities()) {
      if (current.getName().equalsIgnoreCase("question")) {
        questionEntity = current;

        List<?> questions = controller.getLab01Data().getQuestions();
        if (questions != null && !questions.isEmpty()) {
          Class<?> javaType = current.getJavaType();
          assertSame(questions.get(0).getClass(), javaType,
              "Question class used by Lab01Data should be same as entity type");
        }

        return;
      }
    }
    fail("Could not find Question entity");
  }

  @Test
  void test4FindAnswer() {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (questionEntity == null) {
      fail("Could not find questionEntity");
    }

    for (Attribute<?, ?> member : questionEntity.getAttributes()) {
      DatabaseMapping mapping = ((AttributeImpl<?, ?>) member).getMapping();
      if (mapping instanceof DirectMapMapping) {
        return;
      }
      if (mapping instanceof AggregateCollectionMapping) {
        return;
      }
      if (mapping instanceof DirectCollectionMapping) {
        return;
      }
    }
    fail("Could not find a possible answer constellation in Question");
  }

  @Test
  void test5AnswerStringsInQuestion() {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (questionEntity == null) {
      fail("Could not find questionEntity");
    }

    int stringCount = 0;
    for (Object member : questionEntity.getAttributes()) {
      if (member instanceof SingularAttribute) {
        if (((SingularAttribute<?, ?>) member).getType().getJavaType() == String.class) {
          stringCount = stringCount + 1;
        }
      }
    }

    if (stringCount > 2) {
      fail("Found more then 2 Strings in Question!");
    }
  }

  @Test
  void test6QuestionInCategory() {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (questionEntity == null || categoryEntity == null) {
      fail("Could not find questionEntity or categoryEntity");
    }

    for (Object member : categoryEntity.getAttributes()) {
      try {
        Type<?> type = ((PluralAttribute<?, ?, ?>) member).getElementType();
        if (type == questionEntity) {
          return;
        }
      } catch (Exception ignored) {
        //This is expected
      }
    }

    fail("Could not find questions in category");
  }

  private static boolean hasEqualsMethod(Class<?> c) {
    try {
      c.getDeclaredMethod("equals", Object.class);
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  // Explicitly want to test `equals` implementation; ignore IntelliJ warnings about `equals` misuse
  @SuppressFBWarnings("EC_NULL_ARG")
  @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself", "ConstantConditions"})
  private static void assertEqualsImplementation(Object a, Object b) {
    String className = a.getClass().getSimpleName();
    assertTrue(a.equals(a), className + " equals method should return true for `this`");
    assertFalse(a.equals(null),
        className + " equals method should return false for `null`");
    assertFalse(a.equals(b),
        className + " equals method should return false for different objects");
  }

  @Test
  void test7EqualsMethod() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (questionEntity == null || categoryEntity == null) {
      fail("Could not find questionEntity or categoryEntity");
    }

    if (!hasEqualsMethod(questionEntity.getJavaType())) {
      fail("Could not find equals method in Question entity");
    }

    List<?> questions = controller.getLab01Data().getQuestions();
    Object question1 = questions.get(0);
    Object question2 = questions.get(1);
    assertEqualsImplementation(question1, question2);

    if (!hasEqualsMethod(categoryEntity.getJavaType())) {
      fail("Could not find equals method in Category entity");
    }

    List<?> categories = controller.getLab01Data().getCategories();
    Object category1 = categories.get(0);
    Object category2 = categories.get(1);
    assertEqualsImplementation(category1, category2);

    if (answerEntity != null) {
      if (!hasEqualsMethod(answerEntity.getJavaType())) {
        fail("Could not find equals method in Answer entity");
      }
    }
  }

  private static boolean hasHashCodeMethod(Class<?> c) {
    try {
      c.getDeclaredMethod("hashCode");
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  private static void assertHashCodeImplementation(List<?> objects) {
    assertFalse(objects.isEmpty());

    Object obj = objects.get(0);
    String className = obj.getClass().getSimpleName();
    assertEquals(obj.hashCode(), obj.hashCode(),
        className + " hashCode method should return consistent results");

    // Check if student implementation just calls `super.hashCode()`
    // Because super.hashCode() could coincidentally be the same as properly implemented hashCode()
    // reduce likelihood for false positive test failure by checking all objects
    boolean differsFromIdentityHashCode = false;
    for (Object o : objects) {
      if (o.hashCode() != System.identityHashCode(o)) {
        differsFromIdentityHashCode = true;
        break;
      }
    }

    if (!differsFromIdentityHashCode) {
      fail(className + " hashCode method seems to call super.hashCode()");
    }
  }

  @Test
  void test8HashCodeMethod() throws URISyntaxException, IOException {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (questionEntity == null || categoryEntity == null) {
      fail("Could not find questionEntity or categoryEntity");
    }

    if (!hasHashCodeMethod(questionEntity.getJavaType())) {
      fail("Could not find hashCode method in Question entity");
    }

    List<?> questions = controller.getLab01Data().getQuestions();
    assertHashCodeImplementation(questions);

    if (!hasHashCodeMethod(categoryEntity.getJavaType())) {
      fail("Could not find hashCode method in Category entity");
    }

    List<?> categories = controller.getLab01Data().getCategories();
    assertHashCodeImplementation(categories);

    if (answerEntity != null) {
      if (!hasHashCodeMethod(answerEntity.getJavaType())) {
        fail("Could not find hashCode method in Answer entity");
      }
    }
  }

  @Test
  void test9CategoryNameUnique() {
    if (metaData == null) {
      fail("No MetaModel");
    }

    if (categoryEntity == null) {
      fail("Could not find categoryEntity");
    }

    for (Attribute<?, ?> member : categoryEntity.getAttributes()) {
      try {
        SingularAttributeImpl<?, ?> attribute = (SingularAttributeImpl<?, ?>) member;
        String name = attribute.getName();
        if (name.equals("name")) {
          boolean isUnique = attribute.getMapping().getField().isUnique();
          if (!isUnique) {
            fail("Attribute 'name' in Category is not unique");
          } else {
            return;
          }
        }
      } catch (Exception ignored) {
        //This is expected
      }
    }
    fail("Could not find attribute 'name' in Category");
  }
}