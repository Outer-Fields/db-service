package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.schema.RewardDispersal;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GameRequests {

    public static JsonNode getPawnSets(int playerId) {
        String query = """
                SELECT set_number, set_data
                FROM pawn_sets
                WHERE player_id = ?
                LIMIT 5
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            HashMap<Integer, String> pawnSets = new HashMap<>(10);
            try (ResultSet result = pStatement.executeQuery()) {
                if (!result.next()) {
                    return JsonUtils.failMsg();
                }
                do {
                    var setNum = result.getInt("set_number");
                    var setData = result.getString("set_data");
                    pawnSets.put(setNum, setData);
                } while (result.next());
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("pawn_sets", pawnSets));
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updatePawnSet(int playerId, int setNum, String pawnSet) {
        String query = """
                INSERT INTO pawn_sets (player_id, set_data, set_number)
                VALUES (?, ?, ?)
                ON CONFLICT(player_id, set_number)
                DO UPDATE SET
                set_data = EXCLUDED.set_data
                """;

        if (setNum < 0 || setNum > 5) { return JsonUtils.errorMsg("Set number out of bounds"); }

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            pStatement.setString(2, pawnSet);
            pStatement.setInt(3, setNum);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode deletePawnSet(int playerId, int setNum) {
        String query = """
                DELETE FROM pawn_sets
                WHERE player_id = ? AND set_number = ?
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            pStatement.setInt(2, setNum);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPotionTokenAmount(int playerId) {
        String query = """
                SELECT potion_token_amount
                FROM player_funds
                WHERE player_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            var amount = 0;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    amount = result.getInt("potion_token_amount");
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("amount", amount));

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayerFunds(int playerId) {
        String query = """
                select okra_token_amount, outr_token_amount, potion_token_amount, nft_drop_amount
                FROM player_funds
                WHERE player_id = ?
                LIMIT 1
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            var amount = new int[4];
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    amount[0] = result.getInt("okra_token_amount");
                    amount[1] = result.getInt("potion_token_amount");
                    amount[2] = result.getInt("nft_drop_amount");
                    amount[3] = result.getInt("outr_token_amount");
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("okra_token_amount", amount[0])
                            .put("potion_token_amount", amount[1])
                            .put("nft_drop_amount", amount[2])
                            .put("outr_token_amount", amount[3])
                            .buildNode()
            );
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode commitPotionPurchase(int playerId, int potionAmount) {
        String query = """
                    UPDATE player_funds
                    SET potion_token_amount = potion_token_amount + ?
                    WHERE player_id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(2, playerId);
            pStatement.setInt(1, potionAmount);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode commitPotionUse(int playerId, int potionAmount) {
        String query = """
                    UPDATE player_funds
                    SET potion_token_amount = potion_token_amount - ?
                    WHERE player_id = ?;
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(2, playerId);
            pStatement.setInt(1, potionAmount);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

//    public static JsonNode commitMatchResult(int player_id, boolean isWin) {
//        String query = """
//                BEGIN;
//                    INSERT INTO daily_match_results AS dm(player_id, wins, losses)
//                        VALUES (?, ?, ?)
//                    ON CONFLICT(id)
//                        DO UPDATE SET
//                        wins = dm.wins + excluded.wins,
//                        losses = dm.losses + excluded.losses;
//                    INSERT INTO historical_match_results AS hm(player_id, wins, losses)
//                        VALUES (?, ?, ?)
//                    ON CONFLICT(id)
//                        DO UPDATE SET
//                        wins = hm.wins + excluded.wins,
//                        losses = hm.losses + excluded.losses;
//                COMMIT;
//                """;
//        try (Connection connection = ConnectionManager.getConnection();
//             PreparedStatement pStatement = connection.prepareStatement(query)) {
//            pStatement.setInt(1, player_id);
//            pStatement.setInt(2, isWin ? 1 : 0);
//            pStatement.setInt(3, isWin ? 0 : 1);
//            pStatement.setInt(4, player_id);
//            pStatement.setInt(5, isWin ? 1 : 0);
//            pStatement.setInt(6, isWin ? 0 : 1);
//            pStatement.execute();
//            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
//        } catch (Exception e) {
//            return JsonUtils.errorMsg(e.getMessage());
//        }
//    }

    public static JsonNode commitMatchResult(int player_id, boolean isWin) {
        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            String query1 = """
                    INSERT INTO daily_match_results AS dm(player_id, wins, losses)
                        VALUES (?, ?, ?)
                    ON CONFLICT(player_id)
                        DO UPDATE SET
                        wins = dm.wins + excluded.wins,
                        losses = dm.losses + excluded.losses;
                    """;

            try (PreparedStatement pStatement = connection.prepareStatement(query1)) {
                pStatement.setInt(1, player_id);
                pStatement.setInt(2, isWin ? 1 : 0);
                pStatement.setInt(3, isWin ? 0 : 1);
                pStatement.execute();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

            String query2 = """
                    INSERT INTO historical_match_results AS hm(player_id, wins, losses)
                        VALUES (?, ?, ?)
                    ON CONFLICT(player_id)
                        DO UPDATE SET
                        wins = hm.wins + excluded.wins,
                        losses = hm.losses + excluded.losses;
                    """;

            try (PreparedStatement pStatement = connection.prepareStatement(query2)) {
                pStatement.setInt(1, player_id);
                pStatement.setInt(2, isWin ? 1 : 0);
                pStatement.setInt(3, isWin ? 0 : 1);
                pStatement.execute();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

            connection.commit();

            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayersDailyResults(int playerId) {
        String query = """
                SELECT wins, losses
                FROM daily_match_results
                WHERE player_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            var results = new int[]{0, 0};
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    results[0] = result.getInt("wins");
                    results[1] = result.getInt("losses");
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("wins", results[0])
                            .put("losses", results[1])
                            .buildNode()
            );
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayersHistoricalResults(int playerId) {
        String query = """
                SELECT wins, losses
                FROM historical_match_results
                WHERE player_id = ?
                LIMIT 1
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            var results = new int[]{0, 0};
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    results[0] = result.getInt("wins");
                    results[1] = result.getInt("losses");
                }
            }
            return JsonUtils.successMsg(
                    new JsonUtils.ObjectBuilder()
                            .put("wins", results[0])
                            .put("losses", results[1])
                            .buildNode()
            );
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode commitPlayerRewards(int playerId, String type, int amount) {
        String nft = """
                UPDATE player_funds SET nft_drop_amount = nft_drop_amount + ?
                WHERE player_id = ?;
                """;
        String okra = """
                UPDATE player_funds SET okra_token_amount = okra_token_amount + ?
                WHERE player_id = ?;
                """;

        String outr = """
                UPDATE player_funds SET outr_token_amount = outr_token_amount + ?
                WHERE player_id = ?;
                """;

        String potion = """
                UPDATE player_funds SET potion_token_amount = potion_token_amount + ?
                WHERE player_id = ?;
                """;

        String query = null;

        switch (type) {
            case "NFT" -> query = nft;
            case "OKRA" -> query = okra;
            case "POTION" -> query = potion;
            case "OUTR" -> query = outr;
        }

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            try {
                connection.setAutoCommit(false);
                pStatement.setInt(1, amount);
                pStatement.setInt(2, playerId);
                int rowsUpdated = pStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    connection.rollback();
                    return JsonUtils.failMsg();
                }
                connection.commit();
                return JsonUtils.successMsg(JsonUtils.newEmptyNode());
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayerCardUIDs(String did) {
        if (did == null || did.isEmpty() || did.equals("null")) {
            return JsonUtils.successMsg(JsonUtils.newSingleNode("card_uids", List.of()));
        }
        String query = """
                SELECT card_uid FROM card_nfts
                WHERE owner_did = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, did);

            var cardList = new ArrayList<String>();
            try (ResultSet result = pStatement.executeQuery()) {
                while (result.next()) {
                    cardList.add(result.getString("card_uid"));
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("card_uids", cardList));
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayerRewards(int playerId) {
        String query = """
                SELECT okra_token_amount, nft_drop_amount
                FROM player_funds
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            ObjectNode rtnNode;
            try (ResultSet result = pStatement.executeQuery()) {
                if (!result.next()) {
                    return JsonUtils.failMsg();
                }
                do {
                    rtnNode = new JsonUtils.ObjectBuilder()
                            .put("okra_tokens", result.getInt("okra_token_amount"))
                            .put("dft_drops", result.getInt("nft_drop_amount"))
                            .buildNode();
                } while (result.next());
            }
            return JsonUtils.successMsg(rtnNode);
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getRewardsForDispersal() {
        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);  // Start the transaction

            // Lock and get the current rewards for all players with > 0 amounts
            String selectQuery = """
                    SELECT player_id, okra_token_amount, nft_drop_amount, outr_token_amount
                    FROM player_funds
                    WHERE okra_token_amount > 0 OR nft_drop_amount > 0 OR outr_token_amount > 0
                    FOR UPDATE
                    """;

            List<RewardDispersal> rewards = new ArrayList<>(100);

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    int okraTokens = rs.getInt("okra_token_amount");
                    int nftDrops = rs.getInt("nft_drop_amount");
                    int outrTokens = rs.getInt("outr_token_amount");
                    rewards.add(new RewardDispersal(playerId, okraTokens, nftDrops, outrTokens));
                }
            } catch (Exception e) {
                connection.rollback();
                return JsonUtils.errorMsg(e.getMessage());
            }

            // Reset rewards for players
            String updateQuery = """
                    UPDATE player_funds
                    SET okra_token_amount = 0, nft_drop_amount = 0
                    WHERE okra_token_amount > 0 OR nft_drop_amount > 0
                    """;

            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    connection.rollback();
                    return JsonUtils.failMsg();
                }
            } catch (Exception e) {
                connection.rollback();
                return JsonUtils.errorMsg(e.getMessage());
            }

            connection.commit();
            return JsonUtils.successMsg(JsonUtils.newSingleNode("rewards", rewards));
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getDisplayName(int playerId) {
        String query = """
                SELECT display_name
                FROM player_settings
                WHERE player_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            String displayName = "";
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    displayName = result.getString("display_name");
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("display_name", displayName));

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updateDisplayName(int playerId, String name) {
        String query = """
                Update player_settings
                SET display_name = ?, last_display_name_update = CURRENT_TIMESTAMP
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, name);
            pStatement.setInt(2, playerId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updatePlayerAvatar(int playerId, String imgLink) {
        String query = """
                UPDATE player_settings
                SET avatar = ?, last_avatar_update = CURRENT_TIMESTAMP
                WHERE player_id = ?;

                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, imgLink);
            pStatement.setInt(2, playerId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode getLastAvatarUpdate(int playerId) {
        String query = """
                SELECT last_avatar_update
                FROM player_settings
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            long timeStamp = 0;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    timeStamp = result.getTimestamp("last_avatar_update").toInstant().getEpochSecond();
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("last_time", timeStamp));

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getLastDidUpdate(int playerId) {
        String query = """
                SELECT last_did_update
                FROM player_auth
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            long timeStamp = 0;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    timeStamp = result.getTimestamp("last_did_update").toInstant().getEpochSecond();
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("last_time", timeStamp));

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getLastDisplayNameUpdate(int playerId) {
        String query = """
                SELECT last_display_name_update
                FROM player_settings
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            long timeStamp = 0;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    timeStamp = result.getTimestamp("last_display_name_update").toInstant().getEpochSecond();
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("last_time", timeStamp));

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode isValidDIDLauncher(String launcherId) {
        String query = """
                SELECT 1 FROM did_nfts
                WHERE launcher_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, launcherId);
            boolean exists = false;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(JsonUtils.newEmptyNode());
                }
            }
            return JsonUtils.failMsg();

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayerInfo(int playerId) {
        String query = """
                SELECT display_name, avatar
                FROM player_settings
                WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            String displayName = null;
            String avatar = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    displayName = result.getString("display_name");
                    avatar = result.getString("avatar");
                }
            }
            if (displayName != null) {
                return JsonUtils.successMsg(
                        new JsonUtils.ObjectBuilder()
                                .put("display_name", displayName)
                                .put("avatar", avatar == null ? "" : avatar)
                                .buildNode()
                );
            } else {
                return JsonUtils.failMsg();
            }
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getPlayerDid(int playerId) {
        String query = """
                SELECT did_nfts.owner_did
                FROM did_nfts
                JOIN player_auth ON did_nfts.launcher_id = player_auth.did_launcher
                WHERE player_auth.player_id = ?;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);
            String did = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    did = result.getString("owner_did");
                }
            }
            return JsonUtils.successMsg(JsonUtils.newSingleNode("did", did));
        } catch (
                Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode isExistingName(String name) {
        String query = """
                SELECT EXISTS (
                SELECT 1 FROM player_settings
                WHERE display_name = ?
                ) AS exists;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, name);
            boolean exists = false;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    exists = result.getBoolean("exists");
                }
            }
            return exists ? JsonUtils.successMsg(JsonUtils.newEmptyNode()) : JsonUtils.failMsg();

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getAccountCoin(int playerId) {
        String query = """
                SELECT did_nfts.curr_coin
                FROM did_nfts
                JOIN player_auth ON did_nfts.launcher_id = player_auth.did_launcher
                WHERE player_auth.player_id = ?;
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(
                            JsonUtils.newSingleNode("curr_coin", result.getString("curr_coin"))
                    );
                }
            }
            return JsonUtils.failMsg();

        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode resetDailyResults() {
        String query = "TRUNCATE TABLE daily_match_results";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            try {
                pStatement.execute();
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode commitFullMatchResult(String uid, int player1, int player2, boolean player1Won,
            boolean player2Won, int roundCount, String finishState, List<String> player1Ips, List<String> player2Ips) {
        try (Connection connection = ConnectionManager.getConnection()) {

            String query = """
                    INSERT INTO full_match_results(match_uid, player1, player2, player1_won, player2_won, round_count, finish_state, player1_ips, player2_ips)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                       
                    """;

            try (PreparedStatement pStatement = connection.prepareStatement(query)) {
                pStatement.setString(1, uid);
                pStatement.setInt(2, player1);
                pStatement.setInt(3, player2);
                pStatement.setBoolean(4, player1Won);
                pStatement.setBoolean(5, player2Won);
                pStatement.setInt(6, roundCount);
                pStatement.setString(7, finishState);
                pStatement.setArray(8, connection.createArrayOf("text", player1Ips.toArray()));
                pStatement.setArray(9, connection.createArrayOf("text", player2Ips.toArray()));
                pStatement.execute();
            }
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode addFlaggedDispersal(int playerId, int okraAmount, int outrAmount, int nftAmount) {
        try (Connection connection = ConnectionManager.getConnection()) {

            String query = """
                    INSERT INTO flagged_reward_distribution(player_id, okra_token_amount, outr_token_amount, nft_drop_amount)
                        VALUES (?, ?, ?, ?)
                                       
                    """;

            try (PreparedStatement pStatement = connection.prepareStatement(query)) {
                pStatement.setInt(1, playerId);
                pStatement.setInt(2, okraAmount);
                pStatement.setInt(3, outrAmount);
                pStatement.setInt(4, nftAmount);
                pStatement.executeUpdate();
            }
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode addFreeGamePlayed(int playerId) {
        try (Connection connection = ConnectionManager.getConnection()) {

            String query = """
                    INSERT INTO free_games_played (player_id, games_played)
                    VALUES (?, 1)
                    ON CONFLICT(player_id)
                    DO UPDATE SET
                    games_played = free_games_played.games_played + 1                                 
                    """;

            try (PreparedStatement pStatement = connection.prepareStatement(query)) {
                pStatement.setInt(1, playerId);
                pStatement.executeUpdate();
            }
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getFreeGamesPlayed(int playerId) {
        String query = """
                SELECT games_played FROM free_games_played WHERE player_id = ?
                """;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return JsonUtils.successMsg(
                            JsonUtils.newSingleNode("games_played", result.getString("games_played"))
                    );
                }
            }
            return JsonUtils.failMsg();
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }



    public static JsonNode resetFreeGames() {
        try (Connection connection = ConnectionManager.getConnection()) {
            String query = "UPDATE free_games_played SET games_played = 0";

            try (PreparedStatement pStatement = connection.prepareStatement(query)) {
                pStatement.execute();
            }
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

}
