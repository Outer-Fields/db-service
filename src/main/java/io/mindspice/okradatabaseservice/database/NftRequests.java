package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.mindspice.okradatabaseservice.util.JsonUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class NftRequests {

    public static JsonNode checkIfCardExists(String coin) throws SQLException {
        String query = """
                SELECT launcher_id
                FROM nft_list
                WHERE curr_coin = ?
                LIMIT 1
                """;
        String exists = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, coin);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    exists = result.getString("launcher_id");
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("launcher_id", exists));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In checkIfCardExists :" + e.getSQLState() + " | " + e.getMessage());
        }
    }

    public static JsonNode checkIfPackExists(String coin) throws SQLException {
        String query = """
                SELECT launcher_id, pack_type
                FROM card_packs 
                WHERE curr_coin = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, coin);

            String launcherId = "";
            String packType = "";
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    launcherId = result.getString("launcher_id");
                    packType = result.getString("pack_type");
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("launcher_id", launcherId)
                            .put("pack_type", packType)
                            .buildNode()
            );
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In checkIfPackExists :" + e.getSQLState() + " | " + e.getMessage());
        }
    }

    public static void updateNFT(NFTInfo info, int height) throws SQLException {
        String query = """
                UPDATE nft_list
                SET owner_did = ?, curr_coin = ?, last_height = ?
                WHERE launcher_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, info.owner_did);
            pStatement.setString(2, info.nft_coin_id);
            pStatement.setInt(3, height);
            pStatement.setString(4, info.launcher_id);
            pStatement.executeUpdate();
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In updateNFT :" + e.getSQLState() + " | " + e.getMessage()
//                                 + info + " Height: " + height);
            throw e;
        }
    }

    public static void addNewNFT(NFTInfo info) throws SQLException {
        String query = """
                INSERT INTO card_nfts (launcher_id, card_uid, curr_coin, owner_did)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, info.launcher_id);
            pStatement.setString(2, info.getUid());
            pStatement.setString(4, info.nft_coin_id);
            pStatement.setString(3, info.owner_did);

            pStatement.executeUpdate();
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In addNewNFT :" + e.getSQLState() + " | " + e.getMessage()
//                                 + info.toString());
            throw e;
        }
    }

    public static Integer checkIfAccountExist(String addr) throws SQLException {
        String query = """
                SELECT id
                FROM user_fund
                WHERE potion_token_addr = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, addr);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("id");
                }
            }
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In checkIfPackExists :" + e.getSQLState() + " | " + e.getMessage());
            throw e;
        }
        return null;
    }

    public static void updateUserPotionTokens(int id, int amount) throws SQLException {
        String query = """
                        UPDATE player_funds
                        SET potion_token_amount = potion_token_amount + ?
                        WHERE id = ?;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, amount);
            pStatement.setInt(2, id);
            pStatement.executeUpdate();
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In updatePotionTokens :" + e.getSQLState() + " | " + e.getMessage()
//                                 + "player Id: " + id + " Amount: " + amount);
            throw e;
        }
    }

    public static String getDidForPlayerId(int playerId) throws SQLException {

        String query = """
                SELECT owner_did
                FROM did_nfts
                WHERE player_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return result.getString("owner_did");
                }
            }
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In getDidForPlayerId :" + e.getSQLState() + " | " + e.getMessage());
            throw e;
        }
        return null;
    }

    public static void updateDidForNFT(String launcherId, String did) throws SQLException {
        String query = """
                        UPDATE card_nfts
                        SET owner_did =  ?
                        WHERE launcher_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, did);
            pStatement.setString(2, launcherId);
            pStatement.executeUpdate();
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
            throw e;
        }
    }

}
