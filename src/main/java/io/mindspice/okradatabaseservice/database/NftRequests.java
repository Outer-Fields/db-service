package io.mindspice.okradatabaseservice.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.mindspice.jxch.rpc.schemas.wallet.nft.MetaData;
import io.mindspice.mindlib.util.JsonUtils;
import io.mindspice.okradatabaseservice.schema.AccountDid;
import io.mindspice.okradatabaseservice.schema.Card;
import io.mindspice.okradatabaseservice.schema.CardDomain;
import io.mindspice.okradatabaseservice.schema.CardType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class NftRequests {

    public static JsonNode checkIfCardExists(String coinId) throws SQLException {
        String query = """
                SELECT launcher_id
                FROM card_nfts
                WHERE curr_coin = ?
                LIMIT 1
                """;
        String exists = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, coinId);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    exists = result.getString("launcher_id");
                }
            }
            if (exists != null) {
                return JsonUtils.successMsg(JsonUtils.newSingleNode("launcher_id", exists));
            } else {
                return JsonUtils.failMsg();
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In checkIfCardExists :" + e.getSQLState() + " | " + e.getMessage());
        }
    }

    public static String checkIfCardExistsBulk(String coinId) throws SQLException {
        String query = """
                SELECT launcher_id
                FROM card_nfts
                WHERE curr_coin = ?
                LIMIT 1
                """;
        String exists = null;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, coinId);

            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return result.getString("launcher_id");
                }
            }
        } catch (SQLException e) {
//            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In checkIfCardExists :" + e.getSQLState() + " | " + e.getMessage());
        }
        return null;
    }

    public static AccountDid checkIfAccountNftExistsBulk(String coinId) throws SQLException {

        String query = "SELECT launcher_id, owner_did FROM did_nfts WHERE curr_coin = ?";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            String[] rtnData = new String[2];
            pStatement.setString(1, coinId);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    return new AccountDid(
                            result.getString("launcher_id"),
                            result.getString("owner_did")
                    );
                }
            }
        } catch (SQLException e) {
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
        return null;
    }

    public static JsonNode checkIfPackExists(String coinId) throws SQLException {
        String query = """
                SELECT launcher_id, pack_type
                FROM card_packs
                WHERE curr_coin = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, coinId);

            String launcherId = null;
            String packType = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    launcherId = result.getString("launcher_id");
                    packType = result.getString("pack_type");
                }
            }
            if (launcherId != null) {
                return JsonUtils.successMsg(
                        new JsonUtils.ObjectBuilder()
                                .put("launcher_id", launcherId)
                                .put("pack_type", packType)
                                .buildNode()
                );
            } else {
                return JsonUtils.failMsg();
            }
        } catch (
                SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In checkIfPackExists :" + e.getSQLState() + " | " + e.getMessage());
        }

    }

    public static JsonNode updateNFT(String ownerDid, String nftCoinId, String launcherId, long height)
            throws SQLException {
        String query = """
                UPDATE card_nfts
                SET owner_did = ?, curr_coin = ?, height_updated = ?
                WHERE launcher_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, ownerDid);
            pStatement.setString(2, nftCoinId);
            pStatement.setLong(3, height);
            pStatement.setString(4, launcherId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateNFT :" + e.getSQLState() + " | " + e.getMessage()
//                                 + info + " Height: " + height);
        }
    }

    public static JsonNode addNewCardNFT(String ownerDid, String nftCoinId, String launcherId, String uid, long height)
            throws SQLException {
        String query = """
                INSERT INTO card_nfts (launcher_id, card_uid, curr_coin, owner_did, height_updated)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, launcherId);
            pStatement.setString(2, uid);
            pStatement.setString(3, nftCoinId);
            pStatement.setString(4, ownerDid);
            pStatement.setLong(5, height);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In addNewNFT :" + e.getSQLState() + " | " + e.getMessage()
//                                 + info.toString());
        }
    }

    public static JsonNode addNewAccountNFT(int playerId, String launcherId, String nftCoinId, String ownerDid,
            long height) throws SQLException {
        String query = """
                INSERT INTO did_nfts (launcher_id, curr_coin, owner_did, height_updated)
                VALUES ( ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setString(1, launcherId);
            pStatement.setString(2, nftCoinId);
            if (ownerDid == null || ownerDid.isEmpty() || ownerDid.equals("null")) {
                pStatement.setNull(3, Types.VARCHAR);
            } else {
                pStatement.setString(3, ownerDid);

            }
            pStatement.setLong(4, height);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In addNewNFT :" + e.getSQLState() + " | " + e.getMessage()
//                                 + info.toString());
        }
    }

