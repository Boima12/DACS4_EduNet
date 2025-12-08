package org.example.common.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonHelper {

    private static final Logger log = LoggerFactory.getLogger(GsonHelper.class);

    // Dùng 1 instance duy nhất
    private static final Gson gson = new GsonBuilder()
            // Use compact JSON (no pretty-printing) so network messages are single-line
            .create();

    /* toJson: Object -> JSON string
    * example uses: String json = GsonHelper.toJson(user);
    * */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /* fromJson: JSON string -> Object
     * example uses: User u = GsonHelper.fromJson(jsonString, User.class);
     * */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    /* fromJsonToList: JSON -> List
     * example uses: List<User> list = GsonHelper.fromJsonToList(jsonString, User.class);
     * */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, type);
    }

    /* fromJsonToMap: JSON -> Map
     * example uses: Map<String, Object> map = GsonHelper.fromJsonToMap(jsonString);
     * */
    public static Map<String, Object> fromJsonToMap(String json) {
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }


    /* readJsonFile: read JSON file -> Object
     * example uses: memoryBox = GsonHelper.readJsonFile("memoryBox.json", MemoryBox.class);
     * */
    public static <T> T readJsonFile(String path, Class<T> clazz) {
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            log.error(String.valueOf(e));
            return null;
        }
    }

    /* writeJsonFile: write JSON file <- Object
     * example uses: GsonHelper.writeJsonFile("memoryBox.json", memoryBox);
     * */
    public static boolean writeJsonFile(String path, Object data) {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            log.error(String.valueOf(e));
            return false;
        }
    }
}
