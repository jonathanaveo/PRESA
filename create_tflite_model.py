import tensorflow as tf
import numpy as np

# Create a simple model that takes 5 inputs
model = tf.keras.Sequential([
    tf.keras.layers.Dense(16, activation='relu', input_shape=(5,)),
    tf.keras.layers.Dense(8, activation='relu'),
    tf.keras.layers.Dense(1)
])

# Compile the model
model.compile(optimizer='adam', loss='mse')

# Create some sample data (we'll use random data for now)
X_train = np.random.random((100, 5))  # 100 samples, 5 features each
y_train = np.random.random(100)       # 100 target values

# Train the model briefly
model.fit(X_train, y_train, epochs=10, verbose=1)

# Convert the model to TFLite format
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Create assets directory if it doesn't exist
import os
os.makedirs('app/src/main/assets', exist_ok=True)

# Save the TFLite model
with open('app/src/main/assets/model.tflite', 'wb') as f:
    f.write(tflite_model)

print("Model saved as model.tflite")
