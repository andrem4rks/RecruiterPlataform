package com.marks.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CadastroRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\p{Alnum}]).+$",
                message = "deve conter letra maiúscula, minúscula, número e caractere especial"
        )
        String senha,
        @NotNull @PastOrPresent LocalDate dataAdmissao
) {
}
