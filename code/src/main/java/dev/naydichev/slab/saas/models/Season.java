package dev.naydichev.slab.saas.models;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Season {
    private int id;
    private List<Episode> episodes;
}
