import pickle
import numpy as np

# Sample input data (using our assumed order)
test_input = [25.0,  # temperature
              45.0,  # rainfall
              1.0,   # fertilizer (yes)
              1.0,   # irrigation (yes)
              60.0]  # days

try:
    # Try to load the model
    print("Loading model...")
    with open('/Users/jonathanaveo/Downloads/model.pkl', 'rb') as f:
        model = pickle.load(f)
    print("Model loaded successfully!")
    print("Model type:", type(model))
    
    # Try to make a prediction
    print("\nTrying to predict with sample data:", test_input)
    X = np.array(test_input).reshape(1, -1)
    prediction = model.predict(X)
    print("Prediction result:", prediction)
    
except Exception as e:
    print("Error:", str(e))
    print("Error type:", type(e).__name__)
