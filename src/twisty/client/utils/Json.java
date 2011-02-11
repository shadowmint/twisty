/**
 * Copyright 2010 Douglas Linder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twisty.client.utils;

import java.util.ArrayList;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/** Helper class for creating handing common json actions. */
public class Json {

	/** JSON Object for this instance. */
	private JSONValue root = null;

	/** Query list */
	private ArrayList<Item> query = new ArrayList<Item>();

	/** Types. */
	public enum Type { Array, Boolean, Null, Number, Object, String };

	public Json() {
		root = new JSONObject();
	}

	public Json(String raw) {
		root = JSONParser.parseStrict(raw);
	}

	/** Returns the root node. */
	public JSONValue getJSONValue() {
		return(root);
	}

	/** Query shortcut. */
	public JSONValue query(String path) {
		return(query(root, path));
	}

	/**
	 * Query a JSON node from a JSON object using xpath like notation.
	 * <p>
	 * Valid paths are in the form: item.item.[index].item.item
	 * <p>
	 * Only single element returns are possible; use jsonCount to count
	 * the elements as at a node position.
	 */
	public JSONValue query(JSONValue root, String path) {
		parseQueryString(path);
		boolean failed = false;
		JSONValue current = root;
		for (Item item : query) {
			// Index into arrays
			if (item.name == null) {
				if (current.isArray() != null) {
					JSONArray a = current.isArray();
					a.get(item.index);
				}
				else {
					failed = true;
					current = null;
				}
			}
			// Key into objects
			else {
				if (current.isObject() != null) {
					JSONObject o = current.isObject();
					current = o.get(item.name);
				}
				else {
					failed = true;
					current = null;
				}
			}

			if ((failed) || (current == null))
				break;
		}
		return(current);
	}

	/** Appends a new object to the root. */
	public JSONValue appendObject(String key) {
		return(appendObject(root, key));
	}

	/** Appends a new object to a JSONValue node. */
	public JSONValue appendObject(JSONValue root, String key) {
		JSONObject rtn = new JSONObject();
		return(append(root, key, rtn));
	}

	/** Appends a new array to the root. */
	public JSONValue appendArray(String key) {
		return(appendArray(root, key));
	}

	/** Appends a new array to a JSONValue node. */
	public JSONValue appendArray(JSONValue root, String key) {
		JSONArray rtn = new JSONArray();
		return(append(root, key, rtn));
	}

	/** Appends a new node to the root. */
	public JSONValue append(String key, double value) {
		return(append(root, key, value));
	}

	/** Appends a new node to a JSONValue node. */
	public JSONValue append(JSONValue root, String key, double value) {
		JSONNumber rtn = new JSONNumber(value);
		return(append(root, key, rtn));
	}

	/** Appends a new node to the root. */
	public JSONValue append(String key, boolean value) {
		return(append(root, key, value));
	}

	/** Appends a new node to a JSONValue node. */
	public JSONValue append(JSONValue root, String key, boolean value) {
		JSONBoolean rtn = JSONBoolean.getInstance(value);
		return(append(root, key, rtn));
	}

	/** Appends a new node to the root. */
	public JSONValue append(String key, String value) {
		return(append(root, key, value));
	}

	/** Appends a new node to a JSONValue node. */
	public JSONValue append(JSONValue root, String key, String value) {
		JSONString rtn = new JSONString(value);
		return(append(root, key, rtn));
	}

	/** Appends a new node to the root. */
	public JSONValue append(String key, JSONValue value) {
		return(append(root, key, value));
	}

	/** Appends a new node to a JSONValue node. */
	public JSONValue append(JSONValue root, String key, JSONValue value) {
		JSONObject object;
		JSONArray array;
		JSONValue rtn = value;
		if ((object = root.isObject()) != null) {
			object.put(key, value);
		}
		else if ((array = root.isArray()) != null) {
			int offset = array.size();
			array.set(offset, value);
		}
		else
			rtn = null;
		return(rtn);
	}

