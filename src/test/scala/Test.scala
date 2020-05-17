import org.scalatest.funsuite.AnyFunSuite

class Test extends AnyFunSuite{

  test("testing user prompts") {
    // TODO: test user input 
    assert(Set.empty.isEmpty)
  }

  test("Invoking head on an empty Set should produce NoSuchElementException") {
    assertThrows[NoSuchElementException] {
      Set.empty.head
    }
  }

}
