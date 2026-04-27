package dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
		@NotBlank
		String nomeCategoria
		) {

}
