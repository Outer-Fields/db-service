package io.mindspice.okradatabaseservice.schema;

import com.fasterxml.jackson.annotation.JsonProperty;


public record CoinEntry(
        @JsonProperty("coin_name") String coinName,
        @JsonProperty("coin_parent") String coinParent,
        @JsonProperty("puzzle_hash") String puzzle_hash,
        @JsonProperty("amount") long amount,
        @JsonProperty("confirmed_index") long confirmedIndex,
        @JsonProperty("spent_index") long spentIndex,
        @JsonProperty("coinbase") boolean coinBase,
        @JsonProperty("timestamp") long timestamp
) {
}
