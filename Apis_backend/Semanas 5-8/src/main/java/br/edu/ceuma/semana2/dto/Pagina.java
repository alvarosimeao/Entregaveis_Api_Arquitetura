package br.edu.ceuma.semana2.dto;

import java.util.List;

public record Pagina<T>(List<T> data, Meta meta) {
    public record Meta(long totalItems, int itemsPerPage, int currentPage, int totalPages) {}
}
