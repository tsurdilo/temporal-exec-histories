package histories.workshop.chapter2;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.temporal.internal.common.ProtoEnumNameUtils;

import java.util.function.BiFunction;

public class HistoryJsonUtils {
    private static final Configuration JSON_PATH_CONFIGURATION =
            Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

    private enum EnumValueConversionPolicy {
        EVENT_TYPE("EVENT_TYPE_", JsonPath.compile("$.events.*.eventType")),
        TASK_QUEUE_KIND("TASK_QUEUE_KIND_", JsonPath.compile("$.events.*.*.taskQueue.kind")),
        PARENT_CLOSE_POLICY("PARENT_CLOSE_POLICY_", JsonPath.compile("$.events.*.*.parentClosePolicy")),
        WORKFLOW_ID_REUSE_POLICY(
                "WORKFLOW_ID_REUSE_POLICY_", JsonPath.compile("$.events.*.*.workflowIdReusePolicy")),
        INITIATOR("CONTINUE_AS_NEW_INITIATOR_", JsonPath.compile("$.events.*.*.initiator")),
        RETRY_STATE(
                "RETRY_STATE_",
                // can be inside workflowExecutionFailedEventAttributes
                JsonPath.compile("$.events.*.*.retryState"),
                // or inside workflowExecutionFailedEventAttributes.childWorkflowExecutionFailureInfo
                JsonPath.compile("$.events.*.*.*.retryState"));

        private final String protobufEnumPrefix;
        private final JsonPath[] jsonPaths;

        EnumValueConversionPolicy(String protobufEnumPrefix, JsonPath... jsonPaths) {
            this.jsonPaths = jsonPaths;
            this.protobufEnumPrefix = protobufEnumPrefix;
        }
    }

    private HistoryJsonUtils() {}

    public static String protoJsonToHistoryFormatJson(String protoJson) {
        return convertEnumValues(protoJson, ProtoEnumNameUtils::uniqueToSimplifiedName);
    }

    public static String historyFormatJsonToProtoJson(String historyFormatJson) {
        return convertEnumValues(historyFormatJson, ProtoEnumNameUtils::simplifiedToUniqueName);
    }

    private static String convertEnumValues(
            String json, BiFunction<String, String, String> convertEnumValue) {
        DocumentContext parsed = JsonPath.parse(json, JSON_PATH_CONFIGURATION);
        for (HistoryJsonUtils.EnumValueConversionPolicy policy : HistoryJsonUtils.EnumValueConversionPolicy.values()) {
            for (JsonPath jsonPath : policy.jsonPaths) {
                parsed.map(
                        jsonPath,
                        (currentValue, configuration) ->
                                convertEnumValue.apply((String) currentValue, policy.protobufEnumPrefix));
            }
        }
        return parsed.jsonString();
    }
}

