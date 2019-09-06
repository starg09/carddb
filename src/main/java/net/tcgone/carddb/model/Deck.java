package net.tcgone.carddb.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author axpendix@hotmail.com
 * @since 20.05.2019
 */
@Data
public class Deck {
    private String id; // external id, generated randomly
    private String oldId; // old internal mongo id, stored for legacy decks
    private String seoName; // generated seo name except id
    @NotBlank
    private String name;
    @NotBlank
    private String format; // main format
    private String description;
    private String creatorId;
    private String creatorUsername; // not serialized
    @NotNull
    private Map<String, Integer> contents; // contents with new id
    private Date lastUpdated;
    private List<String> tags = new ArrayList<>(); // theme, public, private, draft, career, list
    private List<String> colors = new ArrayList<>(); // fire, grass, psychic
    private List<String> tiers; // tournament, tier1, tier2, tier3, experimental, other, fun
    private List<String> variants; // unholy paladin, haymaker
    private List<String> formats = new ArrayList<>(); // all valid formats that this can be played in
    private boolean ready;
    private String error;
    private int timesUsed;

    public int size(){
        int count = 0;
        for (Integer value : contents.values()) {
            count += value;
        }
        return count;
    }
}
