package farto.cleva.guilherme.utils;

import java.util.Collections;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public abstract class JSONUtil {

	private static final String SPACES = "  ";
	private static final String EMPTY = "";

	/**
	 * Recursive method to enable JSON pretty printing
	 * 
	 * @param jsonObject
	 */
	public static final void prettyPrint(JSONObject jsonObject) {
		JSONUtil.prettyPrint(jsonObject, 0);
	}

	/**
	 * Recursive method to enable JSON pretty printing
	 * 
	 * @param jsonObject
	 * @param indentation
	 */
	private static final void prettyPrint(JSONObject jsonObject, int indentation) {
		for (Iterator<?> iJsonObject = jsonObject.keySet().iterator(); iJsonObject.hasNext();) {
			String key = String.valueOf(iJsonObject.next());
			Object value = jsonObject.get(key);

			if (value instanceof org.json.simple.JSONObject) {
				JSONUtil.prettyPrint((JSONObject) value, indentation++);

				System.out.println();
			} else if (value instanceof org.json.simple.JSONArray) {
				indentation++;

				JSONArray jsonArray = ((JSONArray) value);

				for (Iterator<?> iJsonArray = jsonArray.iterator(); iJsonArray.hasNext();) {
					JSONUtil.prettyPrint((JSONObject) iJsonArray.next(), indentation);

					System.out.println();
				}
			} else {
				// ### for Java 6 ###
				// System.out.println((indentation > 0 ? String.format("%0" + indentation + "d", 0).replace("0", SPACES) : EMPTY) + key + " = " + String.valueOf(value));

				// ### for Java 8 ###
				System.out.println((indentation > 0 ? String.join(EMPTY, Collections.nCopies(indentation, SPACES)) : EMPTY) + key + " = " + String.valueOf(value));
			}
		}
	}

}
