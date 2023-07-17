package io.mindspice.okradatabaseservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.okradatabaseservice.database.AuthRequests;
import io.mindspice.okradatabaseservice.util.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthEndpoint {


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
        JsonNode jsonResp = AuthRequests.addUserAccount(user, password);
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


}
