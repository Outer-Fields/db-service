package io.mindspice.okradatabaseservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.database.AuthRequests;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;


@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthEndpoints {


    @PostMapping("/user_exists")
    public ResponseEntity<String> userExists(@RequestBody String jsonReq) throws IOException {
        String user = JsonUtils.readTree(jsonReq).get("username").asText();
        JsonNode jsonResp = AuthRequests.userAlreadyExist(user);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/register_user")
    public ResponseEntity<String> registerUser(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String user = node.get("username").asText();
        String password = node.get("password").asText();
        String displayName = node.get("display_name").asText();
        Boolean termsAccept = node.get("terms_accept").asBoolean();
        String termsHash = node.get("terms_hash").asText();
        JsonNode jsonResp = AuthRequests.addUserAccount(user, displayName, password, termsAccept, termsHash);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_credentials")
    public ResponseEntity<String> getCredentials(@RequestBody String jsonReq) throws IOException {
        String user = JsonUtils.readTree(jsonReq).get("username").asText();
        JsonNode jsonResp = AuthRequests.getUserCredentials(user);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/set_fund_addresses")
    public ResponseEntity<String> setFundAddresses(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        String userXchAddr = node.get("player_xch_addr").asText();
        String internalXchAddr = node.get("internal_xch_addr").asText();
        String internalPotionAddr = node.get("internal_potion_addr").asText();
        JsonNode jsonResp = AuthRequests.setUserFundAddress(playerId, userXchAddr, internalXchAddr, internalPotionAddr);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_last_login")
    public ResponseEntity<String> updateLastLogin(@RequestBody String jsonReq) throws IOException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        int playerId = node.get("player_id").asInt();
        JsonNode jsonResp = AuthRequests.updateLastLogin(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_player_did")
    public ResponseEntity<String> updatePlayerDID(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        String launcherId = json.get("launcher_id").asText();
        JsonNode jsonResp = AuthRequests.updatePlayerDID(playerId, launcherId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/update_player_password")
    public ResponseEntity<String> updatePlayerPassword(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        String passHash = json.get("password").asText();
        JsonNode jsonResp = AuthRequests.updateUserPassword(playerId, passHash);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/get_player_account_launcher")
    public ResponseEntity<String> getPlayerAccountLauncher(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = AuthRequests.getPlayerAccountLauncher(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }


    @PostMapping("/get_last_password_reset")
    public ResponseEntity<String> getLastPasswordReset(@RequestBody String jsonReq) throws IOException {
        JsonNode json = JsonUtils.readTree(jsonReq);
        int playerId = json.get("player_id").asInt();
        JsonNode jsonResp = AuthRequests.getLastPasswordReset(playerId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/check_if_did_exists")
    public ResponseEntity<String> checkIfDidExists2(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String did = node.get("did").asText();
        JsonNode jsonResp = AuthRequests.checkIfDidExists(did);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("check_for_existing_launcher")
    public ResponseEntity<String> checkForExistingLauncher(@RequestBody String jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String launcherId = node.get("launcher_id").asText();
        JsonNode jsonResp = AuthRequests.checkIfLauncherExists(launcherId);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("check_for_duplicate_launcher")
    public ResponseEntity<byte[]> checkForDuplicateLauncher(@RequestBody byte[] jsonReq) throws IOException, SQLException {
        JsonNode node = JsonUtils.readTree(jsonReq);
        String launcherId = node.get("launcher_id").asText();
        String did = node.get("did").asText();
        JsonNode jsonResp = AuthRequests.checkIfDuplicateLauncher(launcherId, did);
        return new ResponseEntity<>(JsonUtils.writeBytes(jsonResp), HttpStatus.OK);
    }













}
