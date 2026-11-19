package br.edu.ceuma.semana2.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoRequest {

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotNull
    @Min(1)
    @Max(130)
    private Integer idade;

    @Pattern(regexp = "[A-Z0-9]{4,24}", message = "Use de 4 a 24 letras maiúsculas ou números.")
    private String matricula;
}
