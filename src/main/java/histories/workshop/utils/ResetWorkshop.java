package histories.workshop.utils;

import com.google.protobuf.Duration;
import io.temporal.api.operatorservice.v1.DeleteNamespaceRequest;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.serviceclient.OperatorServiceStubs;
import io.temporal.serviceclient.OperatorServiceStubsOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class ResetWorkshop {
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    private static final OperatorServiceStubs operatorService = OperatorServiceStubs.newServiceStubs(
            OperatorServiceStubsOptions.newBuilder()
                    .setChannel(service.getRawChannel())
                    .validateAndBuildWithDefaults());

    public static void main(String[] args) {
        // for cleanup we just delete default ns and recreate it..its simpler that way
        operatorService.blockingStub().deleteNamespace(
                DeleteNamespaceRequest.newBuilder()
                        .setNamespace("default")
                        .build()
        );

        service.blockingStub().registerNamespace(RegisterNamespaceRequest.newBuilder()
                .setNamespace("default")
                .setWorkflowExecutionRetentionPeriod(Duration.newBuilder()
                        .setSeconds(60*60)
                        .build())
                .build());
    }
}
