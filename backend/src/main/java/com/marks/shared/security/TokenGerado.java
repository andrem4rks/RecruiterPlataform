package com.marks.shared.security;

import java.time.Instant;

public record TokenGerado(String valor, Instant expiraEm) {
}
