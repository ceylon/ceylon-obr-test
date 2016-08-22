import tsc {
    ...
}

"Initial lowercase form of [[name]]."
throws (`class AssertionError`, "If [[name]] is empty.")
String initLCase(String name) {
    assert (exists first = name.first);
    return first.lowercased.string + name.rest;
}

"Initial uppercase form of [[name]]."
throws (`class AssertionError`, "If [[name]] is empty.")
String initUCase(String name) {
    assert (exists first = name.first);
    return first.uppercased.string + name.rest;
}

"Tests whether the set of flags [[haystack]] contains the flag [[needle]].
 (The haystack has type `Anything` so that call sites do not attempt to typecheck the argument.)"
Boolean hasTypeFlag(Anything haystack, TypeFlags needle) {
    dynamic {
        return eval("(function(h,n){return (h&n)!=0})")(haystack, needle);
    }
}

"Tests whether the set of flags [[haystack]] contains the flag [[needle]].
 (The haystack has type `Anything` so that call sites do not attempt to typecheck the argument.)"
Boolean hasNodeFlag(Anything haystack, NodeFlags needle) {
    dynamic {
        return eval("(function(h,n){return (h&n)!=0})")(haystack, needle);
    }
}
