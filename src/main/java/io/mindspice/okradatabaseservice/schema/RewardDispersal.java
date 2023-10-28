package io.mindspice.okradatabaseservice.schema;

import com.fasterxml.jackson.annotation.JsonProperty;


public record RewardDispersal(
        @JsonProperty("player_id") int playerId,
        @JsonProperty("okra_tokens") int okraTokens,
        @JsonProperty("nft_drops") int nftDrop,
        @JsonProperty("outr_tokens") int outrTokens
) { }
