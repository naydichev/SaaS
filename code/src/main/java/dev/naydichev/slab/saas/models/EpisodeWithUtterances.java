package dev.naydichev.slab.saas.models;

import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class EpisodeWithUtterances extends Episode {
    private List<Utterance> utterances;
}
