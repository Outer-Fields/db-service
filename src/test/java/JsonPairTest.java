import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.data.Pair;
import io.mindspice.mindlib.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class JsonPairTest {
    public static final TypeReference<List<Pair<String, Integer>>> PAIR_LIST = new TypeReference<>() { };
    public static final TypeReference<Map<String, List<String>>> ROYALTY_MAP = new TypeReference<>() { };
    @Test
    void testPairSerialization() throws IOException {
        Pair<String, Integer> pair = new Pair("test", 69);
        List<Pair<String, Integer>> list = List.of(pair,pair,pair);
        String json = JsonUtils.writeString(JsonUtils.newSingleNode("list_test", list));
        byte[] byteJson =  JsonUtils.writeBytes(JsonUtils.newSingleNode("list_test", list));
        System.out.println(json);
        JsonNode list2 = JsonUtils.readTree(byteJson);
        System.out.println(JsonUtils.readJson(list2.get("list_test").traverse(), PAIR_LIST));
    }
}
