package io.mindspice.okradatabaseservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.database.ChiaRequests;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@CrossOrigin
@RestController
@RequestMapping("/chia")
public class ChiaEndpoints {

    @PostMapping("/coin_records_by_height")
    public ResponseEntity<String> recordByHeight(@RequestBody String jsonReq) throws IOException {
        int height = JsonUtils.readTree(jsonReq).get("height").asInt();
        JsonNode jsonResp = ChiaRequests.getCoinRecordsByHeight(height);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/coin_records_by_puzzlehash")
    public ResponseEntity<String> recordByPuzzle(@RequestBody String jsonReq) throws IOException {
        String puzzleHash = JsonUtils.readTree(jsonReq).get("puzzle_hash").asText();
        JsonNode jsonResp = ChiaRequests.getCoinRecordsByPuzzleHash(puzzleHash);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/coin_record_by_name")
    public ResponseEntity<String> recordByName(@RequestBody String jsonReq) throws IOException {
        String name = JsonUtils.readTree(jsonReq).get("name").asText();
        JsonNode jsonResp = ChiaRequests.getCoinRecordsByName(name);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }

    @PostMapping("/coin_record_by_parent_and_height")
    public ResponseEntity<String> recordByParentAndHeight(@RequestBody String jsonReq) throws IOException {
        var json = JsonUtils.readTree(jsonReq);
        String name = json.get("name").asText();
        int height = json.get("height").asInt();
        JsonNode jsonResp = ChiaRequests.getCoinRecordByParentId(name, height);
        return new ResponseEntity<>(JsonUtils.writeString(jsonResp), HttpStatus.OK);
    }


}
