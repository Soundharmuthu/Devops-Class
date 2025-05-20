package com.example.ModelClass;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataDTO {

    private String channel; // maps to l2v_cs or null if missing

    private Integer movieDraft; // nullable Integer to allow null if missing
    private Integer moviePublished;
    private Integer movieTotal;

    private Integer tvepisodeDraft;
    private Integer tvepisodePublished;
    private Integer tvepisodeTotal;

    private Integer tvseasonDraft;
    private Integer tvseasonPublished;
    private Integer tvseasonTotal;

    private Integer tvseriesDraft;
    private Integer tvseriesPublished;
    private Integer tvseriesTotal;

    private Integer grandTotal;

    // For content type query (fields might be present or null)
    private String cty;
    private Integer totalDraft;
    private Integer totalPublished;

    // For purchase type query (fields might be present or null)

    private Integer purchaseType;

}
