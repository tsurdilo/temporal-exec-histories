package histories.workshop.chapter2;

import com.google.gson.JsonElement;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.history.v1.History;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryRequest;
import io.temporal.api.workflowservice.v1.GetWorkflowExecutionHistoryResponse;
import io.temporal.common.converter.DataConverterException;
import io.temporal.serviceclient.WorkflowServiceStubs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class GetFullHistory {

    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    public static void main(String[] args) {
        History fullHistory = pullExecHistory(History.newBuilder().build(),
                WorkflowExecution.newBuilder()
                .setWorkflowId("HelloActivityWorkflow")
                .build(), "default", null);
        System.out.println(historyToJson(fullHistory, false));
    }


    private static History pullExecHistory(History history, WorkflowExecution execution, String nameaspace, ByteString pageToken) {
        GetWorkflowExecutionHistoryResponse res;
        if(pageToken == null) {
            res =  service.blockingStub().getWorkflowExecutionHistory(
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setExecution(execution)
                            .setNamespace(nameaspace)
                            .build()
            );
        } else {
            res = service.blockingStub().getWorkflowExecutionHistory(
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setExecution(execution)
                            .setNamespace(nameaspace)
                            .setNextPageToken(pageToken)
                            .build()
            );
        }
        history = history.toBuilder().addAllEvents(res.getHistory().getEventsList()).build();
        if (res.getNextPageToken() != null && res.getNextPageToken().size() > 0) {
            return pullExecHistory(history, execution, nameaspace, res.getNextPageToken());
        }
        return history;
    }

    public static String historyToJson(History history, boolean prettyPrint) {
        Gson GSON_PRETTY_PRINTER = new GsonBuilder().setPrettyPrinting().create();
        @SuppressWarnings("deprecation")
        JsonParser GSON_PARSER = new JsonParser();
        JsonFormat.Printer printer = JsonFormat.printer();
        try {
            String protoJson = printer.print(history);
            String historyFormatJson = HistoryJsonUtils.protoJsonToHistoryFormatJson(protoJson);

            if (prettyPrint) {
                @SuppressWarnings("deprecation")
                JsonElement je = GSON_PARSER.parse(historyFormatJson);
                return GSON_PRETTY_PRINTER.toJson(je);
            } else {
                return historyFormatJson;
            }
        } catch (InvalidProtocolBufferException e) {
            throw new DataConverterException(e);
        }
    }
}
