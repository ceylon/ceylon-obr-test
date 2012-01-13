void expect(Equality actual, Equality expected, String text) {
    print(text + ": actual='" + actual.string + "', expected='"
            + expected.string + "' => "
            + ((actual==expected) then "ok" else "FAIL"));
}

//Another test for the compiler.
void test_interpolate() {
    //print("String part " 1 " interpolation " 2 " works");
}

void testCharacter() {
    Character c1 = `A`;
    //Character c2 = `𝄞`;
    //Character c3 = `Ũ`;
    expect(c1.string, "A", "Character.string");
    //expect(c2.string, "𝄞", "Character.string");
    //expect(c3.string, "Ũ", "Character.string");
    //expect(`Ä`.lowercased, `ä`, "Character.lowercased");
    //expect(`x`.lowercased, `x`, "Character.lowercased");
    //expect(`ö`.uppercased, `Ö`, "Character.uppercased");
    //expect(`#`.uppercased, `#`, "Character.uppercased");
}

void testString() {
    expect("".empty, true, "String.empty");
    expect("x".empty, false, "String.empty");
    expect("".size, 0, "String.size");
    String s1 = "abc";
    String s2 = "ä€Ũ\t";
    String s3 = "A𝄞`ŨÖ";
    expect(s1.size, 3, "String.size");
    expect(s2.size, 4, "String.size");
    expect(s3.size, 5, "String.size");
    expect((s1+s2).size, 7, "String.size");
    expect((s1+s3).size, 8, "String.size");
    
    expect("".shorterThan(0), false, "String.shorterThan");
    expect("".shorterThan(1), true, "String.shorterThan");
    expect("abc".shorterThan(3), false, "String.shorterThan");
    expect("abc".shorterThan(4), true, "String.shorterThan");
    expect("".longerThan(0), false, "String.longerThan");
    expect("x".longerThan(0), true, "String.longerThan");
    expect("abc".longerThan(3), false, "String.longerThan");
    expect("abc".longerThan(2), true, "String.longerThan");
    
    variable Integer cnt := 0;
    variable String s4 := "";
    for (c in s3) {
        s4 := c.string + s4;
        ++cnt;
    }
    expect(cnt, 5, "String.iterator");
    expect(s4, "ÖŨ`𝄞A", "String.iterator");
}

shared void test() {
    print("--- Start Language Module Tests ---");
    test_largest();
    test_smallest();
    test_max();
    test_min();
    test_join();
    test_zip();
    test_coalesce();
    test_append();
    test_singleton();
    test_entries();
    test_exists_nonempty();
    test_foreach();
    test_arraysequence();
    test_iterators();
    test_ranges();
    //test_interpolate();
    testCharacter();
    testString();
    print("--- End Language Module Tests ---");
}
