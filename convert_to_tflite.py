import pickle
import numpy as np
import tensorflow as tf
from sklearn.preprocessing import StandardScaler

# Load the XGBoost model
with open('/Users/jonathanaveo/Downloads/model.pkl', 'rb') as f:
    xgb_model = pickle.load(f)

# Create a TensorFlow model that mimics the XGBoost model
class TFLiteModel(tf.Module):
    def __init__(self, xgb_model):
        super(TFLiteModel, self).__init__()
        self.xgb_model = xgb_model
        
    @tf.function(input_signature=[tf.TensorSpec(shape=[1, 5], dtype=tf.float32)])
    def predict(self, inputs):
        # Convert inputs to numpy for XGBoost
        np_inputs = inputs.numpy()
        # Get prediction from XGBoost
        prediction = self.xgb_model.predict(np_inputs)
        # Convert back to tensor
        return tf.constant(prediction, dtype=tf.float32)

# Create and save the TF Lite model
tflite_model = TFLiteModel(xgb_model)

# Convert the model to TF Lite format
converter = tf.lite.TFLiteConverter.from_keras_model(tflite_model)
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
tflite_model_content = converter.convert()

# Save the TF Lite model
with open('app/src/main/assets/model.tflite', 'wb') as f:
    f.write(tflite_model_content)

print("Model converted and saved as model.tflite")
