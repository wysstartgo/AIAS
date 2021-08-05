package me.calvin.example;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import me.calvin.aias.SentenceEncoder;
import me.calvin.aias.util.FeatureComparison;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SentenceEncoderExample {

  private static final Logger logger = LoggerFactory.getLogger(SentenceEncoderExample.class);

  private SentenceEncoderExample() {}

  public static void main(String[] args) throws IOException, ModelException, TranslateException {
    List<String> inputs = new ArrayList<>();
    inputs.add("I am a sentence for which I would like to get its embedding");
    inputs.add("I am a sentence");
    inputs.add("I am a sentence for which I would like to get ...");

    SentenceEncoder sentenceEncoder = new SentenceEncoder();
    try (ZooModel<String[], float[][]> model = ModelZoo.loadModel(sentenceEncoder.criteria());
         Predictor<String[], float[][]> predictor = model.newPredictor()) {

      float[][] embeddings =  predictor.predict(inputs.toArray(new String[0]));

      float[] feature1 = embeddings[0];
      float[] feature2 = embeddings[1];
      float[] feature3 = embeddings[2];
      
      logger.info("length: " + feature1.length);

      logger.info(Arrays.toString(feature1));
      logger.info(Arrays.toString(feature2));
      logger.info(Arrays.toString(feature3));
      
      logger.info(Float.toString(FeatureComparison.calculSimilar(feature1, feature2)));
      logger.info(Float.toString(FeatureComparison.calculSimilar(feature1, feature3)));
      
    }
  }
}
