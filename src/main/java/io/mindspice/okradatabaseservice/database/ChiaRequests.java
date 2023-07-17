package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.okradatabaseservice.util.Bytes32;
import io.mindspice.okradatabaseservice.util.JsonUtils;
import io.mindspice.okradatabaseservice.util.Pair;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;


public class ChiaRequest {
    public JsonNode getCoinRemovals(int height) {
        String query = """
                SELECT * FROM coin_record WHERE spent_index = ? AND coinbase = 0
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, height);

            var coinRecords = new HashSet<String>();
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    do {
                        coinRecords.add("0x" + new Bytes32(result.getBytes("coin_parent")));
                    } while (result.next());
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("coin_records", coinRecords));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public JsonNode getCoinAdditions(int height) {
        String query = """
                SELECT * FROM coin_record WHERE confirmed_index = ? AND coinbase = 0
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, height);

            var coinRecords = new ArrayList<Pair<String, Integer>>();
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    do {
                        var pHash = ("0x" + new Bytes32(result.getBytes("puzzle_hash")));
                        var amount = (int) new BigInteger(result.getBytes("amount")).longValue() / 1000;
                        coinRecords.add(new Pair<>(pHash, (amount)));
                    } while (result.next());
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("coin_records", coinRecords));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

}
