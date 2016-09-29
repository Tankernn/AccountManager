package eu.tankernn.accounts.util.encryption;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EncryptedComplexSerializer
		implements JsonSerializer<EncryptedComplex>, JsonDeserializer<EncryptedComplex> {

	@Override
	public JsonElement serialize(EncryptedComplex src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("salt", src.getEncodedSalt());
		object.addProperty("IV", src.getEncodedIv());
		object.addProperty("data", src.getEncodedData());
		return object;
	}

	@Override
	public EncryptedComplex deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		return new EncryptedComplex(obj.get("salt").getAsString(), obj.get("IV").getAsString(), obj.get("data").getAsString());
	}

}
