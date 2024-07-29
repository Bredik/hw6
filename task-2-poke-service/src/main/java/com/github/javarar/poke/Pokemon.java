package com.github.javarar.poke;

import java.util.List;

public record Pokemon(
        String name,
        Double h,
        Double w,
        List<String> abilities) {
}
