package io.mindspice.okradatabaseservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.jxch.rpc.schemas.TypeRefs;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.database.NftRequests;
import io.mindspice.okradatabaseservice.schema.AccountDid;
import io.mindspice.okradatabaseservice.schema.Card;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


@CrossOrigin
@RestController
@RequestMapping("/nft")
public class NftEndpoints {
    private final ExecutorService virtualExec = Executors.newVirtualThreadPerTaskExecutor();

    @PostMapping("/check_if_card_exists")
    public ResponseEntity<String> checkIfCardExists(@RequestBody String jsonReq) throws IOException, SQLException {
        String coinId = JsonUtils.readTree(jsonReq).get("coin_id").asText();
        JsonNode jsonResp = NftRequests.checkIfCardExists(coinId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/check_if_did_exists")
    public ResponseEntity<String> checkIfDidExists(@RequestBody String jsonReq) throws IOException, SQLException {
        String coinId = JsonUtils.readTree(jsonReq).get("coin_id").asText();
        JsonNode jsonResp = NftRequests.checkIfAccountNftExists(coinId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/check_if_exists_bulk")
    public ResponseEntity<String> bulkAssetLookup(@RequestBody String jsonReq) throws IOException, SQLException, InterruptedException {

        try {
            JsonNode json = JsonUtils.readTree(jsonReq);
            List<String> coinIds = JsonUtils.readJson(json.get("coin_ids").traverse(), TypeRefs.STRING_LIST);

            CountDownLatch latch = new CountDownLatch(coinIds.size() * 2);
            Semaphore semaphore = new Semaphore(10);

            List<AccountDid> existingAccounts = Collections.synchronizedList(new ArrayList<>());
            List<String> existingCards = Collections.synchronizedList(new ArrayList<>());

            coinIds.forEach(c -> {
                virtualExec.submit(() -> {
                    try {
                        semaphore.acquire();
                        var exists = NftRequests.checkIfCardExistsBulk(c);
                        if (exists != null) {
                            existingCards.add(exists);
                        }

                    } catch (SQLException | InterruptedException ignored) {
                    } finally {
                        latch.countDown();
                        semaphore.release();
                    }
                });

                virtualExec.submit(() -> {
                    try {
                        semaphore.acquire();
                        var exists = NftRequests.checkIfAccountNftExistsBulk(c);
                        if (exists != null) {
                            existingAccounts.add(exists);
                        }
                    } catch (SQLException | InterruptedException ignored) {
                    } finally {
                        latch.countDown();
                        semaphore.release();
                    }
                });

            });
            latch.await();
            var jsonResp = JsonUtils.successMsg(new JsonUtils.ObjectBuilder()
                    .put("existing_accounts", existingAccounts)
                    .put("existing_cards", existingCards)
                    .buildNode()
            );

            return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(JsonUtils.writeString(JsonUtils.failMsg()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/check_if_pack_exists")
    public ResponseEntity<String> checkIfPackExists(@RequestBody String jsonReq) throws IOException, SQLException {
        String coinId = JsonUtils.readTree(jsonReq).get("coin_id").asText();
        JsonNode jsonResp = NftRequests.checkIfPackExists(coinId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

//    @PostMapping("/check_if_account_exists")
//    public ResponseEntity<String> checkIfAccountExists(@RequestBody String jsonReq) throws IOException, SQLException {
//        String address = JsonUtils.readTree(jsonReq).get("address").asText();
//        JsonNode jsonResp = NftRequests.checkIfAccountExist(address);
//        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
//    }

//    @PostMapping("/update_nft")
//    public ResponseEntity<String> updateNft(@RequestBody String jsonReq) throws IOException, SQLException {
//        JsonNode node = JsonUtils.readTree(jsonReq);
//        String ownerDid = node.get("owner_did").asText();
//        String nftCoinId = node.get("coin_id").asText();
//        String launcherId = node.get("launcher_id").asText();
//        int height = node.get("height").asInt();
//        JsonNode jsonResp = NftRequests.updateNFT(ownerDid, nftCoinId, launcherId, height);
//        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
//    }

    @PostMapping("/add_new_card_nft")
    public ResponseEntity<String> addNewCardNft(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String ownerDid = node.get("owner_did").asText();
        String nftCoinId = node.get("coin_id").asText();
        String launcherId = node.get("launcher_id").asText();
        String uid = node.get("uid").asText();
        long height = node.get("height").asLong();
        JsonNode jsonResp = NftRequests.addNewCardNFT(ownerDid, nftCoinId, launcherId, uid, height);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_new_account_nft")
    public ResponseEntity<String> addNewAccountNft(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        String ownerDid = node.get("owner_did").asText();
        String nftCoinId = node.get("coin_id").asText();
        String launcherId = node.get("launcher_id").asText();
        long height = node.get("height").asLong();
        JsonNode jsonResp = NftRequests.addNewAccountNFT(playerId, launcherId, nftCoinId, ownerDid, height);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_players_did")
    public ResponseEntity<String> getDidForPlayer(@RequestBody String jsonReq) throws IOException, SQLException {
        int playerId = JsonUtils.readTree(jsonReq).get("player_id").asInt();
        JsonNode jsonResp = NftRequests.getDidForPlayerId(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_card_did")
    public ResponseEntity<String> updateCardDid(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String launcherId = node.get("launcher_id").asText();
        String ownerDid = node.get("owner_did").asText();
        String coinId = node.get("coin_id").asText();
        long height = node.get("height").asLong();
        JsonNode jsonResp = NftRequests.updateDidForCard(launcherId, ownerDid, coinId, height);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_account_did")
    public ResponseEntity<String> updateAccountDid(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String launcherId = node.get("launcher_id").asText();
        String ownerDid = node.get("owner_did").asText();
        String coinId = node.get("coin_id").asText();
        long height = node.get("height").asLong();
        boolean collision = node.get("collision").asBoolean();
        JsonNode jsonResp = NftRequests.updateDidForAccountNft(launcherId, ownerDid, coinId, height, collision);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/check_if_account_nft_exists")
    public ResponseEntity<String> checkIfAccountNftExists(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String coinId = node.get("coin_id").asText();
        JsonNode jsonResp = NftRequests.checkIfAccountNftExists(coinId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_and_inc_edition")
    public ResponseEntity<String> getAndIncEdition(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String collection = node.get("collection_table").asText();
        String uid = node.get("uid").asText();
        JsonNode jsonResp = NftRequests.getAndIncEdition(collection, uid);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_card_collection")
    public ResponseEntity<String> getCardCollection(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String collection = node.get("collection_table").asText();
        JsonNode jsonResp = NftRequests.getCardCollection(collection);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_player_xch_address")
    public ResponseEntity<String> updateXchAddress(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        String address = json.get("xch_address").asText();
        JsonNode jsonResp = NftRequests.updatePlayerXchAddr(playerId, address);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_mint_log")
    public ResponseEntity<String> addMintLog(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        String uuid = json.get("uuid").asText();
        String address = json.get("address").asText();
        List<String> nftIds = JsonUtils.readJson(json.get("nft_ids").traverse(), TypeRefs.STRING_LIST);
        JsonNode jsonResp = NftRequests.addMintLog(uuid, address, nftIds);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_transaction_log")
    public ResponseEntity<String> addTransactionLog(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        String uuid = json.get("uuid").asText();
        String address = json.get("address").asText();
        long amount = json.get("amount").asLong();
        String coinId = json.get("coin_id").asText();
        JsonNode jsonResp = NftRequests.addTransactionLog(uuid, address, amount, coinId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/add_card_to_collection")
    public ResponseEntity<String> addCardMintData(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        JsonNode jCard = json.get("card");
        String collectionSet = json.get("collection_set").asText();
        Card card = JsonUtils.getMapper().treeToValue(jCard, Card.class);

        JsonNode jsonResp = NftRequests.addCardToCollection(collectionSet, card);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }




}