//    public static JsonNode checkIfAccountExist(String addr) throws SQLException {
//        String query = """
//                SELECT player_id
//                FROM user_fund
//                WHERE potion_token_addr = ?
//                LIMIT 1
//                """;
//        try (Connection connection = ConnectionManager.getConnection();
//             PreparedStatement pStatement = connection.prepareStatement(query)) {
//            pStatement.setString(1, addr);
//
//            Integer id = null;
//            try (ResultSet result = pStatement.executeQuery()) {
//                if (result.next()) {
//                    id = result.getInt("player_id");
//                }
//            }
//            if (id != null) {
//                return JsonUtils.successMsg(JsonUtils.newSingleNode("id", id));
//            } else {
//                return JsonUtils.failMsg();
//            }
//        } catch (SQLException e) {
//            return JsonUtils.errorMsg(e.getMessage());
////            SYSLOG.fatal("Error In checkIfPackExists :" + e.getSQLState() + " | " + e.getMessage());
//        }
//    }

    public static JsonNode updateUserPotionTokens(int id, int amount) throws SQLException {
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
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updatePotionTokens :" + e.getSQLState() + " | " + e.getMessage()
//                                 + "player Id: " + id + " Amount: " + amount);
        }
    }

    public static JsonNode getDidForPlayerId(int playerId) throws SQLException {

        String query = """
                SELECT owner_did
                FROM did_nfts
                WHERE player_id = ?
                LIMIT 1
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {
            pStatement.setInt(1, playerId);

            String ownerDid = null;
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    ownerDid = result.getString("owner_did");
                }
            }
            if (ownerDid != null) {
                return JsonUtils.successMsg(JsonUtils.newSingleNode("did", ownerDid));
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In getDidForPlayerId :" + e.getSQLState() + " | " + e.getMessage());
        }
        return null;
    }

    public static JsonNode updateDidForCard(String launcherId, String ownerDid, String coinId, long height)
            throws SQLException {
        String query = """
                        UPDATE card_nfts
                        SET owner_did =  ?, curr_coin = ?, height_updated = ?
                        WHERE launcher_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, ownerDid);
            pStatement.setString(2, coinId);
            pStatement.setLong(3, height);
            pStatement.setString(4, launcherId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode checkIfAccountNftExists(String coinId) throws SQLException {

        String query = "SELECT launcher_id, owner_did FROM did_nfts WHERE curr_coin = ?";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            ObjectNode rtnData = null;
            pStatement.setString(1, coinId);
            try (ResultSet result = pStatement.executeQuery()) {
                if (result.next()) {
                    rtnData = new JsonUtils.ObjectBuilder()
                            .put("launcher_id", result.getString("launcher_id"))
                            .put("did", result.getString("owner_did"))
                            .buildNode();
                }
            }
            if (rtnData == null) {
                return JsonUtils.failMsg();
            } else {
                return JsonUtils.successMsg(rtnData);
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode updateDidForAccountNft(String launcherId, String ownerDid, String coinId, long height,
            boolean collision) throws SQLException {

        String query;
        if (collision) {
            query = """
                    UPDATE did_nfts
                    SET curr_coin = ?, height_updated = ?
                    WHERE launcher_id = ?
                    """;
        } else {
            query = """
                    UPDATE did_nfts
                    SET owner_did =  ?, curr_coin = ?, height_updated = ?
                    WHERE launcher_id = ?
                    """;
        }
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            if (collision) {
                pStatement.setString(1, coinId);
                pStatement.setLong(2, height);
                pStatement.setString(3, launcherId);
            } else {
                if (ownerDid == null || ownerDid.isEmpty() || ownerDid.equals("null")) {
                    pStatement.setNull(1, Types.VARCHAR);
                } else {
                    pStatement.setString(1, ownerDid);
                }
                pStatement.setString(2, coinId);
                pStatement.setLong(3, height);
                pStatement.setString(4, launcherId);
            }
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode getAndIncEdition(String collection, String uid) {
        String query = "SELECT get_and_inc_edt(?, ?)";
        int edt = 0;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatementSelect = connection.prepareStatement(query)) {
            pStatementSelect.setString(1, collection);
            pStatementSelect.setString(2, uid);

            try (ResultSet result = pStatementSelect.executeQuery()) {
                if (result.next()) {
                    edt = result.getInt(1);
                }
            }

            return JsonUtils.successMsg(JsonUtils.newSingleNode("edt", edt));

        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode addCardToCollection(String collectionName, Card card) {
        String query = String.format("INSERT INTO %s (uid, level, domain, type, isgold, isholo, uris, metauris, licenseuris, hash, metahash, licensehash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", collectionName);

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, card.uid());
            pStatement.setInt(2, card.level());
            pStatement.setString(3, card.domain().name());
            pStatement.setString(4, card.type() == null ? CardType.NONE.name() : card.type().name());
            pStatement.setBoolean(5, card.isGold());
            pStatement.setBoolean(6, card.isHolo());
            pStatement.setArray(7, connection.createArrayOf("text", card.metaData().uris().toArray()));
            pStatement.setArray(8, connection.createArrayOf("text", card.metaData().metaUris().toArray()));
            pStatement.setArray(9, connection.createArrayOf("text", card.metaData().licenseUris().toArray()));
            pStatement.setString(10, card.metaData().hash());
            pStatement.setString(11, card.metaData().metaHash());
            pStatement.setString(12, card.metaData().licenseHash());
            pStatement.executeUpdate();

            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (Exception e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode getCardCollection(String collection) {
        String query = "SELECT * FROM " + collection;

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatementSelect = connection.prepareStatement(query)) {

            try (ResultSet rs = pStatementSelect.executeQuery()) {
                List<Card> cards = new ArrayList<>();

                while (rs.next()) {
                    Array urisArray = rs.getArray("uris");
                    String[] uris = (String[]) urisArray.getArray();

                    Array metaUrisArray = rs.getArray("metaUris");
                    String[] metaUris = (String[]) metaUrisArray.getArray();

                    Array licenseUrisArray = rs.getArray("licenseUris");
                    String[] licenseUris = (String[]) licenseUrisArray.getArray();

                    MetaData metaData = new MetaData(
                            Arrays.asList(uris),
                            Arrays.asList(metaUris),
                            Arrays.asList(licenseUris),
                            rs.getString("hash"),
                            rs.getString("metaHash"),
                            rs.getString("licenseHash"),
                            rs.getInt("editionNumber"),
                            rs.getInt("editionTotal")
                    );
                    String cardType = rs.getString("type");
                    Card card = new Card(
                            rs.getString("uid"),
                            rs.getInt("level"),
                            CardDomain.valueOf(rs.getString("domain")),
                            cardType.isEmpty() ? CardType.NONE : CardType.valueOf(cardType),
                            rs.getBoolean("isGold"),
                            rs.getBoolean("isHolo"),
                            metaData
                    );

                    cards.add(card);
                }

                return JsonUtils.successMsg(JsonUtils.newSingleNode("card_list", cards));
            }
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
        }
    }

    public static JsonNode updatePlayerXchAddr(int playerId, String xchAddress) {
        String query = """
                        UPDATE player_settings
                        SET xch_address =  ?,
                        WHERE player_id = ?
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, xchAddress);
            pStatement.setInt(2, playerId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode addMintLog(String uuid, String address, List<String> nft_ids) {
        String query = """
                    INSERT INTO mint_log (uuid, address, nft_ids)
                    VALUES (?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, uuid);
            pStatement.setString(2, address);
            Array array = connection.createArrayOf("text", nft_ids.toArray());
            pStatement.setArray(3, array);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

    public static JsonNode addTransactionLog(String uuid, String address, long amount, String coinId) {
        String query = """
                    INSERT INTO transaction_log (uuid, address, amount, coin_id) 
                    VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement pStatement = connection.prepareStatement(query)) {

            pStatement.setString(1, uuid);
            pStatement.setString(2, address);
            pStatement.setLong(3, amount);
            pStatement.setString(4, coinId);
            pStatement.executeUpdate();
            return JsonUtils.successMsg(JsonUtils.newEmptyNode());
        } catch (SQLException e) {
            return JsonUtils.errorMsg(e.getMessage());
//            SYSLOG.fatal("Error In updateDidForNFT :" + launcherId + " | " + e.getMessage());
        }
    }

}
