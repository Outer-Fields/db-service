package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.util.Log;

import java.sql.*;


public class AuthRequests {

    public static JsonNode userAlreadyExist(String user) {
        String query = """
                SELECT *
                FROM player_auth
                WHERE username = ?
                LIMIT 1""";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, user);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(JsonUtils.newSingleNode("exists", true));
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("exists", false));
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode addUserAccount(String username, String displayName, String password,
            boolean termsAccept, String termsHash) {
        String query = "SELECT add_user_account(?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            pStatement.setString(3, displayName);
            pStatement.setBoolean(4, termsAccept);
            pStatement.setString(5, termsHash);

            try (ResultSet rs = pStatement.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    return JsonUtils.successMsg(JsonUtils.newSingleNode("player_id", id));
                } else {
                    return JsonUtils.errorMsg("Creating user failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getUserCredentials(String user) {
        String query = """
                SELECT player_id, passhash
                FROM player_auth
                WHERE username = ?""";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, user);

            int id = -1;
            String passHash = "";
            String displayName = "";
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    id = result.getInt("player_id");
                    passHash = result.getString("passhash");
                }
            }
            if (id != -1) {
                return JsonUtils.successMsg(
                        new JsonUtils.ObjectBuilder()
                                .put("player_id", id)
                                .put("passhash", passHash)
                                .put("display_name", displayName)
                                .buildNode()
                );
            } else {
                return JsonUtils.failMsg();
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode setUserFundAddress(int userId, String userXchAddr, String internalXchAddr, String potionAddr) {
        String query = """
                UPDATE player_addresses
                SET    player_xch_addr = ?,
                       internal_xch_addr = ?
                       internal_potion_addr = ?
                WHERE  player_id = ?;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, userXchAddr);
            pStatement.setString(2, internalXchAddr);
            pStatement.setString(3, potionAddr);
            pStatement.setInt(4, userId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updateLastLogin(int playerId) {
        String query = """
                UPDATE player_auth
                SET last_login = CURRENT_TIMESTAMP
                WHERE player_id = ?;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updatePlayerDID(int playerId, String launcherId) {
        String query = """
                        UPDATE player_auth
                        SET did_launcher =  ?
                        WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, launcherId);
            pStatement.setInt(2, playerId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode updateUserPassword(int playerId, String password) {
        String query = """
                        UPDATE player_auth
                        SET passhash =  ?, last_password_reset = CURRENT_TIMESTAMP
                        WHERE player_id = ?
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, password);
            pStatement.setInt(2, playerId);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
        return JsonUtils.successMsg(JsonUtils.newEmptyNode());
    }

    public static JsonNode getPlayerAccountLauncher(int playerId) {
        String query = """
                        SELECT did_launcher
                        FROM player_auth
                        WHERE player_id = ?
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setInt(1, playerId);

            String launcherId = null;
            try (ResultSet result = pStatement.executeQuery();) {

                if (result.next()) {
                    launcherId = result.getString("did_launcher");
                }
            }
            if (launcherId != null) {
                return JsonUtils.successMsg(JsonUtils.newSingleNode("did_launcher", launcherId));
            } else {
                return JsonUtils.failMsg();
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getLastPasswordReset(int playerId) {
        String query = """
                        SELECT last_password_reset
                        FROM player_auth
                        WHERE player_id = ?
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setInt(1, playerId);

            Timestamp lastReset = null;
            try (ResultSet result = pStatement.executeQuery();) {

                if (result.next()) {
                    lastReset = result.getTimestamp("last_password_reset");
                }
            }
            if (lastReset != null) {
                return JsonUtils.successMsg(JsonUtils.newSingleNode("last_reset", lastReset.getTime() / 1000));
            } else {
                return JsonUtils.failMsg();
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode checkIfDidExists(String ownerDid) throws SQLException {
        String query = "SELECT 1 FROM did_nfts WHERE owner_did = ? LIMIT 1";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, ownerDid);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(JsonUtils.newSingleNode("found", true));
                }
            }
            return JsonUtils.failMsg();
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode checkIfLauncherExists(String launcherId) throws SQLException {
        String query = "SELECT 1 FROM player_auth WHERE did_launcher = ? LIMIT 1";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, launcherId);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(JsonUtils.newSingleNode("found", true));
                }
            }
            return JsonUtils.failMsg();
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode checkIfDuplicateLauncher(String launcherId, String did) {
        String query = """
                WITH DualMatch AS (
                    SELECT launcher_id
                    FROM did_nfts
                    WHERE launcher_id = ? AND owner_did = ?
                    LIMIT 1
                )
                                
                SELECT
                    EXISTS (SELECT 1 FROM DualMatch) AS is_valid,
                    COALESCE(
                        (SELECT launcher_id FROM DualMatch),
                        (SELECT launcher_id FROM did_nfts WHERE owner_did = ? LIMIT 1)
                    ) AS launcher_id
                ;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, launcherId);
            pStatement.setString(2, did);
            pStatement.setString(3, did);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(
                            new JsonUtils.ObjectBuilder()
                                    .put("is_valid", result.getBoolean("is_valid"))
                                    .put("launcher_id", result.getString("launcher_id"))
                                    .buildNode()
                    );
                }
            }
            return JsonUtils.failMsg();
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());

        }
    }
}