	/** Count shortcut. */
	public int count(String path) {
		return(count(root, path));
	}

	/**
	 * Query a JSON node from a JSON object using xpath like notation.
	 * <p>
	 * Valid paths are in the form: item.item.[index].item.item
	 * <p>
	 * Returns a count of elements at the target path.
	 * <p>
	 * If the final item in the path is not an array the count return is 1.
	 */
	public int count(JSONValue root, String path) {
		int rtn = 0;
		JSONValue value = query(root, path);
		if (value != null) {
			rtn = 1;
			if (value.isArray() != null) {
				JSONArray a = value.isArray();
				rtn = a.size();
			}
		}
		return(rtn);
	}

	/** Exists shortcut. */
	public boolean exists(String path) {
		return(exists(root, path));
	}

	/**
	 * Query a JSON node from a JSON object using xpath like notation.
	 * <p>
	 * Valid paths are in the form: item.item.[index].item.item
	 * <p>
	 * Returns true if the count of nodes at the path is > 0.
	 */
	public boolean exists(JSONValue root, String path) {
		boolean rtn = false;
		if (count(root, path) > 0)
			rtn = true;
		return(rtn);
	}

	/**
	 * Query a JSON node from a JSON object using xpath like notation.
	 * <p>
	 * Valid paths are in the form: item.item.[index].item.item
	 * <p>
	 * Returns true if the count of nodes at the path is > 0
	 * and the type of the first match is correct.
	 */
	boolean exists(JSONValue root, String path, Type type) {
		boolean rtn = true;
		JSONValue v = query(root, path);
		if (v == null)
			rtn = false;
		else if ((type == Type.Array) && (v.isArray() == null))
			rtn = false;
		else if ((type == Type.Number) && (v.isNumber() == null))
			rtn = false;
		else if ((type == Type.Boolean) && (v.isBoolean() == null))
			rtn = false;
		else if ((type == Type.Object) && (v.isObject() == null))
			rtn = false;
		else if ((type == Type.Null) && (v.isNull() == null))
			rtn = false;
		else if ((type == Type.String) && (v.isString() == null))
			rtn = false;
		return(rtn);
	}

	/** Safe conversion function for JSONValue to string: default is null. */
	public String asString(JSONValue v) {
		String rtn = null;
		if (v != null) {
			JSONString value = v.isString();
			if (value != null)
				rtn = value.stringValue();
			else
				rtn = v.toString();
		}
		return(rtn);
	}

	/** Safe conversion function for JSONValue to boolean: default is false. */
	public boolean asBoolean(JSONValue v) {
		boolean rtn = false;
		if (v != null) {
			JSONBoolean value = v.isBoolean();
			if (value != null)
				rtn = value.booleanValue();
		}
		return(rtn);
	}

	/** Safe conversion function for JSONValue to int: default is 0. */
	public double asDouble(JSONValue v) {
		double rtn = 0;
		if (v != null) {
			JSONNumber value = v.isNumber();
			if (value != null)
				rtn = value.doubleValue();
		}
		return(rtn);
	}

	/** Parses a query string. */
	private void parseQueryString(String path) {
		query.clear();
		String names[] = path.split("\\.");
		for (String name : names) {
			Item i = new Item();
			int startOffset = name.indexOf('[', 0);
			int endOffset = name.indexOf(']', 0);
			if ((startOffset != -1) && (endOffset != -1) && (startOffset < endOffset)) {
				String index = name.substring(startOffset + 1, endOffset); // Exclude []
				try {
					i.name = null;
					i.index = Integer.parseInt(index);
				}
				catch(Exception e) {
					// Invalid index; assume 0.
					i.name = null;
					i.index = 0;
				}
			}
			else
				i.name = name;
			query.add(i);
		}
	}

	/** Query string container. */
	private class Item {
		public String name;
		public int index = 0;
	}
}
