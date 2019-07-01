package dev.naydichev.slab.saas.models;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class Episode {
    private int id;
    private int season;
    private String title;
    private Date date;
    private String director;
    private List<String> writers;
}
