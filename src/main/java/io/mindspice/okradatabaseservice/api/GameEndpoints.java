package io.mindspice.okradatabaseservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.database.ChiaRequests;
import io.mindspice.okradatabaseservice.database.GameRequests;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;


@CrossOrigin
@RestController
@RequestMapping("/game")
public class GameEndpoints {

    @PostMapping("/get_pawn_sets")
    public ResponseEntity<String> getPawnSets(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPawnSets(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_pawn_set")
    public ResponseEntity<String> updatePawnSet(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        int setNum = node.get("set_num").asInt();
        String setData = node.get("set_data").asText();
        JsonNode jsonResp = GameRequests.updatePawnSet(playerId, setNum, setData);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/delete_pawn_set")
    public ResponseEntity<String> deletePawnSet(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        int setNum = node.get("set_num").asInt();
        JsonNode jsonResp = GameRequests.deletePawnSet(playerId, setNum);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_potion_token_amount")
    public ResponseEntity<String> getPotionTokenAmount(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPotionTokenAmount(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_funds")
    public ResponseEntity<String> getPlayerFunds(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayerFunds(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/commit_potion_purchase")
    public ResponseEntity<String> commitPotionPurchase(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        int amount = node.get("amount").asInt();
        JsonNode jsonResp = GameRequests.commitPotionPurchase(playerId, amount);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/commit_potion_use")
    public ResponseEntity<String> commitPotionUse(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        int amount = node.get("amount").asInt();
        JsonNode jsonResp = GameRequests.commitPotionUse(playerId, amount);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/commit_match_result")
    public ResponseEntity<String> commitMatchResult(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        boolean isWin = node.get("is_win").asBoolean();
        JsonNode jsonResp = GameRequests.commitMatchResult(playerId, isWin);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/commit_player_rewards")
    public ResponseEntity<String> commitPlayerRewards(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        String type = node.get("type").asText();
        int amount = node.get("amount").asInt();
        JsonNode jsonResp = GameRequests.commitPlayerRewards(playerId, type, amount);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_daily_results")
    public ResponseEntity<String> getPlayerDailyResults(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayersDailyResults(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_historical_results")
    public ResponseEntity<String> getPlayerHistoricalResults(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayersHistoricalResults(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_cards")
    public ResponseEntity<String> getPlayerCards(@RequestBody String jsonReq) throws IOException {
        String ownerDid = JsonUtils.readTree(jsonReq).get("did").asText();
        JsonNode jsonResp = GameRequests.getPlayerCardUIDs(ownerDid);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_rewards")
    public ResponseEntity<String> getPlayerRewards(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayerRewards(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_display_name")
    public ResponseEntity<String> getDisplayName(@RequestBody String jsonReq) throws IOException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getDisplayName(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_player_display_name")
    public ResponseEntity<String> updateDisplayName(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        String name = json.get("display_name").asText();
        JsonNode jsonResp = GameRequests.updateDisplayName(playerId, name);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_player_avatar")
    public ResponseEntity<String> updateAvatar(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        String imgLink = json.get("img_link").asText();
        JsonNode jsonResp = GameRequests.updatePlayerAvatar(playerId, imgLink);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_last_did_update")
    public ResponseEntity<String> lastDidUpdate(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getLastDidUpdate(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_last_avatar_update")
    public ResponseEntity<String> lastAvatarUpdate(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getLastAvatarUpdate(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_last_display_name_update")
    public ResponseEntity<String> lastDisplayNameUpdate(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getLastDisplayNameUpdate(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/is_valid_did_launcher")
    public ResponseEntity<String> isValidDID(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        String launcherId = json.get("launcher_id").asText();
        JsonNode jsonResp = GameRequests.isValidDIDLauncher(launcherId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_info")
    public ResponseEntity<String> getPlayerInfo(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayerInfo(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_did")
    public ResponseEntity<String> getPlayerDid(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getPlayerDid(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }


    @PostMapping("/does_name_exist")
    public ResponseEntity<String> doesNameExist(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        String launcherId = json.get("name").asText();
        JsonNode jsonResp = GameRequests.isExistingName(launcherId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_rewards_for_dispersal")
    public ResponseEntity<String> getRewardsForDispersal(@RequestBody String jsonReq) throws IOException {
        JsonNode jsonResp = GameRequests.getRewardsForDispersal();
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_account_coin")
    public ResponseEntity<String> getAccountCoin(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getAccountCoin(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/reset_daily_results")
    public ResponseEntity<String> resetDailyResults(@RequestBody String jsonReq) throws IOException {
        JsonNode jsonResp = GameRequests.resetDailyResults();
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/commit_full_match_result")
    public ResponseEntity<String> commitFullResult(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        String matchUid = json.get("match_uid").asText();
        int player1 = json.get("player_1").asInt();
        int player2 = json.get("player_2").asInt();
        boolean player1Won = json.get("player_1_won").asBoolean();
        boolean player2Won = json.get("player_2_won").asBoolean();
        int roundCount = json.get("round_count").asInt();
        String finishState = json.get("finish_state").asText();
        JsonNode jsonResp = GameRequests.commitFullMatchResult(
                matchUid,
                player1,
                player2,
                player1Won,
                player2Won,
                roundCount,
                finishState
        );
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_flagged_dispersal")
    public ResponseEntity<String> addFlaggedDispersal(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        int okraAmount = json.get("okra_amount").asInt();
        int outrAmount = json.get("outr_amount").asInt();
        int nftAmount = json.get("nft_amount").asInt();
        JsonNode jsonResp = GameRequests.addFlaggedDispersal(playerId, okraAmount, outrAmount, nftAmount);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_free_games_played")
    public ResponseEntity<String> getFreeGamesPlayed(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.getFreeGamesPlayed(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_free_game_played")
    public ResponseEntity<String> addFreeGamePlayed(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = GameRequests.addFreeGamePlayed(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/reset_free_games_played")
    public ResponseEntity<String> resetFreeGamesPlayed(@RequestBody String jsonReq) throws IOException {
        JsonNode jsonResp = GameRequests.resetFreeGames();
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }
}
