# Test Endpoint
## **/health**
### Requires:
    ping                  : String
### Returns:
    ping                  : String

<br>

# **Auth Endpoints**
## **/auth/user_exists**
### Requires:
    username                : String
### Returns:
    exists                  : boolean
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>


## **/auth/register_user**
### Requires:
    username                : String
    password                : String
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/auth/get_credentials**
### Requires:
    username                : String
### Returns:
    player_id               : String
    passhash                : String
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/auth/set_fund_address**
### Requires:
    player_id               : String
    player_xch_addr         : String
    internal_xch_addr       : String
    internal_potion_addr    : String
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>
<br>

# **Chia Endpoints**

<br>

## **/chia/coin_records_by_height**
### Requires:
    height                  : int
### Returns:
    additions               : List<CoinRecord>
    removals                : List<CoinRecord>
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/chia/coin_records_by_puzzlehash**
### Requires:
    puzzle_hash             : String
### Returns:
    additions               : List<CoinRecord>
    removals                : List<CoinRecord>
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/chia/coin_records_by_name**
### Requires:
    name                    : String
### Returns:
    coin_record             : CoinRecord
    error                   : boolean
    success                 : boolean
    error_msg               : String



<br>

<br>

# **Game Endpoints**
<br>

## **/game/get_pawn_sets**
### Requires:
    player_id               : int
### Returns:
    pawn_sets               : HashMap<Integer, JsonNode>
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/update_pawn_set**
### Requires:
    player_id               : int
    set_num                 : int
    set_data                : JsonNode
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/delete_pawn_set**
### Requires:
    player_id               : int
    set_num                 : int
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/get_potion_token_amount**
### Requires:
    player_id               : int
### Returns:
    amount                  : int
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/get_player_funds**
### Requires:
    player_id               : int
### Returns:
    okra_token_amount       : int
    potion_token_amount     : int
    nft_drop_amount         : int
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/get_potion_token_amount**
### Requires:
    player_id               : int
    amount                  : int
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/commit_potion_use**
### Requires:
    player_id               : int
    amount                  : int
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/game/commit_match_result**
### Requires:
    player_id               : int
    is_win                  : boolean
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/game/commit_player_rewards**
### Requires:
    player_id               : int
    type                    : string {NFT, OKRA, POTION}
    amount                  : int
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/game/get_player_daily_results**
### Requires:
    player_id               : int
### Returns:
    wins                    : int
    losses                  : int
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/game/get_player_historical_results**
### Requires:
    player_id               : int
### Returns:
    wins                    : int
    losses                  : int
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/game/get_player_cards**
### Requires:
    player_id               : int
### Returns:
    card_uids               : String
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>
<br>

# **NFT Endpoints**

<br>

## **/nft/check_if_card_exists**
### Requires:
    coin_id                 : string
### Returns:
    launcher_id             : String    * If successful
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/check_if_card_exists**
### Requires:
    coin_id                 : string
### Returns:
    launcher_id             : String    * If successful
    did             : String    * If successful
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/check_if_pack_exists**
### Requires:
    coin_id                 : string
### Returns:
    launcher_id             : String    * If successful
    pack_type               : String    * If successful
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

<!-- ## **/nft/check_if_account_exists**
### Requires:
    coin_id                 : String
### Returns:
    id                      : int    * If successful
    error                   : boolean
    success                 : boolean
    error_msg               : String -->


<br>

## **/nft/update_nft**
### Requires:
    owner_did               : String
    coin_id                 : String
    launcher_id             : String
    height                  : int
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/nft/add_card_new_nft**
### Requires:
    owner_did               : String
    coin_id                 : String
    launcher_id             : String
    uid                     : String
    height                  : long
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/add_account_new_nft**
### Requires:
    player_id               : int
    owner_did               : String
    coin_id                 : String
    launcher_id             : String
    height                  : long
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/get_players_did**
### Requires:
    player_id               : int
### Returns:
    did                     : String
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/update_card_did**
### Requires:
    launcher_id             : String
    owner_did               : String
    coin_id                 : String
    height:                 : long
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/update_card_did**
### Requires:
    launcher_id             : String
    owner_did               : String
    coin_id                 : String
    height:                 : long
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/update_account_did**
### Requires:
    launcher_id             : String
    owner_did               : String
    coin_id                 : String
    height:                 : long
    collision               : boolean
### Returns:
    error                   : boolean
    success                 : boolean
    error_msg               : String


<br>

## **/nft/check_if_account_nft_exist**
### Requires:
    coin_id                 : string
### Returns:
    launcher_id             : String    * If successful
    did                     : String    * If successful
    error                   : boolean
    success                 : boolean
    error_msg               : String

<br>

## **/nft/get_and_inc_edition**
### Requires:
    uid                     : string
### Returns:
    edt                     : int
    error                   : boolean
    success                 : boolean
    error_msg               : String