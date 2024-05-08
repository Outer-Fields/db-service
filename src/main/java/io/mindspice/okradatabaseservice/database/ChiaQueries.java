package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.jxch.rpc.schemas.object.Coin;
import io.mindspice.jxch.rpc.schemas.object.CoinRecord;
import io.mindspice.mindlib.data.Pair;
import io.mindspice.mindlib.util.JsonUtils;

import io.mindspice.okradatabaseservice.util.Bytes32;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HexFormat;


public class ChiaRequests {

    public static JsonNode getCoinRecordsByHeight(int height) {
        String query = """
                SELECT * FROM coin_record WHERE confirmed_index = ? AND coinbase = 0
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, height);

            var additions = new ArrayList<CoinRecord>();
            var removals = new ArrayList<CoinRecord>();
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    do {
                        var record = new CoinRecord(
                                new Coin(
                                        "0x" + new Bytes32(result.getBytes("coin_parent")),
                                        "0x" + new Bytes32(result.getBytes("puzzle_hash")),
                                        new BigInteger(result.getBytes("amount")).longValue()
                                ),
                                result.getInt("confirmed_index"),
                                result.getInt("spent_index"),
                                result.getLong("spent_index") != 0,
                                result.getBoolean("coinbase"),
                                result.getLong("timestamp")
                        );
                        if (record.spent()) {
                            removals.add(record);
                        } else {
                            additions.add(record);
                        }
                    } while (result.next());
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("additions", additions)
                            .put("removals", removals)
                            .buildNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getCoinRecordsByPuzzleHash(String puzzleHash) {
        String query = """
                SELECT * FROM coin_record WHERE puzzle_hash = ? AND coinbase = 0
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            String phash = puzzleHash;
            if (phash.length() == 66) {
                phash = phash.substring(2);
            } else if (phash.length() != 64) {
                return JsonUtils.errorMsg("Invalid puzzle hash length (!= 64)");
            }
            pStatement.setBytes(1, HexFormat.of().parseHex(phash));

            var additions = new ArrayList<CoinRecord>();
            var removals = new ArrayList<CoinRecord>();
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    do {
                        var record = new CoinRecord(
                                new Coin(
                                        "0x" + new Bytes32(result.getBytes("coin_parent")),
                                        "0x" + new Bytes32(result.getBytes("puzzle_hash")),
                                        new BigInteger(result.getBytes("amount")).longValue()
                                ),
                                result.getInt("confirmed_index"),
                                result.getInt("spent_index"),
                                result.getLong("spent_index") != 0,
                                result.getBoolean("coinbase"),
                                result.getLong("timestamp")
                        );
                        if (record.spent()) {
                            removals.add(record);
                        } else {
                            additions.add(record);
                        }
                    } while (result.next());
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("additions", additions)
                            .put("removals", removals)
                            .buildNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getCoinRecordsByName(String coin_name) {
        String query = """
                SELECT * FROM coin_record WHERE coin_name = ? AND coinbase = 0 LIMIT 1;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            String name = coin_name;
            if (name.length() == 66) {
                name = name.substring(2);
            } else if (name.length() != 64) {
                return JsonUtils.errorMsg("Invalid puzzle hash length (!= 64)");
            }
            pStatement.setBytes(1, HexFormat.of().parseHex(name));
            CoinRecord record = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    record = new CoinRecord(
                            new Coin(
                                    "0x" + new Bytes32(result.getBytes("coin_parent")),
                                    "0x" + new Bytes32(result.getBytes("puzzle_hash")),
                                    new BigInteger(result.getBytes("amount")).longValue()
                            ),
                            result.getInt("confirmed_index"),
                            result.getInt("spent_index"),
                            result.getLong("spent_index") != 0,
                            result.getBoolean("coinbase"),
                            result.getLong("timestamp")
                    );
                }
            }
            if (record == null) { return JsonUtils.errorMsg("Record Not Found"); }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("coin_record", record));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getCoinRecordByParentId(String coin_name, int height) {
        String query = """
                SELECT * FROM coin_record WHERE parent_id = ? AND coinbase = 0 AND spent_height = ? LIMIT 1;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            String name = coin_name;
            if (name.length() == 66) {
                name = name.substring(2);
            } else if (name.length() != 64) {
                return JsonUtils.errorMsg("Invalid puzzle hash length (!= 64)");
            }
            pStatement.setBytes(1, HexFormat.of().parseHex(name));
            pStatement.setInt(2, height);
            CoinRecord record = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    record = new CoinRecord(
                            new Coin(
                                    "0x" + new Bytes32(result.getBytes("coin_parent")),
                                    "0x" + new Bytes32(result.getBytes("puzzle_hash")),
                                    new BigInteger(result.getBytes("amount")).longValue()
                            ),
                            result.getInt("confirmed_index"),
                            result.getInt("spent_index"),
                            result.getLong("spent_index") != 0,
                            result.getBoolean("coinbase"),
                            result.getLong("timestamp")
                    );
                }
            }
            if (record == null) { return JsonUtils.errorMsg("Record Not Found"); }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("coin_record", record));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }
}
