/**
 * 
 */
package hx.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * <p>Created by BenSNW on Nov 30, 2016
 */
public class JsonUtil {

	public static boolean isNullJson(JsonNode json) {
		return json == null || json.isNull() || json.isMissingNode();
	}

	public static boolean isEmptyJsonNode(JsonNode json) {
		return isNullJson(json) || (json.isValueNode() && isBlank(json.asText()))
				|| (json.isContainerNode() && json.size() == 0);
	}

	/**
	 * null-safe and type-tolerant get json field as String
	 *
	 * @param json
	 * @param field
     * @return empty String if not found even in case json is null
     */
	public static String getFieldAsText(JsonNode json, String field) {
		return getFieldAsText(json, field, "");
	}
	
	public static String getFieldAsText(JsonNode json, String field, String defaultValue) {
		if (isNullJson(json) || isNullJson(json.get(field)))
			return defaultValue;
		// toString will quote TextNode so "text" becomes "\"text\""
		JsonNode node = json.get(field);
		return node.isValueNode() ? node.asText() : node.toString();
	}

	/**
	 * supports nested field such as a.b.c, NullNode will be treated as valid node
	 * @param json
	 * @param field
     * @return	empty String if not found or "null" for NullNode
     */
	public static String getFieldAsString(JsonNode json, String field) {
		return (isNullJson(json) || isBlank(field)) ? "" : getInternalField(json, field);
	}

	private static String getInternalField(JsonNode json, String field) {
		if (json.isValueNode())
			return field.isEmpty() ? json.asText() : "";
		if (field.isEmpty())
			return json.toString();
		return getInternalField(json.get(getFieldPrefix(field)), getFieldSuffix(field));
	}

	/**
	 * supports nested field such as a.b.c
	 *
	 * @param json
	 * @param field
	 * @return	empty String if not found
	 */
	public static String getFieldValueAsString(JsonNode json, String field) {
		return getFieldValueAsString(json, field, "");
	}

	public static String getFieldValueAsString(JsonNode json, String field, String defaultValue) {
		if (isNullJson(json) || isBlank(field))
			return defaultValue;
		JsonNode childJson = json.get(getFieldPrefix(field));
		String childField = getFieldSuffix(field);
		if (isNullJson(childJson))
			return defaultValue;
		if (childJson.isValueNode())
			return childField.isEmpty() ? childJson.asText() : defaultValue;
		if (childField.isEmpty())
			return childJson.toString();
		return getFieldValueAsString(childJson, childField, defaultValue);
	}

	private static String getFieldPrefix(String field) {
		int index = field.indexOf(".");
		return index < 0 ? field : field.substring(0, index);
	}

	private static String getFieldSuffix(String field) {
		int index = field.indexOf(".");
		return index < 0 ? "" : field.substring(index + 1);
	}

	/**
	 * used to filter out all non-container nodes such as metadata nodes
	 *
	 * @param jsonNode
	 */
	public List<JsonNode> getContainerNodes(JsonNode jsonNode) {
		Iterable<Map.Entry<String, JsonNode>> iterable = jsonNode::fields;
		return StreamSupport.stream(iterable.spliterator(), false)
				.map(Map.Entry::getValue)
				.filter(JsonNode::isContainerNode)
				.collect(Collectors.toList());
	}

	/**
	 * reserve child nodes whose name is contained in fields and remove all the others
	 *
	 * @param jsonNode
	 * @param fields
	 * @return all removed field names
     */
	public List<String> filterFields(JsonNode jsonNode, String... fields) {
		return filterFields(jsonNode, new HashSet<>(Arrays.asList(fields)));
	}

	public List<String> filterFields(JsonNode jsonNode, Set<String> fields) {
		if (jsonNode == null || ! jsonNode.isContainerNode())
			throw new IllegalArgumentException("Unsupported: not a container node");
		Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();
		List<String> removedFields = new ArrayList<>(jsonNode.size());
		while (iterator.hasNext()) {
			String field = iterator.next().getKey();
			if (!fields.contains(field)) {
				iterator.remove();
				removedFields.add(field);
			}
		}
		return removedFields;
	}

	public static void removeFields(JsonNode jsonNode, String... fields) {
		removeFields(jsonNode, new HashSet<>(Arrays.asList(fields)));
	}

	public static void removeFields(JsonNode jsonNode, Set<String> fields) {
		if (jsonNode == null || ! jsonNode.isContainerNode())
			throw new IllegalArgumentException("Unsupported: not a container node");
		if (ObjectUtil.isEmpty(fields))
			return;
		fields.stream().forEach( field -> {
			JsonNode parent = jsonNode.findParent(field);
			if (parent != null)
				((ObjectNode) parent).remove(field);
		});
	}
	
}
