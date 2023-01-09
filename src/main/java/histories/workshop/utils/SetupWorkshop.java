package histories.workshop.utils;

import histories.workshop.chapter3.Chapter3Workflows;
import io.grpc.Metadata;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

public class SetupWorkshop {
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                    .setHeaders(getMetadata())
                    .build());

    public static WorkflowClient getClient(String namespace, String identity) {
        WorkflowClient client = WorkflowClient.newInstance(service,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(namespace)
                        .setIdentity(identity)
                        .build()
                );
        return client;
    }

    private static final Metadata getMetadata() {
        Metadata metadata = new Metadata();
        Metadata.Key<String> testKey = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(testKey, "someTestValue");
        return metadata;
    }

    public static void main(String[] args) {
        Chapter3Workflows.startChapter1(getClient("default", "chapter1"));
    }
}
