package me.aias.example;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import me.aias.example.utils.LightFaceDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/

public class FaceDetectionExample {

    private static final String TOPIC = "face-data";

    public static void main(String[] args)
            throws MalformedModelException, ModelNotFoundException, IOException {
        Criteria<Image, DetectedObjects> criteria = new LightFaceDetection().criteria();
        ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(criteria);
        Predictor<Image, DetectedObjects> predictor = model.newPredictor();

        int numConsumers = 1;
        List<String> topics = Collections.singletonList(TOPIC);
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        // setup consumer
        final List<ConsumerLoop> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, topics, predictor);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    for (ConsumerLoop consumer : consumers) {
                                        consumer.shutdown();
                                    }
                                    executor.shutdown();
                                    try {
                                        executor.awaitTermination(50000, TimeUnit.MILLISECONDS);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }));
    }
}
