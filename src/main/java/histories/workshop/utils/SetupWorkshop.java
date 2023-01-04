package histories.workshop.utils;

import histories.workshop.chapter2.Chapter2Workflows;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class SetupWorkshop {
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    public static WorkflowClient getClient(String namespace, String identity) {
        WorkflowClient client = WorkflowClient.newInstance(service,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(namespace)
                        .setIdentity(identity)
                        .build()
                );
        return client;
    }

    public static void main(String[] args) {
        Chapter2Workflows.startChapter1(getClient("default", "chapter1"));
    }
}
